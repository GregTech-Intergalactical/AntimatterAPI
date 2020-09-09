package muramasa.antimatter.capability.pipe;

import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.capability.ICapabilityHandler;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.Capability;

public class PipeCoverHandler extends CoverHandler<TileEntityPipe> {

    public PipeCoverHandler(TileEntityPipe tile, CompoundNBT tag) {
        super(tile, tile.getValidCovers());
        if (tag != null) deserialize(tag);
    }
}
