package muramasa.antimatter.capability.machine;

import muramasa.antimatter.capability.CapabilityType;
import muramasa.antimatter.capability.ComponentHandler;
import muramasa.antimatter.capability.ICapabilityHandler;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.nbt.CompoundNBT;

public class ControllerComponentHandler extends ComponentHandler implements ICapabilityHandler {

    public ControllerComponentHandler(TileEntityMachine componentTile, CompoundNBT tag) {
        super(componentTile.getMachineType().getId(), componentTile);
    }

    @Override
    public CapabilityType getCapabilityType() {
        return CapabilityType.COMPONENT;
    }
}
