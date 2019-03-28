package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.capability.IConfigHandler;
import muramasa.gregtech.api.tileentities.pipe.TileEntityPipe;
import muramasa.gregtech.api.tools.ToolType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

public class PipeConfigHandler implements IConfigHandler {

    private TileEntityPipe tile;

    public PipeConfigHandler(TileEntityPipe tile) {
        this.tile = tile;
    }

    @Override
    public boolean onInteract(EntityPlayer player, EnumHand hand, EnumFacing side, ToolType type) {
        if (type == null) return false;
        switch (type) {
            case WRENCH:
                getTile().toggleConnection(side);
                return true;
            default: return false;
        }
    }

    @Override
    public TileEntityPipe getTile() {
        if (tile == null) throw new NullPointerException("ConfigHandler cannot have a null tile");
        return tile;
    }
}
