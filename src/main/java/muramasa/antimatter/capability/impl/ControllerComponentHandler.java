package muramasa.antimatter.capability.impl;

import muramasa.antimatter.machines.types.Machine;
import muramasa.antimatter.tileentities.multi.TileEntityMultiMachine;

public class ControllerComponentHandler extends ComponentHandler {

    public ControllerComponentHandler(Machine type, TileEntityMultiMachine componentTile) {
        super(type.getId(), componentTile);
    }
}
