package muramasa.antimatter.tile;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.*;
import muramasa.antimatter.capability.machine.*;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverInstance;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.integration.jei.renderer.IInfoRenderer;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.util.LazyHolder;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static muramasa.antimatter.capability.AntimatterCaps.*;
import static muramasa.antimatter.machine.MachineFlag.*;
import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

public class TileEntityMachine extends TileEntityTickable implements INamedContainerProvider, IInfoRenderer, IMachineHandler, IGuiHandler {

    /** Machine Data **/
    protected Machine<?> type;
    protected Tier tier;
    protected MachineState machineState;

    /** Handlers **/
    public LazyHolder<MachineItemHandler<?>> itemHandler;
    public LazyHolder<MachineFluidHandler<?>> fluidHandler;
    public LazyHolder<MachineEnergyHandler<?>> energyHandler;
    public LazyHolder<MachineRecipeHandler<?>> recipeHandler;
    public LazyHolder<MachineCoverHandler<?>> coverHandler;
    public LazyHolder<MachineInteractHandler<?>> interactHandler;

    public TileEntityMachine(Machine<?> type) {
        super(type.getTileType());
        this.type = type;
        this.machineState = getDefaultMachineState();
        this.itemHandler = type.has(ITEM) ? LazyHolder.of(() -> new MachineItemHandler<>(this)) : LazyHolder.empty();
        this.fluidHandler = type.has(FLUID) ? LazyHolder.of(() -> new MachineFluidHandler<>(this)) : LazyHolder.empty();
        this.energyHandler = type.has(ENERGY) ? LazyHolder.of(() -> new MachineEnergyHandler<>(this, type.has(GENERATOR))) : LazyHolder.empty();
        this.recipeHandler = type.has(RECIPE) ? LazyHolder.of(() -> new MachineRecipeHandler<>(this)) : LazyHolder.empty();
        this.coverHandler = type.has(COVERABLE) ? LazyHolder.of(() -> new MachineCoverHandler<>(this)) : LazyHolder.empty();
        this.interactHandler = LazyHolder.empty();
    }

