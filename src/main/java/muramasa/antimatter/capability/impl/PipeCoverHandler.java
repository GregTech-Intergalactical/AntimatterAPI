package muramasa.antimatter.capability.impl;

import muramasa.antimatter.tile.pipe.TileEntityPipe;

public class PipeCoverHandler extends CoverHandler<TileEntityPipe> {

    public PipeCoverHandler(TileEntityPipe tile) {
        super(tile, tile.getValidCovers());
    }
}
