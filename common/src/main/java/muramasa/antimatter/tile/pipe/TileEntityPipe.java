package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.capability.Holder;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.capability.pipe.PipeCoverHandler;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.gui.widget.BackgroundWidget;
import muramasa.antimatter.network.packets.AbstractGuiEventPacket;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.pipe.types.PipeType;
import muramasa.antimatter.tile.TileEntityTickable;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import tesseract.TesseractPlatformUtils;
import tesseract.api.IConnectable;
import tesseract.graph.Connectivity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public abstract class TileEntityPipe<T extends PipeType<T>> extends TileEntityTickable<TileEntityPipe<T>> implements IMachineHandler, MenuProvider, IGuiHandler, IConnectable {

    /**
     * Pipe Data
     **/
    protected T type;
    protected PipeSize size;

    /**
     * Capabilities
     **/
    public final LazyOptional<PipeCoverHandler<?>> coverHandler;

    ///** Tesseract **/
    //private Direction direction; // when cap not initialized yet, it will help to store preset direction

    /**
     * Connection data
     **/
    private byte connection;

    protected Holder pipeCapHolder;

    public TileEntityPipe(T type, BlockPos pos, BlockState state) {
        super(type.getTileType(), pos, state);
        this.size = getPipeSize(state);
        this.type = getPipeType(state);
        this.coverHandler = LazyOptional.of(() -> new PipeCoverHandler<>(this));
        this.pipeCapHolder = new Holder<>(getCapClass(), this.dispatch);
    }

    @Override
    public String handlerDomain() {
        return getPipeType().domain;
    }

    //@Override
    public void onLoad() {
        if (isServerSide()) {
            register();
        }
    }

    public Holder getPipeCapHolder() {
        return pipeCapHolder;
    }

    public boolean isConnector() {
        return !this.getBlockState().getValue(BlockPipe.TICKING);
    }

    public void onBlockUpdate(BlockPos neighbor) {
        Direction facing = Utils.getOffsetFacing(this.getBlockPos(), neighbor);
        coverHandler.ifPresent(h -> h.get(facing).onBlockUpdate());
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

    public TileEntityPipe<?> getPipe(Direction side) {
        return getPipe(getBlockPos().relative(side));
    }

    public TileEntityPipe<?> getPipe(BlockPos side) {
        BlockEntity tile = getLevel().getBlockEntity(side);
        if (!(tile instanceof TileEntityPipe)) return null;
        TileEntityPipe<?> pipe = (TileEntityPipe<?>) tile;
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
        TileEntityPipe<?>  pipe = getPipe(side);
        //If it is a tile but invalid do not connect.
        connection = Connectivity.set(connection, side.get3DDataValue());
        boolean ok = validate(side);
        if (!ok && pipe == null && level.getBlockState(worldPosition.relative(side)).hasBlockEntity()) {
            connection = Connectivity.clear(connection, side.get3DDataValue());
            return;
        }

        refreshConnection();
        if (pipe != null) {
            pipe.setConnection(side.getOpposite());
        }
    }

    public void clearConnection(Direction side) {
        //If we don't check for connection pipes can cause stackoverflow!
        if (!connects(side)) return;
        connection = Connectivity.clear(connection, side.get3DDataValue());
        dispatch.invalidate(side);
        refreshConnection();
        TileEntityPipe<?>  pipe = getPipe(side);
        if (pipe != null) {
            pipe.clearConnection(side.getOpposite());
        }
    }

    public boolean canConnect(int side) {
        return Connectivity.has(connection, side);
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
        TileEntityPipe<?> pipe = (TileEntityPipe<?>) level.getBlockEntity(getBlockPos());
        if (pipe != this && pipe != null) {
            pipe.load(nbt);
            if (pipe.isConnector()) {
                pipe.register();
            }
            level.sendBlockUpdated(getBlockPos(), old, new_s, 3);
        }
    }

    @Override
    protected void serverTick(Level level, BlockPos pos, BlockState state) {
        super.serverTick(level, pos, state);
        coverHandler.ifPresent(CoverHandler::onUpdate);
    }

    public CoverFactory[] getValidCovers() {
        return AntimatterAPI.all(CoverFactory.class).stream().filter(t -> {
            try {
                return !t.get().get(ICoverHandler.empty(this), t.getValidTier(), Direction.SOUTH, t).blocksCapability(getCapClass(), null);
            } catch (Exception ex) {
                return false;
            }
        }).toArray(CoverFactory[]::new);
    }

    public ICover[] getAllCovers() {
        return coverHandler.map(CoverHandler::getAll).orElse(new ICover[0]);
    }

    public ICover getCover(Direction side) {
        return coverHandler.map(h -> h.get(side)).orElse(null);
    }

    public boolean blocksSide(Direction side) {
        return coverHandler.map(t -> t.blocksCapability(getCapClass(), side)).orElse(false);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains(Ref.KEY_PIPE_TILE_COVER))
            coverHandler.ifPresent(t -> t.deserializeNBT(tag.getCompound(Ref.KEY_PIPE_TILE_COVER)));
        byte newConnection = tag.getByte(Ref.TAG_PIPE_TILE_CONNECTIVITY);
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
        coverHandler.ifPresent(h -> tag.put(Ref.KEY_PIPE_TILE_COVER, h.serializeNBT()));
        tag.putByte(Ref.TAG_PIPE_TILE_CONNECTIVITY, connection);
    }

    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        this.saveAdditional(tag);
        return tag;
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        info.add("Pipe Type: " + getPipeType().getId());
        info.add("Pipe Size: " + getPipeSize().getId());
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
        return new TextComponent(this.type.getTypeName());
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
}
