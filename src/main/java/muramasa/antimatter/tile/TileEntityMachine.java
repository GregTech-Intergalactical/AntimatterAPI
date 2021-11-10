package muramasa.antimatter.tile;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.*;
import muramasa.antimatter.capability.machine.*;
import muramasa.antimatter.client.dynamic.DynamicTexturer;
import muramasa.antimatter.client.dynamic.DynamicTexturers;
import muramasa.antimatter.client.dynamic.IDynamicModelProvider;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.SlotData;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.gui.widget.FluidSlotWidget;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.network.packets.AbstractGuiEventPacket;
import muramasa.antimatter.network.packets.TileGuiEventPacket;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.structure.StructureCache;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import tesseract.api.capability.TesseractGTCapability;
import tesseract.api.gt.IEnergyHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

import static muramasa.antimatter.capability.AntimatterCaps.COVERABLE_HANDLER_CAPABILITY;
import static muramasa.antimatter.capability.AntimatterCaps.RECIPE_HANDLER_CAPABILITY;
import static muramasa.antimatter.gui.event.GuiEvent.FLUID_EJECT;
import static muramasa.antimatter.gui.event.GuiEvent.ITEM_EJECT;
import static muramasa.antimatter.machine.MachineFlag.*;
import static net.minecraft.block.Blocks.AIR;
import static net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
import static net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;

public class TileEntityMachine<T extends TileEntityMachine<T>> extends TileEntityTickable<T> implements INamedContainerProvider, IMachineHandler, IGuiHandler, IDynamicModelProvider {

    /**
     * Open container. Allows for better syncing
     **/
    protected final Set<ContainerMachine<T>> openContainers = new ObjectOpenHashSet<>();

    /**
     * Machine Data
     **/
    protected Machine<?> type;
    protected Tier tier;
    protected MachineState machineState;

    protected MachineState disabledState;

    /**
     * Handlers
     **/
   /* public LazyOptional<MachineItemHandler<?>> itemHandler;
    public LazyOptional<MachineFluidHandler<?>> fluidHandler;
    public LazyOptional<MachineEnergyHandler<T>> energyHandler;
    public LazyOptional<MachineRecipeHandler<T>> recipeHandler;
    public LazyOptional<MachineCoverHandler<TileEntityMachine>> coverHandler;*/

    public Holder<IItemHandler, MachineItemHandler<T>> itemHandler = new Holder<>(ITEM_HANDLER_CAPABILITY, dispatch);
    public Holder<IFluidHandler, MachineFluidHandler<T>> fluidHandler = new Holder<>(FLUID_HANDLER_CAPABILITY, dispatch);
    public Holder<ICoverHandler, MachineCoverHandler<T>> coverHandler = new Holder<>(COVERABLE_HANDLER_CAPABILITY, dispatch);
    public Holder<IEnergyHandler, MachineEnergyHandler<T>> energyHandler = new Holder<>(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY, dispatch);
    public Holder<MachineRecipeHandler, MachineRecipeHandler<T>> recipeHandler = new Holder<>(RECIPE_HANDLER_CAPABILITY, dispatch);

    /**
     * Texture related areas.
     **/
    public LazyValue<DynamicTexturer<TileEntityMachine<?>, DynamicKey>> multiTexturer;

    public TileEntityMachine(Machine<?> type) {
        super(type.getTileType());
        this.type = type;
        this.machineState = getDefaultMachineState();
        if (type.has(ITEM) || type.has(CELL)) {
            itemHandler.set(() -> new MachineItemHandler<>((T) this));
        }
        if (type.has(FLUID)) {
            fluidHandler.set(() -> new MachineFluidHandler<>((T) this));
        }
        if (type.has(ENERGY)) {
            energyHandler.set(() -> new MachineEnergyHandler<>((T) this, type.amps(), type.has(GENERATOR)));
        }
        if (type.has(RECIPE)) {
            recipeHandler.set(() -> new MachineRecipeHandler<>((T) this));
        }
        if (type.has(COVERABLE)) {
            coverHandler.set(() -> new MachineCoverHandler<>((T) this));
        }
        multiTexturer = new LazyValue<>(() -> new DynamicTexturer<>(DynamicTexturers.TILE_DYNAMIC_TEXTURER));
    }

