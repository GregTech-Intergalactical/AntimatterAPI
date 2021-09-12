package muramasa.antimatter.capability.fluid;

import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.FluidHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nonnull;

public class FluidHandlerSidedWrapper<T extends TileEntity> implements IFluidHandler {
    protected FluidHandler<?> fluidHandler;
    protected T tile;
    protected Direction side;
    public FluidHandlerSidedWrapper(FluidHandler<?> fluidHandler, T tile, Direction side){
        this.fluidHandler = fluidHandler;
        this.tile = tile;
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
        if (tile.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY, side).map(c -> c.get(side).getCover().blocksInput(c.get(side), CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)).orElse(false)){
            return 0;
        }
        return fluidHandler.fill(resource, action);
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        if (tile.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY, side).map(c -> c.get(side).getCover().blocksOutput(c.get(side), CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)).orElse(false)){
            return FluidStack.EMPTY;
        }
        return fluidHandler.drain(resource, action);
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        if (tile.getCapability(AntimatterCaps.COVERABLE_HANDLER_CAPABILITY, side).map(c -> c.get(side).getCover().blocksOutput(c.get(side), CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)).orElse(false)){
            return FluidStack.EMPTY;
        }
        return fluidHandler.drain(maxDrain, action);
    }
}