    @Override
    public void onFirstTick() {
        if (isServerSide()) {
            this.itemHandler.ifPresent(MachineItemHandler::init);
            this.energyHandler.ifPresent(MachineEnergyHandler::init);
            this.recipeHandler.ifPresent(MachineRecipeHandler::init);
        }
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.pos, 3, this.getUpdateTag());
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        super.onDataPacket(net, pkt);
        handleUpdateTag(pkt.getNbtCompound());
    }

    @Override
    public void onServerUpdate() {
        itemHandler.ifPresent(MachineItemHandler::onServerUpdate);
        energyHandler.ifPresent(MachineEnergyHandler::onServerUpdate);
        fluidHandler.ifPresent(MachineFluidHandler::onUpdate);
        coverHandler.ifPresent(MachineCoverHandler::onUpdate);
        this.recipeHandler.ifPresent(MachineRecipeHandler::onServerUpdate);
    }

    @Override
    public void onRemove() {
        coverHandler.ifPresent(MachineCoverHandler::onRemove);
        fluidHandler.ifPresent(MachineFluidHandler::onRemove);
        itemHandler.ifPresent(MachineItemHandler::onRemove);
        energyHandler.ifPresent(MachineEnergyHandler::onRemove);
        recipeHandler.ifPresent(MachineRecipeHandler::resetRecipe);
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        coverHandler.ifPresent(c -> c.onMachineEvent(event, data));
        itemHandler.ifPresent(i -> i.onMachineEvent(event, data));
        energyHandler.ifPresent(e -> e.onMachineEvent(event, data));
        fluidHandler.ifPresent(f -> f.onMachineEvent(event, data));
        recipeHandler.ifPresent(r -> r.onMachineEvent(event, data));
    }

    /** Getters **/
    public Machine<?> getMachineType() {
        return type != null ? type : ((BlockMachine) getBlockState().getBlock()).getType();
    }

    public Tier getMachineTier() {
        return tier != null ? tier : ((BlockMachine) getBlockState().getBlock()).getTier();
    }

    public boolean has(MachineFlag flag) {
        return getMachineType().has(flag);
    }

    public Direction getFacing() {
        return getBlockState().get(BlockStateProperties.HORIZONTAL_FACING);
    }

    // TODO: Finish
    public boolean setFacing(Direction side) {
        return false;
    }

    // TODO: Fix
    public Direction getOutputFacing() {
        return coverHandler.map(MachineCoverHandler::getOutputFacing).orElse(getFacing().getOpposite());
    }

    public boolean setOutputFacing(Direction side) {
        return coverHandler.map(h -> h.setOutputFacing(side)).orElse(false);
    }

    public MachineState getMachineState() {
        return machineState;
    }

    public MachineState getDefaultMachineState() {
        return MachineState.IDLE;
    }

    public boolean isDefaultMachineState() {
        return getMachineState() == getDefaultMachineState();
    }

    public int getMaxInputVoltage() {
        return energyHandler.map(EnergyHandler::getInputVoltage).orElse(0);
    }

    /** Helpers **/
    public void resetMachine() {
        setMachineState(getDefaultMachineState());
    }

    public void toggleMachine() {
        //setMachineState(getDefaultMachineState());
        //recipeHandler.ifPresent(MachineRecipeHandler::checkRecipe);
        setMachineState(getMachineState() == MachineState.DISABLED ? getDefaultMachineState() : MachineState.DISABLED);
    }

    public void setMachineState(MachineState newState) {
        if (this.machineState != newState) {
            Utils.markTileForRenderUpdate(this);
            this.machineState = newState;
        }
    }

    public Cover[] getValidCovers() { //TODO fix me
        return AntimatterAPI.all(Cover.class).toArray(new Cover[0]);
    }

    public CoverInstance<?> getCover(Direction side) {
        return coverHandler.map(h -> h.get(side)).orElse(null);
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        ModelDataMap.Builder builder = new ModelDataMap.Builder().withInitial(AntimatterProperties.MACHINE_TYPE, getMachineType());
        coverHandler.ifPresent(machineCoverHandler -> builder.withInitial(AntimatterProperties.MACHINE_COVER, machineCoverHandler.getAll()));
        return builder.build();
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return getMachineType().getDisplayName(getMachineTier());
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, @Nonnull PlayerInventory inv, @Nonnull PlayerEntity player) {
        return getMachineType().has(GUI) ? getMachineType().getGui().getMenuHandler().getMenu(this, inv, windowId) : null;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == ITEM_HANDLER_CAPABILITY && itemHandler.isPresent()) return itemHandler.map(ih -> ih.getHandlerForSide(side)).transform().cast();
        else if (cap == FLUID_HANDLER_CAPABILITY && fluidHandler.isPresent()) return fluidHandler.map(fh -> fh.getWrapperForSide(side)).transform().cast();
        else if (cap == CapabilityEnergy.ENERGY && energyHandler.isPresent()) return energyHandler.transform().cast();
        else if (cap == COVERABLE_HANDLER_CAPABILITY && coverHandler.isPresent()) return coverHandler.transform().cast();
        else if (cap == INTERACTABLE_HANDLER_CAPABILITY && interactHandler.isPresent()) return interactHandler.transform().cast();
        return super.getCapability(cap, side);
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        this.write(tag);
        return tag;
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        this.tier = AntimatterAPI.get(Tier.class, tag.getString(Ref.KEY_MACHINE_TIER));
        setMachineState(MachineState.VALUES[tag.getInt(Ref.KEY_MACHINE_STATE)]);
        itemHandler.ifPresent(i -> i.deserializeNBT(tag.getCompound(Ref.KEY_MACHINE_ITEMS)));
        energyHandler.ifPresent(e -> e.deserializeNBT(tag.getCompound(Ref.KEY_MACHINE_ENERGY)));
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);
        tag.putString(Ref.KEY_MACHINE_TIER, getMachineTier().getId());
        tag.putInt(Ref.KEY_MACHINE_STATE, machineState.ordinal());
        itemHandler.ifPresent(i -> tag.put(Ref.KEY_MACHINE_ITEMS, i.serializeNBT()));
        energyHandler.ifPresent(e -> tag.put(Ref.KEY_MACHINE_ENERGY, e.serializeNBT()));
        return tag;
    }

    /*
    // Runs earlier then onFirstTick (server only)
    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        if (tag.contains(Ref.KEY_MACHINE_INTERACT)) interactHandler.read(tag.getCompound(Ref.KEY_MACHINE_INTERACT));
        if (tag.contains(Ref.KEY_MACHINE_ITEMS)) itemHandler.read(tag.getCompound(Ref.KEY_MACHINE_ITEMS));
        if (tag.contains(Ref.KEY_MACHINE_FLUIDS)) fluidHandler.read(tag.getCompound(Ref.KEY_MACHINE_FLUIDS));
        if (tag.contains(Ref.KEY_MACHINE_COVER)) coverHandler.read(tag.getCompound(Ref.KEY_MACHINE_COVER));
        if (tag.contains(Ref.KEY_MACHINE_ENERGY)) energyHandler.read(tag.getCompound(Ref.KEY_MACHINE_ENERGY));
        if (tag.contains(Ref.KEY_MACHINE_RECIPE)) recipeHandler.read(tag.getCompound(Ref.KEY_MACHINE_RECIPE));
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag); //TODO get tile data tag
        interactHandler.ifPresent(h -> tag.put(Ref.KEY_MACHINE_INTERACT, h.serialize()));
        itemHandler.ifPresent(h -> tag.put(Ref.KEY_MACHINE_ITEMS, h.serialize()));
        fluidHandler.ifPresent(h -> tag.put(Ref.KEY_MACHINE_FLUIDS, h.serialize()));
        coverHandler.ifPresent(h -> tag.put(Ref.KEY_MACHINE_COVER, h.serialize()));
        energyHandler.ifPresent(h -> tag.put(Ref.KEY_MACHINE_ENERGY, h.serialize()));
        recipeHandler.ifPresent(h -> tag.put(Ref.KEY_MACHINE_RECIPE, h.serialize()));
        return tag;
    }

    @Override
    public void update(CompoundNBT tag) {
        //super.update(tag);
        if (tag.contains(Ref.KEY_MACHINE_INTERACT)) interactHandler.ifPresent(h -> h.deserialize(tag.getCompound(Ref.KEY_MACHINE_INTERACT)));
        if (tag.contains(Ref.KEY_MACHINE_ITEMS)) itemHandler.ifPresent(h -> h.deserialize(tag.getCompound(Ref.KEY_MACHINE_ITEMS)));
        if (tag.contains(Ref.KEY_MACHINE_FLUIDS)) fluidHandler.ifPresent(h -> h.deserialize(tag.getCompound(Ref.KEY_MACHINE_FLUIDS)));
        if (tag.contains(Ref.KEY_MACHINE_COVER)) coverHandler.ifPresent(h -> h.deserialize(tag.getCompound(Ref.KEY_MACHINE_COVER)));
        if (tag.contains(Ref.KEY_MACHINE_ENERGY)) energyHandler.ifPresent(h -> h.deserialize(tag.getCompound(Ref.KEY_MACHINE_ENERGY)));
        if (tag.contains(Ref.KEY_MACHINE_RECIPE)) recipeHandler.ifPresent(h -> h.deserialize(tag.getCompound(Ref.KEY_MACHINE_RECIPE)));
    }
     */

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        info.add("Machine: " + getMachineType().getId() + " Tier: " + getMachineTier().getId());
        String slots = "";
        if (getMachineType().has(ITEM)) {
            int inputs = getMachineType().getGui().getSlots(SlotType.IT_IN, getMachineTier()).size();
            int outputs = getMachineType().getGui().getSlots(SlotType.IT_OUT, getMachineTier()).size();
            if (inputs > 0) slots += (" IT_IN: " + inputs + ",");
            if (outputs > 0) slots += (" IT_OUT: " + outputs + ",");
        }
        if (getMachineType().has(FLUID)) {
            int inputs = getMachineType().getGui().getSlots(SlotType.FL_IN, getMachineTier()).size();
            int outputs = getMachineType().getGui().getSlots(SlotType.FL_OUT, getMachineTier()).size();
            if (inputs > 0) slots += (" FL_IN: " + inputs + ",");
            if (outputs > 0) slots += (" FL_OUT: " + outputs + ",");
        }
        if (slots.length() > 0) info.add("Slots:" + slots);
        energyHandler.ifPresent(h -> info.add("Energy: " + h.getEnergyStored() + " / " + h.getMaxEnergyStored()));
        coverHandler.ifPresent(h -> {
            StringBuilder builder = new StringBuilder("Covers: ");
            for (Direction side : Ref.DIRS) {
                builder.append(h.get(side).getId()).append(" ");
            }
            info.add(builder.toString());
        });
        return info;
    }
}