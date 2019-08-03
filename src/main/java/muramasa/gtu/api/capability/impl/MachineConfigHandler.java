package muramasa.gtu.api.capability.impl;

import muramasa.gtu.api.capability.IConfigHandler;
import muramasa.gtu.api.tools.ToolType;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;

public class MachineConfigHandler implements IConfigHandler {

    private TileEntityMachine tile;

    public MachineConfigHandler(TileEntityMachine tile) {
        this.tile = tile;
    }

    @Override
    public boolean onInteract(EntityPlayer player, EnumHand hand, EnumFacing side, ToolType type) {
        if (type == null) return false;
        switch (type) {
            case WRENCH:
            case WRENCH_P:
                return player.isSneaking() ? getTile().setFacing(side) : getTile().getCoverHandler().setOutputFacing(side);
            case HAMMER:
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
