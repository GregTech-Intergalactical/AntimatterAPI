package muramasa.antimatter.util;

import java.util.function.Supplier;

public class Cache<T> {
    private final Supplier<T> getter;
    private T resolved;
    private boolean valid;

    public Cache(Supplier<T> getter) {
        this.getter = getter;
    }

    public T get() {
        if (valid) return resolved;
        resolved = getter.get();
        valid = true;
        return get();
    }

    public void invalidate() {
        this.valid = false;
        this.resolved = null;
    }
}
