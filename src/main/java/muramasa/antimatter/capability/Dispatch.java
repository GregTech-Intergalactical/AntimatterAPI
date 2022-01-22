package muramasa.antimatter.capability;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import java.util.Map;

public class Dispatch {

    private final Map<Capability<?>, Holder<?, ?>> capabilityHolderMap = new Object2ObjectOpenHashMap<>();

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

    public Dispatch invalidate(Capability<?> cap) {
        capabilityHolderMap.get(cap).invalidate();
        return this;
    }

    public Dispatch invalidate(Capability<?> cap, Direction side) {
        capabilityHolderMap.get(cap).invalidate(side);
        return this;
    }

    public Holder<?, ?> getHolder(Capability<?> cap) {
        return capabilityHolderMap.get(cap);
    }

    public interface Sided<U> {
        LazyOptional<? extends U> forSide(Direction side);

        default LazyOptional<? extends U> forNullSide() {
            return LazyOptional.empty();
        }
    }
}
