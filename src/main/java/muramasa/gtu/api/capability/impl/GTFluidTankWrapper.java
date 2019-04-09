package muramasa.gtu.api.capability.impl;

import muramasa.gtu.api.machines.ContentUpdateType;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public class GTFluidTankWrapper implements IFluidHandler {

    protected GTFluidTank[] tanks;

    public GTFluidTankWrapper(TileEntityMachine machine, int count, int capacity, boolean input) {
        tanks = new GTFluidTank[count];
        for (int i = 0; i < count; i++) {
            tanks[i] = new GTFluidTank(capacity, true, input) {
                @Override
                protected void onContentsChanged() {
                    machine.onContentsChanged(input ? ContentUpdateType.FLUID_INPUT : ContentUpdateType.FLUID_OUTPUT, 0, fluid == null);
                }
            };
        }
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        IFluidTankProperties[] properties = new IFluidTankProperties[tanks.length];
        for (int i = 0; i < tanks.length; i++) {
            properties[i] = new FluidTankProperties(tanks[i].getFluid(), tanks[i].getCapacity(), tanks[i].canFill(), tanks[i].canDrain());
        }
        return properties;
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        GTFluidTank tank = findFluidInTanks(resource);
        if (tank != null) {
            return tank.fill(resource, doFill);
        } else {
            tank = getFirstEmptyTank();
            if (tank != null) return tank.fill(resource, doFill);
        }
        return 0;
    }

    @Nullable
    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        GTFluidTank tank = findFluidInTanks(resource);
        if (tank != null) return tank.drain(resource, doDrain);
        return null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        GTFluidTank tank = getFirstValidTank();
        if (tank != null) return tank.drain(maxDrain, doDrain);
        return null;
    }

    public void setFirstValidOrEmptyTank(FluidStack stack) {
        GTFluidTank tank = getFirstValidTank();
        if (tank == null) tank = getFirstEmptyTank();
        if (tank != null) {
            tank.setFluid(stack);
        }
    }

    public GTFluidTank getFirstEmptyTank() {
        for (int i = 0; i < tanks.length; i++) {
            if (tanks[i].getFluid() == null) return tanks[i];
        }
        return null;
    }

    @Nullable
    public GTFluidTank getFirstValidTank() {
        for (int i = 0; i < tanks.length; i++) {
            if (tanks[i].getFluid() != null) return tanks[i];
        }
        return null;
    }

    @Nullable
    public GTFluidTank findFluidInTanks(FluidStack stack) {
        for (int i = 0; i < tanks.length; i++) {
            if (tanks[i].getFluid() != null && tanks[i].getFluid().isFluidEqual(stack)) return tanks[i];
        }
        return null;
    }
}
