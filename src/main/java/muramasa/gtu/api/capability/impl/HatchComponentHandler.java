package muramasa.gtu.api.capability.impl;

import muramasa.gtu.api.tileentities.multi.TileEntityHatch;
import muramasa.gtu.api.tileentities.multi.TileEntityMultiMachine;

public class HatchComponentHandler extends ComponentHandler {

    public HatchComponentHandler(TileEntityHatch componentTile) {
        super(componentTile.getMachineType().getId(), componentTile);
    }

    @Override
    public void onStructureFormed(TileEntityMultiMachine controllerTile) {
        super.onStructureFormed(controllerTile);
        ((TileEntityHatch) getTile()).setTextureOverride((controllerTile.getMachineTypeId() * 1000) + controllerTile.getTierId());
        getTile().markForRenderUpdate();
    }

    @Override
    public void onStructureInvalidated(TileEntityMultiMachine controllerTile) {
        super.onStructureInvalidated(controllerTile);
        ((TileEntityHatch) getTile()).setTextureOverride(-1);
        getTile().markForRenderUpdate();
    }
}
