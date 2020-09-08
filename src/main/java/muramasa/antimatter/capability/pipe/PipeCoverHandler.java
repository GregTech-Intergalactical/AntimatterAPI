package muramasa.antimatter.capability.pipe;

import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.capability.ICapabilityHandler;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import net.minecraft.nbt.CompoundNBT;

public class PipeCoverHandler extends CoverHandler<TileEntityPipe> implements ICapabilityHandler {

    public PipeCoverHandler(TileEntityPipe tile, CompoundNBT tag) {
        super(tile, tile.getValidCovers());
        deserialize(tag);
    }
}
