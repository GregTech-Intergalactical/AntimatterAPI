package muramasa.gregtech.common.tileentities.base.multi;

import muramasa.gregtech.api.capability.GTCapabilities;
import muramasa.gregtech.api.capability.impl.ComponentHandler;
import muramasa.gregtech.api.enums.Casing;
import muramasa.gregtech.common.blocks.BlockCasing;
import muramasa.gregtech.common.tileentities.base.TileEntityBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class TileEntityCasing extends TileEntityBase {

    private ComponentHandler componentHandler = new ComponentHandler("null", this) {
        @Override
        public String getId() {
            return getType().getName();
        }
    };

    public Casing getType() {
        return ((BlockCasing) getState().getBlock()).getType();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side) {
        if (capability == GTCapabilities.COMPONENT) {
            return true;
        }
        return super.hasCapability(capability, side);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side) {
        if (capability == GTCapabilities.COMPONENT) {
            return GTCapabilities.COMPONENT.cast(componentHandler);
        }
        return super.getCapability(capability, side);
    }
}
