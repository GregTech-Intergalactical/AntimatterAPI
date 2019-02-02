package muramasa.itech.api.capability.impl;

import muramasa.itech.api.machines.objects.MachineTankPropertiesWrapper;
import muramasa.itech.common.tileentities.base.TileEntityMachine;
import net.minecraftforge.fluids.FluidEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public class MachineTankHandler implements IFluidTank, IFluidHandler {

    @Nullable
    public FluidStack fluidStack;
    public int capacity;
    public boolean canFill = true, canDrain = true;

    protected TileEntityMachine tile;
    protected IFluidTankProperties[] tankProperties;

    public MachineTankHandler(TileEntityMachine tile, int capacity, FluidStack fluidStack, boolean canFill, boolean canDrain) {
        this.tile = tile;
        this.capacity = capacity;
        this.fluidStack = fluidStack;
        this.canFill = canFill;
        this.canDrain = canDrain;
    }

    /* IFluidTank */
    @Override
    @Nullable
    public FluidStack getFluid() {
        return fluidStack;
    }

    @Override
    public int getFluidAmount() {
        if (fluidStack == null) return 0;
        return fluidStack.amount;
    }

    @Override
    public int getCapacity() {
        return capacity;
    }

    @Override
    public FluidTankInfo getInfo() {
        return new FluidTankInfo(this);
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        if (this.tankProperties == null) {
            this.tankProperties = new IFluidTankProperties[] { new MachineTankPropertiesWrapper(this) };
        }
        return this.tankProperties;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        if (!canFillFluidType(resource)) return 0;
        return fillInternal(resource, doFill);
    }

    public int fillInternal(FluidStack resource, boolean doFill) {
        if (resource == null || resource.amount <= 0) {
            return 0;
        }

        if (!doFill) {
            if (fluidStack == null) {
                return Math.min(capacity, resource.amount);
            }

            if (!fluidStack.isFluidEqual(resource)) {
                return 0;
            }

            return Math.min(capacity - fluidStack.amount, resource.amount);
        }

        if (fluidStack == null) {
            fluidStack = new FluidStack(resource, Math.min(capacity, resource.amount));

            if (tile != null) {
                FluidEvent.fireEvent(new FluidEvent.FluidFillingEvent(fluidStack, tile.getWorld(), tile.getPos(), this, fluidStack.amount));
            }

            onContentsChanged();
            return fluidStack.amount;
        }

        if (!fluidStack.isFluidEqual(resource)) {
            return 0;
        }
        int filled = capacity - fluidStack.amount;

        if (resource.amount < filled) {
            fluidStack.amount += resource.amount;
            filled = resource.amount;
        } else {
            fluidStack.amount = capacity;
        }

        if (tile != null) {
            FluidEvent.fireEvent(new FluidEvent.FluidFillingEvent(fluidStack, tile.getWorld(), tile.getPos(), this, filled));
        }

        onContentsChanged();
        return filled;
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        if (!canDrainFluidType(getFluid())) return null;
        return drainInternal(resource, doDrain);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        if (!canDrainFluidType(fluidStack)) return null;
        return drainInternal(maxDrain, doDrain);
    }

    @Nullable
    public FluidStack drainInternal(FluidStack resource, boolean doDrain) {
        if (resource == null || !resource.isFluidEqual(getFluid())) return null;
        return drainInternal(resource.amount, doDrain);
    }

    @Nullable
    public FluidStack drainInternal(int maxDrain, boolean doDrain) {
        if (fluidStack == null || maxDrain <= 0) {
            return null;
        }

        int drained = maxDrain;
        if (fluidStack.amount < drained) {
            drained = fluidStack.amount;
        }

        FluidStack stack = new FluidStack(fluidStack, drained);
        if (doDrain) {
            fluidStack.amount -= drained;
            if (fluidStack.amount <= 0) {
                fluidStack = null;
            }

            if (tile != null) {
                FluidEvent.fireEvent(new FluidEvent.FluidDrainingEvent(fluidStack, tile.getWorld(), tile.getPos(), this, drained));
            }
            onContentsChanged();
        }
        return stack;
    }

    /**
     * Returns true if the tank can be filled with this type of fluidStack.
     * Used as a filter for fluidStack types.
     * Does not consider the current contents or capacity of the tank,
     * only whether it could ever fill with this type of fluidStack.
     *
     * @see IFluidTankProperties#canFillFluidType(FluidStack)
     */
    public boolean canFillFluidType(FluidStack fluid) {
        return canFill;
    }

    /**
     * Returns true if the tank can drain out this type of fluidStack.
     * Used as a filter for fluidStack types.
     * Does not consider the current contents or capacity of the tank,
     * only whether it could ever drain out this type of fluidStack.
     *
     * @see IFluidTankProperties#canDrainFluidType(FluidStack)
     */
    public boolean canDrainFluidType(@Nullable FluidStack fluid) {
        return fluid != null && canDrain;
    }

    protected void onContentsChanged() {
//        tile.markDirty();
    }
}