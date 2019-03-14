package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.capability.IConfigHandler;
import muramasa.gregtech.api.enums.ToolType;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import net.minecraft.util.EnumFacing;

public class MachineConfigHandler implements IConfigHandler {

    private TileEntityMachine tile;

    public MachineConfigHandler(TileEntityMachine tile) {
        this.tile = tile;
    }

    @Override
    public boolean onInteract(EnumFacing side, ToolType type) {
        switch (type) {
            case WRENCH:
                getTile().setFacing(side);
                return true;
            case HAMMER:
                getTile().toggleDisabled();
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
