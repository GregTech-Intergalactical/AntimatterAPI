package muramasa.itech.common.tileentities.base.multi;

import muramasa.itech.common.blocks.BlockCoils;

public class TileEntityCoil extends TileEntityComponent {

    @Override
    public String getId() {
        return getState().getValue(BlockCoils.COIL_TYPE).getName();
    }
}
