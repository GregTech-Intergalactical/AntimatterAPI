package muramasa.antimatter.capability;

import muramasa.antimatter.Ref;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Holder<V, T extends Dispatch.Sided<V>> {
    private final Dispatch dispatch;
    public final Capability<?> cap;
    private final LazyOptional[] sided;
    private LazyOptional<T> opt = LazyOptional.empty();
    private Supplier<? extends T> supplier;
    private T resolved;
    private boolean flag;

    public Holder(Capability<?> cap, Dispatch dispatch, Supplier<T> source) {
        this.dispatch = dispatch;
        this.cap = cap;
        this.sided = new LazyOptional[Ref.DIRS.length];
        for (Direction dir : Ref.DIRS) {
            sided[dir.getIndex()] = LazyOptional.empty();
        }
        this.flag = false;
        this.supplier = source;
        dispatch.registerHolder(this);
    }

    public Holder(Capability<V> cap, Dispatch dispatch) {
        this(cap, dispatch, null);
    }

    public boolean isPresent() {
        return supplier != null;
    }

    public void set(Supplier<? extends T> supplier) {
        this.supplier = supplier;
    }

    public void invalidate(Direction side) {
        sided[side.getIndex()].invalidate();
    }

    public void invalidate() {
        for (LazyOptional opt : sided) {
            opt.invalidate();
        }
    }

    public void refresh() {
        if (!isPresent()) return;
        if (resolved == null) get();
        resolved.refresh();
    }

    @Nullable
    public T get() {
       if (flag) {
           return resolved;
       }
       if (supplier == null) {
           flag = true;
           return null;
       }
       resolved = supplier.get();
       flag = true;
       return resolved;
    }

    public LazyOptional<? extends T> nullSide() {
        if (!opt.isPresent()) {
            opt = isPresent() ? LazyOptional.of(this::get) : LazyOptional.empty();
        }
        return opt;
    }

    public T orElse(T orElse) {
        T value = get();
        return value == null ? orElse : value;
    }

    public T orElseGet(Supplier<T> orElse) {
        T value = get();
        return value == null ? orElse.get() : value;
    }

    public T orElseRetrieve(Supplier<Supplier<T>> orElse) {
        T value = get();
        return value == null ? orElse.get().get() : value;
    }

    public <X extends Throwable> T orElseThrow(Supplier<X> throwable) throws X {
        T value = get();
        if (value != null) {
            return value;
        }
        throw throwable.get();
    }

    public void ifPresent(Consumer<? super T> consumer) {
        T value = get();
        if (value != null) {
            consumer.accept(value);
        }
    }

    public void ifPresentOrElse(Consumer<? super T> consumer, Runnable runnable) {
        T value = get();
        if (value != null) {
            consumer.accept(value);
        } else {
            runnable.run();
        }
    }

    public <X extends Throwable> void ifPresentOrThrow(Consumer<? super T> consumer, Supplier<X> throwable) throws X {
        T value = get();
        if (value != null) {
            consumer.accept(value);
        } else {
            throw throwable.get();
        }
    }

    public <U> LazyOptional<U> lazyMap(Function<? super T, ? extends U> mapper) {
        T value = get();
        return value == null ? LazyOptional.empty() : LazyOptional.of(() -> mapper.apply(value));
    }

    public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
        T value = get();
        return value == null ? Optional.empty() : Optional.of(mapper.apply(value));
    }

    public Optional<T> filter(Predicate<? super T> predicate) {
        T value = get();
        return value != null && predicate.test(value) ? Optional.of(value)  : Optional.empty();
    }

    public LazyOptional<? extends V> side(Direction side) {
        if (!isPresent()) {
            return LazyOptional.empty();
        }
        if (resolved == null) {
            get();
        }
        if (side == null) {
            return nullSide().cast();
        }
        LazyOptional<? extends V> t = sided[side.getIndex()];
        if (!t.isPresent()) {
            sided[side.getIndex()] = (t = resolved.forSide(side));
        }
        return t;
    }

}
