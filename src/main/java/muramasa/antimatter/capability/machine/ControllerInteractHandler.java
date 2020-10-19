package muramasa.antimatter.capability.machine;

import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.MaterialTool;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

import javax.annotation.Nullable;

import static muramasa.antimatter.Data.HAMMER;

public class ControllerInteractHandler extends MachineInteractHandler<TileEntityMachine> {

    public ControllerInteractHandler(TileEntityMachine tile) {
        super(tile);
    }

    @Override
    public boolean onInteract(PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type) {
        if (player.getHeldItem(hand).getItem() instanceof MaterialTool && ((MaterialTool) player.getHeldItem(hand).getItem()).getType() == HAMMER) {
            TileEntityMultiMachine machine = (TileEntityMultiMachine) getTile();
            if (!machine.isStructureValid()) {
                machine.checkStructure();
                return true;
            }
        }
        return super.onInteract(player, hand, side, type);
    }
}
