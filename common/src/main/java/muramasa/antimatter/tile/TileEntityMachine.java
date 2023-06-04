package muramasa.antimatter.tile;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.EnergyHandler;
import muramasa.antimatter.capability.Holder;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.capability.machine.MachineCoverHandler;
import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.capability.machine.MachineRecipeHandler;
import muramasa.antimatter.client.SoundHelper;
import muramasa.antimatter.client.dynamic.DynamicTexturer;
import muramasa.antimatter.client.dynamic.DynamicTexturers;
import muramasa.antimatter.client.tesr.Caches;
import muramasa.antimatter.client.tesr.MachineTESR;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.SlotData;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.gui.event.SlotClickEvent;
import muramasa.antimatter.gui.widget.FluidSlotWidget;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.types.BasicMultiMachine;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.network.packets.AbstractGuiEventPacket;
import muramasa.antimatter.network.packets.TileGuiEventPacket;
import muramasa.antimatter.recipe.IRecipe;
import muramasa.antimatter.structure.StructureCache;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Cache;
import muramasa.antimatter.util.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import tesseract.api.gt.IEnergyHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Set;

import static muramasa.antimatter.gui.event.GuiEvents.FLUID_EJECT;
import static muramasa.antimatter.gui.event.GuiEvents.ITEM_EJECT;
import static muramasa.antimatter.machine.MachineFlag.*;
import static net.minecraft.world.level.block.Blocks.AIR;

public class TileEntityMachine<T extends TileEntityMachine<T>> extends TileEntityTickable<T> implements MenuProvider, IMachineHandler, IGuiHandler {

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

    protected long lastSoundTime;

    protected boolean muffled = false;

    @Environment(EnvType.CLIENT)
    public SoundInstance playingSound;

    /**
     * Handlers
     **/
   /* public LazyOptional<MachineItemHandler<?>> itemHandler;
    public LazyOptional<MachineFluidHandler<?>> fluidHandler;
    public LazyOptional<MachineEnergyHandler<U>> energyHandler;
    public LazyOptional<MachineRecipeHandler<U>> recipeHandler;
    public LazyOptional<MachineCoverHandler<TileEntityMachine>> coverHandler;*/

    public Holder<IItemHandler, MachineItemHandler<T>> itemHandler = new Holder<>(IItemHandler.class, dispatch);
    public Holder<IFluidHandler, MachineFluidHandler<T>> fluidHandler = new Holder<>(IFluidHandler.class, dispatch);
    public Holder<ICoverHandler<?>, MachineCoverHandler<T>> coverHandler = new Holder<>(ICoverHandler.class, dispatch, null);
    public Holder<IEnergyHandler, MachineEnergyHandler<T>> energyHandler = new Holder<>(IEnergyHandler.class, dispatch);
    public Holder<MachineRecipeHandler<?>, MachineRecipeHandler<T>> recipeHandler = new Holder<>(MachineRecipeHandler.class, dispatch, null);

    /**
     * Client related fields.
     **/
    public LazyLoadedValue<DynamicTexturer<Machine<?>, DynamicKey>> multiTexturer;
    public Cache<List<Caches.LiquidCache>> liquidCache;

    public TileEntityMachine(Machine<?> type, BlockPos pos, BlockState state) {
        super(type.getTileType(), pos, state);
        this.tier = ((BlockMachine) state.getBlock()).getTier();
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
        multiTexturer = new LazyLoadedValue<>(() -> new DynamicTexturer<>(DynamicTexturers.TILE_DYNAMIC_TEXTURER)); }

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
    public void onLoad() {
        super.onLoad();
        if (this.level.isClientSide) {
            liquidCache = new Cache<>(() -> MachineTESR.buildLiquids(this));
        }
    }

    protected void cacheInvalidate() {
        if (this.liquidCache != null) liquidCache.invalidate();
    }
    public String getDomain() {
        return getMachineType().getDomain();
    }

    @Override
    public boolean isRemote() {
        return level.isClientSide;
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
        lastSoundTime = 0;
    }

    //Called whenever a recipe is activated, might be the same as before (e.g. no new recipe).
    public void onMachineStarted(IRecipe r) {

    }

    public void onBlockUpdate(BlockPos neighbor) {
        Direction facing = Utils.getOffsetFacing(this.getBlockPos(), neighbor);
        coverHandler.ifPresent(h -> h.get(facing).onBlockUpdate());
    }


