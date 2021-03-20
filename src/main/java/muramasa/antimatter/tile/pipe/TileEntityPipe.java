package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.capability.pipe.PipeCoverHandler;
import muramasa.antimatter.cover.BaseCover;
import muramasa.antimatter.cover.CoverStack;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.pipe.PipeCache;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.pipe.types.PipeType;
import muramasa.antimatter.tile.TileEntityBase;
import muramasa.antimatter.tile.TileEntityTickable;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import tesseract.graph.Connectivity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class TileEntityPipe extends TileEntityBase {

    /** Pipe Data **/
    protected PipeType<?> type;
    protected PipeSize size;

    /** Capabilities **/
    public final LazyOptional<PipeCoverHandler<?>> coverHandler;

    ///** Tesseract **/
    //private Direction direction; // when cap not initialized yet, it will help to store preset direction

    /** Connection data **/
    private byte connection, interaction;

    public TileEntityPipe(PipeType<?> type) {
        super(type.getTileType());
        this.type = type;
        this.coverHandler = LazyOptional.of(() -> new PipeCoverHandler<>(this));
    }

    //TODO: what does this do
    @Override
    public void onLoad() {
        CoverStack<?>[] covers = this.getAllCovers();
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
        }
    }

    @Override
    public void onRemove() {
        coverHandler.ifPresent(PipeCoverHandler::onRemove);
        for (Direction side : Ref.DIRS) {
            if (Connectivity.has(interaction, side.getIndex())) {
                TileEntity neighbor = Utils.getTile(this.getWorld(), this.getPos().offset(side));
                if (Utils.isForeignTile(neighbor)) { // Check that entity is not GT one
                    PipeCache.remove(this.getPipeType(), this.getWorld(), side, neighbor);
                }
            }
        }
    }

    public PipeType<?> getPipeType() {
        return type != null ? type : (type = ((BlockPipe<?>) getBlockState().getBlock()).getType());
    }

    public PipeSize getPipeSize() { //TODO need to store? when getBlockState is cached?
        return size != null ? size : (size = ((BlockPipe<?>) getBlockState().getBlock()).getSize());
    }

    public void setConnection(Direction side) {
        connection = Connectivity.set(connection, side.getIndex());
        refreshConnection();
    }

    public void toggleConnection(Direction side) {
        connection = Connectivity.toggle(connection, side.getIndex());
        refreshConnection();
    }

    public void clearConnection(Direction side) {
        connection = Connectivity.clear(connection, side.getIndex());
        refreshConnection();
    }

    public void setInteract(Direction side) {
        interaction = Connectivity.set(interaction, side.getIndex());
        refreshConnection();
    }

    public void toggleInteract(Direction side) {
        interaction = Connectivity.toggle(interaction, side.getIndex());
        refreshConnection();
    }

    public void clearInteract(Direction side) {
        interaction = Connectivity.clear(interaction, side.getIndex());
        refreshConnection();
    }

    public boolean canConnect(int side) {
        return Connectivity.has(connection, side);
    }

    public void changeConnection(Direction side) {
        TileEntity neighbor = Utils.getTile(this.getWorld(), this.getPos().offset(side));
        if (Utils.isForeignTile(neighbor)) {
            setInteract(side);
            PipeCache.update(this.getPipeType(), this.getWorld(), side, neighbor, this.getCover(side).getCover());
        } else {
            clearInteract(side);
        }
    }

    public void refreshConnection() {
        sidedSync(true);
    }

    public ICover[] getValidCovers() {
        return AntimatterAPI.all(ICover.class).toArray(new ICover[0]);
    }

    public CoverStack<?>[] getAllCovers() {
        return coverHandler.map(CoverHandler::getAll).orElse(new CoverStack[0]);
    }

    public CoverStack<?> getCover(Direction side) {
        return coverHandler.map(h -> h.get(side)).orElse(null);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == AntimatterCaps.COVERABLE_HANDLER_CAPABILITY) return coverHandler.map(h -> !h.get(side).isEmpty()).orElse(false) ? coverHandler.cast() : super.getCapability(cap, side);
        return LazyOptional.empty();
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag); //TODO get tile data tag
        coverHandler.ifPresent(h -> tag.put(Ref.KEY_PIPE_TILE_COVER, h.serializeNBT()));
        interaction = tag.getByte(Ref.TAG_PIPE_TILE_INTERACT);
        byte oldConnection = connection;
        connection = tag.getByte(Ref.TAG_PIPE_TILE_CONNECTIVITY);
        if (connection != oldConnection && (world != null && world.isRemote)) {
            Utils.markTileForRenderUpdate(this);
        }
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);
        if (tag.contains(Ref.KEY_PIPE_TILE_COVER)) coverHandler.ifPresent(t -> t.deserializeNBT(tag.getCompound(Ref.KEY_PIPE_TILE_COVER)));
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
