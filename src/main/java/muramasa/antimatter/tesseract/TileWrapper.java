package muramasa.antimatter.tesseract;

import it.unimi.dsi.fastutil.objects.ObjectSets;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverInstance;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nullable;
import java.util.Set;

public abstract class TileWrapper<T> {

    protected TileEntity tile;
    protected T handler;
    protected boolean removed;

    protected final CoverInstance<?>[] covers = new CoverInstance[6];

    public TileWrapper(TileEntity tile, Capability<T> cap) {
        LazyOptional<T> capability = tile.getCapability(cap);
        if (capability.isPresent()) {
            this.tile = tile;
            this.handler = capability.orElse(null);
            onInit();
            capability.addListener(x -> this.remove(null));
        } else {
            removed = true;
        }
    }

    public void remove(@Nullable Direction side) {
        if (side == null) {
            if (tile.isRemoved()) {
                onRemove();
                removed = true;
            } else {
                // What if tile is recreate cap ?
            }
        } else {
            covers[side.getIndex()] = null;
        }
    }

    public void update(Direction side, CoverInstance<?> cover) {
        covers[side.getIndex()] = cover;
    }

    public boolean isRemoved() {
        return removed;
    }

    protected Set<?> getFilterAt(int dir) {
        return covers[dir] != null ? covers[dir].getFilter() : ObjectSets.EMPTY_SET;
    }

    protected Cover getCoverAt(int dir) {
        return covers[dir] != null ? covers[dir].getCover() : null;
    }
    
    protected abstract void onInit();

    protected abstract void onRemove();
}
