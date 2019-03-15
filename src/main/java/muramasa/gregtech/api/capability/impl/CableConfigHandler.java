package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.capability.IConfigHandler;
import muramasa.gregtech.api.enums.ToolType;
import muramasa.gregtech.common.tileentities.base.TileEntityCable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

public class CableConfigHandler implements IConfigHandler {

    private TileEntityCable tile;

    public CableConfigHandler(TileEntityCable tile) {
        this.tile = tile;
    }

    @Override
    public boolean onInteract(EntityPlayer player, EnumFacing side, ToolType type) {
        if (type == null) return false;
        switch (type) {
            case WRENCH:
                getTile().toggleConnection(side);
                return true;
            default: return false;
        }
    }

    @Override
    public TileEntityCable getTile() {
        if (tile == null) throw new NullPointerException("ConfigHandler cannot have a null tile");
        return tile;
    }
}
