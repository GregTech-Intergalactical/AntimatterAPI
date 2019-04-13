package muramasa.gtu.api.tileentities.multi;

import muramasa.gtu.Ref;
import muramasa.gtu.api.capability.GTCapabilities;
import muramasa.gtu.api.capability.impl.*;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.interfaces.IComponent;
import muramasa.gtu.api.machines.ContentUpdateType;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.texture.TextureData;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

import static muramasa.gtu.api.machines.MachineFlag.*;

public class TileEntityHatch extends TileEntityMachine implements IComponent {

    protected MachineItemHandler itemHandler;
    protected MachineFluidHandler fluidHandler;
    protected MachineConfigHandler configHandler;
    protected MachineCoverHandler coverHandler;
    protected HatchComponentHandler componentHandler;
    protected int textureOverride = -1;

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        if (getType().hasFlag(ITEM)) itemHandler = new MachineItemHandler(this, itemData);
        if (getType().hasFlag(FLUID)) fluidHandler = new MachineFluidHandler(this, 8000 * getTierId(), fluidData);
        if (getType().hasFlag(CONFIGURABLE)) configHandler = new MachineConfigHandler(this);
        if (getType().hasFlag(COVERABLE)) coverHandler = new MachineCoverHandler(this);
        componentHandler = new HatchComponentHandler(this);
        markDirty();
    }

    @Override
    public MachineItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public MachineFluidHandler getFluidHandler() {
        return fluidHandler;
    }

    @Override
    public MachineCoverHandler getCoverHandler() {
        return coverHandler;
    }

    @Override
    public ComponentHandler getComponentHandler() {
        return componentHandler;
    }

    @Override
    public void onContentsChanged(ContentUpdateType type, int slot, boolean empty) {
        if (empty) return;
        if (componentHandler == null) return;
        TileEntityMultiMachine controller = componentHandler.getFirstController();
        if (controller == null) return;
        switch (type) {
            case ITEM_INPUT:
                controller.onContentsChanged(type, slot, empty);
                break;
            case ITEM_OUTPUT:
                controller.onContentsChanged(type, slot, empty);
            case ITEM_CELL:
                //TODO handle cells
                break;
            case FLUID_INPUT:
                //TODO
                break;
        }
    }

    @Override
    public boolean setFacing(EnumFacing side) {
        if (facing == side.getIndex()) return false;
        facing = side.getIndex();
        markForRenderUpdate();
        return true;
    }

    @Override
    public TextureData getTextureData() {
        TextureData data = super.getTextureData();
        if (textureOverride > -1) data.base(Machines.get(textureOverride).getBaseTexture(Tier.MULTI));
        return data;
    }

    @Override
    public void setTextureOverride(int textureOverride) {
        this.textureOverride = textureOverride;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && getType().hasFlag(ITEM)) {
            return side == null || side == getEnumFacing();
        } else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && getType().hasFlag(FLUID)) {
            return side == null || side == getEnumFacing();
        } else if (capability == GTCapabilities.COMPONENT) {
            return true;
        } else if (capability == GTCapabilities.CONFIGURABLE) {
            return true;
        } else if (capability == GTCapabilities.COVERABLE) {
            return true;
        }
        return super.hasCapability(capability, side);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && side == getEnumFacing()) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemHandler.getOutputHandler());
        } else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && fluidHandler != null) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluidHandler.getInputWrapper());
        } else if (capability == GTCapabilities.COMPONENT) {
            return GTCapabilities.COMPONENT.cast(componentHandler);
        } else if (capability == GTCapabilities.CONFIGURABLE) {
            return GTCapabilities.CONFIGURABLE.cast(configHandler);
        } else if (capability == GTCapabilities.COVERABLE) {
            return GTCapabilities.COVERABLE.cast(coverHandler);
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        textureOverride = compound.hasKey(Ref.KEY_MACHINE_TILE_TEXTURE) ? compound.getInteger(Ref.KEY_MACHINE_TILE_TEXTURE) : -1;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (textureOverride != -1) compound.setInteger(Ref.KEY_MACHINE_TILE_TEXTURE, textureOverride);
        return compound;
    }
}
