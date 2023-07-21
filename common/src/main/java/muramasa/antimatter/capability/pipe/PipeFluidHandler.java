package muramasa.antimatter.capability.pipe;

import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import muramasa.antimatter.capability.FluidHandler;
import muramasa.antimatter.tile.pipe.TileEntityFluidPipe;

public class PipeFluidHandler extends FluidHandler<TileEntityFluidPipe> {
    public PipeFluidHandler(TileEntityFluidPipe tile, int capacity, int pressure, int inputCount, int outputCount) {
        super(tile, capacity, pressure, inputCount, outputCount);
    }

    @Override
    public FluidContainer copy() {
        return new PipeFluidHandler(tile, capacity, pressure, tanks.containsKey(FluidDirection.INPUT) ? tanks.get(FluidDirection.INPUT).getSize() : 0, tanks.containsKey(FluidDirection.OUTPUT) ? tanks.get(FluidDirection.OUTPUT).getSize() : 0);
    }
}
