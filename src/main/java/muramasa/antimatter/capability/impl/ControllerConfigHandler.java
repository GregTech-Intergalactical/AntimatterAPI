package muramasa.antimatter.capability.impl;

import muramasa.antimatter.Data;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

public class ControllerConfigHandler extends MachineConfigHandler {

    public ControllerConfigHandler(TileEntityMultiMachine tile) {
        super(tile);
    }

    @Override
    public boolean onInteract(PlayerEntity player, Hand hand, Direction side, AntimatterToolType type) {
        if (type == Data.HAMMER) {
            TileEntityMultiMachine machine = (TileEntityMultiMachine) getTile();
            if (!machine.isStructureValid()) {
                machine.checkStructure();
                return true;
            }
        }
        return super.onInteract(player, hand, side, type);
    }
}
