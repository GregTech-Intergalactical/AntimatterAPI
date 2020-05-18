package muramasa.antimatter.capability.impl;

import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

import javax.annotation.Nonnull;

public class MachineConfigHandler extends ConfigHandler {

    public MachineConfigHandler(TileEntityMachine tile) {
        super(tile);
    }

    @Override
    public boolean onInteract(@Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull Direction side, AntimatterToolType type) {
        if (type == null) return false;
        /*
        switch (type) {
            case WRENCH:
            case WRENCH_P:
                if (player.isCrouching()) {
                    return getTile().setFacing(side);
                } else {
                    if (getTile().coverHandler.isPresent()) return getTile().coverHandler.get().setOutputFacing(side);
                }
            case HAMMER:
//                getTile().setMachineState(MachineState.IDLE);
//                if (getTile() instanceof TileEntityRecipeMachine) {
//                    ((TileEntityRecipeMachine) getTile()).checkRecipe();
//                }
//                getTile().toggleDisabled();
//                player.sendMessage(new TextComponentString("Machine was " + (getTile().getMachineState() == MachineState.DISABLED ? "disabled" : "enabled")));
                return true;
            default: return false;
        }
         */
        return true;
    }
}
