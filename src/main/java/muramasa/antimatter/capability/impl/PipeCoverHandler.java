package muramasa.antimatter.capability.impl;

import muramasa.antimatter.Data;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverInstance;
import muramasa.antimatter.tile.pipe.TileEntityPipe;

public class PipeCoverHandler extends CoverHandler {

    public PipeCoverHandler(TileEntityPipe tile) {
        super(tile, tile.getValidCovers());
        covers = new CoverInstance[]{
                Data.COVER_EMPTY, Data.COVER_EMPTY, Data.COVER_EMPTY, Data.COVER_OUTPUT, Data.COVER_EMPTY, Data.COVER_EMPTY
        };
    }
}
