package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.*;
import muramasa.antimatter.capability.pipe.PipeCoverHandler;
import muramasa.antimatter.cover.CoverNone;
import muramasa.antimatter.cover.CoverStack;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.gui.GuiInstance;
import muramasa.antimatter.gui.IGuiElement;
import muramasa.antimatter.gui.event.IGuiEvent;
import muramasa.antimatter.gui.widget.BackgroundWidget;
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
import java.util.List;

public abstract class TileEntityPipe<T extends PipeType<T>> extends TileEntityBase<TileEntityPipe<T>> implements IMachineHandler, INamedContainerProvider, IGuiHandler, IConnectable {

    /** Pipe Data **/
    protected T type;
    protected PipeSize size;

    /** Capabilities **/
    public final LazyOptional<PipeCoverHandler<?>> coverHandler;

    ///** Tesseract **/
    //private Direction direction; // when cap not initialized yet, it will help to store preset direction

    /** Connection data **/
    private byte connection, interaction;

    protected Holder pipeCapHolder;

    public TileEntityPipe(T type, boolean covered) {
        super(covered ? type.getCoveredType() : type.getTileType());
        this.type = type;
        this.coverHandler = LazyOptional.of(() -> new PipeCoverHandler<>(this));
        this.pipeCapHolder = new Holder<>(getCapability(), this.dispatch);
    }

    @Override
    public boolean interacts(Direction direction) {
        return Connectivity.has(interaction, direction.getIndex());
    }

    protected abstract void registerNode(BlockPos pos, Direction side, boolean remove);

    @Override
    public void onLoad() {
        if (isServerSide()) initTesseract();
    }

    public void onBlockUpdate(BlockPos neighbor){
        Direction facing = Utils.getOffsetFacing(this.getPos(), neighbor);
        coverHandler.ifPresent(h -> h.get(facing).onBlockUpdate(facing));
    }

    public void ofState(BlockState state) {
        this.size = getPipeSize(state);
        this.type = getPipeType(state);
    }

    protected void initTesseract() {
        for (Direction side : Ref.DIRS) {
            if (Connectivity.has(interaction, side.getIndex())) {
                registerNode(this.pos.offset(side), side, false);
            }
        }
    }

    public void onInvalidate(Direction side) {
        if (!Connectivity.has(interaction, side.getIndex())) return;
        clearInteract(side);
        TileEntity tile = this.world.getTileEntity(pos.offset(side));
        if (tile == null) return;
        if (this.validateTile(tile, side.getOpposite()) && !(tile instanceof TileEntityPipe)) {
            toggleInteract(side);
        }
    }

    public abstract boolean validateTile(TileEntity tile, Direction side);

    @Override
    public void onRemove() {
        coverHandler.ifPresent(PipeCoverHandler::onRemove);
        if (isServerSide()) {
            dispatch.invalidate();
            for (Direction side : Ref.DIRS) {
                if (Connectivity.has(interaction, side.getIndex())) {
                    registerNode(this.getPos().offset(side), side, true);
                }
            }
        }
    }

    public T getPipeType() {
        if (type == null) {
            type = getPipeType(getBlockState());
        }
        return type;
    }

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

    public void setConnection(Direction side) {
        if (blocksSide(side)) return;
        connection = Connectivity.set(connection, side.getIndex());
        refreshConnection();
    }

    public void toggleConnection(Direction side) {
        if (Connectivity.has(connection, side.getIndex())) {
            clearConnection(side);
        } else {
            setConnection(side);
        }
    }

    public void clearConnection(Direction side) {
        connection = Connectivity.clear(connection, side.getIndex());
        dispatch.invalidate(side);
        refreshConnection();
    }

    public void setInteract(Direction side) {
        if (blocksSide(side)) return;
        byte oldInteract = interaction;
        interaction = Connectivity.set(interaction, side.getIndex());
        if (isServerSide() && oldInteract != interaction)
            registerNode(this.pos.offset(side), side,false);
        refreshConnection();
    }

    public void toggleInteract(Direction side) {
        if (Connectivity.has(interaction, side.getIndex())) {
            clearInteract(side);
        } else {
            setInteract(side);
        }
    }

    public void clearInteract(Direction side) {
        byte oldInteract = interaction;
        interaction = Connectivity.clear(interaction, side.getIndex());
        if (isServerSide() && interaction != oldInteract)
            registerNode(this.pos.offset(side), side, true);
        refreshConnection();
    }

    public boolean canConnect(int side) {
        return Connectivity.has(connection, side);
    }

    public void refreshSide(Direction side) {
        if (this.canConnect(side.getIndex())) {
            BlockPos pos = this.pos.offset(side);
            TileEntity tile = world.getTileEntity(pos);
            if (tile != null) {
                if (!(tile instanceof TileEntityPipe)) {
                    clearConnection(side);
                    clearInteract(side);
                    if (validateTile(tile, side.getOpposite())) {
                        setConnection(side);
                        setInteract(side);
                    }
                }
            } else {
                clearInteract(side);
            }
        }
    }

    protected abstract Capability<?> getCapability();

    public void refreshConnection() {
        sidedSync(true);
    }

