package muramasa.gregtech.common.tileentities.base;

import muramasa.gregtech.api.capability.ITechCapabilities;
import muramasa.gregtech.api.capability.impl.CableConfigHandler;
import muramasa.gregtech.api.capability.impl.MachineCoverHandler;
import muramasa.gregtech.api.enums.CoverType;
import muramasa.gregtech.api.util.Utils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class TileEntityCable extends TileEntityTickable {

    public CoverType[] covers = new CoverType[6];
    public int cableConnections, machineConnections, disabledConnections;

    private TileEntity tileBeingChecked;

    /** Capabilities **/
    private MachineCoverHandler coverHandler;
    private CableConfigHandler configHandler;

    public TileEntityCable() {
        coverHandler = new MachineCoverHandler(this, CoverType.BLANK);
        configHandler = new CableConfigHandler(this);
    }

    @Override
    public void update() {
        super.update();
        cableConnections = 0;
        for (int side = 0; side < 6; side++) {
            tileBeingChecked = Utils.getTile(world, pos.offset(EnumFacing.VALUES[side]));
            if (tileBeingChecked == null) continue;
            int sideMask = 1 << side;
            if ((disabledConnections & sideMask) == 0) { //Connection side has not been disabled
                if (tileBeingChecked instanceof TileEntityCable) {
                    cableConnections |=  sideMask;
                } else if (tileBeingChecked.hasCapability(ITechCapabilities.ENERGY, EnumFacing.VALUES[side].getOpposite())) {
                    System.out.println(EnumFacing.VALUES[side].getOpposite());
                    cableConnections |= sideMask;
                    machineConnections |= sideMask;
                }
                /*else if (tileBeingChecked instanceof TileEntityMachine) {
                    if (((TileEntityMachine) tileBeingChecked).getCover(cachedFacing[side].getOpposite()) == CoverType.ENERGYPORT) {
                        cableConnections |= sideMask;
                        machineConnections |= sideMask;
                    }
                }*/
            }
        }
    }

    public void toggleConnection(EnumFacing side) {
        int sideMask = 1 << side.getIndex();
//        if ((cableConnections & sideMask) != 0) { //Has a connection to toggle
            if ((disabledConnections & sideMask) != 0) { //Is Disabled, so remove mask
                disabledConnections &= ~sideMask;
                System.out.println("Enabled Connection for " + side);
            } else { //Is not disabled, so add mask
                disabledConnections |= sideMask;
                System.out.println("Disabled Connection for " + side);
            }
            markForRenderUpdate();
//        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == ITechCapabilities.COVERABLE) {
            return true;
        } else if (capability == ITechCapabilities.CONFIGURABLE) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == ITechCapabilities.COVERABLE) {
            return ITechCapabilities.COVERABLE.cast(coverHandler);
        } else if (capability == ITechCapabilities.CONFIGURABLE) {
            return ITechCapabilities.CONFIGURABLE.cast(configHandler);
        }
        return super.getCapability(capability, facing);
    }

//    @Override
//    public boolean setCover(EnumFacing side, CoverType coverType) {
//        covers[side.getIndex()] = coverType;
//        markDirty();
//        return true;
//    }
//
//    @Override
//    public CoverType getCover(EnumFacing side) {
//        return covers[side.getIndex()];
//    }
//
//    @Override
//    public boolean isCoverValid(CoverType coverType) {
//        return false;
//    }

//    @Override
//    public boolean onWrench(EnumFacing side) {
//        toggleConnection(side);
//        return true;
//    }
//
//    @Override
//    public boolean onCrowbar(EnumFacing side) {
//        return false;
//    }
}
