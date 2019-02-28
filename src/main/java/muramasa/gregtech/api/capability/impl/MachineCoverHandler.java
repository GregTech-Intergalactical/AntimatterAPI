package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.cover.Cover;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import net.minecraft.util.EnumFacing;

public class MachineCoverHandler extends CoverHandler {

    public MachineCoverHandler(TileEntityMachine tile, Cover... covers) {
        super(tile, covers);
    }

    @Override
    public boolean isCoverValid(EnumFacing side, Cover cover) {
        return side != ((TileEntityMachine) tile).getEnumFacing() && super.isCoverValid(side, cover);
    }
}
