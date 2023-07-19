package muramasa.antimatter.capability;

import com.google.common.collect.ImmutableList;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Ref;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Holder<V, T extends Dispatch.Sided<V>> {
    private final Dispatch dispatch;
    public final Class<?> cap;
    private final Optional[] sided;
    private List<Consumer<? super T>> consumers = new ObjectArrayList<>();
    private final ImmutableList<Set<Runnable>> listeners;
    private Supplier<? extends T> supplier;
    private T resolved;
    private boolean flag;

    public Holder(Class<?> cap, Dispatch dispatch, Supplier<T> source) {
        this.dispatch = dispatch;
        this.cap = cap;
        //7th side is null side
        this.listeners = ImmutableList.of(new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>(), new HashSet<>());
        this.sided = new Optional[Ref.DIRS.length + 1];
        for (Direction dir : Ref.DIRS) {
            sided[dir.get3DDataValue()] = Optional.empty();
        }
        sided[6] = Optional.empty();
        this.flag = false;
        this.supplier = source;
        dispatch.registerHolder(this);
    }

    public Holder(Class<V> cap, Dispatch dispatch) {
        this(cap, dispatch, null);
    }

    public boolean isPresent() {
        return supplier != null;
    }

    public void set(Supplier<? extends T> supplier) {
        this.supplier = supplier;
    }

    public void onInit(Consumer<? super T> consumer) {
        consumers.add(consumer);
    }

    public void addListener(Direction direction, Runnable runnable){
        listeners.get(direction == null ? 6 : direction.get3DDataValue()).add(runnable);
    }

    public void invalidate(Direction side) {
        if (side == null) {
            listeners.get(6).forEach(Runnable::run);
            return;
        }
        listeners.get(side.get3DDataValue()).forEach(Runnable::run);
    }

    public void invalidate() {
        listeners.forEach(l -> l.forEach(Runnable::run));
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
        for (Consumer<? super T> con : consumers) {
            con.accept(resolved);
        }
        return resolved;
    }

    public Optional<? extends V> nullSide() {
        if (resolved == null) {
            get();
        }
        return resolved.forNullSide();
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

    public <U> Optional<U> map(Function<? super T, ? extends U> mapper) {
        T value = get();
        return value == null ? Optional.empty() : Optional.of(mapper.apply(value));
    }

    public Optional<T> filter(Predicate<? super T> predicate) {
        T value = get();
        return value != null && predicate.test(value) ? Optional.of(value) : Optional.empty();
    }

    public Optional<? extends V> side(Direction side) {
        if (!isPresent()) {
            return Optional.empty();
        }
        if (resolved == null) {
            get();
        }
        int index = side == null ? 6 : side.get3DDataValue();
        Optional<? extends V> t = sided[index];
        if (!t.isPresent()) {
            sided[index] = (t = (side == null ? nullSide() : resolved.forSide(side)));
        }
        return t;
    }

}
