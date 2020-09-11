package muramasa.antimatter.capability.pipe;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.CapabilityType;
import muramasa.antimatter.capability.ICapabilityHandler;
import muramasa.antimatter.capability.InteractHandler;
import muramasa.antimatter.cover.CoverInstance;
import muramasa.antimatter.pipe.PipeCache;
import muramasa.antimatter.tile.pipe.TileEntityCable;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import tesseract.graph.Connectivity;

import javax.annotation.Nullable;

import static muramasa.antimatter.Data.WIRE_CUTTER;
import static muramasa.antimatter.Data.WRENCH;

public class PipeInteractHandler<T extends TileEntityPipe> extends InteractHandler<T> implements ICapabilityHandler {

    private byte connection, interaction;

    public PipeInteractHandler(T tile, CompoundNBT tag) {
        super(tile);
        if (tag != null) deserialize(tag);
    }

    // TODO: Block if covers are exist
    // TODO: use parsedSide when working properl
    @Override
    public boolean onInteract(PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type) {
        if (type == getTool() && hand == Hand.MAIN_HAND) {
            boolean isTarget = false;
            T tile = getTile();
            TileEntity target = tile.getWorld().getTileEntity(tile.getPos().offset(side));
            if (target instanceof TileEntityPipe) {
                ((TileEntityPipe) target).toggleConnection(side.getOpposite());
            } else {
                isTarget = tile.isServerSide() && Utils.isForeignTile(target); // Check that entity is not GT one
            }
            tile.toggleConnection(side);

            // If some target in front of, then create wrapper
            if (isTarget) {
                if (tile.canConnect(side.getIndex())) {
                    interaction = Connectivity.set(interaction, side.getIndex());
                    PipeCache.update(tile.getPipeType(), tile.getWorld(), side, target, tile.getCover(side).getCover());
                } else {
                    interaction = Connectivity.clear(interaction, side.getIndex());
                    PipeCache.remove(tile.getPipeType(), tile.getWorld(), side, target);
                }
            }
            return true;
        }
        return false;
    }

    private void onLoad() {
        T tile = getTile();
        CoverInstance<?>[] covers = tile.getAllCovers();
        if (covers.length == 0) return;
        for (Direction side : Ref.DIRS) {
            if (Connectivity.has(interaction, side.getIndex())) {
                TileEntity neighbor = Utils.getTile(tile.getWorld(), tile.getPos().offset(side));
                if (Utils.isForeignTile(neighbor)) { // Check that entity is not GT one
                    PipeCache.update(tile.getPipeType(), tile.getWorld(), side, neighbor, covers[side.getIndex()].getCover());
                } else {
                    interaction = Connectivity.clear(interaction, side.getIndex());
                }
            }
        }
    }

    /** Called when neighbor was placed near */
    public void onChange(Direction side) {
        T tile = getTile();
        TileEntity neighbor = Utils.getTile(tile.getWorld(), tile.getPos().offset(side));
        if (Utils.isForeignTile(neighbor)) {
            interaction = Connectivity.set(interaction, side.getIndex());
            PipeCache.update(tile.getPipeType(), tile.getWorld(), side, neighbor, tile.getCover(side).getCover());
        } else {
            interaction = Connectivity.clear(interaction, side.getIndex());
        }
    }

    public void onRemove() {
        T tile = getTile();
        for (Direction side : Ref.DIRS) {
            if (Connectivity.has(interaction, side.getIndex())) {
                TileEntity neighbor = Utils.getTile(tile.getWorld(), tile.getPos().offset(side));
                if (Utils.isForeignTile(neighbor)) { // Check that entity is not GT one
                    PipeCache.remove(tile.getPipeType(), tile.getWorld(), side, neighbor);
                }
            }
        }
    }

    public void setConnection(Direction side) {
        connection = Connectivity.set(connection, side.getIndex());
    }

    public void toggleConnection(Direction side) {
        connection = Connectivity.toggle(connection, side.getIndex());
    }

    public void clearConnection(Direction side) {
        connection = Connectivity.clear(connection, side.getIndex());
    }

    public boolean canConnect(int side) {
        return Connectivity.has(connection, side);
    }

    /** NBT **/
    @Override
    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        tag.putByte(Ref.TAG_PIPE_TILE_INTERACT, interaction);
        tag.putByte(Ref.TAG_PIPE_TILE_CONNECTIVITY, connection);
        return tag;
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        interaction = tag.getByte(Ref.TAG_PIPE_TILE_INTERACT);
        connection = tag.getByte(Ref.TAG_PIPE_TILE_CONNECTIVITY);
        onLoad();
    }

    private AntimatterToolType getTool() {
        return getTile() instanceof TileEntityCable ? WIRE_CUTTER : WRENCH;
    }

    @Override
    public CapabilityType getCapabilityType() {
        return CapabilityType.INTERACTABLE;
    }
}
