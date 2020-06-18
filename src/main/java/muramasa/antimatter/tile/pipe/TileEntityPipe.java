package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.impl.CoverHandler;
import muramasa.antimatter.capability.impl.PipeInteractHandler;
import muramasa.antimatter.capability.impl.PipeCoverHandler;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverInstance;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.pipe.types.PipeType;
import muramasa.antimatter.tile.TileEntityTickable;
import net.minecraft.nbt.CompoundNBT;
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
    public Optional<PipeInteractHandler> interactHandler = Optional.empty();

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
        if (!interactHandler.isPresent()) interactHandler = Optional.of(new PipeInteractHandler(this));
    }

    @Override
    public void onRemove() {
        coverHandler.ifPresent(PipeCoverHandler::onRemove);
        interactHandler.ifPresent(PipeInteractHandler::onRemove);
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

    public void toggleConnection(Direction side) {
        connection = Connectivity.toggle(connection, side.getIndex());
        refreshConnection();
    }

    public void clearConnection(Direction side) {
        connection = Connectivity.clear(connection, side.getIndex());
        refreshConnection();
    }

    public void changeConnection(Direction side) {
        interactHandler.ifPresent(h -> h.onChange(side));
    }

    public void refreshConnection() {
        markForRenderUpdate();
    }

    public boolean canConnect(int side) {
        return Connectivity.has(connection, side);
    }

    public Cover[] getValidCovers() {
        return AntimatterAPI.all(Cover.class).toArray(new Cover[0]);
    }

    public CoverInstance[] getAllCovers() {
        return coverHandler.map(CoverHandler::getAll).orElse(new CoverInstance[0]);
    }

    public CoverInstance getCover(Direction side) {
        return coverHandler.map(h -> h.getCoverInstance(side)).orElse(Data.COVER_EMPTY);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        return cap == AntimatterCaps.INTERACTABLE && interactHandler.isPresent() ? LazyOptional.of(() -> interactHandler.get()).cast() : super.getCapability(cap);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, Direction side) {
        return cap == AntimatterCaps.COVERABLE && coverHandler.map(h -> !h.getCoverInstance(side).isEmpty()).orElse(false) ? LazyOptional.of(() -> coverHandler.get()).cast() : super.getCapability(cap, side);
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        if (tag.contains(Ref.KEY_PIPE_TILE_CONNECTIVITY)) connection = tag.getByte(Ref.KEY_PIPE_TILE_CONNECTIVITY);
        if (tag.contains(Ref.KEY_PIPE_TILE_COVER)) coverHandler.ifPresent(h -> h.deserialize(tag.getCompound(Ref.KEY_MACHINE_TILE_COVER)));
        if (tag.contains(Ref.KEY_PIPE_TILE_CONFIG)) interactHandler.ifPresent(h -> h.deserialize(tag.getCompound(Ref.KEY_PIPE_TILE_CONFIG)));
        //TODO refreshConnection(); causes crash as the world object has not yet been assigned
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag); //TODO get tile data tag
        tag.putByte(Ref.KEY_PIPE_TILE_CONNECTIVITY, connection);
        coverHandler.ifPresent(h -> tag.put(Ref.KEY_PIPE_TILE_COVER, h.serialize()));
        interactHandler.ifPresent(h -> tag.put(Ref.KEY_PIPE_TILE_CONFIG, h.serialize()));
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
