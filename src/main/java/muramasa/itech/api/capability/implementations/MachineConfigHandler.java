package muramasa.itech.api.capability.implementations;

import muramasa.itech.api.capability.IConfigurable;
import muramasa.itech.api.capability.ICoverable;
import muramasa.itech.api.capability.ITechCapabilities;
import muramasa.itech.api.enums.CoverType;
import muramasa.itech.api.util.SoundList;
import muramasa.itech.common.blocks.BlockMachines;
import muramasa.itech.common.tileentities.TileEntityMachine;
import muramasa.itech.loaders.ContentLoader;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

public class MachineConfigHandler implements IConfigurable {

    protected TileEntity tile;

    public MachineConfigHandler(TileEntity tile) {
        this.tile = tile;
    }

    @Override
    public void onWrench(EnumFacing side) {
        if (tile == null) return;
        if (tile.hasCapability(ITechCapabilities.COVERABLE, side)) { //Side has cover, configure.
            ICoverable coverHandler = tile.getCapability(ITechCapabilities.COVERABLE, side);
            if (coverHandler == null) return;
            CoverType coverType = coverHandler.getCover(side);
            if (coverType.canWrenchToggleState() && coverType != CoverType.NONE) {
                //TODO toggle state
                SoundList.WRENCH.play(tile.getWorld(), tile.getPos());
            }
        } else if (tile instanceof TileEntityMachine) { //Used wrench on side with no cover, rotate.
            if (side.getAxis() != EnumFacing.Axis.Y) {
                tile.getWorld().setBlockState(tile.getPos(), ContentLoader.blockMachines.getDefaultState().withProperty(BlockMachines.FACING, side), 3);
            }
        }
    }

    @Override
    public void onCrowbar(EnumFacing side) {
        if (tile == null) return;
        if (tile.hasCapability(ITechCapabilities.COVERABLE, side)) { //Side has cover
            ICoverable coverHandler = tile.getCapability(ITechCapabilities.COVERABLE, side);
            if (coverHandler == null) return;
            if (coverHandler.getCover(side) != CoverType.NONE) {
                coverHandler.setCover(side, CoverType.NONE);
                SoundList.BREAK.play(tile.getWorld(), tile.getPos());
            }
        } else { //Used crowbar on side with no cover
            //TODO
        }
    }
}
