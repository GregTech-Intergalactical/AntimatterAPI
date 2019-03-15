package muramasa.gregtech.common.tileentities.base.multi;

import muramasa.gregtech.api.capability.GTCapabilities;
import muramasa.gregtech.api.capability.IComponentHandler;
import muramasa.gregtech.api.capability.ICoverHandler;
import muramasa.gregtech.api.capability.impl.ComponentHandler;
import muramasa.gregtech.api.capability.impl.CoverHandler;
import muramasa.gregtech.api.enums.Casing;
import muramasa.gregtech.api.texture.IBakedTile;
import muramasa.gregtech.api.texture.TextureData;
import muramasa.gregtech.common.blocks.BlockCasing;
import muramasa.gregtech.common.tileentities.base.TileEntityBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.List;

public class TileEntityCasing extends TileEntityBase implements IComponent, IBakedTile {

    protected IComponentHandler componentHandler = new ComponentHandler("null", this) {
        @Override
        public String getId() {
            return getType().getName();
        }
    };
    protected ICoverHandler coverHandler = new CoverHandler(this);
    protected TextureData data;

    public Casing getType() {
        return ((BlockCasing) getState().getBlock()).getType();
    }

    @Override
    public IComponentHandler getComponentHandler() {
        return componentHandler;
    }

    @Override
    public TextureData getTextureData() {
        return data != null ? data : (data = TextureData.get().base(getType().getTexture()));
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side) {
        if (capability == GTCapabilities.COMPONENT || capability == GTCapabilities.COVERABLE) return true;
       return super.hasCapability(capability, side);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side) {
        if (capability == GTCapabilities.COMPONENT) return GTCapabilities.COMPONENT.cast(componentHandler);
        else if (capability == GTCapabilities.COVERABLE) return GTCapabilities.COVERABLE.cast(coverHandler);
        return super.getCapability(capability, side);
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        info.add("Casing Type: " + getType().getName());
        return info;
    }
}
