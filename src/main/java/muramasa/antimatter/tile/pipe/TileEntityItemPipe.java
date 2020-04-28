package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.pipe.types.PipeType;

public class TileEntityItemPipe extends TileEntityPipe {

    public TileEntityItemPipe(PipeType<?> type) {
        super(type);
    }

    @Override
    public void onServerUpdate() {
        if (!isServerSide()) return;
        callTick();
    }
}
