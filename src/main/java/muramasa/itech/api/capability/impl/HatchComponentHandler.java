package muramasa.itech.api.capability.impl;

import muramasa.itech.common.tileentities.base.multi.TileEntityHatch;
import muramasa.itech.common.tileentities.base.multi.TileEntityMultiMachine;

public class HatchComponentHandler extends ComponentHandler {

    public HatchComponentHandler(String componentId, TileEntityHatch componentTile) {
        super(componentId, componentTile);
    }

    @Override
    public void linkController(TileEntityMultiMachine controllerTile) {
        super.linkController(controllerTile);
        ((TileEntityHatch) getTile()).setTextureId(controllerTile.getMachineType().getInternalId());
        getTile().markForRenderUpdate();
    }

    @Override
    public void unlinkController(TileEntityMultiMachine controllerTile) {
        super.unlinkController(controllerTile);
        ((TileEntityHatch) getTile()).setTextureId(((TileEntityHatch) getTile()).getTierId());
        getTile().markForRenderUpdate();
    }
}
