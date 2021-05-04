package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.capability.pipe.PipeCoverHandler;
import muramasa.antimatter.cover.CoverStack;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.pipe.types.PipeType;
import muramasa.antimatter.tile.TileEntityBase;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import tesseract.graph.Connectivity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

public abstract class TileEntityPipe extends TileEntityBase implements IMachineHandler {

    /** Pipe Data **/
    protected PipeType<?> type;
    protected PipeSize size;

    /** Capabilities **/
    public final LazyOptional<PipeCoverHandler<?>> coverHandler;

    ///** Tesseract **/
    //private Direction direction; // when cap not initialized yet, it will help to store preset direction

    /** Connection data **/
    private byte connection, interaction;

    /** CAPABILITIES **/
    protected LazyOptional<?>[] SIDE_CAPS;

    public TileEntityPipe(PipeType<?> type) {
        super(type.getTileType());
        this.type = type;
        this.coverHandler = LazyOptional.of(() -> new PipeCoverHandler<>(this));
        SIDE_CAPS = Arrays.stream(Ref.DIRS).map(t -> buildCapForSide(t)).toArray(LazyOptional[]::new);
    }

    protected abstract void registerNode(BlockPos pos, Direction side, boolean remove);

    @Override
    public void onLoad() {
        if (isServerSide()) initTesseract();
        /*CoverStack<?>[] covers = this.getAllCovers();
        if (covers.length == 0) return;
        for (Direction side : Ref.DIRS) {
            if (Connectivity.has(interaction, side.getIndex())) {
                TileEntity neighbor = Utils.getTile(this.getWorld(), this.getPos().offset(side));
                if (Utils.isForeignTile(neighbor)) { // Check that entity is not GT one
                    PipeCache.update(this.getPipeType(), this.getWorld(), side, neighbor, covers[side.getIndex()].getCover());
                } else {
                    interaction = Connectivity.clear(interaction, side.getIndex());
                }
            }
        }*/
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
        coverHandler.invalidate();
        if (isServerSide()) {
            for (Direction side : Ref.DIRS) {
                if (Connectivity.has(interaction, side.getIndex())) {
                    registerNode(this.getPos().offset(side), side, true);
                }
            }
            if (SIDE_CAPS != null) {
                for (LazyOptional<?> side_cap : SIDE_CAPS) {
                    side_cap.invalidate();
                }
            }
        }
    }

    public PipeType<?> getPipeType() {
        if (type == null) {
            type = getPipeType(getBlockState());
        }
        return type;
    }

    private PipeType<?> getPipeType(BlockState state) {
        return (((BlockPipe<?>) state.getBlock()).getType());
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
        SIDE_CAPS[side.getIndex()].invalidate();
        SIDE_CAPS[side.getIndex()] = buildCapForSide(side);
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
            if (!(tile instanceof TileEntityPipe)) {
                clearConnection(side);
                clearInteract(side);
                if (tile != null && validateTile(tile, side.getOpposite())) {
                    setConnection(side);
                    setInteract(side);
                }
            }
        }
    }

    protected abstract LazyOptional<?> buildCapForSide(Direction side);

    protected abstract Capability<?> getCapability();

    public void refreshConnection() {
        sidedSync(true);
    }


    public void onCoverUpdate(boolean remove, boolean hasNonEmpty, Direction side, CoverStack<? extends TileEntityPipe> old, CoverStack<? extends TileEntityPipe> stack) {
        if (stack.getCover().blocksCapability(stack, getCapability(), side)) {
            this.clearConnection(side);
            this.clearInteract(side);
        }
        if (this instanceof ITickablePipe) {
            if (remove && !hasNonEmpty) {
                world.setBlockState(getPos(), getBlockState().with(BlockPipe.COVERED, false), 11);
                TileEntityPipe pipe = (TileEntityPipe) world.getTileEntity(getPos());
                if (pipe != this) {
                    pipe.read(pipe.getBlockState(), this.write(new CompoundNBT()));
                }
            }
        } else if (!remove && hasNonEmpty) {
            //set this to be covered.
            world.setBlockState(getPos(), getBlockState().with(BlockPipe.COVERED, true), 11);
            TileEntityPipe pipe = (TileEntityPipe) world.getTileEntity(getPos());
            if (pipe != this) {
                pipe.read(pipe.getBlockState(), this.write(new CompoundNBT()));
            }
        }
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
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (side == null) return LazyOptional.empty();
        if (cap == AntimatterCaps.COVERABLE_HANDLER_CAPABILITY && coverHandler.isPresent()) return coverHandler.cast();
        if (!this.canConnect(side.getIndex())) return LazyOptional.empty();
        if (cap == getCapability()) {
            return SIDE_CAPS[side.getIndex()].cast();
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
}