    @Override
    public boolean registerAsNode() {
        return this.coverHandler.map(t -> {
            for (CoverStack<?> coverStack : t.getAll()) {
                if (coverStack.getCover().ticks()) return true;
            }
            return false;
        }).orElse(false);
    }

    /**
     * Handles cover updates, to check if the tile has to be replaced.
     * @param remove if a cover was removed.
     * @param hasNonEmpty if there is at least one cover still present.
     * @param side which side the cover was removed on.
     * @param old the old coverstack, can be empty.
     * @param stack the new coverstack, can be empty.
     * @return if the tile was updated.
     */
    public boolean onCoverUpdate(boolean remove, boolean hasNonEmpty, Direction side, CoverStack<? extends TileEntityPipe> old, CoverStack<? extends TileEntityPipe> stack) {
        if (stack.getCover().blocksCapability(stack, getCapability(), side)) {
            this.clearConnection(side);
            this.clearInteract(side);
        }
        if (this instanceof ITickablePipe) {
            if (remove && !hasNonEmpty) {
                CompoundNBT nbt = this.write(new CompoundNBT());
                world.setBlockState(getPos(), getBlockState().with(BlockPipe.COVERED, false), 11);
                TileEntityPipe pipe = (TileEntityPipe) world.getTileEntity(getPos());
                if (pipe != this) {
                    pipe.read(pipe.getBlockState(), nbt);
                }
                return true;
            }
        } else if (!remove && hasNonEmpty) {
            //set this to be covered.
            CompoundNBT nbt = this.write(new CompoundNBT());
            world.setBlockState(getPos(), getBlockState().with(BlockPipe.COVERED, true), 11);
            TileEntityPipe pipe = (TileEntityPipe) world.getTileEntity(getPos());
            if (pipe != this) {
                pipe.read(pipe.getBlockState(), nbt);
            }
            return true;
        }
        return false;
    }

    public ICover[] getValidCovers() {
        return AntimatterAPI.all(ICover.class).stream().filter(t -> !t.blocksCapability(new CoverStack<>(t), getCapability(), null)).toArray(ICover[]::new);
    }

    public CoverStack<?>[] getAllCovers() {
        return coverHandler.map(CoverHandler::getAll).orElse(new CoverStack[0]);
    }

    public CoverStack<?> getCover(Direction side) {
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
        if (!this.canConnect(side.getIndex())) return LazyOptional.empty();
        if (cap == getCapability()) {
            return pipeCapHolder.side(side).cast();
        }
        return LazyOptional.empty();
    }

    //For covers
    @Nonnull
    public <U> LazyOptional<U> getCoverCapability(@Nonnull Capability<U> cap, @Nullable Direction side) {
        if (side == null) return LazyOptional.empty();
        if (!this.canConnect(side.getIndex())) return LazyOptional.empty();
        if (cap == getCapability()) {
            return pipeCapHolder.side(null).cast();
        }
        return LazyOptional.empty();
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag); //TODO get tile data tag
        ofState(state);
        if (tag.contains(Ref.KEY_PIPE_TILE_COVER)) coverHandler.ifPresent(t -> t.deserializeNBT(tag.getCompound(Ref.KEY_PIPE_TILE_COVER)));
        byte oldInteract = interaction;
        interaction = tag.getByte(Ref.TAG_PIPE_TILE_INTERACT);
        byte oldConnection = connection;
        connection = tag.getByte(Ref.TAG_PIPE_TILE_CONNECTIVITY);
        if (connection != oldConnection && (world != null && world.isRemote)) {
            Utils.markTileForRenderUpdate(this);
        }
        if (connection != oldConnection && world != null) {
            refreshConnection();
        }
        //E.g. replaced with cover or created from a create contraption. 
        //Make sure Tesseract is up to date.
        if (interaction != oldInteract && world != null && isServerSide())  {
            for (Direction dir : Ref.DIRS) {
                boolean firstHas = Connectivity.has(interaction, dir.getIndex());
                boolean secondHas = Connectivity.has(oldInteract, dir.getIndex());
                if (firstHas != secondHas) {
                    registerNode(pos.offset(dir), dir, !firstHas && secondHas);
                }
            }
        }
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);
        coverHandler.ifPresent(h -> tag.put(Ref.KEY_PIPE_TILE_COVER, h.serializeNBT()));
        tag.putByte(Ref.TAG_PIPE_TILE_INTERACT, interaction);
        tag.putByte(Ref.TAG_PIPE_TILE_CONNECTIVITY, connection);
        return tag;
    }

    public CompoundNBT getUpdateTag() {
        CompoundNBT tag = super.getUpdateTag();
        this.write(tag);
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
        return this.getWorld().isRemote;
    }

    @Override
    public ResourceLocation getGuiTexture() {
        return new ResourceLocation(Ref.ID, "textures/gui/empty_multi.png");
    }

    @Override
    public AbstractGuiEventPacket createGuiPacket(IGuiEvent event, int... data) {
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
        //instance.addWidget(WidgetSupplier.build((a,b) -> TextWidget.build(a.screen.getTitle().getString(), 4210752).setPos(10,10).build(a,b)).clientSide());
        instance.addWidget(BackgroundWidget.build(instance.handler.getGuiTexture(),  instance.handler.guiSize(), instance.handler.guiHeight()));
    }
}
