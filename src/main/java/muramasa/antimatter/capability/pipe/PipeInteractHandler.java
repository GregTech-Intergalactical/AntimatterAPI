package muramasa.antimatter.capability.pipe;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.AntimatterCaps;
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
import net.minecraftforge.common.capabilities.Capability;
import tesseract.graph.Connectivity;

import javax.annotation.Nullable;

import static muramasa.antimatter.Data.WIRE_CUTTER;
import static muramasa.antimatter.Data.WRENCH;

public class PipeInteractHandler extends InteractHandler<TileEntityPipe> implements ICapabilityHandler {

    private byte interact;

    public PipeInteractHandler(TileEntityPipe tile, CompoundNBT tag) {
        super(tile);
        if (tag != null) deserialize(tag);
    }

    // TODO: Block if covers are exist
    // TODO: use parsedSide when working properl
    @Override
    public boolean onInteract(PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type) {
        if (type == getTool() && hand == Hand.MAIN_HAND) {
            boolean isTarget = false;
            TileEntityPipe tile = getTile();
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
                    interact = Connectivity.set(interact, side.getIndex());
                    PipeCache.update(tile.getPipeType(), tile.getWorld(), side, target, tile.getCover(side).getCover());
                } else {
                    interact = Connectivity.clear(interact, side.getIndex());
                    PipeCache.remove(tile.getPipeType(), tile.getWorld(), side, target);
                }
            }
            return true;
        }
        return false;
    }

    private void onLoad() {
        TileEntityPipe tile = getTile();
        CoverInstance[] covers = tile.getAllCovers();
        if (covers.length == 0) return;
        for (Direction side : Ref.DIRS) {
            if (Connectivity.has(interact, side.getIndex())) {
                TileEntity neighbor = Utils.getTile(tile.getWorld(), tile.getPos().offset(side));
                if (Utils.isForeignTile(neighbor)) { // Check that entity is not GT one
                    PipeCache.update(tile.getPipeType(), tile.getWorld(), side, neighbor, covers[side.getIndex()].getCover());
                } else {
                    interact = Connectivity.clear(interact, side.getIndex());
                }
            }
        }
    }

    /** Called when neighbor was placed near */
    public void onChange(Direction side) {
        TileEntityPipe tile = getTile();
        TileEntity neighbor = Utils.getTile(tile.getWorld(), tile.getPos().offset(side));
        if (Utils.isForeignTile(neighbor)) {
            interact = Connectivity.set(interact, side.getIndex());
            PipeCache.update(tile.getPipeType(), tile.getWorld(), side, neighbor, tile.getCover(side).getCover());
        } else {
            interact = Connectivity.clear(interact, side.getIndex());
        }
    }

    public void onRemove() {
        TileEntityPipe tile = getTile();
        for (Direction side : Ref.DIRS) {
            if (Connectivity.has(interact, side.getIndex())) {
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
        tag.putByte(Ref.TAG_PIPE_TILE_INTERACT, interact);
        return tag;
    }

    public void deserialize(CompoundNBT tag) {
        interact = tag.getByte(Ref.TAG_PIPE_TILE_INTERACT);
        onLoad();
    }

    private AntimatterToolType getTool() {
        return getTile() instanceof TileEntityCable ? WIRE_CUTTER : WRENCH;
    }

    @Override
    public Capability<?> getCapability() {
        return AntimatterCaps.INTERACTABLE_HANDLER_CAPABILITY;
    }
}
