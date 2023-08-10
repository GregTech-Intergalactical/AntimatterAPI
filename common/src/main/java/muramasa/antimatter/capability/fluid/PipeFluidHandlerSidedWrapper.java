package muramasa.antimatter.capability.fluid;

import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.tile.pipe.TileEntityFluidPipe;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import tesseract.api.fluid.IFluidNode;

public class PipeFluidHandlerSidedWrapper extends FluidHandlerSidedWrapper{
    TileEntityFluidPipe<?> pipe;
    public PipeFluidHandlerSidedWrapper(IFluidNode fluidHandler, TileEntityFluidPipe<?> fluidPipe, Direction side) {
        super(fluidHandler, fluidPipe.coverHandler.orElse(null), side);
        pipe = fluidPipe;
    }

    @Override
    public long insertFluid(FluidHolder resource, boolean simulate) {
        if (side == null) return 0;
        long insert = super.insertFluid(resource, simulate);
        if (insert > 0 && !simulate){
            pipe.setLastSide(side);
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
