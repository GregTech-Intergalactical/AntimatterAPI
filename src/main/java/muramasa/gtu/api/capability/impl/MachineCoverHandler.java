package muramasa.gtu.api.capability.impl;

import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.cover.Cover;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.util.Utils;
import net.minecraft.util.EnumFacing;

public class MachineCoverHandler extends CoverHandler {

    private int outputSide = 3;

    public MachineCoverHandler(TileEntityMachine tile) {
        //TODO add valid covers to Machine class
        super(tile, GregTechAPI.CoverPlate, GregTechAPI.CoverOutput);
        covers = new Cover[] {
            GregTechAPI.CoverNone, GregTechAPI.CoverNone, GregTechAPI.CoverNone, GregTechAPI.CoverOutput, GregTechAPI.CoverNone, GregTechAPI.CoverNone
        };
    }

    public boolean setOutputFacing(EnumFacing side) {
        if (set(side, GregTechAPI.CoverOutput)) {
            covers[outputSide] = GregTechAPI.CoverNone;
            outputSide = Utils.rotateFacing(side, getTileFacing()).getIndex();
            return true;
        }
        return false;
    }

    @Override
    public boolean isValid(EnumFacing side, Cover cover) {
        return /*side != getTileFacing() &&*/ super.isValid(side, cover);
    }

    @Override
    public EnumFacing getTileFacing() {
        return ((TileEntityMachine) getTile()).getFacing();
    }
}
