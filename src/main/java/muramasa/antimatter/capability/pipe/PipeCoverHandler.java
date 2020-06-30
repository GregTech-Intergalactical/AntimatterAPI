package muramasa.antimatter.capability.pipe;

import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.tile.pipe.TileEntityPipe;

public class PipeCoverHandler extends CoverHandler<TileEntityPipe> {

    public PipeCoverHandler(TileEntityPipe tile) {
        super(tile, tile.getValidCovers());
    }
}
