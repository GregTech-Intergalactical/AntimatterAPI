package muramasa.antimatter.capability.impl;

import muramasa.antimatter.tile.pipe.TileEntityCable;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

import javax.annotation.Nonnull;

import static muramasa.antimatter.Data.WIRE_CUTTER;
import static muramasa.antimatter.Data.WRENCH;

public class PipeConfigHandler extends ConfigHandler {

    public PipeConfigHandler(TileEntityPipe tile) {
        super(tile);
    }

    @Override
    public boolean onInteract(@Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull Direction side, AntimatterToolType type) {
        if (type == getTool() && hand == Hand.MAIN_HAND) {
            // TODO: Block if covers are exist
            boolean isTarget = false; // used to init node
            TileEntity target = Utils.getTile(getTile().getWorld(), getTile().getPos().offset(side));
            if (target instanceof TileEntityPipe) {
                ((TileEntityPipe) target).toggleConnection(side.getOpposite(), false);
            } else isTarget = true;
            ((TileEntityPipe) getTile()).toggleConnection(side, isTarget);
            return true;
        } else {
            return false;
        }
    }

    private AntimatterToolType getTool() {
        return tile instanceof TileEntityCable ? WIRE_CUTTER : WRENCH;
    }
}
