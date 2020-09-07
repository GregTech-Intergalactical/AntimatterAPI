package muramasa.antimatter.capability;

import muramasa.antimatter.tile.TileEntityBase;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

public class CapabilityHolder<T extends TileEntityBase, H extends ICapabilityHandler> {

    protected H handler;
    protected T tile;
    protected boolean canInit;

    public CapabilityHolder(T tile, CapabilitySide side) {
        this.tile = tile;
        this.canInit = isValid(side);
    }

    public void init(Function<T, H> capFunc) {
        if (canInit) {
            handler = capFunc.apply(tile);
        }
    }

    public boolean isPresent() {
        return handler != null;
    }

    public void ifPresent(Consumer<? super H> action) {
        if (isPresent()) {
            action.accept(handler);
        }
    }

    public H get() {
        if (handler == null) {
            throw new NoSuchElementException("No Handler initialized");
        } else {
            return handler;
        }
    }

    public <U> Optional<U> map(Function<? super H, ? extends U> mapper) {
        return Optional.ofNullable(mapper.apply(handler));
    }

    public <U> Optional<U> flatMap(Function<? super H, ? extends Optional<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        if (!isPresent()) {
            return Optional.empty();
        } else {
            Optional<U> r = (Optional)mapper.apply(handler);
            return Objects.requireNonNull(r);
        }
    }

    public H orElse(H other) {
        return isPresent() ? handler : other;
    }

    private boolean isValid(CapabilitySide side) {
        switch (side) {
            case CLIENT: return tile.isClientSide();
            case SERVER: return tile.isServerSide();
            default: return true;
        }
    }
}
