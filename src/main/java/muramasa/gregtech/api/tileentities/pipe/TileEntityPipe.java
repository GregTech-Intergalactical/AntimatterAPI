package muramasa.gregtech.api.tileentities.pipe;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.capability.GTCapabilities;
import muramasa.gregtech.api.capability.impl.CoverHandler;
import muramasa.gregtech.api.capability.impl.PipeConfigHandler;
import muramasa.gregtech.api.pipe.PipeSize;
import muramasa.gregtech.api.pipe.types.Pipe;
import muramasa.gregtech.api.tileentities.TileEntityBase;
import muramasa.gregtech.api.util.Utils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public abstract class TileEntityPipe extends TileEntityBase {

    protected Pipe type;
    protected PipeSize size;
    protected CoverHandler coverHandler;
    protected PipeConfigHandler configHandler;

    public int cableConnections, machineConnections, disabledConnections;

    public TileEntityPipe() {
        configHandler = new PipeConfigHandler(this);
    }

    public final void init(Pipe type, PipeSize size) {
        this.type = type;
        this.size = size;
    }

    public Pipe getType() {
        return type;
    }

    public PipeSize getSize() {
        return size;
    }

    public void refreshConnections() {
        System.out.println("refresh");
        cableConnections = 0;
        int sideMask;
        TileEntity currentTile;
        for (int side = 0; side < 6; side++) {
            currentTile = Utils.getTile(world, pos.offset(EnumFacing.VALUES[side]));
            if (currentTile == null) continue;
            sideMask = 1 << side;
            if ((disabledConnections & sideMask) == 0) { //Connection side has not been disabled
                if (currentTile instanceof TileEntityPipe) {
                    cableConnections |=  sideMask;
                } else if (currentTile.hasCapability(GTCapabilities.ENERGY, EnumFacing.VALUES[side].getOpposite())) {
                    System.out.println(EnumFacing.VALUES[side].getOpposite());
                    cableConnections |= sideMask;
//                    machineConnections |= sideMask;
                }
                /*else if (tileBeingChecked instanceof TileEntityMachine) {
                    if (((TileEntityMachine) tileBeingChecked).get(cachedFacing[side].getOpposite()) == CoverType.ENERGYPORT) {
                        cableConnections |= sideMask;
                        machineConnections |= sideMask;
                    }
                }*/
            }
        }
        markForRenderUpdate();
    }

    public void toggleConnection(EnumFacing side) {
        int sideMask = 1 << side.getIndex();
        if ((disabledConnections & sideMask) != 0) { //Is Disabled, so remove mask
            disabledConnections &= ~sideMask;
            System.out.println("Enabled Connection for " + side);
        } else { //Is not disabled, so add mask
            disabledConnections |= sideMask;
            System.out.println("Disabled Connection for " + side);
        }
        refreshConnections();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side) {
        if (capability == GTCapabilities.COVERABLE || capability == GTCapabilities.CONFIGURABLE) return true;
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

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey(Ref.KEY_PIPE_SIZE)) size = PipeSize.VALUES[compound.getInteger(Ref.KEY_PIPE_SIZE)];
        if (compound.hasKey(Ref.KEY_CABLE_CONNECTIONS)) cableConnections = compound.getInteger(Ref.KEY_CABLE_CONNECTIONS);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (size != null) compound.setInteger(Ref.KEY_PIPE_SIZE, size.ordinal());
        /*if (cableConnections > 0)*/ compound.setInteger(Ref.KEY_CABLE_CONNECTIONS, cableConnections);
        return compound;
    }
}
