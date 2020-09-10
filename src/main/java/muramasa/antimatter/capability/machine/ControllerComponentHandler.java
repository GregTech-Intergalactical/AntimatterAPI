package muramasa.antimatter.capability.machine;

import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.ComponentHandler;
import muramasa.antimatter.capability.ICapabilityHandler;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.Capability;

public class ControllerComponentHandler extends ComponentHandler implements ICapabilityHandler {

    public ControllerComponentHandler(TileEntityMachine componentTile, CompoundNBT tag) {
        super(componentTile.getMachineType().getId(), componentTile);
    }

    @Override
    public Capability<?> getCapability() {
        return AntimatterCaps.COMPONENT_HANDLER_CAPABILITY;
    }
}
