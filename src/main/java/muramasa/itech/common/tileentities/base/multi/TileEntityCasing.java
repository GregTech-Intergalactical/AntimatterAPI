package muramasa.itech.common.tileentities.base.multi;

import muramasa.itech.common.blocks.BlockCasings;

public class TileEntityCasing extends TileEntityComponent {

    @Override
    public String getId() {
        return getState().getValue(BlockCasings.CASING_TYPE).getName();
    }
}
