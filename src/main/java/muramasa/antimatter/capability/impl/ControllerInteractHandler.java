package muramasa.antimatter.capability.impl;

import muramasa.antimatter.Data;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

import javax.annotation.Nonnull;

public class ControllerInteractHandler extends MachineInteractHandler {

    public ControllerInteractHandler(TileEntityMultiMachine tile) {
        super(tile);
    }

    @Override
    public boolean onInteract(@Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull Direction side, AntimatterToolType type) {
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
