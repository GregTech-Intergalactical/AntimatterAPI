package muramasa.antimatter.capability;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.Direction;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Map;

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
        LazyOptional<? extends U> forSide(Direction side);

        default LazyOptional<? extends U> forNullSide() {
            return LazyOptional.empty();
        }
    }
}
