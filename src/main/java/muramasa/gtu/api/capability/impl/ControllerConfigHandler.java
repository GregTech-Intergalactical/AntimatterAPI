package muramasa.gtu.api.capability.impl;

import muramasa.gtu.api.tools.GregTechToolType;
import muramasa.gtu.api.tileentities.multi.TileEntityMultiMachine;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

public class ControllerConfigHandler extends MachineConfigHandler {

    public ControllerConfigHandler(TileEntityMultiMachine tile) {
        super(tile);
    }

    @Override
    public boolean onInteract(PlayerEntity player, Hand hand, Direction side, GregTechToolType type) {
        if (type == GregTechToolType.HAMMER) {
            TileEntityMultiMachine machine = (TileEntityMultiMachine) getTile();
            if (!machine.isStructureValid()) {
                machine.checkStructure();
                return true;
            }
        }
        return super.onInteract(player, hand, side, type);
    }
}
