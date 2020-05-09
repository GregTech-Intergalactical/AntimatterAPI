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
import muramasa.antimatter.tile.TileEntityTickable;
import muramasa.antimatter.util.Utils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
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

    protected byte connections, disabledConnections;

    public TileEntityPipe(TileEntityType<?> tileType) {
        super(tileType);
    }

    public TileEntityPipe(PipeType<?> type) {
        this(type.getTileType());
        this.type = type;
    }

    @Override
    public void onInit() {
        coverHandler = Optional.of(new PipeCoverHandler(this));
        configHandler = Optional.of(new PipeConfigHandler(this));
    }

    @Override
    public void onFirstTick() {
        if (isServerSide()) refreshConnections();
    }

    public PipeType<?> getPipeType() {
        return type != null ? type : (type = ((BlockPipe<?>) getBlockState().getBlock()).getType());
    }

    public PipeSize getPipeSize() { //TODO need to store? when getBlockState is cached?
        return size != null ? size : (size = ((BlockPipe<?>) getBlockState().getBlock()).getSize());
    }

    public byte getConnections() {
        return connections;
    }

    public byte getDisabledConnections() {
        return disabledConnections;
    }

    public void refreshConnections() {
        connections = 0;
        //int sideMask, smallerPipes = 0;
        for (Direction direction : Ref.DIRECTIONS) {
            TileEntity adjTile = Utils.getTile(world, pos.offset(direction));
            if (adjTile == null) continue;
            int mask = 1 << direction.getIndex();
            if ((disabledConnections & mask) == 0) { //Connection side has not been disabled
                if (canConnect(adjTile, direction)) {
                    connections |= mask;
                    //TODO check isFullCube to allow more culled connections?
                    //if (((TileEntityPipe) adjTile).getPipeSize().ordinal() < getPipeSize().ordinal()) smallerPipes++;
                }
            }
        }
        //if (smallerPipes == 0) connections += 64; //Use culled models if there are no smaller pipes adjacent
        markForNBTSync();
    }

    public boolean canConnect(TileEntity tile, Direction side) {
        return true;
    }

    public void toggleConnection(Direction side) {
        int mask = 1 << side.getIndex();
        if ((disabledConnections & mask) > 0) { // Is Disabled, so remove mask
            disabledConnections &= ~mask;
        } else { // Is not disabled, so add mask
            disabledConnections |= mask;
        }
        refreshConnections();
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
        if (cap == AntimatterCaps.COVERABLE && coverHandler.isPresent()) return LazyOptional.of(() -> coverHandler.get()).cast();
        else if (cap == AntimatterCaps.CONFIGURABLE && configHandler.isPresent()) return LazyOptional.of(() -> configHandler.get()).cast();
        return super.getCapability(cap);
    }

    //TODO move to cap
//    @Override
//    public void readFromNBT(CompoundNBT tag) {
//        super.readFromNBT(tag);
//        if (tag.hasKey(Ref.KEY_PIPE_CONNECTIONS)) connections = tag.getByte(Ref.KEY_PIPE_CONNECTIONS);
//    }
//
//    @Override
//    public CompoundNBT writeToNBT(CompoundNBT tag) {
//        super.writeToNBT(tag);
//        tag.setInteger(Ref.KEY_PIPE_CONNECTIONS, connections);
//        return tag;
//    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        info.add("Pipe Type: " + getPipeType().getId());
        info.add("Pipe Size: " + getPipeSize().getId());
        return info;
    }
}