    public void addOpenContainer(ContainerMachine<T> c) {
        this.openContainers.add(c);
    }

    public void onContainerClose(ContainerMachine<T> c) {
        this.openContainers.remove(c);
    }

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        if (isServerSide()) {
            this.itemHandler.ifPresent(MachineItemHandler::init);
            this.fluidHandler.ifPresent(MachineFluidHandler::init);
            this.energyHandler.ifPresent(MachineEnergyHandler::init);
            this.recipeHandler.ifPresent(MachineRecipeHandler::init);
        }
    }


    @Override
    public String getDomain() {
        return getMachineType().getDomain();
    }

    @Override
    public boolean isRemote() {
        return world.isRemote;
    }

    @Override
    public void addWidgets(GuiInstance instance, IGuiElement parent) {
        int index = 0;
        for (SlotData<?> slot : this.getMachineType().getGui().getSlots().getSlots(SlotType.FL_IN, getMachineTier())) {
            instance.addWidget(FluidSlotWidget.build(index++, slot));
        }
        for (SlotData<?> slot : this.getMachineType().getGui().getSlots().getSlots(SlotType.FL_OUT, getMachineTier())) {
            instance.addWidget(FluidSlotWidget.build(index++, slot));
        }
        this.getMachineType().getCallbacks().forEach(t -> t.accept(instance));
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return getMachineType().getGui().getTexture(this.getMachineTier(), "machine");
    }

    /**
     * RECIPE UTILITY METHODS
     **/

    //Called before a recipe ticks.
    public void onRecipePreTick() {
        //NOOP
    }

    //Called after a recipe ticks.
    public void onRecipePostTick() {
        //NOOP
    }

    //Called whenever a recipe is stopped.
    public void onMachineStop() {

    }

    //Called whenever a recipe is activated, might be the same as before (e.g. no new recipe).
    public void onMachineStarted(Recipe r) {

    }

    public void onBlockUpdate(BlockPos neighbor) {
        Direction facing = Utils.getOffsetFacing(this.getPos(), neighbor);
        coverHandler.ifPresent(h -> h.get(facing).onBlockUpdate());
    }

    public void ofState(@Nonnull BlockState state) {
        Block block = state.getBlock();
        this.tier = ((BlockMachine) block).getTier();
        this.type = ((BlockMachine) block).getType();
    }

    @Override
    public void onServerUpdate() {
        itemHandler.ifPresent(MachineItemHandler::onUpdate);
        energyHandler.ifPresent(MachineEnergyHandler::onUpdate);
        fluidHandler.ifPresent(MachineFluidHandler::onUpdate);
        coverHandler.ifPresent(MachineCoverHandler::onUpdate);
        this.recipeHandler.ifPresent(MachineRecipeHandler::onServerUpdate);

        if (false) {
            double d = Ref.RNG.nextDouble();
            if (d > 0.97D && this.world.isRainingAt(new BlockPos(this.pos.getX(), this.pos.getY() + 1, this.pos.getZ()))) {
                if (this.energyHandler.map(t -> t.getEnergy() > 0).orElse(false))
                    Utils.createExplosion(this.world, pos, 6.0F, Explosion.Mode.DESTROY);
            }
        }
    }

    @Override
    public void onClientUpdate() {
        super.onClientUpdate();
        coverHandler.ifPresent(MachineCoverHandler::onUpdate);
    }

    @Override
    public void onRemove() {
        if (isServerSide()) {
            coverHandler.ifPresent(MachineCoverHandler::onRemove);
            fluidHandler.ifPresent(MachineFluidHandler::onRemove);
            itemHandler.ifPresent(MachineItemHandler::onRemove);
            energyHandler.ifPresent(MachineEnergyHandler::onRemove);
            recipeHandler.ifPresent(MachineRecipeHandler::onRemove);

            dispatch.invalidate();
        }
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        if (this.getWorld() != null && !this.getWorld().isRemote) {
            coverHandler.ifPresent(c -> c.onMachineEvent(event, data));
            itemHandler.ifPresent(i -> i.onMachineEvent(event, data));
            energyHandler.ifPresent(e -> e.onMachineEvent(event, data));
            fluidHandler.ifPresent(f -> f.onMachineEvent(event, data));
            recipeHandler.ifPresent(r -> r.onMachineEvent(event, data));
            if (event instanceof ContentEvent && openContainers.size() > 0) {
                //openContainers.forEach(ContainerMachine::detectAndSendLiquidChanges);
            }
        }
    }

    /**
     * Getters
     **/
    public Machine<?> getMachineType() {
        if (type != null) return type;
        Block block = getBlockState().getBlock();
        if (!(block instanceof BlockMachine)) return null;
        return ((BlockMachine) block).getType();
    }

    public Tier getMachineTier() {
        if (tier != null) return tier;
        Block block = getBlockState().getBlock();
        if (!(block instanceof BlockMachine)) return Tier.LV;
        return ((BlockMachine) block).getTier();
    }

    //Returns the tier level for recipe overclocking.
    public Tier getPowerLevel() {
        return getMachineTier();
    }

    public boolean has(MachineFlag flag) {
        return getMachineType().has(flag);
    }

    public int getWeakRedstonePower(Direction facing) {
        if (facing != null && !this.getCover(facing).isEmpty()) {
            return this.getCover(facing).getWeakPower();
        }
        return 0;
    }

    public int getStrongRedstonePower(Direction facing) {
        if (facing != null && !this.getCover(facing).isEmpty()) {
            return this.getCover(facing).getStrongPower();
        }
        return 0;
    }

    public Direction getFacing() {
        if (this.world == null) return Direction.NORTH;
        BlockState state = getBlockState();
        if (state == AIR.getDefaultState()) {
            return Direction.NORTH;
        }
        if (getMachineType().allowVerticalFacing()) {
            return state.get(BlockStateProperties.FACING);
        }
        return state.get(BlockStateProperties.HORIZONTAL_FACING);
    }

    public boolean setFacing(Direction side) {
        if (side == getFacing() || (side.getAxis() == Direction.Axis.Y && !getMachineType().allowVerticalFacing()))
            return false;
        boolean isEmpty = coverHandler.map(ch -> ch.get(side).isEmpty()).orElse(true);
        if (isEmpty) {
            BlockState state = getBlockState();
            if (getMachineType().allowVerticalFacing()) {
                state = state.with(BlockStateProperties.FACING, side);
                if (side.getAxis() != Direction.Axis.Y) {
                    state = state.with(BlockMachine.HORIZONTAL_FACING, side);
                }
            } else {
                state = state.with(BlockStateProperties.HORIZONTAL_FACING, side);
            }
            getWorld().setBlockState(getPos(), state);
            refreshCaps();

            return true;
        }
        return false;
    }

    protected boolean setFacing(PlayerEntity player, Direction side) {
        boolean setFacing = setFacing(side);
        if (setFacing) player.playSound(Ref.WRENCH, SoundCategory.BLOCKS, 1.0f, 1.0f);
        return setFacing;
    }

    public boolean wrenchMachine(PlayerEntity player, BlockRayTraceResult res, boolean crouch) {
        if (crouch || getMachineType().getOutputCover() == ICover.emptyFactory) {
            //Machine has no output
            return setFacing(player, Utils.getInteractSide(res));
        }
        return setOutputFacing(player, Utils.getInteractSide(res));
    }

    @Override
    public void onGuiEvent(IGuiEvent event, PlayerEntity player, int... data) {
        if (event == ITEM_EJECT || event == FLUID_EJECT) {
            coverHandler.ifPresent(ch -> {
                ch.get(ch.getOutputFacing()).onGuiEvent(event, player, data);
            });
        }
    }

    @Override
    public AbstractGuiEventPacket createGuiPacket(IGuiEvent event, int... data) {
        return new TileGuiEventPacket(event, getPos(), data);
    }

    @Override
    public String handlerDomain() {
        return getDomain();
    }

    // TODO: Fix
    public Direction getOutputFacing() {
        if (type.getOutputCover() != null && !(type.getOutputCover() == ICover.emptyFactory) && coverHandler.isPresent()) {
            Direction dir = coverHandler.get().getOutputFacing();
            return dir == null ? getFacing().getOpposite() : dir;
        }
        return null;
    }

    public boolean setOutputFacing(PlayerEntity player, Direction side) {
        return coverHandler.map(h -> h.setOutputFacing(player, side)).orElse(false);
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

    public int getMaxOutputVoltage() {
        return energyHandler.map(EnergyHandler::getOutputVoltage).orElse(0);
    }

    /**
     * Helpers
     **/
    public void resetMachine() {
        setMachineState(getDefaultMachineState());
    }

    public void toggleMachine() {
        if (getMachineState() == MachineState.DISABLED) {
            setMachineState(disabledState);
            disabledState = null;
            if (getMachineState().allowRecipeCheck()) {
                recipeHandler.ifPresent(MachineRecipeHandler::checkRecipe);
            }
        } else {
            disableMachine();
        }
    }

    protected void disableMachine() {
        disabledState = getMachineState();
        setMachineState(MachineState.DISABLED);
    }

    public void setMachineState(MachineState newState) {
        if (this.machineState != newState) {
            MachineState old = this.machineState;
            this.machineState = newState;
            if (world != null) {
                sidedSync(true);
                if (!world.isRemote) {
                    if (old == MachineState.ACTIVE) {
                        this.onMachineStop();
                    } else if (newState == MachineState.ACTIVE) {
                        if (recipeHandler.isPresent()) {
                            MachineRecipeHandler<?> handler = recipeHandler.get();
                            this.onMachineStarted(handler.getActiveRecipe());
                        }
                    }
                }
            }
            markDirty();
        }
    }

    public ICover[] getValidCovers() { //TODO fix me
        return AntimatterAPI.all(ICover.class).toArray(new ICover[0]);
    }

    public ICover getCover(Direction side) {
        return coverHandler.map(h -> h.get(side)).orElse(ICover.empty);
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        ModelDataMap.Builder builder = new ModelDataMap.Builder();
        TileEntityBasicMultiMachine mTile = StructureCache.getAnyMulti(this.getWorld(), pos, TileEntityBasicMultiMachine.class);
        if (mTile != null) {
            builder.withInitial(AntimatterProperties.MULTI_MACHINE_TEXTURE, a -> {
                Texture[] tex = mTile.getMachineType().getBaseTexture(mTile.getMachineTier());
                if (tex.length == 1) return tex[0];
                return tex[a.getIndex()];
            });
        }

        return builder.build();
    }

    public ActionResultType onInteract(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit, @Nullable AntimatterToolType type) {
        //DEFAULT
        return ActionResultType.PASS;
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return getMachineType().getDisplayName(getMachineTier());
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, @Nonnull PlayerInventory inv, @Nonnull PlayerEntity player) {
        return getMachineType().has(GUI) ? getMachineType().getGui().getMenuHandler().menu(this, inv, windowId) : null;
    }

    public boolean canPlayerOpenGui(PlayerEntity playerEntity) {
        return true;
    }

    public void refreshCaps() {
        if (isServerSide()) {
            dispatch.refresh();
        }
    }

    public void refreshCap(Capability<?> cap) {
        if (isServerSide()) {
            dispatch.refresh(cap);
        }
    }

    public void invalidateCaps() {
        if (isServerSide()) {
            dispatch.invalidate();
        }
    }

    public void invalidateCaps(Direction side) {
        if (isServerSide()) {
            dispatch.invalidate(side);
        }
    }

    public void invalidateCap(Capability<?> cap) {
        if (isServerSide()) {
            dispatch.invalidate(cap);
        }
    }

    public <V> boolean blocksCapability(@Nonnull Capability<V> cap, Direction side) {
        return coverHandler.map(t -> t.blocksCapability(cap, side)).orElse(false);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        if (cap == COVERABLE_HANDLER_CAPABILITY && coverHandler.isPresent()) return coverHandler.side(side).cast();
        if (side == getFacing() && !allowsFrontIO()) return LazyOptional.empty();
        if (blocksCapability(cap, side)) return LazyOptional.empty();
        if (cap == ITEM_HANDLER_CAPABILITY && itemHandler.isPresent()) return itemHandler.side(side).cast();
        if (cap == RECIPE_HANDLER_CAPABILITY && recipeHandler.isPresent()) return recipeHandler.side(side).cast();
        else if (cap == FLUID_HANDLER_CAPABILITY && fluidHandler.isPresent()) return fluidHandler.side(side).cast();
        else if (cap == TesseractGTCapability.ENERGY_HANDLER_CAPABILITY && energyHandler.isPresent())
            return energyHandler.side(side).cast();
        return super.getCapability(cap, side);
    }

    public final boolean allowsFrontIO() {
        return getMachineType().allowsFrontIO();
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        this.tier = AntimatterAPI.get(Tier.class, tag.getString(Ref.KEY_MACHINE_TIER));
        setMachineState(MachineState.VALUES[tag.getInt(Ref.KEY_MACHINE_STATE)]);
        if (tag.contains(Ref.KEY_MACHINE_STATE_D)) {
            disabledState = MachineState.VALUES[tag.getInt(Ref.KEY_MACHINE_STATE_D)];
        }
        if (tag.contains(Ref.KEY_MACHINE_ITEMS))
            itemHandler.ifPresent(i -> i.deserializeNBT(tag.getCompound(Ref.KEY_MACHINE_ITEMS)));
        if (tag.contains(Ref.KEY_MACHINE_ENERGY))
            energyHandler.ifPresent(e -> e.deserializeNBT(tag.getCompound(Ref.KEY_MACHINE_ENERGY)));
        if (tag.contains(Ref.KEY_MACHINE_COVER))
            coverHandler.ifPresent(e -> e.deserializeNBT(tag.getCompound(Ref.KEY_MACHINE_COVER)));
        if (tag.contains(Ref.KEY_MACHINE_FLUIDS))
            fluidHandler.ifPresent(e -> e.deserializeNBT(tag.getCompound(Ref.KEY_MACHINE_FLUIDS)));
        if (tag.contains(Ref.KEY_MACHINE_RECIPE))
            recipeHandler.ifPresent(e -> e.deserializeNBT(tag.getCompound(Ref.KEY_MACHINE_RECIPE)));
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);
        tag.putString(Ref.KEY_MACHINE_TIER, getMachineTier().getId());
        tag.putInt(Ref.KEY_MACHINE_STATE, machineState.ordinal());
        if (disabledState != null)
            tag.putInt(Ref.KEY_MACHINE_STATE_D, disabledState.ordinal());
        itemHandler.ifPresent(i -> tag.put(Ref.KEY_MACHINE_ITEMS, i.serializeNBT()));
        energyHandler.ifPresent(e -> tag.put(Ref.KEY_MACHINE_ENERGY, e.serializeNBT()));
        coverHandler.ifPresent(e -> tag.put(Ref.KEY_MACHINE_COVER, e.serializeNBT()));
        fluidHandler.ifPresent(e -> tag.put(Ref.KEY_MACHINE_FLUIDS, e.serializeNBT()));
        recipeHandler.ifPresent(e -> tag.put(Ref.KEY_MACHINE_RECIPE, e.serializeNBT()));
        return tag;
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        coverHandler.ifPresent(e -> tag.put(Ref.KEY_MACHINE_COVER, e.serializeNBT()));
        tag.putString(Ref.KEY_MACHINE_TIER, getMachineTier().getId());
        tag.putInt(Ref.KEY_MACHINE_STATE, machineState.ordinal());
        return tag;
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        info.add("Machine: " + getMachineType().getId() + " Tier: " + getMachineTier().getId());
        info.add("State: " + getMachineState().getId());
        String slots = "";
        if (getMachineType().has(ITEM)) {
            int inputs = getMachineType().getSlots(SlotType.IT_IN, getMachineTier()).size();
            int outputs = getMachineType().getSlots(SlotType.IT_OUT, getMachineTier()).size();
            if (inputs > 0) slots += (" IT_IN: " + inputs + ",");
            if (outputs > 0) slots += (" IT_OUT: " + outputs + ",");
        }
        if (getMachineType().has(FLUID) && getMachineType().has(GUI)) {
            int inputs = getMachineType().getSlots(SlotType.FL_IN, getMachineTier()).size();
            int outputs = getMachineType().getSlots(SlotType.FL_OUT, getMachineTier()).size();
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
    public ResourceLocation getModel(String type, Direction dir, Direction facing) {
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