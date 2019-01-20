package muramasa.itech.api.capability.implementations;

import muramasa.itech.common.tileentities.TileEntityCable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class CableConfigHandler extends MachineConfigHandler {

    public CableConfigHandler(TileEntity tile) {
        super(tile);
    }

    @Override
    public void onWrench(EnumFacing side) {
        if (tile instanceof TileEntityCable) {
            ((TileEntityCable) tile).toggleConnection(side);
        }
    }

    @Override
    public void onCrowbar(EnumFacing side) {
        //NOPP
    }
}
