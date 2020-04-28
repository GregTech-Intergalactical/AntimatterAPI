package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.pipe.types.PipeType;

public class TileEntityFluidPipe extends TileEntityPipe {

    public TileEntityFluidPipe(PipeType<?> type) {
        super(type);
    }

    @Override
    public void onServerUpdate() {
        if (!isServerSide()) return;
        callPipeTick();
    }
}
