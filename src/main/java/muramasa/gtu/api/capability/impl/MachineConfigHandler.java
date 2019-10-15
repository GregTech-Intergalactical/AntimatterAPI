package muramasa.gtu.api.capability.impl;

import muramasa.gtu.api.capability.IConfigHandler;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.tools.GregTechToolType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;

public class MachineConfigHandler implements IConfigHandler {

    private TileEntityMachine tile;

    public MachineConfigHandler(TileEntityMachine tile) {
        this.tile = tile;
    }

    @Override
    public boolean onInteract(PlayerEntity player, Hand hand, Direction side, GregTechToolType type) {
        if (type == null) return false;
        switch (type) {
            case WRENCH:
            case WRENCH_P:
                if (player.isSneaking()) {
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
    }

    @Override
    public TileEntityMachine getTile() {
        if (tile == null) throw new NullPointerException("ConfigHandler cannot have a null tile");
        return tile;
    }
}
