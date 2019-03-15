package muramasa.gregtech.common.tileentities.base.multi;

import muramasa.gregtech.api.capability.impl.ComponentHandler;
import muramasa.gregtech.common.blocks.BlockCoil;

public class TileEntityCoil extends TileEntityComponent {

    public TileEntityCoil() {
        componentHandler = new ComponentHandler("null", this) {
            @Override
            public String getId() {
                return ((BlockCoil) getState().getBlock()).getType().getName();
            }
        };
    }

    public int getHeatingCapacity() {
        return ((BlockCoil) getState().getBlock()).getType().getHeatingCapacity();
    }
}
