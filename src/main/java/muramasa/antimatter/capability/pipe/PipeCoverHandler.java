package muramasa.antimatter.capability.pipe;

import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import net.minecraft.util.Direction;

public class PipeCoverHandler<T extends TileEntityPipe<?>> extends CoverHandler<T> {

    public PipeCoverHandler(T tile) {
        super(tile, tile.getValidCovers());
        // if (tag != null) deserialize(tag);
    }

    @Override
    public boolean set(Direction side, ICover old, ICover stack, boolean sync) {
        boolean ok = super.set(side, old, stack, sync);
        if (ok && sync) {
            boolean anyEmpty = this.covers.values().stream().anyMatch(t -> !t.isEmpty());
            this.getTile().onCoverUpdate(!old.isEmpty() && stack.isEmpty(), anyEmpty, side, old, stack);
        }
        return ok;
    }


    public void onTransfer(Object obj, Direction from, Direction towards, boolean execute) {
        this.get(from).onTransfer(obj, true, execute);
        this.get(towards).onTransfer(obj, false, execute);
    }
}
