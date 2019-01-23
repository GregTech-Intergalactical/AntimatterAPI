package muramasa.itech.api.capability.implementations;

import muramasa.itech.api.capability.IConfigurable;
import muramasa.itech.api.capability.ICoverable;
import muramasa.itech.api.capability.ITechCapabilities;
import muramasa.itech.api.enums.CoverType;
import muramasa.itech.api.util.SoundList;
import muramasa.itech.common.tileentities.base.TileEntityMachine;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class MachineConfigHandler implements IConfigurable {

    protected TileEntity tile;

    public MachineConfigHandler(TileEntity tile) {
        this.tile = tile;
    }

    @Override
    public boolean onWrench(EnumFacing side) {
        if (tile == null) return false;
        if (tile.hasCapability(ITechCapabilities.COVERABLE, side)) { //Side has cover, configure.
            ICoverable coverHandler = tile.getCapability(ITechCapabilities.COVERABLE, side);
            if (coverHandler == null) return false;
            CoverType coverType = coverHandler.getCover(side);
            if (coverType.canWrenchToggleState() && coverType != CoverType.NONE) {
                //TODO toggle state
                SoundList.WRENCH.play(tile.getWorld(), tile.getPos());
                return true;
            }
        } else if (tile instanceof TileEntityMachine) { //Used wrench on side with no cover, rotate.
            if (side.getAxis() != EnumFacing.Axis.Y) {
                ((TileEntityMachine) tile).rotate(side);
            }
        }
        return true;
    }

    @Override
    public boolean onCrowbar(EnumFacing side) {
        if (tile == null) return false;
        if (tile.hasCapability(ITechCapabilities.COVERABLE, side)) { //Side has cover
            ICoverable coverHandler = tile.getCapability(ITechCapabilities.COVERABLE, side);
            if (coverHandler != null && coverHandler.getCover(side) != CoverType.NONE) {
                coverHandler.setCover(side, CoverType.NONE);
                SoundList.BREAK.play(tile.getWorld(), tile.getPos());
                return true;
            }
        } else { //Used crowbar on side with no cover
            //TODO
        }
        return false;
    }
}
