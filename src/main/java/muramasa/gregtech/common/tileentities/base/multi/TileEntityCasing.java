package muramasa.gregtech.common.tileentities.base.multi;

import muramasa.gregtech.api.capability.GTCapabilities;
import muramasa.gregtech.api.capability.ICoverHandler;
import muramasa.gregtech.api.capability.impl.ComponentHandler;
import muramasa.gregtech.api.capability.impl.CoverHandler;
import muramasa.gregtech.api.enums.Casing;
import muramasa.gregtech.api.texture.IBakedTile;
import muramasa.gregtech.api.texture.TextureData;
import muramasa.gregtech.common.blocks.BlockCasing;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

public class TileEntityCasing extends TileEntityComponent implements IBakedTile {

    protected ICoverHandler coverHandler;

    public TileEntityCasing() {
        componentHandler = new ComponentHandler("null", this) {
            @Override
            public String getId() {
                return getType().getName();
            }
        };
        coverHandler = new CoverHandler(this);
    }

    public Casing getType() {
        return ((BlockCasing) getState().getBlock()).getType();
    }

    @Override
    public TextureData getTextureData() {
        return new TextureData(getType().getTexture());
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side) {
        return capability == GTCapabilities.COVERABLE || super.hasCapability(capability, side);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side) {
        if (capability == GTCapabilities.COVERABLE) return GTCapabilities.COVERABLE.cast(coverHandler);
        return super.getCapability(capability, side);
    }
}
