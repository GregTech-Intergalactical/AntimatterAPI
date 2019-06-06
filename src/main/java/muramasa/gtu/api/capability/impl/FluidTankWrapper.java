package muramasa.gtu.api.capability.impl;

import muramasa.gtu.api.machines.ContentEvent;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.util.Utils;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public class FluidTankWrapper implements IFluidHandler {

    protected FluidTank[] tanks;
    public boolean dirty = false;

    public FluidTankWrapper(TileEntityMachine machine, int count, int capacity, boolean input) {
        tanks = new FluidTank[count];
        for (int i = 0; i < count; i++) {
            tanks[i] = new FluidTank(capacity) {
                @Override
                protected void onContentsChanged() {
                    dirty = true;
                    machine.onContentsChanged(input ? ContentEvent.FLUID_INPUT : ContentEvent.FLUID_OUTPUT, 0);
                }
            };
            tanks[i].setCanFill(true);
            tanks[i].setCanDrain(input);
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
        FluidTank tank = findFluidInTanks(resource);
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
        FluidTank tank = findFluidInTanks(resource);
        if (tank != null) return tank.drain(resource, doDrain);
        return null;
    }

    @Nullable
    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        FluidTank tank = getFirstValidTank();
        if (tank != null) return tank.drain(maxDrain, doDrain);
        return null;
    }

    public void setFirstValidOrEmptyTank(FluidStack fluid) {
        FluidTank tank = getFirstValidTank();
        if (tank == null) tank = getFirstEmptyTank();
        if (tank != null) {
            tank.setFluid(fluid);
        }
    }

    public FluidTank getFirstEmptyTank() {
        for (int i = 0; i < tanks.length; i++) {
            if (tanks[i].getFluid() == null) return tanks[i];
        }
        return null;
    }

    @Nullable
    public FluidTank getFirstValidTank() {
        for (int i = 0; i < tanks.length; i++) {
            if (tanks[i].getFluid() != null) return tanks[i];
        }
        return null;
    }

    @Nullable
    public FluidTank findFluidInTanks(FluidStack fluid) {
        for (int i = 0; i < tanks.length; i++) {
            if (tanks[i].getFluid() != null && Utils.equals(tanks[i].getFluid(), fluid)) return tanks[i];
        }
        return null;
    }
}
