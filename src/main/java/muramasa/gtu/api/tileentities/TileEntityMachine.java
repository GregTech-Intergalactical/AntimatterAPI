package muramasa.gtu.api.tileentities;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.capability.GTCapabilities;
import muramasa.gtu.api.capability.impl.*;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.gui.SlotType;
import muramasa.gtu.api.machines.ContentUpdateType;
import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.machines.MachineState;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.machines.types.Machine;
import muramasa.gtu.api.texture.IBakedTile;
import muramasa.gtu.api.texture.TextureData;
import muramasa.gtu.common.blocks.BlockMachine;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;
import java.util.List;

import static muramasa.gtu.api.machines.MachineFlag.*;

public class TileEntityMachine extends TileEntityTickable implements IBakedTile {

    /** NBT Data **/
    protected NBTTagCompound itemData, fluidData;

    /** Capabilities **/
    protected MachineItemHandler itemHandler;
    protected MachineFluidHandler fluidHandler;
    protected MachineEnergyHandler energyHandler;
    protected MachineCoverHandler coverHandler;
    protected MachineConfigHandler configHandler;

    /** Machine Data **/
    private Machine type;
    private Tier tier;
    private MachineState machineState = MachineState.IDLE;
    private EnumFacing facing;

    public final void init(Tier tier, EnumFacing facing) {
        this.type = ((BlockMachine) getBlockType()).getType();
        this.tier = tier;
        this.facing = facing;
    }

    @Override
    public void onFirstTick() { //Using first tick as this fires on both client & server, unlike onLoad
        type = ((BlockMachine) getBlockType()).getType();
        if (getType().hasFlag(ITEM)) itemHandler = new MachineItemHandler(this, itemData);
        if (getType().hasFlag(FLUID)) fluidHandler = new MachineFluidHandler(this, fluidData);
        if (getType().hasFlag(ENERGY)) energyHandler = new MachineEnergyHandler(this);
        if (getType().hasFlag(COVERABLE)) coverHandler = new MachineCoverHandler(this);
        if (getType().hasFlag(CONFIGURABLE)) configHandler = new MachineConfigHandler(this);
        markDirty();
    }

    /** Events **/
    public void onContentsChanged(ContentUpdateType type, int slot, boolean air) {
        //NOOP
    }

    /** Getters **/
    public Machine getType() {
        return type != null ? type : Machines.INVALID;
    }

    public Tier getTier() {
        return tier != null ? tier : Tier.LV;
    }

    public int getTypeId() {
        return getType().getInternalId();
    }

    public int getTierId() {
        return getTier().getInternalId();
    }

    public EnumFacing getFacing() {
        return facing;
    }

    public MachineState getMachineState() {
        return machineState;
    }

    public int getCurProgress() {
        return 0;
    }

    public int getMaxProgress() {
        return 0;
    }

    @Nullable
    public MachineItemHandler getItemHandler() {
        return itemHandler;
    }

    @Nullable
    public MachineFluidHandler getFluidHandler() {
        return fluidHandler;
    }

    @Nullable
    public MachineEnergyHandler getEnergyHandler() {
        return energyHandler;
    }

    @Nullable //TODO was CoverHandler, validate does not break
    public MachineCoverHandler getCoverHandler() {
        return coverHandler;
    }

    @Nullable
    public MachineConfigHandler getConfigHandler() {
        return configHandler;
    }

    /** Setters **/
    public boolean setFacing(EnumFacing side) { //Rotate the front to face a given direction
        if (side.getAxis() == EnumFacing.Axis.Y) return false;
        if (facing == side) return false;
        facing = side;
        markForRenderUpdate();
        markDirty();
        return true;
    }

    public void toggleDisabled() {
        setMachineState(machineState == MachineState.DISABLED ? MachineState.IDLE : MachineState.DISABLED);
    }

    public void setMachineState(MachineState newState) {
        machineState = newState;
    }

    @Override
    public TextureData getTextureData() {
        return TextureData.get().base(getType().getBaseTexture(getTier())).overlay(getType().getOverlayTextures(getMachineState()));
    }

