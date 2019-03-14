package muramasa.gregtech.common.tileentities.base.multi;

import muramasa.gregtech.api.capability.GTCapabilities;
import muramasa.gregtech.api.capability.impl.*;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.texture.TextureData;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

import static muramasa.gregtech.api.machines.MachineFlag.*;

public class TileEntityHatch extends TileEntityMachine {

    private MachineItemHandler itemHandler;
    private MachineFluidHandler fluidHandler;
    private MachineConfigHandler configHandler;
    private MachineCoverHandler coverHandler;
    private ComponentHandler componentHandler;

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        if (getType().hasFlag(ITEM)) itemHandler = new MachineItemHandler(this, itemData);
        if (getType().hasFlag(FLUID)) fluidHandler = new MachineFluidHandler(this, 8000 * getTierId(), fluidData);
        if (getType().hasFlag(CONFIGURABLE)) configHandler = new MachineConfigHandler(this);
        if (getType().hasFlag(COVERABLE)) coverHandler = new MachineCoverHandler(this);
        componentHandler = new HatchComponentHandler(this);
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
    public void onContentsChanged(int type, int slot) {
        if (type == 2) {
            //TODO handle cells
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
        if (!componentHandler.hasLinkedController()) return data;
        TileEntityMultiMachine tile = componentHandler.getFirstController();
        if (tile == null) return data;
        data.setBase(tile.getType().getBaseTextures(Tier.MULTI));
        return data;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return side == getEnumFacing();
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
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        return compound;
    }
}