    @Override
    public void serverTick(Level level, BlockPos pos, BlockState state) {
        itemHandler.ifPresent(MachineItemHandler::onUpdate);
        energyHandler.ifPresent(MachineEnergyHandler::onUpdate);
        fluidHandler.ifPresent(MachineFluidHandler::onUpdate);
        coverHandler.ifPresent(MachineCoverHandler::onUpdate);
        this.recipeHandler.ifPresent(MachineRecipeHandler::onServerUpdate);

        if (false) {
            double d = Ref.RNG.nextDouble();
            if (d > 0.97D && this.level.isRainingAt(new BlockPos(this.worldPosition.getX(), this.worldPosition.getY() + 1, this.worldPosition.getZ()))) {
                if (this.energyHandler.map(t -> t.getEnergy() > 0).orElse(false))
                    Utils.createExplosion(this.level, worldPosition, 6.0F, Explosion.BlockInteraction.DESTROY);
            }
        }
    }

    @Override
    public void clientTick(Level level, BlockPos pos, BlockState state) {
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
        } else {
            if (level != null) SoundHelper.clear(level, worldPosition);
        }
    }

    protected void markDirty() {
        this.getLevel().getChunkAt(this.getBlockPos()).setUnsaved(true);
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        if (this.getLevel() != null && !this.getLevel().isClientSide) {
            coverHandler.ifPresent(c -> c.onMachineEvent(event, data));
            itemHandler.ifPresent(i -> i.onMachineEvent(event, data));
            energyHandler.ifPresent(e -> e.onMachineEvent(event, data));
            fluidHandler.ifPresent(f -> f.onMachineEvent(event, data));
            recipeHandler.ifPresent(r -> r.onMachineEvent(event, data));
            if (event instanceof ContentEvent && openContainers.size() > 0) {
                //openContainers.forEach(ContainerMachine::detectAndSendLiquidChanges);
            }
            markDirty();
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

    public boolean isMuffled() {
        return muffled;
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
        if (this.level == null) return Direction.SOUTH;
        BlockState state = getBlockState();
        if (state == AIR.defaultBlockState()) {
            return Direction.SOUTH;
        }
        if (getMachineType().allowVerticalFacing()) {
            return state.getValue(BlockStateProperties.FACING);
        }
        return state.getValue(BlockStateProperties.HORIZONTAL_FACING);
    }

    public boolean setFacing(Direction side) {
        if (side == getFacing() || (side.getAxis() == Direction.Axis.Y && !getMachineType().allowVerticalFacing()))
            return false;
        boolean isEmpty = coverHandler.map(ch -> ch.get(side).isEmpty()).orElse(true);
        if (isEmpty) {
            BlockState state = getBlockState();
            if (getMachineType().allowVerticalFacing()) {
                state = state.setValue(BlockStateProperties.FACING, side);
                if (side.getAxis() != Direction.Axis.Y) {
                    state = state.setValue(BlockMachine.HORIZONTAL_FACING, side);
                }
            } else {
                state = state.setValue(BlockStateProperties.HORIZONTAL_FACING, side);
            }
            getLevel().setBlockAndUpdate(getBlockPos(), state);
            invalidateCaps();

            return true;
        }
        return false;
    }

    protected boolean setFacing(Player player, Direction side) {
        boolean setFacing = setFacing(side);
        if (setFacing) player.playNotifySound(Ref.WRENCH, SoundSource.BLOCKS, 1.0f, 1.0f);
        return setFacing;
    }

    public boolean wrenchMachine(Player player, BlockHitResult res, boolean crouch) {
        if (crouch || getMachineType().getOutputCover() == ICover.emptyFactory) {
            //Machine has no output
            return setFacing(player, Utils.getInteractSide(res));
        }
        return setOutputFacing(player, Utils.getInteractSide(res));
    }

    @Override
    public void onGuiEvent(IGuiEvent event, Player player) {
        if (event.getFactory() == ITEM_EJECT || event.getFactory() == FLUID_EJECT) {
            coverHandler.ifPresent(ch -> {
                ch.get(ch.getOutputFacing()).onGuiEvent(event, player);
            });
        }
        if (event.getFactory() == SlotClickEvent.SLOT_CLICKED) {
            itemHandler.ifPresent(t -> {
               // ItemStack stack = player.get;
              //  Antimatter.LOGGER.info("packet got");
            });
        }
    }

    @Override
    public AbstractGuiEventPacket createGuiPacket(IGuiEvent event) {
        return new TileGuiEventPacket(event, getBlockPos());
    }

    @Override
    public String handlerDomain() {
        return getDomain();
    }

    public void setMuffled(boolean muffled) {
        this.muffled = muffled;
        sidedSync(true);
        if (this.muffled && level != null && level.isClientSide) SoundHelper.clear(level, this.getBlockPos());
    }

    // TODO: Fix
    public Direction getOutputFacing() {
        if (type.getOutputCover() != null && !(type.getOutputCover() == ICover.emptyFactory) && coverHandler.isPresent()) {
            Direction dir = coverHandler.get().getOutputFacing();
            return dir == null ? getFacing().getOpposite() : dir;
        }
        return null;
    }

    public boolean setOutputFacing(Player player, Direction side) {
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

    public long getMaxInputVoltage() {
        return energyHandler.map(EnergyHandler::getInputVoltage).orElse(0L);
    }

    public long getMaxOutputVoltage() {
        return energyHandler.map(EnergyHandler::getOutputVoltage).orElse(0L);
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
        if (!has(GENERATOR)) recipeHandler.ifPresent(MachineRecipeHandler::resetProgress);
        if (level != null && level.isClientSide) SoundHelper.clear(level, this.getBlockPos());
        setMachineState(MachineState.DISABLED);
    }

    public void setMachineState(MachineState newState) {
        if (this.machineState != newState) {
            MachineState old = this.machineState;
            this.machineState = newState;
            if (level != null) {
                setMachineStateBlockState(machineState);
                sidedSync(true);
                if (!level.isClientSide) {
                    if (old == MachineState.ACTIVE) {
                        this.onMachineStop();
                    } else if (newState == MachineState.ACTIVE) {
                        if (recipeHandler.isPresent()) {
                            MachineRecipeHandler<?> handler = recipeHandler.get();
                            this.onMachineStarted(handler.getActiveRecipe());
                        }
                    }
                } else {
                    cacheInvalidate();
                }
            }
            setChanged();
            if (this.level != null && this.level.isClientSide && this.getMachineType().machineNoise != null) {
                if (newState == MachineState.ACTIVE) {
                    if (!muffled) SoundHelper.startLoop(this.type, level, this.getBlockPos());
                } else if (old == MachineState.ACTIVE) {
                    SoundHelper.clear(level, this.getBlockPos());
                }
            }
        }
    }

    protected void setMachineStateBlockState(MachineState newState){
        BlockState state = getBlockState();
        if (newState == MachineState.ACTIVE || newState == MachineState.IDLE){
            state = state.setValue(BlockMachine.MACHINE_STATE, newState);
            getLevel().setBlockAndUpdate(getBlockPos(), state);
        }
    }

    public CoverFactory[] getValidCovers() { 
        return AntimatterAPI.all(CoverFactory.class).toArray(new CoverFactory[0]);
    }

    public ICover getCover(Direction side) {
        return coverHandler.map(h -> h.get(side)).orElse(ICover.empty);
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        ModelDataMap.Builder builder = new ModelDataMap.Builder();
        if (this.getMachineType() instanceof BasicMultiMachine) return builder.build();
        TileEntityBasicMultiMachine mTile = StructureCache.getAnyMulti(this.getLevel(), worldPosition, TileEntityBasicMultiMachine.class);
        if (mTile != null) {
            builder.withInitial(AntimatterProperties.MULTI_TEXTURE_PROPERTY, a -> {
                Texture[] tex = mTile.getMachineType().getBaseTexture(mTile.getMachineTier());
                if (tex.length == 1) return tex[0];
                return tex[a.get3DDataValue()];
            });
        }

        return builder.build();
    }

    public InteractionResult onInteractBoth(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, @Nullable AntimatterToolType type) {
        //DEFAULT
        return isServerSide() ? onInteractServer(state, world, pos, player, hand, hit, type) : onInteractClient(state, world, pos, player, hand, hit, type);
    }

    public InteractionResult onInteractServer(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, @Nullable AntimatterToolType type){
        return InteractionResult.PASS;
    }

    public InteractionResult onInteractClient(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit, @Nullable AntimatterToolType type){
        return InteractionResult.PASS;
    }

    @Nonnull
    @Override
    public Component getDisplayName() {
        return getMachineType().getDisplayName(getMachineTier());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, @Nonnull Inventory inv, @Nonnull Player player) {
        return getMachineType().has(GUI) ? getMachineType().getGui().getMenuHandler().menu(this, inv, windowId) : null;
    }

    public boolean canPlayerOpenGui(Player playerEntity) {
        return true;
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

    public void invalidateCap(Class<?> cap) {
        if (isServerSide()) {
            dispatch.invalidate(cap);
        }
    }

    public <V> boolean blocksCapability(@Nonnull Class<V> cap, Direction side) {
        return coverHandler.map(t -> t.blocksCapability(cap, side)).orElse(false);
    }

    public final boolean allowsFrontIO() {
        return getMachineType().allowsFrontIO();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.tier = AntimatterAPI.get(Tier.class, tag.getString(Ref.KEY_MACHINE_TIER));

        setMachineState(MachineState.VALUES[tag.getInt(Ref.KEY_MACHINE_STATE)]);
        if (tag.contains(Ref.KEY_MACHINE_MUFFLED)) {
            setMuffled(tag.getBoolean(Ref.KEY_MACHINE_MUFFLED));
        }
        if (tag.contains(Ref.KEY_MACHINE_STATE_D)) {
            disabledState = MachineState.VALUES[tag.getInt(Ref.KEY_MACHINE_STATE_D)];
        }
        if (tag.contains(Ref.KEY_MACHINE_ITEMS))
            itemHandler.ifPresent(i -> i.deserializeNBT(tag.getCompound(Ref.KEY_MACHINE_ITEMS)));
        if (tag.contains(Ref.KEY_MACHINE_ENERGY))
            energyHandler.ifPresent(e -> e.deserialize(tag.getCompound(Ref.KEY_MACHINE_ENERGY)));
        if (tag.contains(Ref.KEY_MACHINE_COVER))
            coverHandler.ifPresent(e -> e.deserializeNBT(tag.getCompound(Ref.KEY_MACHINE_COVER)));
        if (tag.contains(Ref.KEY_MACHINE_FLUIDS)) {
            fluidHandler.ifPresent(e -> e.deserializeNBT(tag.getCompound(Ref.KEY_MACHINE_FLUIDS)));
            if (level != null && level.isClientSide) {
                cacheInvalidate();
            }

        }
        if (tag.contains(Ref.KEY_MACHINE_RECIPE))
            recipeHandler.ifPresent(e -> e.deserializeNBT(tag.getCompound(Ref.KEY_MACHINE_RECIPE)));
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putString(Ref.KEY_MACHINE_TIER, getMachineTier().getId());
        tag.putInt(Ref.KEY_MACHINE_STATE, machineState.ordinal());
        tag.putBoolean(Ref.KEY_MACHINE_MUFFLED, muffled);
        if (disabledState != null)
            tag.putInt(Ref.KEY_MACHINE_STATE_D, disabledState.ordinal());
        itemHandler.ifPresent(i -> tag.put(Ref.KEY_MACHINE_ITEMS, i.serializeNBT()));
        energyHandler.ifPresent(e -> tag.put(Ref.KEY_MACHINE_ENERGY, e.serialize(new CompoundTag())));
        coverHandler.ifPresent(e -> tag.put(Ref.KEY_MACHINE_COVER , e.serializeNBT()));
        fluidHandler.ifPresent(e -> tag.put(Ref.KEY_MACHINE_FLUIDS, e.serializeNBT()));
        recipeHandler.ifPresent(e -> tag.put(Ref.KEY_MACHINE_RECIPE, e.serializeNBT()));
    }

    @Nonnull
    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        coverHandler.ifPresent(e -> tag.put(Ref.KEY_MACHINE_COVER, e.serializeNBT()));
        if (this.getMachineType().renderContainerLiquids()) {
            fluidHandler.ifPresent(e -> tag.put(Ref.KEY_MACHINE_FLUIDS, e.serializeNBT()));
        }
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
        energyHandler.ifPresent(h -> info.add("Energy: " + h.getEnergy() + " / " + h.getCapacity()));
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