package muramasa.gtu.api.tileentities.multi;

import muramasa.gtu.Ref;
import muramasa.gtu.api.capability.GTCapabilities;
import muramasa.gtu.api.capability.IComponentHandler;
import muramasa.gtu.api.capability.ICoverHandler;
import muramasa.gtu.api.capability.impl.ComponentHandler;
import muramasa.gtu.api.capability.impl.CoverHandler;
import muramasa.gtu.api.data.Casing;
import muramasa.gtu.api.interfaces.IComponent;
import muramasa.gtu.api.texture.IBakedTile;
import muramasa.gtu.api.texture.TextureData;
import muramasa.gtu.api.tileentities.TileEntityBase;
import muramasa.gtu.api.blocks.BlockCasing;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;
import java.util.List;

public class TileEntityCasing extends TileEntityBase implements IComponent, IBakedTile {

    private Casing type;

    protected IComponentHandler componentHandler = new ComponentHandler("null", this) {
        @Override
        public String getId() {
            return getType().getName();
        }
    };
    protected ICoverHandler coverHandler = new CoverHandler(this);
    protected int textureOverride = -1;

    public Casing getType() {
        return type == null ? (type = ((BlockCasing) getState().getBlock()).getType()) : type;
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
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        textureOverride = tag.hasKey(Ref.KEY_MACHINE_TILE_TEXTURE) ? tag.getInteger(Ref.KEY_MACHINE_TILE_TEXTURE) : -1;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        tag = super.writeToNBT(tag);
        if (textureOverride != -1) tag.setInteger(Ref.KEY_MACHINE_TILE_TEXTURE, textureOverride);
        return tag;
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        info.add("Casing Type: " + getType().getName());
        return info;
    }
}
