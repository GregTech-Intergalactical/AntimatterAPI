package muramasa.antimatter.blockentity.pipe;

import lombok.Getter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.blockentity.BlockEntityTickable;
import muramasa.antimatter.capability.*;
import muramasa.antimatter.capability.pipe.PipeCoverHandler;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.gui.widget.BackgroundWidget;
import muramasa.antimatter.network.packets.AbstractGuiEventPacket;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.pipe.TileTicker;
import muramasa.antimatter.pipe.types.PipeType;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import tesseract.api.IConnectable;
import tesseract.graph.Connectivity;

import java.util.List;
import java.util.Optional;

public abstract class BlockEntityPipe<T extends PipeType<T>> extends BlockEntityTickable<BlockEntityPipe<T>> implements IMachineHandler, MenuProvider, IGuiHandler, IConnectable, ICoverHandlerProvider<BlockEntityPipe<?>> {

    /**
     * Pipe Data
     **/
    protected T type;
    protected PipeSize size;

    /**
     * Capabilities
     **/
    public final Optional<PipeCoverHandler<?>> coverHandler;

    ///** Tesseract **/
    //private Direction direction; // when cap not initialized yet, it will help to store preset direction

    /**
     * Connection data
     **/
    protected byte connection, virtualConnection;
    private boolean refreshConnection = false;

    @Getter
    protected Holder pipeCapHolder;

    public BlockEntityPipe(T type, BlockPos pos, BlockState state) {
        super(type.getTileType(), pos, state);
        this.size = getPipeSize(state);
        this.type = getPipeType(state);
        this.coverHandler = Optional.of(new PipeCoverHandler<>(this));
        this.pipeCapHolder = new Holder<>(getCapClass(), this.dispatch);
    }

    @Override
    public String handlerDomain() {
        return getPipeType().domain;
    }

    @Override
    public GuiData getGui() {
        return null;
    }

    //@Override
    public void onLoad() {
        if (isServerSide()) {
            for (Direction facing : Ref.DIRS){
                if (connects(facing)) {
                    BlockEntityPipe<?> pipe = getPipe(facing);
                    if (Connectivity.has(virtualConnection, facing.get3DDataValue())){
                        if (!validate(facing) && pipe == null){
                            virtualConnection = Connectivity.clear(virtualConnection, facing.get3DDataValue());
                            refreshConnection();
                        }
                    } else {
                        if (validate(facing) || pipe != null){
                            virtualConnection = Connectivity.set(virtualConnection, facing.get3DDataValue());
                            refreshConnection();
                        }
                    }
                }
            }
            register();
        }
    }

    public boolean isConnector() {
        return !this.getBlockState().getValue(BlockPipe.TICKING) || this.coverHandler.map(p -> {
            for (ICover cover : p.getAll()) {
                if (cover.isNode()){
                    return false;
                }
            }
            return true;
        }).orElse(true);
    }

    boolean blockUpdating = false;

    public void onBlockUpdate(BlockPos neighbor) {
        super.onBlockUpdate(neighbor);
        Direction facing = Utils.getOffsetFacing(this.getBlockPos(), neighbor);
        if (!blockUpdating && level != null && level.isLoaded(this.getBlockPos()) && facing != null && canConnect(facing.get3DDataValue())){
            blockUpdating = true;
            BlockEntityPipe<?> pipe = getPipe(neighbor);
            if (Connectivity.has(virtualConnection, facing.get3DDataValue())){
                if (!validate(facing) && pipe == null){
                    virtualConnection = Connectivity.clear(virtualConnection, facing.get3DDataValue());
                    refreshConnection();
                }
            } else {
                if (validate(facing) || pipe != null){
                    virtualConnection = Connectivity.set(virtualConnection, facing.get3DDataValue());
                    refreshConnection();
                }
            }
            blockUpdating = false;
        }
        coverHandler.ifPresent(h -> h.get(facing).onBlockUpdate());
        coverHandler.ifPresent(h -> h.getCovers().forEach((d, c) -> c.onBlockUpdateAllSides()));
    }

    @Override
    public void onRemove() {
        coverHandler.ifPresent(PipeCoverHandler::onRemove);
        if (isServerSide()) {
            dispatch.invalidate();
            deregisterTesseract();
        }
    }

    public T getPipeType() {
        if (type == null) {
            type = getPipeType(getBlockState());
        }
        return type;
    }

    @SuppressWarnings("unchecked")
    private T getPipeType(BlockState state) {
        return (((BlockPipe<T>) state.getBlock()).getType());
    }

