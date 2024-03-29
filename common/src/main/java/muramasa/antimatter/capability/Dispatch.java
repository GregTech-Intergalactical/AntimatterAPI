package muramasa.antimatter.capability;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Direction;

import java.util.Map;
import java.util.Optional;

public class Dispatch {

    private final Map<Class<?>, Holder<?, ?>> capabilityHolderMap = new Object2ObjectOpenHashMap<>();

    public Dispatch() {

    }


    public void registerHolder(Holder holder) {
        capabilityHolderMap.put(holder.cap, holder);
    }

    public Dispatch invalidate() {
        for (Holder value : capabilityHolderMap.values()) {
            value.invalidate();
        }
        return this;
    }

    public Dispatch invalidate(Direction side) {
        for (Holder value : capabilityHolderMap.values()) {
            value.invalidate(side);
        }
        return this;
    }

    public Dispatch invalidate(Class<?> cap) {
        capabilityHolderMap.get(cap).invalidate();
        return this;
    }

    public Dispatch invalidate(Class<?> cap, Direction side) {
        capabilityHolderMap.get(cap).invalidate(side);
        return this;
    }

    public Holder<?, ?> getHolder(Class<?> cap) {
        return capabilityHolderMap.get(cap);
    }

    public interface Sided<U> {
        Optional<? extends U> forSide(Direction side);

        default Optional<? extends U> forNullSide() {
            return Optional.empty();
        }
    }
}
