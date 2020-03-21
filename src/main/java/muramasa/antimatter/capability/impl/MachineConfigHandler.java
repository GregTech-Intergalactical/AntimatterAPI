package muramasa.antimatter.capability.impl;

import muramasa.antimatter.capability.IConfigHandler;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

public class MachineConfigHandler implements IConfigHandler {

    private TileEntityMachine tile;

    public MachineConfigHandler(TileEntityMachine tile) {
        this.tile = tile;
    }

    @Override
    public boolean onInteract(PlayerEntity player, Hand hand, Direction side, AntimatterToolType type) {
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

    @Override
    public TileEntityMachine getTile() {
        if (tile == null) throw new NullPointerException("ConfigHandler cannot have a null tile");
        return tile;
    }
}
