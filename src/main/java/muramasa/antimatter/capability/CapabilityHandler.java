package muramasa.antimatter.capability;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.network.packets.CapabilityPacket;
import muramasa.antimatter.tile.TileEntityBase;
import net.minecraft.nbt.CompoundNBT;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

import static muramasa.antimatter.capability.CapabilitySide.*;

public class CapabilityHandler<T extends TileEntityBase, H extends ICapabilityHandler> {

    protected T tile;
    protected H handler;
    protected CompoundNBT tag;
    protected BiFunction<T, CompoundNBT, H> capability = (tile, tag) -> null;
    protected CapabilitySide side;

    public CapabilityHandler(T tile, CapabilitySide side) {
        this.tile = tile;
        this.side = side;
    }

    public void setup(BiFunction<T, CompoundNBT, H> capFunc) {
        capability = capFunc;
    }

    public void init() {
        if (canInit()) {
            if (capability == null) {
                throw new NoSuchElementException("No Capability setup");
            }

            handler = capability.apply(tile, tag);

            if (handler == null) {
                throw new NoSuchElementException("No Handler initialized");
            }

            // For the capabilities which exist on the both side, we should send the initialization tag which was used on the server.
            // That tag will provide correct data for the initialization of capability on the client side in the constructor.
            if (side == SYNC && tile.isClientSide()) {
                Antimatter.NETWORK.sendToServer(new CapabilityPacket(handler.getCapability().getName(), tile.getPos()));
            }
        }
    }

    public void read(CompoundNBT tag) {
        this.tag = tag;
    }

    public boolean isPresent() {
        return handler != null;
    }

    public void ifPresent(Consumer<? super H> action) {
        if (handler != null) {
            action.accept(handler);
        }
    }

    public void ifPresentOrElse(Consumer<? super H> action, Runnable other) {
        if (handler != null) {
            action.accept(handler);
        } else {
            other.run();
        }
    }

    public H get() {
        if (handler == null) {
            throw new NoSuchElementException("No Handler initialized");
        }
        return handler;
    }

    public <U> Optional<U> map(Function<? super H, ? extends U> mapper) {
        return handler != null ? Optional.of(mapper.apply(handler)) : Optional.empty();
    }

    public <U> Optional<U> flatMap(Function<? super H, ? extends Optional<? extends U>> mapper) {
        if (handler != null) {
            Optional<U> r = (Optional)mapper.apply(handler);
            return Objects.requireNonNull(r);
        }
        return Optional.empty();
    }

    public H orElse(H other) {
        return handler != null ? handler : other;
    }

    public boolean canInit() {
        switch (side) {
            case CLIENT: return tile.isClientSide();
            case SERVER: return tile.isServerSide();
            default: return true;
        }
    }

    public boolean equals(String name) {
        return handler != null && (handler.getCapability() == null ? "".equals(name) : handler.getCapability().getName().equals(name));
    }

    public CompoundNBT getOrCreateTag(String key) {
        CompoundNBT nbt = new CompoundNBT();
        if (tag != null) nbt.put(key, tag);
        return nbt;
    }
}
