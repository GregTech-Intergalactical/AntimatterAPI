package muramasa.gtu.api.capability.impl;

import muramasa.gtu.api.machines.types.Machine;
import muramasa.gtu.api.tileentities.multi.TileEntityMultiMachine;

public class ControllerComponentHandler extends ComponentHandler {

    public ControllerComponentHandler(Machine type, TileEntityMultiMachine componentTile) {
        super(type.getId(), componentTile);
    }
}
