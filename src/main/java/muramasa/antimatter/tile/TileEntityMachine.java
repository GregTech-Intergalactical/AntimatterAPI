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
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static muramasa.antimatter.capability.CapabilitySide.SERVER;
import static muramasa.antimatter.capability.CapabilitySide.SERVER_AND_CLIENT;
import static muramasa.antimatter.machine.MachineFlag.*;

public class TileEntityMachine extends TileEntityTickable implements INamedContainerProvider, IMachineHandler, IGuiHandler, IInfoRenderer {

    /** Machine Data **/
    protected Machine<?> type;
    private MachineState machineState;

    /** Client Data **/
    protected float clientProgress = 0; //TODO look into receiveClientEvent
    protected float maxProgress = 0; //TODO look into receiveClientEvent

    /** Capabilities **/
    public MachineCapabilityHolder<MachineItemHandler<?>> itemHandler = new MachineCapabilityHolder<>(this, ITEM, SERVER_AND_CLIENT);
    public MachineCapabilityHolder<MachineFluidHandler<?>> fluidHandler = new MachineCapabilityHolder<>(this, FLUID, SERVER);
    public MachineCapabilityHolder<MachineRecipeHandler<?>> recipeHandler = new MachineCapabilityHolder<>(this, RECIPE, SERVER);
    public MachineCapabilityHolder<MachineEnergyHandler<?>> energyHandler = new MachineCapabilityHolder<>(this, ENERGY, SERVER_AND_CLIENT);
    public MachineCapabilityHolder<MachineCoverHandler<?>> coverHandler = new MachineCapabilityHolder<>(this, COVERABLE, SERVER_AND_CLIENT);
    public MachineCapabilityHolder<MachineInteractHandler<?>> interactHandler = new MachineCapabilityHolder<>(this, CONFIGURABLE, SERVER_AND_CLIENT);

