package muramasa.antimatter.capability;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.Ref;
import muramasa.antimatter.tile.TileEntityBase;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;

import java.util.Map;

public class Dispatch {

    private final Map<Capability<?>, Holder<?,?,?>> capabilityHolderMap = new Object2ObjectOpenHashMap<>();

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

    public void refresh() {
        capabilityHolderMap.forEach((k,v) -> v.refresh());
    }

    public void refresh(Capability<?> cap) {
        Holder holder = capabilityHolderMap.get(cap);
        if (holder != null) holder.refresh();
    }

    public interface Sided<U> {
        LazyOptional<? extends U> forSide(Direction side);
        void refresh();
    }
}
