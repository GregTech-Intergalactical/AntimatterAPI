package muramasa.antimatter.capability.machine;

import muramasa.antimatter.capability.ComponentHandler;
import muramasa.antimatter.tile.TileEntityMachine;

public class ControllerComponentHandler extends ComponentHandler {

    public ControllerComponentHandler(TileEntityMachine componentTile) {
        super(componentTile.getMachineType().getId(), componentTile);
    }

}
