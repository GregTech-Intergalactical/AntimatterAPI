package muramasa.antimatter.capability.machine;

import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.ComponentHandler;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.Capability;

public class ControllerComponentHandler extends ComponentHandler {

    public ControllerComponentHandler(TileEntityMachine componentTile) {
        super(componentTile.getMachineType().getId(), componentTile);
    }

}
