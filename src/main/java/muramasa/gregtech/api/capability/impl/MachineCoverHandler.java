package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.cover.CoverBehaviour;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import net.minecraft.util.EnumFacing;

public class MachineCoverHandler extends CoverHandler {

    public MachineCoverHandler(TileEntityMachine tile, CoverBehaviour... covers) {
        super(tile, covers);
    }

    @Override
    public boolean isCoverValid(EnumFacing side, CoverBehaviour cover) {
        return side != ((TileEntityMachine) tile).getEnumFacing() && super.isCoverValid(side, cover);
    }
}
