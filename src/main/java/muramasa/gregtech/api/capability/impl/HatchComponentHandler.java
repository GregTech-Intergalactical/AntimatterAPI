package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.tileentities.multi.TileEntityHatch;
import muramasa.gregtech.api.tileentities.multi.TileEntityMultiMachine;

public class HatchComponentHandler extends ComponentHandler {

    public HatchComponentHandler(TileEntityHatch componentTile) {
        super(componentTile.getType().getName(), componentTile);
    }

    @Override
    public void linkController(TileEntityMultiMachine controllerTile) {
        super.linkController(controllerTile);
        ((TileEntityHatch) getTile()).setTextureOverride(controllerTile.getTypeId());
        getTile().markForRenderUpdate();
    }

    @Override
    public void unlinkController(TileEntityMultiMachine controllerTile) {
        super.unlinkController(controllerTile);
        ((TileEntityHatch) getTile()).setTextureOverride(-1);
        getTile().markForRenderUpdate();
    }
}
