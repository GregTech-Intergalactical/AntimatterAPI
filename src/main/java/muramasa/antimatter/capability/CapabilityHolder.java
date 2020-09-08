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

    public CapabilityHolder(T tile) {
        this.tile = tile;
    }

    public void init(Function<T, H> capFunc) {
        handler = capFunc.apply(tile);
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
        } else {
            return handler;
        }
    }

    public <U> Optional<U> map(Function<? super H, ? extends U> mapper) {
        return Optional.ofNullable(mapper.apply(handler));
    }

    public <U> Optional<U> flatMap(Function<? super H, ? extends Optional<? extends U>> mapper) {
        Objects.requireNonNull(mapper);
        if (handler == null) {
            return Optional.empty();
        } else {
            Optional<U> r = (Optional)mapper.apply(handler);
            return Objects.requireNonNull(r);
        }
    }

    public H orElse(H other) {
        return handler != null ? handler : other;
    }
}
