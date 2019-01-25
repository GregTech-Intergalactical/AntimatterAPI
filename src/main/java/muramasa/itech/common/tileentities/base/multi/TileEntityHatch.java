package muramasa.itech.common.tileentities.base.multi;

import muramasa.itech.api.capability.ITechCapabilities;
import muramasa.itech.api.capability.implementations.ComponentHandler;
import muramasa.itech.api.capability.implementations.HatchComponentHandler;
import muramasa.itech.common.tileentities.base.TileEntityMachine;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

public class TileEntityHatch extends TileEntityMachine {

    private ItemStackHandler stackHandler;
    private ComponentHandler componentHandler;

    @Override
    public void init(String type, String tier) {
        super.init(type, tier);
        stackHandler = new ItemStackHandler(1);
        componentHandler = new HatchComponentHandler(type, this);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == ITechCapabilities.COMPONENT) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == ITechCapabilities.COMPONENT) {
            return ITechCapabilities.COMPONENT.cast(componentHandler);
        }
        return super.getCapability(capability, facing);
    }
}
