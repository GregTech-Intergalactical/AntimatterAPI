package muramasa.antimatter.util;

import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * A class that incorporates both Java's Optional and Forge's LazyOptional.
 *
 * Equals and hashCode isn't overriden!
 */
public class LazyHolder<T> {

    public static <T> LazyHolder<T> of(final @Nullable Supplier<T> value) {
        return value == null ? empty() : new LazyHolder<>(value);
    }

    public static <T> LazyHolder<T> empty() {
        return new LazyHolder<>();
    }

    private final Supplier<T> value;

    private AtomicReference<T> resolvedValue;
    private LazyOptional<T> optionalDelegate;

    private LazyHolder() {
        this(null);
    }

    private LazyHolder(Supplier<T> value) {
        this.value = value;
    }

    public boolean isPresent() {
        return this.value != null;
    }

    @Nullable
    public T get() {
        if (this.resolvedValue == null) {
            this.resolvedValue = this.value == null ? new AtomicReference<>(null) : new AtomicReference<>(this.value.get());
        }
        return this.resolvedValue.get();
    }

    public LazyOptional<T> transform() {
        if (this.optionalDelegate == null) {
            T value = get();
            return this.optionalDelegate = value == null ? LazyOptional.empty() : LazyOptional.of(() -> value);
        }
        return this.optionalDelegate;
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

    public <U> LazyHolder<U> map(Function<? super T, ? extends U> mapper) {
        T value = get();
        return value == null ? empty() : of(() -> mapper.apply(value));
    }

    public LazyHolder<T> filter(Predicate<? super T> predicate) {
        T value = get();
        return value != null && predicate.test(value) ? this : empty();
    }

}
