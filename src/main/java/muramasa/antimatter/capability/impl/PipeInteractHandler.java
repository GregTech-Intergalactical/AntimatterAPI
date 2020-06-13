package muramasa.antimatter.capability.impl;

import muramasa.antimatter.Ref;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import static muramasa.antimatter.Data.WIRE_CUTTER;
import static muramasa.antimatter.Data.WRENCH;

@ParametersAreNonnullByDefault
public class PipeInteractHandler extends InteractHandler {

    private byte connection; // for wrappers around the tile

    public PipeInteractHandler(TileEntityPipe tile) {
        super(tile);
    }

    // TODO: Block if covers are exist
    //TODO: use parsedSide when working properl
    @Override
    public boolean onInteract(PlayerEntity player, Hand hand, Direction side, @Nonnull Direction parsedSide, @Nullable AntimatterToolType type) {
        if (type == getTool() && hand == Hand.MAIN_HAND) {
            boolean isTarget = false;
            TileEntityPipe tile = (TileEntityPipe) getTile();
            TileEntity target = tile.getWorld().getTileEntity(tile.getPos().offset(parsedSide));
            if (target instanceof TileEntityPipe) {
                ((TileEntityPipe) target).toggleConnection(parsedSide.getOpposite());
            } else {
                isTarget = tile.isServerSide() && Utils.isForeignTile(target); // Check that entity is not GT one
            }
            tile.toggleConnection(parsedSide);

            // If some target in front of, then create wrapper
            if (isTarget) {
                if (tile.canConnect(parsedSide.getIndex())) {
                    connection = Connectivity.set(connection, parsedSide.getIndex());
                    PipeCache.update(tile.getPipeType(), tile.getWorld(), parsedSide, target, tile.getCover(parsedSide).getCover());
                } else {
                    connection = Connectivity.clear(connection, parsedSide.getIndex());
                    PipeCache.remove(tile.getPipeType(), tile.getWorld(), parsedSide, target);
                }
            }
            return true;
        }
        return false;
    }

    private void onInit() {
        TileEntityPipe tile = (TileEntityPipe) getTile();
        CoverInstance[] covers = tile.getAllCovers();
        if (covers.length == 0) return;
        for (Direction side : Ref.DIRECTIONS) {
            if (Connectivity.has(connection, side.getIndex())) {
                TileEntity neighbor = Utils.getTile(tile.getWorld(), tile.getPos().offset(side));
                if (Utils.isForeignTile(neighbor)) { // Check that entity is not GT one
                    PipeCache.update(tile.getPipeType(), tile.getWorld(), side, neighbor, covers[side.getIndex()].getCover());
                } else {
                    connection = Connectivity.clear(connection, side.getIndex());
                }
            }
        }
    }

    /** Called when neighbor was placed near */
    public void onChange(Direction side) {
        TileEntityPipe tile = (TileEntityPipe) getTile();
        TileEntity neighbor = Utils.getTile(tile.getWorld(), tile.getPos().offset(side));
        if (Utils.isForeignTile(neighbor)) {
            connection = Connectivity.set(connection, side.getIndex());
            PipeCache.update(tile.getPipeType(), tile.getWorld(), side, neighbor, tile.getCover(side).getCover());
        } else {
            connection = Connectivity.clear(connection, side.getIndex());
        }
    }

    public void onRemove() {
        TileEntityPipe tile = (TileEntityPipe) getTile();
        for (Direction side : Ref.DIRECTIONS) {
            if (Connectivity.has(connection, side.getIndex())) {
                TileEntity neighbor = Utils.getTile(tile.getWorld(), tile.getPos().offset(side));
                if (Utils.isForeignTile(neighbor)) { // Check that entity is not GT one
                    PipeCache.remove(tile.getPipeType(), tile.getWorld(), side, neighbor);
                }
            }
        }
    }

    /** NBT **/
    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        tag.putByte("Connection", connection);
        return tag;
    }

    public void deserialize(CompoundNBT tag) {
        connection = tag.getByte("Connection");
        onInit();
    }

    private AntimatterToolType getTool() {
        return getTile() instanceof TileEntityCable ? WIRE_CUTTER : WRENCH;
    }
}
