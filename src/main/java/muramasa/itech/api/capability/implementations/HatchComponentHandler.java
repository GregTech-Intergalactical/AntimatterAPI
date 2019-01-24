package muramasa.itech.api.capability.implementations;

import muramasa.itech.api.enums.HatchTexture;
import muramasa.itech.api.properties.ITechProperties;
import muramasa.itech.common.tileentities.base.TileEntityMachine;
import muramasa.itech.common.tileentities.base.multi.TileEntityMultiMachine;

public class HatchComponentHandler extends ComponentHandler {

    public HatchComponentHandler(String componentId, TileEntityMachine componentTile) {
        super(componentId, componentTile);
    }

    @Override
    public void linkController(TileEntityMultiMachine controllerTile) {
        super.linkController(controllerTile);
        getTile().setState(getTile().getState().withProperty(ITechProperties.TEXTURE, HatchTexture.get(controllerTile.getType())));
    }

    @Override
    public void unlinkController(TileEntityMultiMachine controllerTile) {
        super.unlinkController(controllerTile);
        getTile().setState(getTile().getState().withProperty(ITechProperties.TEXTURE, HatchTexture.get(getComponentId())));
    }
}
