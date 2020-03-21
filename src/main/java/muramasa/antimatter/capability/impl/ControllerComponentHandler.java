package muramasa.antimatter.capability.impl;

import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;

public class ControllerComponentHandler extends ComponentHandler {

    public ControllerComponentHandler(Machine type, TileEntityMultiMachine componentTile) {
        super(type.getId(), componentTile);
    }
}
