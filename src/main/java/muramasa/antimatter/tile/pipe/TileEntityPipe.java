package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.CoverHandler;
import muramasa.antimatter.capability.pipe.PipeCoverHandler;
import muramasa.antimatter.capability.pipe.PipeInteractHandler;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverInstance;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.pipe.types.PipeType;
import muramasa.antimatter.tile.TileEntityTickable;
import muramasa.antimatter.util.Utils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import tesseract.api.ITickHost;
import tesseract.api.ITickingController;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static muramasa.antimatter.capability.CapabilitySide.SYNC;

public class TileEntityPipe extends TileEntityTickable implements ITickHost {

    /** Pipe Data **/
    protected PipeType<?> type;
    protected PipeSize size;

    /** Capabilities **/
    public final LazyOptional<PipeCoverHandler<?>> coverHandler;
    public final LazyOptional<PipeInteractHandler<?>> interactHandler;

    /** Tesseract **/
    private ITickingController controller;
    private Direction direction; // when cap not initialized yet, it will help to store preset direction

    public TileEntityPipe(PipeType<?> type) {
        super(type.getTileType());
        this.type = type;
        this.coverHandler = LazyOptional.of(() -> new PipeCoverHandler<>(this));
        this.interactHandler = LazyOptional.of(() -> new PipeInteractHandler<>(this));
    }

    @Override
    public void onFirstTick() {
        // Work when direction was set before handler initialization
        // setConnection() might be called from the BlockPipe when tick not called yet.
        if (direction != null) {
            interactHandler.ifPresent(h -> h.setConnection(direction));
            direction = null;
        }
    }

    @Override
    public void onServerUpdate() {
        if (controller != null) {
            controller.tick();
        }
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

    public void setConnection(Direction side) {
        interactHandler.ifPresent(ih -> ih.setConnection(side));
        // interactHandler.ifPresentOrElse(h -> h.setConnection(side), () -> direction = side);
        refreshConnection();
    }

    public void toggleConnection(Direction side) {
        interactHandler.ifPresent(ih -> ih.toggleConnection(side));
        // interactHandler.ifPresentOrElse(h -> h.toggleConnection(side), () -> direction = side);
        refreshConnection();
    }

    public void clearConnection(Direction side) {
        interactHandler.ifPresent(ih -> ih.clearConnection(side));
        // interactHandler.ifPresentOrElse(h -> h.clearConnection(side), () -> direction = side);
        refreshConnection();
    }

    public void changeConnection(Direction side) {
        interactHandler.ifPresent(ih -> ih.onChange(side));
        // interactHandler.ifPresentOrElse(h -> h.onChange(side), () -> direction = side);
    }

    public void refreshConnection() {
        Utils.markTileForRenderUpdate(this);
    }

    public boolean canConnect(int side) {
        return interactHandler.map(h -> h.canConnect(side)).orElse(false);
    }

    public Cover[] getValidCovers() {
        return AntimatterAPI.all(Cover.class).toArray(new Cover[0]);
    }

    public CoverInstance<?>[] getAllCovers() {
        return coverHandler.map(CoverHandler::getAll).orElse(new CoverInstance[0]);
    }

    public CoverInstance<?> getCover(Direction side) {
        return coverHandler.map(h -> h.get(side)).orElse(null);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == AntimatterCaps.COVERABLE_HANDLER_CAPABILITY) return coverHandler.map(h -> !h.get(side).isEmpty()).orElse(false) ? coverHandler.cast() : super.getCapability(cap, side);
        else if (cap == AntimatterCaps.INTERACTABLE_HANDLER_CAPABILITY) return interactHandler.<LazyOptional<T>>map(h -> LazyOptional.of(() -> h).cast()).orElseGet(() -> super.getCapability(cap));
        return LazyOptional.empty();
    }

    /*
    @Override
    public CompoundNBT getCapabilityTag(String cap) {
        if (coverHandler.equals(cap)) return coverHandler.getOrCreateTag(Ref.KEY_PIPE_TILE_COVER);
        else if (interactHandler.equals(cap)) return interactHandler.getOrCreateTag(Ref.KEY_PIPE_TILE_CONFIG);
        return new CompoundNBT();
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        if (tag.contains(Ref.KEY_PIPE_TILE_COVER)) coverHandler.read(tag.getCompound(Ref.KEY_PIPE_TILE_COVER));
        if (tag.contains(Ref.KEY_PIPE_TILE_CONFIG)) interactHandler.read(tag.getCompound(Ref.KEY_PIPE_TILE_CONFIG));
    }

    @Nonnull
    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag); //TODO get tile data tag
        coverHandler.ifPresent(h -> tag.put(Ref.KEY_PIPE_TILE_COVER, h.serialize()));
        interactHandler.ifPresent(h -> tag.put(Ref.KEY_PIPE_TILE_CONFIG, h.serialize()));
        return tag;
    }

    @Override
    public void update(CompoundNBT tag) {
        if (tag.contains(Ref.KEY_PIPE_TILE_COVER)) coverHandler.ifPresent(h -> h.deserialize(tag.getCompound(Ref.KEY_PIPE_TILE_COVER)));
        if (tag.contains(Ref.KEY_PIPE_TILE_CONFIG)) interactHandler.ifPresent(h -> h.deserialize(tag.getCompound(Ref.KEY_PIPE_TILE_CONFIG)));
        refreshConnection();
    }
     */

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        info.add("Pipe Type: " + getPipeType().getId());
        info.add("Pipe Size: " + getPipeSize().getId());
        return info;
    }

    @Override
    public void reset(ITickingController oldController, ITickingController newController) {
        if (oldController == null || (controller == oldController && newController == null) || controller != oldController) {
            controller = newController;
        }
    }
}