    public PipeSize getPipeSize() {
        if (size == null) {
            size = getPipeSize(getBlockState());
        }
        return size;
    }

    private PipeSize getPipeSize(BlockState state) {
        return ((BlockPipe<?>) state.getBlock()).getSize();
    }

    public BlockEntityPipe<?> getPipe(Direction side) {
        return getPipe(getBlockPos().relative(side));
    }

    public BlockEntityPipe<?> getPipe(BlockPos side) {
        Direction dir = Utils.getOffsetFacing(this.getBlockPos(), side);
        if (dir == null) return null;
        BlockEntity tile = getCachedBlockEntity(dir);
        if (!(tile instanceof BlockEntityPipe<?> pipe)) return null;
        return pipe.getCapClass() == this.getCapClass() ?  pipe : null;
    }

    public void toggleConnection(Direction side) {
        if (connects(side)) {
            clearConnection(side);
        } else {
            setConnection(side);
        }
    }

    public void setConnection(Direction side) {
        if (connects(side)) return;
        if (blocksSide(side)) return;
        BlockEntityPipe<?> pipe = getPipe(side);
        //If it is a tile but invalid do not connect.
        connection = Connectivity.set(connection, side.get3DDataValue());
        boolean ok = validate(side) || pipe != null;
        if (ok){
            virtualConnection = Connectivity.set(virtualConnection, side.get3DDataValue());
        }
        /*if (!ok && (pipe == null) && level.getBlockState(worldPosition.relative(side)).hasBlockEntity()) {
            connection = Connectivity.clear(connection, side.get3DDataValue());
            return;
        }*/

        TileTicker.addTickFunction(this::refreshConnection);
        if (pipe != null) {
            pipe.setConnection(side.getOpposite());
        }
    }

    public void clearConnection(Direction side) {
        //If we don't check for connection pipes can cause stackoverflow!
        if (!connects(side)) return;
        connection = Connectivity.clear(connection, side.get3DDataValue());
        virtualConnection = Connectivity.clear(virtualConnection, side.get3DDataValue());
        dispatch.invalidate(side);
        TileTicker.addTickFunction(this::refreshConnection);
        BlockEntityPipe<?> pipe = getPipe(side);
        if (pipe != null) {
            pipe.clearConnection(side.getOpposite());
        }
    }

    public boolean canConnect(int side) {
        return Connectivity.has(connection, side);
    }

    public boolean canConnectVirtual(int side) {
        return Connectivity.has(virtualConnection, side);
    }

    public abstract Class<?> getCapClass();

    @SuppressWarnings("unchecked")
    public void refreshConnection() {
        sidedSync(true);

        if (isServerSide() && isConnector()) {
            deregisterTesseract();
            register();
        }
    }

    protected abstract void register();
    protected abstract boolean deregister();

    private boolean deregisterTesseract() {
        byte old = this.connection;
        this.connection = 0;
        boolean ok = deregister();
        this.connection = old;
        return ok;
    }


    @Override
    public boolean connects(Direction direction) {
        return Connectivity.has(connection, direction.get3DDataValue());
    }

    @Override
    public boolean validate(Direction dir) {
        if (!connects(dir)) return false;
        BlockState state = level.getBlockState(worldPosition.relative(dir));
        if (state.getBlock() instanceof BlockPipe && !state.getValue(BlockPipe.TICKING)) {
            return false;
        }
        return !blocksSide(dir);
    }

    public void addInventoryDrops(List<ItemStack> drops){

    }

    /**
     * Handles cover updates, to check if the tile has to be replaced.
     *
     * @param remove      if a cover was removed.
     * @param hasNonEmpty if there is at least one cover still present.
     * @param side        which side the cover was removed on.
     * @param old         the old coverstack, can be empty.
     * @param stack       the new coverstack, can be empty.
     * @return if the tile was updated.
     */
    public boolean onCoverUpdate(boolean remove, boolean hasNonEmpty, Direction side, ICover old, ICover stack) {
        if (stack.blocksCapability(getCapClass(), side)) {
            this.clearConnection(side);
        }
        if (this.getBlockState().getValue(BlockPipe.TICKING)) {
            if (remove && !hasNonEmpty) {
                toggleTickingState();
                return true;
            }
        } else if (!remove && hasNonEmpty) {
            toggleTickingState();
            return true;
        }
        return false;
    }

