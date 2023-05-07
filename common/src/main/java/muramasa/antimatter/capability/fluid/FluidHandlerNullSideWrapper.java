package muramasa.antimatter.capability.fluid;

import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import tesseract.api.fluid.IFluidNode;

import javax.annotation.Nonnull;

public class FluidHandlerNullSideWrapper implements IFluidNode {
    IFluidHandler fluidHandler;

    public FluidHandlerNullSideWrapper(IFluidHandler fluidHandler) {
        this.fluidHandler = fluidHandler;
    }

    @Override
    public int getTanks() {
        return fluidHandler.getTanks();
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return fluidHandler.getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return fluidHandler.getTankCapacity(tank);
    }

    @Override
    public long getTankCapacityInDroplets(int tank) {
        return fluidHandler.getTankCapacityInDroplets(tank);
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return fluidHandler.isFluidValid(tank, stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return 0;
    }

    @Override
    public long fillDroplets(FluidStack resource, FluidAction action) {
        return 0;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(long maxDrain, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return FluidStack.EMPTY;
    }

    @Override
    public int getPriority(Direction direction) {
        return fluidHandler instanceof IFluidNode node ? node.getPriority(direction) : 0;
    }

    @Override
    public boolean canOutput() {
        return !(fluidHandler instanceof IFluidNode node) || node.canOutput();
    }

    @Override
    public boolean canInput() {
        return !(fluidHandler instanceof IFluidNode node) || node.canInput();
    }

    @Override
    public boolean canInput(Direction direction) {
        return !(fluidHandler instanceof IFluidNode node) || node.canInput(direction);
    }

    @Override
    public boolean canOutput(Direction direction) {
        return !(fluidHandler instanceof IFluidNode node) || node.canOutput(direction);
    }

    @Override
    public boolean canInput(FluidStack fluid, Direction direction) {
        return !(fluidHandler instanceof IFluidNode node) || node.canInput(fluid, direction);
    }
}
