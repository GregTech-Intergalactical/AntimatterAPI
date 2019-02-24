package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityHatch;
import muramasa.gregtech.common.tileentities.base.multi.TileEntityMultiMachine;

public class HatchComponentHandler extends ComponentHandler {

    public HatchComponentHandler(Machine type, TileEntityHatch componentTile) {
        super(type.getName(), componentTile);
    }

    @Override
    public void linkController(TileEntityMultiMachine controllerTile) {
        super.linkController(controllerTile);
        ((TileEntityHatch) getTile()).setTexture(controllerTile.getType().getBaseTexture(Tier.MULTI));
        getTile().markForRenderUpdate();
    }

    @Override
    public void unlinkController(TileEntityMultiMachine controllerTile) {
        super.unlinkController(controllerTile);
        ((TileEntityHatch) getTile()).setTexture(((TileEntityHatch) getTile()).getType().getBaseTexture(((TileEntityHatch) getTile()).getTier()));
        getTile().markForRenderUpdate();
    }
}
