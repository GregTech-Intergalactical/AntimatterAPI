package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.enums.ToolType;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import net.minecraft.util.EnumFacing;

public class MachineConfigHandler extends ConfigHandler {

    public MachineConfigHandler(TileEntityMachine tile) {
        super(tile);
    }

    @Override
    public boolean onInteract(EnumFacing side, ToolType type) {
         if (getTile() instanceof TileEntityMachine) {
            switch (type) {
                case WRENCH:
                    ((TileEntityMachine) getTile()).setFacing(side);
                    return true;
                case CROWBAR:
                    return super.onInteract(side, type);
                case SCREWDRIVER:
                    return super.onInteract(side, type);
            }
        }
        return false;
    }
}
