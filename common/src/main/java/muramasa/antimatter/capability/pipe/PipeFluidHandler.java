package muramasa.antimatter.capability.pipe;

import muramasa.antimatter.capability.FluidHandler;
import muramasa.antimatter.tile.pipe.TileEntityFluidPipe;

public class PipeFluidHandler extends FluidHandler<TileEntityFluidPipe> {
    public PipeFluidHandler(TileEntityFluidPipe tile, int capacity, int pressure, int inputCount, int outputCount) {
        super(tile, capacity, pressure, inputCount, outputCount);
    }
}
