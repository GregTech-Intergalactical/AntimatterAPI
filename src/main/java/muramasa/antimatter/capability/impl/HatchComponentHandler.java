package muramasa.antimatter.capability.impl;

import muramasa.antimatter.tile.multi.TileEntityHatch;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;

import javax.annotation.Nonnull;

public class HatchComponentHandler extends ComponentHandler {

    public HatchComponentHandler(TileEntityHatch componentTile) {
        super(componentTile.getMachineType().getId(), componentTile);
    }

    @Override
    public void onStructureFormed(@Nonnull TileEntityMultiMachine controllerTile) {
        super.onStructureFormed(controllerTile);
        //((TileEntityHatch) getTile()).setTextureOverride((controllerTile.getMachineTypeId() * 1000) + controllerTile.getTierId());
        getTile().markForRenderUpdate();
    }

    @Override
    public void onStructureInvalidated(@Nonnull TileEntityMultiMachine controllerTile) {
        super.onStructureInvalidated(controllerTile);
        //((TileEntityHatch) getTile()).setTextureOverride(-1);
        getTile().markForRenderUpdate();
    }
}
