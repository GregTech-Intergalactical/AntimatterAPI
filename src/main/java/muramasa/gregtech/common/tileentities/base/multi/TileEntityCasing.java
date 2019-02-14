package muramasa.gregtech.common.tileentities.base.multi;

import muramasa.gregtech.api.capability.ITechCapabilities;
import muramasa.gregtech.api.capability.impl.ComponentHandler;
import muramasa.gregtech.common.blocks.BlockCasing;
import muramasa.gregtech.common.tileentities.base.TileEntityBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class TileEntityCasing extends TileEntityBase {

    private ComponentHandler componentHandler = new ComponentHandler("null", this) {
        @Override
        public String getId() {
            return ((BlockCasing) getState().getBlock()).getType().getName();
        }
    };

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
