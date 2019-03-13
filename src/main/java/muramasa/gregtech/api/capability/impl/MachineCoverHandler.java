package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.cover.Cover;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import net.minecraft.util.EnumFacing;

public class MachineCoverHandler extends CoverHandler {

    public MachineCoverHandler(TileEntityMachine tile, Cover... covers) {
        super(tile, covers);
    }

    @Override
    public boolean isValid(EnumFacing side, Cover cover) {
        return /*side != getTileFacing() &&*/ super.isValid(side, cover);
    }

    @Override
    public EnumFacing getTileFacing() {
        return ((TileEntityMachine) getTile()).getEnumFacing();
    }
}
