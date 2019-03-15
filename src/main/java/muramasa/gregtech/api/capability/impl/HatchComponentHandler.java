package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.common.tileentities.base.multi.TileEntityHatch;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityMultiMachine;

public class HatchComponentHandler extends ComponentHandler {

    public HatchComponentHandler(TileEntityHatch componentTile) {
        super(componentTile.getType().getName(), componentTile);
    }

    @Override
    public void linkController(TileEntityMultiMachine controllerTile) {
        super.linkController(controllerTile);
        getTile().markForRenderUpdate();
    }

    @Override
    public void unlinkController(TileEntityMultiMachine controllerTile) {
        super.unlinkController(controllerTile);
        getTile().markForRenderUpdate();
    }
}
