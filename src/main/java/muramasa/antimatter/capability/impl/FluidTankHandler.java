package muramasa.antimatter.capability.impl;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import tesseract.api.fluid.IFluidNode;

import javax.annotation.Nonnull;

abstract public class FluidTankHandler implements IFluidTank, IFluidNode {

    protected int capacity, pressure_in, pressure_out;
    protected FluidStack fluid;

    public FluidTankHandler(int capacity, int pressure_in, int pressure_out) {
        this.capacity = capacity;
        this.pressure_in = pressure_in;
        this.pressure_out = pressure_out;
        this.fluid = FluidStack.EMPTY;
    }

    /** Tesseract IFluidNode Implementations **/
    @Override
    public int insert(@Nonnull Object stack, boolean simulate) {
        FluidStack resource = (FluidStack) stack;
        if (!canInput() || resource.isEmpty() || !isValid(stack)) return 0;

        if (simulate) {
            if (fluid.isEmpty()) return Math.min(capacity, resource.getAmount());
            if (!fluid.isFluidEqual(resource)) return 0;
            return Math.min(capacity - fluid.getAmount(), resource.getAmount());
        }

        if (fluid.isEmpty()) {
            fluid = new FluidStack(resource, Math.min(capacity, resource.getAmount()));
            onContentsChanged();
            return fluid.getAmount();
        }

        if (!fluid.isFluidEqual(resource)) return 0;
        int filled = capacity - fluid.getAmount();

        if (resource.getAmount() < filled) {
            fluid.grow(resource.getAmount());
            filled = resource.getAmount();
        } else {
            fluid.setAmount(capacity);
        }

        if (filled > 0) {
            onContentsChanged();
        }

        return filled;
    }

    @Nonnull
    @Override
    public Object extract(int maxDrain, boolean simulate) {
        if (!canOutput() || fluid.isEmpty()) return FluidStack.EMPTY;

        int drained = maxDrain;
        if (fluid.getAmount() < drained) drained = fluid.getAmount();
        FluidStack stack = new FluidStack(fluid, drained);
        if (drained > 0) {
            if (!simulate) fluid.shrink(drained);
            onContentsChanged();
        }

        return stack;
    }

    @Nonnull
    @Override
    public Object getFluidStack() {
        return fluid;
    }

    @Override
    public boolean isValid(@Nonnull Object stack) {
        return fluid.equals(stack);
    }

    @Override
    public int getAmount() {
        return fluid.getAmount();
    }

    @Override
    public int getVolume() {
        return capacity;
    }

    @Override
    public int getOutputPressure() {
        return pressure_out;
    }

    @Override
    public int getInputPressure() {
        return pressure_in;
    }

    @Override
    public boolean canOutput() {
        return pressure_out > 0L;
    }

    @Override
    public boolean canInput() {
        return pressure_in > 0L;
    }

    /** Forge IFluidTank Implementations **/
    @Nonnull
    @Override
    public FluidStack getFluid() {
        return (FluidStack) getFluidStack();
    }

    @Override
    public int getFluidAmount() {
        return getAmount();
    }

    @Override
    public int getCapacity() {
        return getVolume();
    }

    @Override
    public boolean isFluidValid(FluidStack stack) {
        return isValid(stack);
    }

    @Override
    public int fill(FluidStack resource, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction action) {
        return insert(resource, action.simulate());
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction action) {
        return (FluidStack) extract(maxDrain, action.simulate());
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, net.minecraftforge.fluids.capability.IFluidHandler.FluidAction action) {
        if (!resource.isFluidEqual(fluid)) return FluidStack.EMPTY;
        return drain(resource.getAmount(), action);
    }

    /** Forge IFluidTank Utils **/
    public void setFluid(FluidStack stack) {
        this.fluid = stack;
    }

    public int getSpace() {
        return Math.max(0, capacity - fluid.getAmount());
    }

    public boolean isEmpty() {
        return fluid.isEmpty();
    }

    public FluidTankHandler readFromNBT(CompoundNBT nbt) {
        FluidStack fluid = FluidStack.loadFluidStackFromNBT(nbt);
        setFluid(fluid);
        return this;
    }

    public CompoundNBT writeToNBT(CompoundNBT nbt) {
        fluid.writeToNBT(nbt);
        return nbt;
    }

    protected void onContentsChanged() { }
}
