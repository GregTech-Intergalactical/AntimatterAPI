package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.enums.ToolType;
import muramasa.gregtech.common.tileentities.base.TileEntityBase;
import muramasa.gregtech.common.tileentities.base.TileEntityCable;
import net.minecraft.util.EnumFacing;

public class CableConfigHandler extends ConfigHandler {

    public CableConfigHandler(TileEntityBase tile) {
        super(tile);
    }

    @Override
    public boolean onInteract(EnumFacing side, ToolType type) {
        if (type == ToolType.WRENCH) {
            ((TileEntityCable) getTile()).toggleConnection(side);
            return true;
        }
        return false;
    }
}
