package muramasa.antimatter.util;

import javax.annotation.Nonnull;

@FunctionalInterface
public interface NonNullSupplier<T> {
    @Nonnull
    T get();
}
