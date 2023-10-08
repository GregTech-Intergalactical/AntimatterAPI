package muramasa.antimatter.capability.fluid;

import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import muramasa.antimatter.capability.FluidHandler;
import muramasa.antimatter.blockentity.pipe.BlockEntityFluidPipe;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

public class PipeFluidHandlerSidedWrapper extends FluidHandlerSidedWrapper{
    BlockEntityFluidPipe<?> pipe;
    FluidHandler<?> fluidHandler;
    public PipeFluidHandlerSidedWrapper(FluidHandler<?> fluidHandler, BlockEntityFluidPipe<?> fluidPipe, Direction side) {
        super(fluidHandler, fluidPipe.coverHandler.orElse(null), side);
        pipe = fluidPipe;
        this.fluidHandler = fluidHandler;
    }

    @Override
    public long insertFluid(FluidHolder resource, boolean simulate) {
        if (side == null) return 0;
        if (coverHandler != null) {
            if (coverHandler.get(side).blocksInput(FluidContainer.class, side)) {
                return 0;
            }
            if(coverHandler.onTransfer(resource, side, true, simulate)) return 0;
        }

        if (!fluidHandler.canInput(resource, side) || !fluidHandler.canInput(side)) {
            return 0;
        }
        int tank = fluidHandler.getInputTanks().getFirstAvailableTank(resource, false);
        if (tank == -1) return 0;
        long insert = fluidHandler.getInputTanks().getTank(tank).insertFluid(resource, simulate);
        if (insert > 0 && !simulate){
            pipe.setLastSide(side, tank);
        }
        return insert;
    }

    @NotNull
    @Override
    public FluidHolder extractFluid(FluidHolder resource, boolean simulate) {
        if (side == null) return FluidHooks.emptyFluid();
        if (coverHandler != null && (coverHandler.get(side).blocksOutput(FluidContainer.class, side) || coverHandler.onTransfer(resource, side, false, simulate))) {
            return FluidHooks.emptyFluid();
        }
        return super.extractFluid(resource, simulate);
    }
}
