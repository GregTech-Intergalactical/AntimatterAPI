package muramasa.antimatter.capability;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.network.packets.CapabilityPacket;
import muramasa.antimatter.tile.TileEntityBase;
import muramasa.antimatter.util.Utils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.capabilities.Capability;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

public class CapabilityHolder<T extends TileEntityBase, H extends ICapabilityHandler> {

    protected T tile;
    protected H handler;
    protected CompoundNBT tag;
    protected BiFunction<T, CompoundNBT, H> capability = (tile, tag) -> null;
    protected Dist side; // when null, works on both sides

    public CapabilityHolder(T tile) {
        this.tile = tile;
    }

    public CapabilityHolder(T tile, Dist side) {
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

            // For the capabilities which exist on the both side, we should send the initialization tag which was extracted from the server.
            // That tag will provide correct data for the initialization of capability on the client side.
            if (side == null && tile.isClientSide()) {
                Antimatter.NETWORK.sendToServer(new CapabilityPacket(handler.getCapability().getName(), tile.getPos(), tile.getDimension()));
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
        if (side == null) return true;
        switch (side) {
            case CLIENT: return tile.isClientSide();
            case DEDICATED_SERVER: return tile.isServerSide();
            default: return true;
        }
    }
}
