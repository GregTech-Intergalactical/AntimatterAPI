package muramasa.antimatter.capability.impl;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

import javax.annotation.Nonnull;
import java.util.List;

public class CoverHandler implements ICoverHandler {

    private TileEntity tile;
    protected List<String> validCovers;

    //TODO
    protected Cover[] covers = new Cover[] {
        Data.COVER_NONE, Data.COVER_NONE, Data.COVER_NONE, Data.COVER_NONE, Data.COVER_NONE, Data.COVER_NONE
    };

    public CoverHandler(TileEntity tile, Cover... covers) {
        this.tile = tile;
        validCovers = new ObjectArrayList<>();
        validCovers.add(Data.COVER_NONE.getId());
        for (Cover cover : covers) {
            validCovers.add(cover.getId());
        }
    }

    @Override
    public void onUpdate() {
        for (int i = 0; i < covers.length; i++) {
            if (covers[i].isEmpty()) continue;
            covers[i].onUpdate(getTile(), Ref.DIRECTIONS[i]);
        }
    }

    @Override
    public boolean onPlace(Direction side, @Nonnull Cover cover) {
        int i = side.getIndex();
        //TODO: Do not allow putting on front face.
        if (((TileEntityMachine)getTile()).getFacing() == Direction.byIndex(i)) {
            return false;
        }
        if (!isValid(side, covers[i], cover)) return false;
        covers[i] = cover;
        covers[i].onPlace(getTile(), side);
        //TODO add cover.onPlace and cover.onRemove to customize sounds
        tile.getWorld().playSound(null, tile.getPos(), SoundEvents.BLOCK_METAL_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        Utils.markTileForRenderUpdate(getTile());
        return true;
    }

    @Override
    public void onRemove() {
        for (int i = 0; i < covers.length; i++) {
            if (covers[i].isEmpty()) continue;
            covers[i].onRemove(getTile(), Ref.DIRECTIONS[i]);
        }
    }

    @Override
    public Cover getCover(Direction side) {
        return covers[side.getIndex()];
    }

    public Cover[] getAll() {
        return covers;
    }

    @Override
    public boolean onInteract(@Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull Direction side, @Nonnull AntimatterToolType type) {
        Cover cover = getCover(side);
        if (cover.isEmpty() || !cover.onInteract(getTile(), player, hand, side, type)) return false;
        if (type == null) return false;
        // switch (type) {
            //case CROWBAR: return AntimatterAPI.removeCover(player, this, side);
            //default: return false;
        //}
        return true;
    }

    @Override
    public boolean hasCover(@Nonnull Direction side, @Nonnull Cover cover) {
        return getCover(side).isEqual(cover);
    }

    @Override
    public boolean isValid(@Nonnull Direction side, Cover existing, @Nonnull Cover replacement) {
        return (existing.isEmpty() || replacement.isEqual(Data.COVER_NONE)) && validCovers.contains(replacement.getId());
    }

    @Override
    public Direction getTileFacing() {
        return Direction.NORTH;
    }

    @Override
    public TileEntity getTile() {
        if (tile == null) throw new NullPointerException("CoverHandler cannot have a null tile");
        return tile;
    }

    /** NBT **/
    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        for (int i = 0; i < covers.length; i++) {
            tag.putString("Cover-".concat(Integer.toString(i)), covers[i].getId());
        }
        return tag;
    }

    public void deserialize(CompoundNBT compound) {
        for (int i = 0; i < covers.length; i++) {
            covers[i] = AntimatterAPI.getCover(compound.getString("Cover-".concat(Integer.toString(i))));
        }
    }
}
