package muramasa.antimatter.tile;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.EnergyHandler;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.capability.machine.*;
import muramasa.antimatter.client.dynamic.DynamicTexturer;
import muramasa.antimatter.client.dynamic.DynamicTexturers;
import muramasa.antimatter.client.dynamic.IDynamicModelProvider;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverStack;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.integration.jei.renderer.IInfoRenderer;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.structure.StructureCache;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import muramasa.antimatter.util.LazyHolder;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static muramasa.antimatter.capability.AntimatterCaps.COVERABLE_HANDLER_CAPABILITY;
import static muramasa.antimatter.capability.AntimatterCaps.ENERGY_HANDLER_CAPABILITY;
import static muramasa.antimatter.gui.event.GuiEvent.FLUID_EJECT;
import static muramasa.antimatter.gui.event.GuiEvent.ITEM_EJECT;
import static muramasa.antimatter.machine.MachineFlag.*;
import static net.minecraft.block.Blocks.AIR;
import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

public class TileEntityMachine extends TileEntityTickable implements INamedContainerProvider, IInfoRenderer, IMachineHandler, IGuiHandler, IDynamicModelProvider {

    /** Open container. Allows for better syncing **/
    @Nullable
    protected Container openContainer;

    /** Machine Data **/
    protected Machine<?> type;
    protected Tier tier;
    protected MachineState machineState;

    /** Handlers **/
    public LazyHolder<MachineItemHandler<?>> itemHandler;
    public LazyHolder<MachineFluidHandler<?>> fluidHandler;
    public LazyHolder<MachineEnergyHandler<?>> energyHandler;
    public LazyHolder<MachineRecipeHandler<?>> recipeHandler;
    public LazyHolder<MachineCoverHandler<TileEntityMachine>> coverHandler;

    /** Texture related areas. **/
    public LazyHolder<DynamicTexturer<TileEntityMachine, DynamicKey>> multiTexturer;

    public TileEntityMachine(Machine<?> type) {
        super(type.getTileType());
        this.type = type;
        this.machineState = getDefaultMachineState();
        this.itemHandler = type.has(ITEM) || type.has(CELL) ? LazyHolder.of(() -> new MachineItemHandler<>(this)) : LazyHolder.empty();
        this.fluidHandler = type.has(FLUID) ? LazyHolder.of(() -> new MachineFluidHandler<>(this)) : LazyHolder.empty();
        this.energyHandler = type.has(ENERGY) ? LazyHolder.of(() -> new MachineEnergyHandler<>(this, type.amps(),type.has(GENERATOR))) : LazyHolder.empty();
        this.recipeHandler = type.has(RECIPE) ? LazyHolder.of(() -> new MachineRecipeHandler<>(this)) : LazyHolder.empty();
        this.coverHandler = type.has(COVERABLE) ? LazyHolder.of(() -> new MachineCoverHandler<>(this)) : LazyHolder.empty();
        //lazy way to only runn on client.
        multiTexturer = LazyHolder.of(() -> new DynamicTexturer<>(DynamicTexturers.TILE_DYNAMIC_TEXTURER));
    }

    public void setOpenContainer(ContainerMachine c) {
        this.openContainer = c;
    }

    public void onContainerClose() {
        this.openContainer = null;
    }

    @Override
    public void onFirstTick() {
        if (isServerSide()) {
            this.itemHandler.ifPresent(MachineItemHandler::init);
            this.fluidHandler.ifPresent(MachineFluidHandler::init);
            this.energyHandler.ifPresent(MachineEnergyHandler::init);
            this.recipeHandler.ifPresent(MachineRecipeHandler::init);
        }
    }
    //Called before a recipe ticks.
    public void onRecipePreTick() {
        //NOOP
    }

    //Called after a recipe ticks.
    public void onRecipePostTick() {
        //NOOP
    }

    public void ofState(@Nonnull BlockState state) {
        Block block = state.getBlock();
        this.tier = ((BlockMachine)block).getTier();
        this.type = ((BlockMachine)block).getType();
    }

