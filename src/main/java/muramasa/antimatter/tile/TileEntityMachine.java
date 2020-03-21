package muramasa.antimatter.tile;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.impl.*;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.gui.GuiEvent;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.machine.*;
import muramasa.antimatter.machine.types.Machine;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

import static muramasa.antimatter.machine.MachineFlag.*;

public class TileEntityMachine extends TileEntityTickable implements INamedContainerProvider {

    /** NBT Data **/
    protected CompoundNBT itemData, fluidData;

    /** Capabilities **/
    //TODO convert to Cap instance ->
    public Optional<MachineItemHandler> itemHandler = Optional.empty();
    public Optional<MachineFluidHandler> fluidHandler = Optional.empty();
    public Optional<MachineEnergyHandler> energyHandler = Optional.empty();
    public Optional<MachineCoverHandler> coverHandler = Optional.empty();
    public Optional<MachineConfigHandler> configHandler = Optional.empty();

    /** Machine Data **/
    //private Machine type;
    //private Tier tier;
    private MachineState machineState;
    private Direction facing;

    public TileEntityMachine() {
        machineState = getDefaultMachineState();
    }

    @Override
    public TileEntityType<?> getType() {
        //if (type == null) type = ((BlockMachine) getBlockState().getBlock()).getType();
        //if (tier == null) tier = ((BlockMachine) getBlockState().getBlock()).getTier();
        return ((BlockMachine) getBlockState().getBlock()).getType().getTileType();
    }

    @Override
    public void onLoad() {
        //type = ((BlockMachine) getBlockState()).getMachineType();
        if (getMachineType().hasFlag(ITEM) && getMachineType().getGui().hasAnyItem(getTier())) itemHandler = Optional.of(new MachineItemHandler(this, itemData));
        if (getMachineType().hasFlag(FLUID) && getMachineType().getGui().hasAnyFluid(getTier())) fluidHandler = Optional.of(new MachineFluidHandler(this, fluidData));
        if (getMachineType().hasFlag(ENERGY)) energyHandler = Optional.of(new MachineEnergyHandler(this));
        if (getMachineType().hasFlag(COVERABLE)) coverHandler = Optional.of(new MachineCoverHandler(this));
        if (getMachineType().hasFlag(CONFIGURABLE)) configHandler = Optional.of(new MachineConfigHandler(this));
    }

    @Override
    public void onServerUpdate() {
        coverHandler.ifPresent(CoverHandler::update);
    }

    /** Events **/
    public void onContentsChanged(ContentEvent type, int slot) {
        //NOOP
    }

    public void onGuiEvent(GuiEvent event) {
        //NOOP
    }

    public void onMachineEvent(MachineEvent event) {
        coverHandler.ifPresent(h -> h.onMachineEvent(event));
    }

    /** Getters **/
    public Machine getMachineType() {
        /*return type != null ? type : Machines.INVALID;*/
        return ((BlockMachine) getBlockState().getBlock()).getType();
    }

    //TODO getMachineTier
    public Tier getTier() {
        return ((BlockMachine) getBlockState().getBlock()).getTier();
    }

    public int getMachineTypeId() {
        return getMachineType().getInternalId();
    }

    public int getTierId() {
        return getTier().getInternalId();
    }

    public boolean hasFlag(MachineFlag flag) {
        return getMachineType().hasFlag(flag);
    }

    public Direction getFacing() {
        return facing != null ? facing : Direction.NORTH;
    }

