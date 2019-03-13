package muramasa.gregtech.common.tileentities.base;

import muramasa.gregtech.api.GregTechAPI;
import muramasa.gregtech.api.capability.GTCapabilities;
import muramasa.gregtech.api.capability.impl.CableConfigHandler;
import muramasa.gregtech.api.capability.impl.CoverHandler;
import muramasa.gregtech.api.util.Utils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class TileEntityCable extends TileEntityTickable {

    public int cableConnections, machineConnections, disabledConnections;

    private TileEntity tileBeingChecked;

    /** Capabilities **/
    private CoverHandler coverHandler;
    private CableConfigHandler configHandler;

    public TileEntityCable() {
        coverHandler = new CoverHandler(this, GregTechAPI.CoverPlate);
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
                } else if (tileBeingChecked.hasCapability(GTCapabilities.ENERGY, EnumFacing.VALUES[side].getOpposite())) {
                    System.out.println(EnumFacing.VALUES[side].getOpposite());
                    cableConnections |= sideMask;
                    machineConnections |= sideMask;
                }
                /*else if (tileBeingChecked instanceof TileEntityMachine) {
                    if (((TileEntityMachine) tileBeingChecked).get(cachedFacing[side].getOpposite()) == CoverType.ENERGYPORT) {
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
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side) {
        if (capability == GTCapabilities.COVERABLE) {
            return true;
        } else if (capability == GTCapabilities.CONFIGURABLE) {
            return true;
        }
        return super.hasCapability(capability, side);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side) {
        if (capability == GTCapabilities.COVERABLE) {
            return GTCapabilities.COVERABLE.cast(coverHandler);
        } else if (capability == GTCapabilities.CONFIGURABLE) {
            return GTCapabilities.CONFIGURABLE.cast(configHandler);
        }
        return super.getCapability(capability, side);
    }
}
