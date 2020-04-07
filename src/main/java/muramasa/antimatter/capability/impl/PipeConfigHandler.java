package muramasa.antimatter.capability.impl;

import muramasa.antimatter.capability.IConfigHandler;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

import static muramasa.antimatter.Data.WRENCH;

public class PipeConfigHandler implements IConfigHandler {

    private TileEntityPipe tile;

    public PipeConfigHandler(TileEntityPipe tile) {
        this.tile = tile;
    }

    @Override
    public boolean onInteract(PlayerEntity player, Hand hand, Direction side, AntimatterToolType type) {
        if (type == WRENCH) {
            getTile().toggleConnection(side);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public TileEntityPipe getTile() {
        if (tile == null) throw new NullPointerException("ConfigHandler cannot have a null tile");
        return tile;
    }
}
