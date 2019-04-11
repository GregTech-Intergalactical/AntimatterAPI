package muramasa.gtu.api.tileentities;

import muramasa.gtu.Ref;
import muramasa.gtu.api.capability.impl.CoverHandler;
import muramasa.gtu.api.capability.impl.MachineFluidHandler;
import muramasa.gtu.api.capability.impl.MachineItemHandler;
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

import java.util.List;

public class TileEntityMachine extends TileEntityTickable implements IBakedTile {

    private Machine type;
    private Tier tier;
    private MachineState machineState = MachineState.IDLE;

    protected int facing, tint = -1;

    /** Data from NBT **/
    protected NBTTagCompound itemData, fluidData;

    public final void init(Tier tier, int facing) {
        this.type = ((BlockMachine) getBlockType()).getType();
        this.tier = tier;
        this.facing = facing;
    }

    @Override
    public void onFirstTick() { //Using first tick as this fires on both client & server, unlike onLoad
        type = ((BlockMachine) getBlockType()).getType();
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

    public int getFacing() {
        return facing;
    }

    public EnumFacing getEnumFacing() {
        return EnumFacing.VALUES[facing];
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

    public float getClientProgress() {
        return 0;
    }

    public MachineItemHandler getItemHandler() {
        return null;
    }

    public MachineFluidHandler getFluidHandler() {
        return null;
    }

    public CoverHandler getCoverHandler() {
        return null;
    }

    /** Setters **/
    public boolean setFacing(EnumFacing side) { //Rotate the front to face a given direction
        if (side.getAxis() == EnumFacing.Axis.Y) return false;
        if (facing == side.getIndex()) return false;
        facing = side.getIndex();
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

    public void setClientProgress(float newProgress) {
        //NOOP
    }

    @Override
    public TextureData getTextureData() {
        return TextureData.get().base(getType().getBaseTexture(getTier())).overlay(getType().getOverlayTextures(getMachineState()));
    }

    @Override
    public void setTextureOverride(int textureOverride) {
        //NOOP
    }

    //TODO move to TextureData
    public int getTint() {
        return tint;
    }

    public void setTint(int newTint) {
        tint = newTint;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey(Ref.KEY_MACHINE_TILE_TIER)) {
            tier = Tier.get(compound.getString(Ref.KEY_MACHINE_TILE_TIER));
        }
        if (compound.hasKey(Ref.KEY_MACHINE_TILE_FACING)) {
            facing = compound.getInteger(Ref.KEY_MACHINE_TILE_FACING);
        }
        if (compound.hasKey(Ref.KEY_MACHINE_TILE_ITEMS)) {
            itemData = (NBTTagCompound) compound.getTag(Ref.KEY_MACHINE_TILE_ITEMS);
        }
        if (compound.hasKey(Ref.KEY_MACHINE_TILE_FLUIDS)) {
            fluidData = (NBTTagCompound) compound.getTag(Ref.KEY_MACHINE_TILE_FLUIDS);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound); //TODO get tile data tag
        compound.setString(Ref.KEY_MACHINE_TILE_TIER, getTier().getName());
        compound.setInteger(Ref.KEY_MACHINE_TILE_FACING, facing);
        if (getItemHandler() != null) {
            compound.setTag(Ref.KEY_MACHINE_TILE_ITEMS, getItemHandler().serialize());
        }
        if (getFluidHandler() != null) {
            compound.setTag(Ref.KEY_MACHINE_TILE_FLUIDS, getFluidHandler().serialize());
        }
        return compound;
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        info.add("Tile Type: " + getClass().getName());
        info.add("Machine Type: " + getType().getName());
        info.add("Machine Tier: " + getTier().getName());
        if (getType().hasFlag(MachineFlag.ITEM)) {
            int inputs = getType().getGui().getSlots(SlotType.IT_IN, getTier()).size();
            int outputs = getType().getGui().getSlots(SlotType.IT_OUT, getTier()).size();
            if (inputs > 0) info.add("Input Slots: " + inputs);
            if (outputs > 0) info.add("Output Slots: " + outputs);
        }
        if (getType().hasFlag(MachineFlag.FLUID)) {
            int inputs = getType().getGui().getSlots(SlotType.FL_IN, getTier()).size();
            int outputs = getType().getGui().getSlots(SlotType.FL_OUT, getTier()).size();
            if (inputs > 0) info.add("Input Tanks: " + inputs);
            if (outputs > 0) info.add("Output Tanks: " + outputs);
        }
        return info;
    }
}
