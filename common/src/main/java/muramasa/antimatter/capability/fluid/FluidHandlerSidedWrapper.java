package muramasa.antimatter.capability.fluid;

import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.capability.FluidHandler;
import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import tesseract.TesseractGraphWrappers;
import tesseract.api.fluid.IFluidNode;

import javax.annotation.Nonnull;

public class FluidHandlerSidedWrapper implements IFluidNode {
    protected FluidHandler<?> fluidHandler;
    protected Direction side;
    CoverHandler<?> coverHandler;

    public FluidHandlerSidedWrapper(FluidHandler<?> fluidHandler, CoverHandler<?> coverHandler, Direction side) {
        this.fluidHandler = fluidHandler;
        this.coverHandler = coverHandler;
        this.side = side;
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
    public long fillDroplets(FluidStack resource, FluidAction action) {
        if (coverHandler != null && coverHandler.get(side).blocksInput(IFluidHandler.class, side)) {
            return 0;
        }
        if (!fluidHandler.canInput(resource, side) || !fluidHandler.canInput(side)) {
            return 0;
        }
        return fluidHandler.fillDroplets(resource, action);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action){
        return (int) (fillDroplets(resource, action) / TesseractGraphWrappers.dropletMultiplier);
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (coverHandler != null && coverHandler.get(side).blocksOutput(IFluidHandler.class, side)) {
            return FluidStack.EMPTY;
        }
        if (!fluidHandler.canOutput(side)) return FluidStack.EMPTY;
        return fluidHandler.drain(resource, action);
    }

    @Nonnull
    @Override
    public FluidStack drain(long maxDrain, FluidAction action) {
        if (coverHandler != null && coverHandler.get(side).blocksOutput(IFluidHandler.class, side)) {
            return FluidStack.EMPTY;
        }
        if (!fluidHandler.canOutput(side)) return FluidStack.EMPTY;
        return fluidHandler.drain(maxDrain, action);
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return drain((long) maxDrain * TesseractGraphWrappers.dropletMultiplier, action);
    }

    @Override
    public int getPriority(Direction direction) {
        return fluidHandler.getPriority(direction);
    }

    @Override
    public boolean canOutput() {
        return fluidHandler.canOutput();
    }

    @Override
    public boolean canInput() {
        return fluidHandler.canInput();
    }

    @Override
    public boolean canInput(Direction direction) {
        return fluidHandler.canInput(direction);
    }

    @Override
    public boolean canOutput(Direction direction) {
        return fluidHandler.canOutput(direction);
    }

    @Override
    public boolean canInput(FluidStack fluid, Direction direction) {
        return fluidHandler.canInput(fluid, direction);
    }
}
