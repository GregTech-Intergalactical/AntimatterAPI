package muramasa.gtu.api.tileentities.multi;

import muramasa.gtu.Ref;
import muramasa.gtu.api.capability.GTCapabilities;
import muramasa.gtu.api.capability.impl.ComponentHandler;
import muramasa.gtu.api.capability.impl.HatchComponentHandler;
import muramasa.gtu.api.capability.impl.MachineFluidHandler;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.interfaces.IComponent;
import muramasa.gtu.api.machines.ContentUpdateType;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.texture.TextureData;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

import javax.annotation.Nullable;

import static muramasa.gtu.api.machines.MachineFlag.FLUID;

public class TileEntityHatch extends TileEntityMachine implements IComponent {

    protected HatchComponentHandler componentHandler;
    protected int textureOverride = -1;

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        if (getType().hasFlag(FLUID)) fluidHandler = new MachineFluidHandler(this, 8000 * getTierId(), fluidData);
        componentHandler = new HatchComponentHandler(this);
        markDirty();
    }

    @Override
    public ComponentHandler getComponentHandler() {
        return componentHandler;
    }

    @Override
    public void onContentsChanged(ContentUpdateType type, int slot) {
        if (componentHandler == null) return;
        TileEntityMultiMachine controller = componentHandler.getFirstController();
        if (controller == null) return;
        switch (type) {
            case ITEM_INPUT:
                controller.onContentsChanged(type, slot);
                break;
            case ITEM_OUTPUT:
                controller.onContentsChanged(type, slot);
            case ITEM_CELL:
                //TODO handle cells
                break;
            case FLUID_INPUT:
                //TODO
                break;
        }
    }

    @Override
    public TextureData getTextureData() {
        TextureData data = super.getTextureData();
        if (textureOverride > -1) data.base(Machines.get(textureOverride / 1000).getBaseTexture(Tier.get(textureOverride % 1000)));
        return data;
    }

    @Override
    public void setTextureOverride(int textureOverride) {
        this.textureOverride = textureOverride;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side) {
        return capability == GTCapabilities.COMPONENT || super.hasCapability(capability, side);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side) {
        return capability == GTCapabilities.COMPONENT ? GTCapabilities.COMPONENT.cast(componentHandler) : super.getCapability(capability, side);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        //TODO should texture be saved? it should be re-overridden when re-linked to a controller?
        super.readFromNBT(tag);
        textureOverride = tag.hasKey(Ref.KEY_MACHINE_TILE_TEXTURE) ? tag.getInteger(Ref.KEY_MACHINE_TILE_TEXTURE) : -1;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        if (textureOverride != -1) tag.setInteger(Ref.KEY_MACHINE_TILE_TEXTURE, textureOverride);
        return tag;
    }
}
