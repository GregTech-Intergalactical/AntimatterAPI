package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.impl.PipeConfigHandler;
import muramasa.antimatter.capability.impl.PipeCoverHandler;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.pipe.types.PipeType;
import muramasa.antimatter.tile.TileEntityBase;
import muramasa.antimatter.tile.TileEntityTickable;
import muramasa.antimatter.util.Utils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import tesseract.graph.Connectivity;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Optional;

public class TileEntityPipe extends TileEntityTickable {

    /** Pipe Data **/
    protected PipeType<?> type;
    protected PipeSize size;

    /** Capabilities **/
    public Optional<PipeCoverHandler> coverHandler = Optional.empty();
    public Optional<PipeConfigHandler> configHandler = Optional.empty();

    protected byte connection;

    public TileEntityPipe(TileEntityType<?> tileType) {
        super(tileType);
    }

    public TileEntityPipe(PipeType<?> type) {
        this(type.getTileType());
        this.type = type;
    }

    @Override
    public void onLoad() {
        if (!coverHandler.isPresent()) coverHandler = Optional.of(new PipeCoverHandler(this));
        if (!configHandler.isPresent()) configHandler = Optional.of(new PipeConfigHandler(this));
    }

    @Override
    public void onRemove() {
        for (Direction side : Ref.DIRECTIONS) {
            TileEntity neighbor = Utils.getTile(world, pos.offset(side));
            // Check that entity is not GT one
            if (neighbor != null && !(neighbor instanceof TileEntityBase)) {
                onNeighborRemove(neighbor, side.getOpposite());
            }
        }
    }

    public PipeType<?> getPipeType() {
        return type != null ? type : (type = ((BlockPipe<?>) getBlockState().getBlock()).getType());
    }

    public PipeSize getPipeSize() { //TODO need to store? when getBlockState is cached?
        return size != null ? size : (size = ((BlockPipe<?>) getBlockState().getBlock()).getSize());
    }

    public byte getConnection() {
        return connection;
    }

    public void setConnection(Direction side) {
        connection = Connectivity.set(connection, side.getIndex());
        refreshConnection();
    }

    public void toggleConnection(Direction side, boolean isTarget) {
        connection = Connectivity.toggle(connection, side.getIndex());
        refreshConnection();
        if (isTarget && isServerSide()) {
            TileEntity target = Utils.getTile(world, pos.offset(side));
            // Check that entity is not GT one
            if (target != null && !(target instanceof TileEntityBase)) {
                if (Connectivity.has(connection, side.getIndex())) {
                    onNeighborUpdate(target, side.getOpposite());
                } else {
                    onNeighborRemove(target, side.getOpposite());
                }
            }
        }
    }

    public void clearConnection(Direction side) {
        connection = Connectivity.clear(connection, side.getIndex());
        refreshConnection();
    }

    public void refreshConnection() {
        markForRenderUpdate();
    }

    public boolean canConnect(int side) {
        return Connectivity.has(connection, side);
    }

    public Cover[] getValidCovers() {
        return AntimatterAPI.getRegisteredCovers().toArray(new Cover[0]);
    }

    public Cover getCover(Direction side) {
        return coverHandler.map(h -> h.getCover(side)).orElse(Data.COVER_NONE);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        return cap == AntimatterCaps.CONFIGURABLE && configHandler.isPresent() ? LazyOptional.of(() -> configHandler.get()).cast() : super.getCapability(cap);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        return cap == AntimatterCaps.COVERABLE && coverHandler.map(h -> h.getCover(side).isEmpty()).orElse(false) ? LazyOptional.of(() -> coverHandler.get()).cast() : super.getCapability(cap, side);
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        if (tag.contains(Ref.KEY_PIPE_TILE_CONNECTIVITY)) connection = tag.getByte(Ref.KEY_PIPE_TILE_CONNECTIVITY);
        if (tag.contains(Ref.KEY_PIPE_TILE_COVER)) coverHandler.ifPresent(h -> h.deserialize(tag.getCompound(Ref.KEY_MACHINE_TILE_COVER)));
        refreshConnection();
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag); //TODO get tile data tag
        tag.putByte(Ref.KEY_PIPE_TILE_CONNECTIVITY, connection);
        coverHandler.ifPresent(h -> tag.put(Ref.KEY_PIPE_TILE_COVER, h.serialize()));
        return tag;
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        info.add("Pipe Type: " + getPipeType().getId());
        info.add("Pipe Size: " + getPipeSize().getId());
        return info;
    }

    /**
     * Called when block is placed to init inputs to neighbors nodes.
     */
    protected void onNeighborUpdate(TileEntity neighbor, Direction direction) {
    }

    /**
     * Called when block is replaced to remove inputs from neighbors nodes.
     */
    protected void onNeighborRemove(TileEntity neighbor, Direction direction) {
    }
}
