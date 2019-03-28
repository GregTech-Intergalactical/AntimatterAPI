package muramasa.gregtech.api.tileentities.multi;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.capability.GTCapabilities;
import muramasa.gregtech.api.capability.IComponentHandler;
import muramasa.gregtech.api.capability.ICoverHandler;
import muramasa.gregtech.api.capability.impl.ComponentHandler;
import muramasa.gregtech.api.capability.impl.CoverHandler;
import muramasa.gregtech.api.data.Casing;
import muramasa.gregtech.api.interfaces.IComponent;
import muramasa.gregtech.api.texture.IBakedTile;
import muramasa.gregtech.api.texture.TextureData;
import muramasa.gregtech.api.tileentities.TileEntityBase;
import muramasa.gregtech.common.blocks.BlockCasing;
import net.minecraft.nbt.NBTTagCompound;
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
    protected int textureOverride = -1;

    public Casing getType() {
        return ((BlockCasing) getState().getBlock()).getType();
    }

    @Override
    public IComponentHandler getComponentHandler() {
        return componentHandler;
    }

    @Override
    public TextureData getTextureData() {
        return TextureData.get().base(getType().getTexture()).overlay(getType().getTexture());
    }

    @Override
    public void setTextureOverride(int textureOverride) {
        this.textureOverride = textureOverride;
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
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        textureOverride = compound.hasKey(Ref.KEY_MACHINE_TILE_TEXTURE) ? compound.getInteger(Ref.KEY_MACHINE_TILE_TEXTURE) : -1;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);
        if (textureOverride != -1) compound.setInteger(Ref.KEY_MACHINE_TILE_TEXTURE, textureOverride);
        return compound;
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        info.add("Casing Type: " + getType().getName());
        return info;
    }
}