    @Override
    public void onServerUpdate() {
        itemHandler.ifPresent(MachineItemHandler::onUpdate);
        energyHandler.ifPresent(MachineEnergyHandler::onUpdate);
        fluidHandler.ifPresent(MachineFluidHandler::onUpdate);
        coverHandler.ifPresent(MachineCoverHandler::onUpdate);
        this.recipeHandler.ifPresent(MachineRecipeHandler::onServerUpdate);
    }

    @Override
    public void onRemove() {
        if (isServerSide()) {
            coverHandler.ifPresent(MachineCoverHandler::onRemove);
            fluidHandler.ifPresent(MachineFluidHandler::onRemove);
            itemHandler.ifPresent(MachineItemHandler::onRemove);
            energyHandler.ifPresent(MachineEnergyHandler::onRemove);
            recipeHandler.ifPresent(MachineRecipeHandler::resetRecipe);
        }
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        if (!this.getWorld().isRemote) {
            coverHandler.ifPresent(c -> c.onMachineEvent(event, data));
            itemHandler.ifPresent(i -> i.onMachineEvent(event, data));
            energyHandler.ifPresent(e -> e.onMachineEvent(event, data));
            fluidHandler.ifPresent(f -> f.onMachineEvent(event, data));
            recipeHandler.ifPresent(r -> r.onMachineEvent(event, data));
            if (event instanceof ContentEvent && openContainer != null) {
                openContainer.detectAndSendChanges();
            }
        }
    }

    /** Getters **/
    public Machine<?> getMachineType() {
        if (type != null) return type;
        Block block = getBlockState().getBlock();
        if (!(block instanceof BlockMachine)) return Data.MACHINE_INVALID;
        return ((BlockMachine) block).getType();
    }

    public Tier getMachineTier() {
        if (tier != null) return tier;
        Block block = getBlockState().getBlock();
        if (!(block instanceof BlockMachine)) return Tier.LV;
        return ((BlockMachine) block).getTier();
    }

    //Returns the tier level for recipes.
    public Tier getPowerLevel() {
        return Tier.getTier(type.amps()*getMachineTier().getVoltage());
    }

    public boolean has(MachineFlag flag) {
        return getMachineType().has(flag);
    }

    public Direction getFacing() {
        if (this.world == null) return Direction.NORTH;
        BlockState state = getBlockState();
        if (state == AIR.getDefaultState()) {
            return Direction.NORTH;
        }
        return state.get(BlockStateProperties.HORIZONTAL_FACING);
    }

    public boolean setFacing(Direction side) {
        //TODO: Move covers as well?
        if (side == getFacing() || side.getAxis() == Direction.Axis.Y) return false;
        boolean isEmpty = coverHandler.map(ch -> ch.get(side).isEmpty()).orElse(true);
        if (isEmpty) {
            getWorld().setBlockState(getPos(), getBlockState().with(BlockStateProperties.HORIZONTAL_FACING, side));
            return true;
        }
        return false;
    }

    @Override
    public void onGuiEvent(IGuiEvent event, int... data) {
        if (event == ITEM_EJECT || event == FLUID_EJECT) {
            coverHandler.ifPresent(ch -> {
                ch.get(ch.getOutputFacing()).onMachineEvent(this, event, data);
            });
        }
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
        setMachineState(getMachineState() == MachineState.DISABLED ? getDefaultMachineState() : MachineState.DISABLED);
    }

    public void setMachineState(MachineState newState) {
        if (this.machineState != newState) {
            this.machineState = newState;
            if (hadFirstTick()) {
                sidedSync(true);
            }
        }
    }

    public Cover[] getValidCovers() { //TODO fix me
        return AntimatterAPI.all(Cover.class).toArray(new Cover[0]);
    }

