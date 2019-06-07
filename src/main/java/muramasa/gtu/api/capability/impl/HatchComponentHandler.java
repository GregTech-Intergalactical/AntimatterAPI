package muramasa.gtu.api.capability.impl;

import muramasa.gtu.api.tileentities.multi.TileEntityHatch;
import muramasa.gtu.api.tileentities.multi.TileEntityMultiMachine;

public class HatchComponentHandler extends ComponentHandler {

    public HatchComponentHandler(TileEntityHatch componentTile) {
        super(componentTile.getType().getId(), componentTile);
    }

    @Override
    public void linkController(TileEntityMultiMachine controllerTile) {
        super.linkController(controllerTile);
        ((TileEntityHatch) getTile()).setTextureOverride((controllerTile.getTypeId() * 1000) + controllerTile.getTierId());
        getTile().markForRenderUpdate();
    }

    @Override
    public void unlinkController(TileEntityMultiMachine controllerTile) {
        super.unlinkController(controllerTile);
        ((TileEntityHatch) getTile()).setTextureOverride(-1);
        getTile().markForRenderUpdate();
    }
}