    protected final IIntArray machineData = new IIntArray() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return Float.floatToRawIntBits(TileEntityMachine.this.recipeHandler.map(recipe -> (float)recipe.getCurProgress() / recipe.getMaxProgress()).orElse(0.0f));
            }
            return -1;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0:
                    float v = Float.intBitsToFloat(value);
                    clientProgress = v;
                    break;
            }
        }

        @Override
        public int size() {
            return 1;
        }
    };

    public IIntArray getContainerData() { return machineData; };

    public TileEntityMachine(TileEntityType<?> tileType) {
        super(tileType);
        machineState = getDefaultMachineState();
    }

    public TileEntityMachine(Machine<?> type) {
        this(type.getTileType());
        this.type = type;
        coverHandler.init(MachineCoverHandler::new);
        interactHandler.init(MachineInteractHandler::new);
        itemHandler.init(MachineItemHandler::new);
        fluidHandler.init(MachineFluidHandler::new);
        recipeHandler.init(MachineRecipeHandler::new);
        energyHandler.init(MachineEnergyHandler::new);
    }

    @Override
    public void onFirstTick() {
        energyHandler.ifPresent(MachineEnergyHandler::onInit);
        fluidHandler.ifPresent(MachineFluidHandler::onInit);
        itemHandler.ifPresent(MachineItemHandler::onInit);
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
    public void onRemove() {
        coverHandler.ifPresent(MachineCoverHandler::onRemove);
        energyHandler.ifPresent(MachineEnergyHandler::onRemove);
        fluidHandler.ifPresent(MachineFluidHandler::onRemove);
        itemHandler.ifPresent(MachineItemHandler::onRemove);
    }

    @Override
    public void onServerUpdate() {
        recipeHandler.ifPresent(MachineRecipeHandler::onUpdate);
        fluidHandler.ifPresent(MachineFluidHandler::onUpdate);
        itemHandler.ifPresent(MachineItemHandler::onUpdate);
        energyHandler.ifPresent(MachineEnergyHandler::onUpdate);
        coverHandler.ifPresent(MachineCoverHandler::onUpdate);
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        recipeHandler.ifPresent(h -> h.onMachineEvent(event, data));
        coverHandler.ifPresent(h -> h.onMachineEvent(event, data));
        itemHandler.ifPresent(h -> h.onMachineEvent(event,data));
        energyHandler.ifPresent(h -> h.onMachineEvent(event,data));
        fluidHandler.ifPresent(h -> h.onMachineEvent(event,data));

        //TODO: Put this in the actual handlers when a change occurs.
    }

    /** Getters **/
    public Machine<?> getMachineType() {
        return type != null ? type : ((BlockMachine) getBlockState().getBlock()).getType();
    }

    public Tier getMachineTier() {
        return ((BlockMachine) getBlockState().getBlock()).getTier();
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
        recipeHandler.ifPresent(MachineRecipeHandler::resetRecipe);
    }

    public void toggleMachine() {
        //setMachineState(getDefaultMachineState());
        //recipeHandler.ifPresent(MachineRecipeHandler::checkRecipe);
        setMachineState(getMachineState() == MachineState.DISABLED ? getDefaultMachineState() : MachineState.DISABLED);
    }

    public void setMachineState(MachineState newState) {
        /*if (machineState.getOverlayId() != newState.getOverlayId() && newState.allowRenderUpdate()) {
            markForRenderUpdate();
            System.out.println("RENDER UPDATE");
        }*/
        if (getMachineState() != newState) {
            Utils.markTileForRenderUpdate(this);
            if (isServerSide()) markDirty();
        }
        machineState = newState;
    }

    public Cover[] getValidCovers() { //TODO fix me
        return AntimatterAPI.all(Cover.class).toArray(new Cover[0]);
    }

    public CoverInstance<?> getCover(Direction side) {
        return coverHandler.map(h -> h.get(side)).orElse(null);
    }

    public float getClientProgress() {
            return clientProgress;
    }

    public void setClientProgress(float prog) {
        clientProgress = prog;
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
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        if ((cap == AntimatterCaps.ENERGY || cap == CapabilityEnergy.ENERGY) && energyHandler.isPresent()) return LazyOptional.of(() -> energyHandler.get()).cast();
        else if (cap == AntimatterCaps.INTERACTABLE && interactHandler.isPresent()) return LazyOptional.of(() -> interactHandler.get()).cast();
        return super.getCapability(cap);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && itemHandler.isPresent()) return LazyOptional.of(() -> itemHandler.get().getHandlerForSide(side)).cast();
        else if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && fluidHandler.isPresent()) return LazyOptional.of(() -> fluidHandler.get().getWrapperForSide(side)).cast();
        else if ((cap == AntimatterCaps.ENERGY || cap == CapabilityEnergy.ENERGY) && energyHandler.isPresent()) return LazyOptional.of(() -> energyHandler.get()).cast();
        else if (cap == AntimatterCaps.COVERABLE && coverHandler.isPresent()/*coverHandler.map(h -> true/*h.getCover(side).isEmpty()).orElse(false)*/) {
            return LazyOptional.of(() -> coverHandler.get()).cast();
        }
        else if (cap == AntimatterCaps.INTERACTABLE && interactHandler.isPresent()) return LazyOptional.of(() -> interactHandler.get()).cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        if (tag.contains(Ref.KEY_MACHINE_TILE_STATE)) setMachineState(MachineState.VALUES[tag.getInt(Ref.KEY_MACHINE_TILE_STATE)]);//TODO saving state needed? if recipe is saved, serverUpdate should handle it.
        if (tag.contains(Ref.KEY_MACHINE_TILE_ITEMS)) itemHandler.ifPresent(h -> h.deserialize(tag.getCompound(Ref.KEY_MACHINE_TILE_ITEMS)));
        if (tag.contains(Ref.KEY_MACHINE_TILE_FLUIDS)) fluidHandler.ifPresent(h -> h.deserialize(tag.getCompound(Ref.KEY_MACHINE_TILE_FLUIDS)));
        if (tag.contains(Ref.KEY_MACHINE_TILE_ENERGY)) energyHandler.ifPresent(h -> h.deserialize(tag.getCompound(Ref.KEY_MACHINE_TILE_ENERGY)));
        if (tag.contains(Ref.KEY_MACHINE_TILE_RECIPE)) recipeHandler.ifPresent(h -> h.deserialize(tag.getCompound(Ref.KEY_MACHINE_TILE_RECIPE)));
        if (tag.contains(Ref.KEY_MACHINE_TILE_COVER)) coverHandler.ifPresent(h -> h.deserialize(tag.getCompound(Ref.KEY_MACHINE_TILE_COVER)));
    }

    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        this.write(nbt);
        return nbt;
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag); //TODO get tile data tag
        if (getMachineState() != null) tag.putInt(Ref.KEY_MACHINE_TILE_STATE, getMachineState().ordinal());
        itemHandler.ifPresent(h -> tag.put(Ref.KEY_MACHINE_TILE_ITEMS, h.serialize()));
        fluidHandler.ifPresent(h -> tag.put(Ref.KEY_MACHINE_TILE_FLUIDS, h.serialize()));
        energyHandler.ifPresent(h -> tag.put(Ref.KEY_MACHINE_TILE_ENERGY, h.serialize()));
        recipeHandler.ifPresent(h -> tag.put(Ref.KEY_MACHINE_TILE_RECIPE, h.serialize()));
        coverHandler.ifPresent(h -> tag.put(Ref.KEY_MACHINE_TILE_COVER, h.serialize()));
        return tag;
    }

    //TODO move toString to capabilities
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
        recipeHandler.ifPresent(h -> info.add("Recipe: " + h.getCurProgress() + " / " + h.getMaxProgress()));
        return info;
    }
}