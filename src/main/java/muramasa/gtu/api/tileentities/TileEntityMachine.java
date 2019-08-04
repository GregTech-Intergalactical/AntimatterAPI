package muramasa.gtu.api.tileentities;

import muramasa.gtu.Ref;
import muramasa.gtu.api.blocks.BlockMachine;
import muramasa.gtu.api.capability.GTCapabilities;
import muramasa.gtu.api.capability.impl.*;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.gui.GuiEvent;
import muramasa.gtu.api.gui.SlotType;
import muramasa.gtu.api.machines.ContentEvent;
import muramasa.gtu.api.machines.MachineFlag;
import muramasa.gtu.api.machines.MachineState;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.machines.types.Machine;
import muramasa.gtu.api.properties.GTProperties;
import muramasa.gtu.api.texture.IBakedTile;
import muramasa.gtu.api.texture.TextureData;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
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
    private MachineState machineState;
    private EnumFacing facing;

    public TileEntityMachine() {
        machineState = getDefaultMachineState();
    }

    @Override
    public void onLoad() {
        type = ((BlockMachine) getBlockType()).getType();
        if (getType().hasFlag(ITEM)) itemHandler = new MachineItemHandler(this, itemData);
        if (getType().hasFlag(FLUID)) fluidHandler = new MachineFluidHandler(this, fluidData);
        if (getType().hasFlag(ENERGY)) energyHandler = new MachineEnergyHandler(this);
        if (getType().hasFlag(COVERABLE)) coverHandler = new MachineCoverHandler(this);
        if (getType().hasFlag(CONFIGURABLE)) configHandler = new MachineConfigHandler(this);
    }

    @Override
    public void onServerUpdate() {
        if (coverHandler != null) coverHandler.update();
    }

    /** Events **/
    public void onContentsChanged(ContentEvent type, int slot) {
        //NOOP
    }

    public void onGuiEvent(GuiEvent event) {
        //NOOP
    }

    /** Getters **/
    public Machine getType() {
        return type != null ? type : Machines.INVALID;
    }

    public Tier getTier() {
        return tier != null ? tier : (tier = Tier.get(getState().getValue(GTProperties.TIER)));
    }

    public int getTypeId() {
        return getType().getInternalId();
    }

    public int getTierId() {
        return getTier().getInternalId();
    }

    public boolean hasFlag(MachineFlag flag) {
        return getType().hasFlag(flag);
    }

    public EnumFacing getFacing() {
        return facing != null ? facing : EnumFacing.NORTH;
    }

    public EnumFacing getOutputFacing() {
        return coverHandler.getOutputFacing();
    }

    public MachineState getMachineState() {
        return machineState;
    }

    public MachineState getDefaultMachineState() {
        return MachineState.IDLE;
    }

    public int getCurProgress() {
        return 0;
    }

    public int getMaxProgress() {
        return 0;
    }

    //TODO convert getFooHandlers to Optionals
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

    @Nullable
    public MachineCoverHandler getCoverHandler() {
        return coverHandler;
    }

    @Nullable
    public MachineConfigHandler getConfigHandler() {
        return configHandler;
    }

    public long getMaxInputVoltage() {
        return energyHandler != null ? energyHandler.getMaxInsert() : 0;
    }

    /** Setters **/
    public boolean setFacing(EnumFacing side) { //Rotate the front to face a given direction
        if (facing == side) return false;
        facing = side;
        markForRenderUpdate();
        markDirty();
        return true;
    }

    //TODO
    public void toggleDisabled() {
        setMachineState(machineState == MachineState.DISABLED ? MachineState.IDLE : MachineState.DISABLED);
    }

    public void setMachineState(MachineState newState) {
        if (machineState.getOverlayId() != newState.getOverlayId() && newState.allowRenderUpdate()) {
            markForRenderUpdate();
            System.out.println("RENDER UPDATE");
        }
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
        //TODO if a side has a cover, disallow energy/items/fluid etc?
        if (getType().hasFlag(ITEM) && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (coverHandler == null) return false;
            //return side == null || coverHandler.hasCover(side, GregTechAPI.CoverItem);
            return true;
        } else if (getType().hasFlag(FLUID) && capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            if (fluidHandler == null || (fluidHandler.getInputWrapper() == null && fluidHandler.getOutputWrapper() == null)) return false;
            //return side == null || (coverHandler != null && coverHandler.hasCover(side, GregTechAPI.CoverFluid));
            return true;
        } else if (getType().hasFlag(ENERGY) && capability == GTCapabilities.ENERGY) {
            if (coverHandler == null) return false;
            //return side == null || coverHandler.hasCover(side, GregTechAPI.CoverEnergy);
            return true;
        } else if (getType().hasFlag(ENERGY) && capability == CapabilityEnergy.ENERGY) {
            if (coverHandler == null) return false;

            return true;
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
        } else if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(energyHandler);
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
        if (tag.hasKey(Ref.KEY_MACHINE_TILE_FACING)) facing = EnumFacing.VALUES[tag.getInteger(Ref.KEY_MACHINE_TILE_FACING)];
        if (tag.hasKey(Ref.KEY_MACHINE_TILE_STATE)) machineState = MachineState.VALUES[tag.getInteger(Ref.KEY_MACHINE_TILE_STATE)];//TODO saving state needed? if recipe is saved, serverUpdate should handle it.
        if (tag.hasKey(Ref.KEY_MACHINE_TILE_ITEMS)) itemData = (NBTTagCompound) tag.getTag(Ref.KEY_MACHINE_TILE_ITEMS);
        if (tag.hasKey(Ref.KEY_MACHINE_TILE_FLUIDS)) fluidData = (NBTTagCompound) tag.getTag(Ref.KEY_MACHINE_TILE_FLUIDS);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag); //TODO get tile data tag
        tag.setInteger(Ref.KEY_MACHINE_TILE_FACING, getFacing().getIndex());
        if (machineState != null) tag.setInteger(Ref.KEY_MACHINE_TILE_STATE, machineState.ordinal());
        if (itemHandler != null) tag.setTag(Ref.KEY_MACHINE_TILE_ITEMS, itemHandler.serialize());
        if (fluidHandler != null) tag.setTag(Ref.KEY_MACHINE_TILE_FLUIDS, fluidHandler.serialize());
        return tag;
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        info.add("Tile: " + getClass().getName());
        info.add("Machine: " + getType().getId() + " Tier: " + getTier().getId());
        String slots = "";
        if (getType().hasFlag(ITEM)) {
            int inputs = getType().getGui().getSlots(SlotType.IT_IN, getTier()).size();
            int outputs = getType().getGui().getSlots(SlotType.IT_OUT, getTier()).size();
            if (inputs > 0) slots += (" IT_IN: " + inputs + ",");
            if (outputs > 0) slots += (" IT_OUT: " + outputs + ",");
        }
        if (getType().hasFlag(FLUID)) {
            int inputs = getType().getGui().getSlots(SlotType.FL_IN, getTier()).size();
            int outputs = getType().getGui().getSlots(SlotType.FL_OUT, getTier()).size();
            if (inputs > 0) slots += (" FL_IN: " + inputs + ",");
            if (outputs > 0) slots += (" FL_OUT: " + outputs + ",");
        }
        if (slots.length() > 0) info.add("Slots:" + slots);
        if (getType().hasFlag(ENERGY)) {
            info.add("Energy: " + energyHandler.getEnergyStored() + " / " + energyHandler.getMaxEnergyStored());
        }
        if (getType().hasFlag(COVERABLE)) {
            StringBuilder builder = new StringBuilder("Covers: ");
            for (int i = 0; i < 6; i++) {
                builder.append(coverHandler.get(EnumFacing.VALUES[i]).getId()).append(" ");
            }
            info.add(builder.toString());
        }
        return info;
    }
}
