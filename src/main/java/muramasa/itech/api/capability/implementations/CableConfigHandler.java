package muramasa.itech.api.capability.implementations;

import muramasa.itech.common.tileentities.TileEntityCable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class CableConfigHandler extends MachineConfigHandler {

    public CableConfigHandler(TileEntity tile) {
        super(tile);
    }

    @Override
    public boolean onWrench(EnumFacing side) {
        if (tile instanceof TileEntityCable) {
            ((TileEntityCable) tile).toggleConnection(side);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCrowbar(EnumFacing side) {
        return false; //NOOP
    }
}
