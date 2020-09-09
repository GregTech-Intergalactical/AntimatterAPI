package muramasa.antimatter.capability.pipe;

import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import net.minecraft.nbt.CompoundNBT;

public class PipeCoverHandler<T extends TileEntityPipe> extends CoverHandler<T> {

    public PipeCoverHandler(T tile, CompoundNBT tag) {
        super(tile, tile.getValidCovers());
        if (tag != null) deserialize(tag);
    }
}
