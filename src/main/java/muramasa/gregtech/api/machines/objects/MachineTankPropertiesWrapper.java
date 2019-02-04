package muramasa.gregtech.api.machines.objects;

import muramasa.gregtech.api.capability.impl.MachineTankHandler;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

import javax.annotation.Nullable;

public class MachineTankPropertiesWrapper implements IFluidTankProperties {

    protected final MachineTankHandler tank;

    public MachineTankPropertiesWrapper(MachineTankHandler tank) {
        this.tank = tank;
    }

    @Nullable
    @Override
    public FluidStack getContents() {
        FluidStack contents = tank.getFluid();
        return contents == null ? null : contents.copy();
    }

    @Override
    public int getCapacity() {
        return tank.getCapacity();
    }

    @Override
    public boolean canFill() {
        return tank.canFill;
    }

    @Override
    public boolean canDrain() {
        return tank.canDrain;
    }

    @Override
    public boolean canFillFluidType(FluidStack fluidStack) {
        return tank.canFillFluidType(fluidStack);
    }

    @Override
    public boolean canDrainFluidType(FluidStack fluidStack) {
        return tank.canDrainFluidType(fluidStack);
    }
}