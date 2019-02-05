package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityHatch;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityMultiMachine;

public class HatchComponentHandler extends ComponentHandler {

    public HatchComponentHandler(String componentId, TileEntityHatch componentTile) {
        super(componentId, componentTile);
    }

    @Override
    public void linkController(TileEntityMultiMachine controllerTile) {
        super.linkController(controllerTile);
        ((TileEntityHatch) getTile()).setTexture(controllerTile.getMachineType().getBaseTexture(Tier.MULTI.getName()));
        getTile().markForRenderUpdate();
    }

    @Override
    public void unlinkController(TileEntityMultiMachine controllerTile) {
        super.unlinkController(controllerTile);
        ((TileEntityHatch) getTile()).setTexture(((TileEntityHatch) getTile()).getMachineType().getBaseTexture(((TileEntityHatch) getTile()).getTier()));
        getTile().markForRenderUpdate();
    }
}