    public Direction getOutputFacing() {
        return coverHandler.isPresent() ? coverHandler.get().getOutputFacing() : getFacing().getOpposite();
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

    public long getMaxInputVoltage() {
        return energyHandler.map(EnergyHandler::getMaxInsert).orElse(0L);
    }

    /** Setters **/
    public boolean setFacing(Direction side) { //Rotate the front to face a given direction
        if (side == side) return false;
        side = side;
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

    public Cover[] getValidCovers() {
        return AntimatterAPI.getRegisteredCovers().toArray(new Cover[0]);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && itemHandler.isPresent()) {
            return LazyOptional.of(() -> itemHandler).cast();
        }
        return super.getCapability(cap);
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        ModelDataMap.Builder builder = new ModelDataMap.Builder().withInitial(AntimatterProperties.MACHINE_TYPE, getMachineType()).withInitial(AntimatterProperties.MACHINE_FACING, facing);
        coverHandler.ifPresent(machineCoverHandler -> builder.withInitial(AntimatterProperties.MACHINE_COVER, machineCoverHandler.getAll()));
        return builder.build();
    }

    @Override
    public ITextComponent getDisplayName() {
        return getMachineType().getDisplayName(getTier());
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, PlayerInventory inv, PlayerEntity player) {
        //return type.hasFlag(GUI) ? type.getGui().getContainerHandler().getContainer(windowId, world, pos, player, playerInv) : null;
        //return getMachineType().hasFlag(GUI) ? getMachineType().getGui().getMenuHandler().getMenu(windowId, inv, this) : null;
        return getMachineType().hasFlag(GUI) ? getMachineType().getGui().getMenuHandler().getMenu(this, inv, windowId) : null;
        //return getMachineType().hasFlag(GUI) ? new ContainerMachine(windowId, this, inv) : null;
    }

    //    @Override
//    public boolean hasCapability(Capability<?> capability, @Nullable Direction side) {
//        //TODO if a side has a cover, disallow energy/items/fluid etc?
//        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && itemHandler.isPresent()) {
//            return true;
//        } else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && fluidHandler.isPresent()) {
//            return true;
//        } else if ((capability == GTCapabilities.ENERGY || capability == CapabilityEnergy.ENERGY) && energyHandler.isPresent()) {
//            return true;
//        } else if (capability == GTCapabilities.COVERABLE && coverHandler.isPresent()) {
//            return side == null || !coverHandler.get().get(side).isEmpty();
//        } else if (getMachineType().hasFlag(CONFIGURABLE) && capability == GTCapabilities.CONFIGURABLE) {
//            return true;
//        }
//        return super.hasCapability(capability, side);
//    }

//    @Nullable
//    @Override
//    public <T> T getCapability(Capability<T> capability, @Nullable Direction side) {
//        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && itemHandler.isPresent()) {
//            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(side == Direction.UP ? itemHandler.get().getInputHandler() : itemHandler.get().getOutputHandler());
//        } else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && fluidHandler.isPresent()) {
//            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(fluidHandler.get().getInputWrapper() != null ? fluidHandler.get().getInputWrapper() : fluidHandler.get().getOutputWrapper());
//        } else if (capability == GTCapabilities.ENERGY && energyHandler.isPresent()) {
//            return GTCapabilities.ENERGY.cast(energyHandler.get());
//        } else if (capability == CapabilityEnergy.ENERGY && energyHandler.isPresent()) {
//            return CapabilityEnergy.ENERGY.cast(energyHandler.get());
//        } else if (capability == GTCapabilities.COVERABLE && coverHandler.isPresent()) {
//            return GTCapabilities.COVERABLE.cast(coverHandler.get());
//        } else if (capability == GTCapabilities.CONFIGURABLE && configHandler.isPresent()) {
//            return GTCapabilities.CONFIGURABLE.cast(configHandler.get());
//        }
//        return super.getCapability(capability, side);
//    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        if (tag.contains(Ref.KEY_MACHINE_TILE_FACING)) facing = Ref.DIRECTIONS[tag.getInt(Ref.KEY_MACHINE_TILE_FACING)];
        if (tag.contains(Ref.KEY_MACHINE_TILE_STATE)) machineState = MachineState.VALUES[tag.getInt(Ref.KEY_MACHINE_TILE_STATE)];//TODO saving state needed? if recipe is saved, serverUpdate should handle it.
        if (tag.contains(Ref.KEY_MACHINE_TILE_ITEMS)) itemData = tag.getCompound(Ref.KEY_MACHINE_TILE_ITEMS);
        if (tag.contains(Ref.KEY_MACHINE_TILE_FLUIDS)) fluidData = tag.getCompound(Ref.KEY_MACHINE_TILE_FLUIDS);
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag); //TODO get tile data tag
        tag.putInt(Ref.KEY_MACHINE_TILE_FACING, getFacing().getIndex());
        if (machineState != null) tag.putInt(Ref.KEY_MACHINE_TILE_STATE, machineState.ordinal());
        itemHandler.ifPresent(h -> tag.put(Ref.KEY_MACHINE_TILE_ITEMS, h.serialize()));
        fluidHandler.ifPresent(h -> tag.put(Ref.KEY_MACHINE_TILE_FLUIDS, h.serialize()));
        return tag;
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        info.add("Tile: " + getClass().getName());
        info.add("Machine: " + getMachineType().getId() + " Tier: " + getTier().getId());
        String slots = "";
        if (getMachineType().hasFlag(ITEM)) {
            int inputs = getMachineType().getGui().getSlots(SlotType.IT_IN, getTier()).size();
            int outputs = getMachineType().getGui().getSlots(SlotType.IT_OUT, getTier()).size();
            if (inputs > 0) slots += (" IT_IN: " + inputs + ",");
            if (outputs > 0) slots += (" IT_OUT: " + outputs + ",");
        }
        if (getMachineType().hasFlag(FLUID)) {
            int inputs = getMachineType().getGui().getSlots(SlotType.FL_IN, getTier()).size();
            int outputs = getMachineType().getGui().getSlots(SlotType.FL_OUT, getTier()).size();
            if (inputs > 0) slots += (" FL_IN: " + inputs + ",");
            if (outputs > 0) slots += (" FL_OUT: " + outputs + ",");
        }
        if (slots.length() > 0) info.add("Slots:" + slots);
        energyHandler.ifPresent(h -> info.add("Energy: " + h.getEnergyStored() + " / " + h.getMaxEnergyStored()));
        coverHandler.ifPresent(h -> {
            StringBuilder builder = new StringBuilder("Covers: ");
            for (int i = 0; i < 6; i++) {
                builder.append(h.get(Ref.DIRECTIONS[i]).getId()).append(" ");
            }
            info.add(builder.toString());
        });
        return info;
    }
}
