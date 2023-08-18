package muramasa.antimatter.capability.fluid;

import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.capability.FluidHandler;
import muramasa.antimatter.tile.pipe.TileEntityFluidPipe;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import tesseract.api.fluid.IFluidNode;

public class PipeFluidHandlerSidedWrapper extends FluidHandlerSidedWrapper{
    TileEntityFluidPipe<?> pipe;
    FluidHandler<?> fluidHandler;
    public PipeFluidHandlerSidedWrapper(FluidHandler<?> fluidHandler, TileEntityFluidPipe<?> fluidPipe, Direction side) {
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
            FluidHolder copy = resource.copyHolder();
            coverHandler.onTransfer(copy, side, side.getOpposite(), simulate);
            if (copy.isEmpty()) return 0;
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
        return super.extractFluid(resource, simulate);
    }
}
