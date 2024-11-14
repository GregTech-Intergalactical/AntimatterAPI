package muramasa.antimatter.capability.pipe;

import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import muramasa.antimatter.blockentity.pipe.BlockEntityFluidPipe;
import muramasa.antimatter.capability.FluidHandler;

public class PipeFluidHandler extends FluidHandler<BlockEntityFluidPipe> {
    public PipeFluidHandler(BlockEntityFluidPipe tile, int capacity, int inputCount, int outputCount) {
        super(tile, capacity, inputCount, outputCount);
    }

    @Override
    public FluidContainer copy() {
        return new PipeFluidHandler(tile, capacity, tanks.containsKey(FluidDirection.INPUT) ? tanks.get(FluidDirection.INPUT).getSize() : 0, tanks.containsKey(FluidDirection.OUTPUT) ? tanks.get(FluidDirection.OUTPUT).getSize() : 0);
    }
}