    public CoverStack<?> getCover(Direction side) {
        return coverHandler.map(h -> h.get(side)).orElse(null);
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        ModelDataMap.Builder builder = new ModelDataMap.Builder().withInitial(AntimatterProperties.MACHINE_TYPE, getMachineType());
        builder.withInitial(AntimatterProperties.MACHINE_TEXTURE,getMachineType().getBaseTexture(getMachineTier()))
                .withInitial(AntimatterProperties.MACHINE_STATE, getMachineState());

        coverHandler.ifPresent(machineCoverHandler -> builder.withInitial(AntimatterProperties.MACHINE_TILE, this));
        BlockPos cPos = StructureCache.get(this.getWorld(), pos);
        if (cPos != null) {
            TileEntityMultiMachine mTile = (TileEntityMultiMachine) world.getTileEntity(cPos);
            builder.withInitial(AntimatterProperties.MULTI_MACHINE_TEXTURE,mTile.getMachineType().getBaseTexture(mTile.getMachineTier()));
        }

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
        if (cap == ITEM_HANDLER_CAPABILITY && itemHandler.isPresent()) return side == null ? itemHandler.map(MachineItemHandler::getOutputHandler).transform().cast() : itemHandler.map(ih -> ih.getHandlerForSide(side)).transform().cast();
        else if (cap == FLUID_HANDLER_CAPABILITY && fluidHandler.isPresent()) return fluidHandler.transform().cast();
        else if (cap == ENERGY_HANDLER_CAPABILITY && energyHandler.isPresent()) return energyHandler.transform().cast();
        else if (cap == COVERABLE_HANDLER_CAPABILITY && coverHandler.isPresent()) return coverHandler.transform().cast();
        return super.getCapability(cap, side);
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        this.tier = AntimatterAPI.get(Tier.class, tag.getString(Ref.KEY_MACHINE_TIER));
        setMachineState(MachineState.VALUES[tag.getInt(Ref.KEY_MACHINE_STATE)]);
        itemHandler.ifPresent(i -> i.deserializeNBT(tag.getCompound(Ref.KEY_MACHINE_ITEMS)));
        energyHandler.ifPresent(e -> e.deserializeNBT(tag.getCompound(Ref.KEY_MACHINE_ENERGY)));
        coverHandler.ifPresent(e -> e.deserializeNBT(tag.getCompound(Ref.KEY_MACHINE_COVER)));
        fluidHandler.ifPresent(e -> e.deserializeNBT(tag.getCompound(Ref.KEY_MACHINE_FLUIDS)));
        recipeHandler.ifPresent(e -> e.deserializeNBT(tag.getCompound(Ref.KEY_MACHINE_RECIPE)));
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);
        tag.putString(Ref.KEY_MACHINE_TIER, getMachineTier().getId());
        tag.putInt(Ref.KEY_MACHINE_STATE, machineState.ordinal());
        itemHandler.ifPresent(i -> tag.put(Ref.KEY_MACHINE_ITEMS, i.serializeNBT()));
        energyHandler.ifPresent(e -> tag.put(Ref.KEY_MACHINE_ENERGY, e.serializeNBT()));
        coverHandler.ifPresent(e -> tag.put(Ref.KEY_MACHINE_COVER, e.serializeNBT()));
        fluidHandler.ifPresent(e -> tag.put(Ref.KEY_MACHINE_FLUIDS, e.serializeNBT()));
        recipeHandler.ifPresent(e -> tag.put(Ref.KEY_MACHINE_RECIPE, e.serializeNBT()));
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
        info.add("State: " + getMachineState().getId());
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
        recipeHandler.ifPresent(rh -> {
            rh.getInfo(info);
        });
        //multiTexture.ifPresent(mt -> info.add("Rendering using texture " + mt.toString() + "."));
        return info;
    }

    @Override
    public ResourceLocation getModel(Direction dir, Direction facing) {
        return this.getMachineType().getOverlayModel(Utils.coverRotateFacing(dir, facing));
    }


    @Override
    public String getId() {
        return this.getMachineType().getId();
    }

    /**
     * The key used to build dynamic textures.
     */
    public static class DynamicKey {
        public final ResourceLocation model;
        public final Texture tex;
        public Direction facing;
        public final MachineState state;

        public DynamicKey(ResourceLocation model, Texture tex, Direction dir, MachineState state) {
            this.model = model;
            this.tex = tex;
            this.facing = dir;
            this.state = state;
        }

        public void setDir(Direction dir) {
            this.facing = dir;
        }

        @Override
        public int hashCode() {
            return tex.hashCode() + facing.hashCode() + state.hashCode() + model.hashCode();
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof DynamicKey) {
                DynamicKey key = (DynamicKey) o;
                return key.state == state && key.facing == facing && tex.equals(key.tex) && model.equals(key.model);
            }
            return false;
        }
    }
}