    @Override
    public void setTextureOverride(int textureOverride) {
        //NOOP
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing side) {
        if (getType().hasFlag(ITEM) && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (coverHandler == null) return false;
            return side == null || coverHandler.hasCover(side, GregTechAPI.CoverItem);
        } else if (getType().hasFlag(FLUID) && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            if (fluidHandler == null || (fluidHandler.getInputWrapper() == null && fluidHandler.getOutputWrapper() == null)) return false;
            return side == null || (coverHandler != null && coverHandler.hasCover(side, GregTechAPI.CoverFluid));
        } else if (getType().hasFlag(ENERGY) && capability == GTCapabilities.ENERGY) {
            if (coverHandler == null) return false;
            return side == null || coverHandler.hasCover(side, GregTechAPI.CoverEnergy);
        } else if (getType().hasFlag(COVERABLE) && capability == GTCapabilities.COVERABLE) {
            if (coverHandler == null) return false;
            return side == null || !coverHandler.get(side).isEmpty();
        } else if (getType().hasFlag(CONFIGURABLE) && capability == GTCapabilities.CONFIGURABLE) {
            return true;
        }
        return super.hasCapability(capability, side);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing side) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemHandler.getInputHandler());
        } else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluidHandler.getInputWrapper() != null ? fluidHandler.getInputWrapper() : fluidHandler.getOutputWrapper());
        } else if (capability == GTCapabilities.ENERGY) {
            return GTCapabilities.ENERGY.cast(energyHandler);
        } else if (capability == GTCapabilities.COVERABLE) {
            return GTCapabilities.COVERABLE.cast(coverHandler);
        } else if (capability == GTCapabilities.CONFIGURABLE) {
            return GTCapabilities.CONFIGURABLE.cast(configHandler);
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        if (tag.hasKey(Ref.KEY_MACHINE_TILE_TIER)) tier = Tier.get(tag.getString(Ref.KEY_MACHINE_TILE_TIER));
        if (tag.hasKey(Ref.KEY_MACHINE_TILE_FACING)) facing = EnumFacing.VALUES[tag.getInteger(Ref.KEY_MACHINE_TILE_FACING)];
        if (tag.hasKey(Ref.KEY_MACHINE_TILE_STATE)) machineState = MachineState.VALUES[tag.getInteger(Ref.KEY_MACHINE_TILE_STATE)];//TODO saving state needed? if recipe is saved, serverUpdate should handle it.
        if (tag.hasKey(Ref.KEY_MACHINE_TILE_ITEMS)) itemData = (NBTTagCompound) tag.getTag(Ref.KEY_MACHINE_TILE_ITEMS);
        if (tag.hasKey(Ref.KEY_MACHINE_TILE_FLUIDS)) fluidData = (NBTTagCompound) tag.getTag(Ref.KEY_MACHINE_TILE_FLUIDS);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag); //TODO get tile data tag
        tag.setString(Ref.KEY_MACHINE_TILE_TIER, getTier().getName());
        tag.setInteger(Ref.KEY_MACHINE_TILE_FACING, facing.getIndex());
        if (machineState != null) tag.setInteger(Ref.KEY_MACHINE_TILE_STATE, machineState.ordinal());
        if (itemHandler != null) tag.setTag(Ref.KEY_MACHINE_TILE_ITEMS, itemHandler.serialize());
        if (fluidHandler != null) tag.setTag(Ref.KEY_MACHINE_TILE_FLUIDS, fluidHandler.serialize());
        return tag;
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        info.add("Tile: " + getClass().getName());
        info.add("Machine: " + getType().getName() + " Tier: " + getTier().getName());
        String slots = "";
        if (getType().hasFlag(MachineFlag.ITEM)) {
            int inputs = getType().getGui().getSlots(SlotType.IT_IN, getTier()).size();
            int outputs = getType().getGui().getSlots(SlotType.IT_OUT, getTier()).size();
            if (inputs > 0) slots += (" IT_IN: " + inputs + ",");
            if (outputs > 0) slots += (" IT_OUT: " + outputs + ",");
        }
        if (getType().hasFlag(MachineFlag.FLUID)) {
            int inputs = getType().getGui().getSlots(SlotType.FL_IN, getTier()).size();
            int outputs = getType().getGui().getSlots(SlotType.FL_OUT, getTier()).size();
            if (inputs > 0) slots += (" FL_IN: " + inputs + ",");
            if (outputs > 0) slots += (" FL_OUT: " + outputs + ",");
        }
        if (slots.length() > 0) info.add("Slots:" + slots);
        return info;
    }
}
