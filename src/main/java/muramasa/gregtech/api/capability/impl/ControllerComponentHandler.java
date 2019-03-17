package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityMultiMachine;

public class ControllerComponentHandler extends ComponentHandler {

    public ControllerComponentHandler(Machine type, TileEntityMultiMachine componentTile) {
        super(type.getName(), componentTile);
    }

    @Override
    public void linkController(TileEntityMultiMachine controllerTile) {
        //NOOP
    }

    @Override
    public void unlinkController(TileEntityMultiMachine controllerTile) {
        //NOOP
    }

    @Override
    public void onComponentRemoved() {
        if (componentTile instanceof TileEntityMultiMachine) {
            ((TileEntityMultiMachine) componentTile).onComponentRemoved();
        }
    }
}
