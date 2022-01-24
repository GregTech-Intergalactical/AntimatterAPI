package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.*;
import muramasa.antimatter.capability.pipe.PipeCoverHandler;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.gui.screen.AntimatterContainerScreen;
import muramasa.antimatter.gui.widget.BackgroundWidget;
import muramasa.antimatter.gui.widget.TextWidget;
import muramasa.antimatter.gui.widget.WidgetSupplier;
import muramasa.antimatter.network.packets.AbstractGuiEventPacket;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.pipe.types.PipeType;
import muramasa.antimatter.tile.TileEntityBase;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import tesseract.api.IConnectable;
import tesseract.graph.Connectivity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public abstract class TileEntityPipe<T extends PipeType<T>> extends TileEntityBase<TileEntityPipe<T>> implements IMachineHandler, INamedContainerProvider, IGuiHandler, IConnectable {

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

    public TileEntityPipe(T type, boolean covered) {
        super(covered ? type.getCoveredType() : type.getTileType());
        this.type = type;
        this.coverHandler = LazyOptional.of(() -> new PipeCoverHandler<>(this));
        this.pipeCapHolder = new Holder<>(getCapability(), this.dispatch);
    }

    @Override
    public String handlerDomain() {
        return getPipeType().domain;
    }

    @Override
    public void onLoad() {
        if (isServerSide()) {
            if (this.isConnector()) {
                register();
            } else if (this.level != null) {
                
            }
        }
    }

    public boolean isConnector() {
        return !(this instanceof ITickablePipe);
    }

    public void onBlockUpdate(BlockPos neighbor) {
        Direction facing = Utils.getOffsetFacing(this.getBlockPos(), neighbor);
        coverHandler.ifPresent(h -> h.get(facing).onBlockUpdate());
    }

    public void ofState(BlockState state) {
        this.size = getPipeSize(state);
        this.type = getPipeType(state);
    }

    @Override
    public void onRemove() {
        coverHandler.ifPresent(PipeCoverHandler::onRemove);
        if (isServerSide()) {
            dispatch.invalidate();
            if (isConnector()) {
                deregisterTesseract();
            }
        }
    }

    public T getPipeType() {
        if (type == null) {
            type = getPipeType(getBlockState());
        }
        return type;
    }

    public void modify(Direction incoming, Direction towards, Object object, boolean simulate) {
        this.coverHandler.ifPresent(t -> t.onTransfer(object, incoming, towards, simulate));
        /*if (object instanceof FluidStack && simulate) {
            ((FluidStack)object).setAmount(1);
        }
        Antimatter.LOGGER.info("Modify txn: " + object.getClass().getSimpleName() + " simulate: " + simulate);*/
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
        TileEntity tile = getLevel().getBlockEntity(side);
        if (!(tile instanceof TileEntityPipe)) return null;
        TileEntityPipe<?> pipe = (TileEntityPipe<?>) tile;
        return pipe.getCapability() == this.getCapability() ?  pipe : null;
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
        if (!ok && pipe == null && level.getBlockState(worldPosition.relative(side)).hasTileEntity()) {
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

    protected abstract Capability<?> getCapability();

    @SuppressWarnings("unchecked")
    public void refreshConnection() {
        sidedSync(true);

        if (isServerSide() && isConnector()) {
            if (deregisterTesseract()) {
                register();
            }
        }
    }

    protected abstract void register();
    protected abstract boolean deregister();

    private final boolean deregisterTesseract() {
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
        if (state.getBlock() instanceof BlockPipe && !state.getValue(BlockPipe.COVERED)) {
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
        if (stack.blocksCapability(getCapability(), side)) {
            this.clearConnection(side);
        }
        if (this instanceof ITickablePipe) {
            if (remove && !hasNonEmpty) {
                CompoundNBT nbt = this.save(new CompoundNBT());
                level.setBlock(getBlockPos(), getBlockState().setValue(BlockPipe.COVERED, false), 11);
                TileEntityPipe pipe = (TileEntityPipe) level.getBlockEntity(getBlockPos());
                if (pipe != this) {
                    pipe.load(pipe.getBlockState(), nbt);
                }
                return true;
            }
        } else if (!remove && hasNonEmpty) {
            //set this to be covered.
            CompoundNBT nbt = this.save(new CompoundNBT());
            level.setBlock(getBlockPos(), getBlockState().setValue(BlockPipe.COVERED, true), 11);
            TileEntityPipe pipe = (TileEntityPipe) level.getBlockEntity(getBlockPos());
            if (pipe != this) {
                pipe.load(pipe.getBlockState(), nbt);
            }
            return true;
        }
        return false;
    }

    public CoverFactory[] getValidCovers() {
        return AntimatterAPI.all(CoverFactory.class).stream().filter(t -> {
            try {
                return !t.get().get(ICoverHandler.empty(this), t.getValidTier(), Direction.SOUTH, t).blocksCapability(getCapability(), null);
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
        return coverHandler.map(t -> t.blocksCapability(getCapability(), side)).orElse(false);
    }

    @Nonnull
    @Override
    public <U> LazyOptional<U> getCapability(@Nonnull Capability<U> cap, @Nullable Direction side) {
        if (side == null) return LazyOptional.empty();
        if (cap == AntimatterCaps.COVERABLE_HANDLER_CAPABILITY && coverHandler.isPresent()) return coverHandler.cast();
        if (!this.connects(side)) return LazyOptional.empty();
        if (cap == getCapability()) {
            return pipeCapHolder.side(side).cast();
        }
        return LazyOptional.empty();
    }

    //For covers
    @Nonnull
    public <U> LazyOptional<U> getCoverCapability(@Nonnull Capability<U> cap, @Nullable Direction side) {
        if (side == null) return LazyOptional.empty();
        //if (!this.connects(side)) return LazyOptional.empty();
        if (cap == getCapability()) {
            return pipeCapHolder.side(null).cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public boolean path() {
        return coverHandler.map(t -> Arrays.stream(t.getAll()).mapToInt(c -> c.ticks() ? 1 : 0).sum() > 0).orElse(false);
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag); //TODO get tile data tag
        ofState(state);
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
        //E.g. replaced with cover or created from a create contraption. 
        //Make sure Tesseract is up to date.
        /*if (interaction != oldInteract && world != null && isServerSide())  {
            for (Direction dir : Ref.DIRS) {
                boolean firstHas = Connectivity.has(interaction, dir.getIndex());
                boolean secondHas = Connectivity.has(oldInteract, dir.getIndex());
                if (firstHas != secondHas) {
                    registerNode(pos.offset(dir), dir, !firstHas && secondHas);
                }
            }
        }*/
    }

    @Nonnull
    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        coverHandler.ifPresent(h -> tag.put(Ref.KEY_PIPE_TILE_COVER, h.serializeNBT()));
        tag.putByte(Ref.TAG_PIPE_TILE_CONNECTIVITY, connection);
        return tag;
    }

    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        this.save(tag);
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
    public ITextComponent getDisplayName() {
        return new StringTextComponent(this.type.getTypeName());
    }

    @Nullable
    @Override
    public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
        return Data.PIPE_MENU_HANDLER.menu(this, p_createMenu_2_, p_createMenu_1_);
    }

    @Override
    public void addWidgets(GuiInstance instance, IGuiElement parent) {
        instance.addWidget(WidgetSupplier.build((a, b) -> TextWidget.build(((AntimatterContainerScreen<?>) b).getTitle().getString(), 4210752).build(a, b)).setPos(9, 5).clientSide());
        instance.addWidget(BackgroundWidget.build(instance.handler.getGuiTexture(), instance.handler.guiSize(), instance.handler.guiHeight()));
    }
}