    private void toggleTickingState() {
        CompoundTag nbt = this.saveWithFullMetadata();
        var old = getBlockState();
        var new_s = getBlockState().setValue(BlockPipe.TICKING, !getBlockState().getValue(BlockPipe.TICKING));
        //no block update here
        level.setBlock(getBlockPos(), new_s, 10);
        BlockEntityPipe<?> pipe = (BlockEntityPipe<?>) level.getBlockEntity(getBlockPos());
        if (pipe != this && pipe != null) {
            pipe.load(nbt);
            if (pipe.isConnector()) {
                pipe.register();
            }
            level.sendBlockUpdated(getBlockPos(), old, new_s, 3);
        }
    }

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        coverHandler.ifPresent(CoverHandler::onFirstTick);
    }

    @Override
    protected void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);
        coverHandler.ifPresent(CoverHandler::onUpdate);
    }

    public CoverFactory[] getValidCovers() {
        return AntimatterAPI.all(CoverFactory.class).stream().filter(t -> !t.getIsValid().test(this)).toArray(CoverFactory[]::new);
    }

    public ICover[] getAllCovers() {
        return coverHandler.map(CoverHandler::getAll).orElse(new ICover[0]);
    }

    public ICover getCover(Direction side) {
        return coverHandler.map(h -> h.get(side)).orElse(null);
    }

    public boolean blocksSide(Direction side) {
        return coverHandler.map(t -> t.blocksCapability(getCapClass(), side) || t.get(side).blockConnection(side)).orElse(false);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(Ref.KEY_PIPE_TILE_COVER))
            coverHandler.ifPresent(t -> t.deserialize(tag.getCompound(Ref.KEY_PIPE_TILE_COVER)));
        byte newConnection = tag.getByte(Ref.TAG_PIPE_TILE_CONNECTIVITY);
        virtualConnection = tag.getByte(Ref.TAG_PIPE_TILE_VIRTUAL_CONNECTIVITY);
        if (newConnection != connection && (level != null && level.isClientSide)) {
            Utils.markTileForRenderUpdate(this);
        }
        if (connection != newConnection && level != null) {
            for (int i = 0; i < Ref.DIRS.length; i++) {
                boolean firstHas = Connectivity.has(connection, i);
                boolean secondHas = Connectivity.has(newConnection, i);
                boolean different = firstHas != secondHas;
                if (different) {
                    //If the incoming value has connection but not the old one, set it.
                    if (secondHas) {
                        setConnection(Ref.DIRS[i]);
                    } else {
                        //If we used to have a connection but don't anymore, cleari t.
                        clearConnection(Ref.DIRS[i]);
                    }
                }
            }
        } else if (level == null) {
            connection = tag.getByte(Ref.TAG_PIPE_TILE_CONNECTIVITY);
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        coverHandler.ifPresent(h -> tag.put(Ref.KEY_PIPE_TILE_COVER, h.serialize(new CompoundTag())));
        tag.putByte(Ref.TAG_PIPE_TILE_CONNECTIVITY, connection);
        tag.putByte(Ref.TAG_PIPE_TILE_VIRTUAL_CONNECTIVITY, virtualConnection);
    }

    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        this.saveAdditional(tag);
        return tag;
    }

    @Override
    public List<String> getInfo(boolean simple) {
        List<String> info = super.getInfo(simple);
        info.add("Pipe Type: " + getPipeType().getId());
        info.add("Pipe Size: " + getPipeSize().getId());
        info.add("Connection: " + connection);
        info.add("Virtual Connection: " + virtualConnection);
        return info;
    }

    @Override
    public boolean isRemote() {
        return this.getLevel().isClientSide;
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return new ResourceLocation(Ref.ID, "textures/gui/empty_multi.png");
    }

    @Override
    public AbstractGuiEventPacket createGuiPacket(IGuiEvent event) {
        return null;
    }

    @Override
    public Component getDisplayName() {
        return Utils.literal(this.type.getTypeName());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int p_createMenu_1_, Inventory p_createMenu_2_, Player p_createMenu_3_) {
        return Data.PIPE_MENU_HANDLER.menu(this, p_createMenu_2_, p_createMenu_1_);
    }

    @Override
    public void addWidgets(GuiInstance instance, IGuiElement parent) {
        //instance.addWidget(WidgetSupplier.build((a, b) -> TextWidget.build(((AntimatterContainerScreen<?>) b).getTitle().getString(), 4210752).build(a, b)).setPos(9, 5).clientSide());
        instance.addWidget(BackgroundWidget.build(instance.handler.getGuiTexture(), instance.handler.guiSize(), instance.handler.guiHeight()));
    }

    @Override
    public Optional<ICoverHandler<BlockEntityPipe<?>>> getCoverHandler() {
        return coverHandler.map(p -> (ICoverHandler<BlockEntityPipe<?>>) p);
    }
}
