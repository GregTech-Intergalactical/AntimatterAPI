package muramasa.antimatter.capability.machine;

import muramasa.antimatter.capability.ComponentHandler;
import muramasa.antimatter.capability.ICapabilityHandler;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import net.minecraft.nbt.CompoundNBT;

public class ControllerComponentHandler extends ComponentHandler implements ICapabilityHandler {

    public ControllerComponentHandler(TileEntityMachine componentTile, CompoundNBT tag) {
        super(componentTile.getMachineType().getId(), componentTile);
    }
}
