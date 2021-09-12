package muramasa.antimatter.capability.fluid;

import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.capability.FluidHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class FluidHandlerSidedWrapper implements IFluidHandler {
    protected FluidHandler<?> fluidHandler;
    protected Direction side;
    CoverHandler<?> coverHandler;
    public FluidHandlerSidedWrapper(FluidHandler<?> fluidHandler, CoverHandler<?> coverHandler, Direction side){
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
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return fluidHandler.isFluidValid(tank, stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (coverHandler != null && coverHandler.get(side).getCover().blocksInput(coverHandler.get(side), CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)){
            return 0;
        }
        if (!fluidHandler.canInput(resource, side) || !fluidHandler.canInput(side)){
            return 0;
        }
        return fluidHandler.fill(resource, action);
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (coverHandler != null && coverHandler.get(side).getCover().blocksOutput(coverHandler.get(side), CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)){
            return FluidStack.EMPTY;
        }
        if (!fluidHandler.canOutput(side)) return FluidStack.EMPTY;
        return fluidHandler.drain(resource, action);
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (coverHandler != null && coverHandler.get(side).getCover().blocksOutput(coverHandler.get(side), CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)){
            return FluidStack.EMPTY;
        }
        if (!fluidHandler.canOutput(side)) return FluidStack.EMPTY;
        return fluidHandler.drain(maxDrain, action);
    }
}
