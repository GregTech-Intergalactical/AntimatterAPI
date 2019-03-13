package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import net.minecraft.util.EnumFacing;

public class MachineConfigHandler extends ConfigHandler {

    public MachineConfigHandler(TileEntityMachine tile) {
        super(tile);
    }

    @Override
    public boolean onWrench(EnumFacing side) {
        if (super.onWrench(side)) { //Tile is coverable and has cover
            return true;
        } else if (getTile() instanceof TileEntityMachine){ //Tile is not coverable and/or cover on side is empty
            ((TileEntityMachine) getTile()).setFacing(side);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCrowbar(EnumFacing side) {
        return super.onCrowbar(side);
    }

    @Override
    public boolean onScrewdriver(EnumFacing side) {
        return super.onScrewdriver(side);
    }
}
