package muramasa.antimatter.capability.impl;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.ICoverHandler;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverInstance;
import muramasa.antimatter.item.ItemCover;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public class CoverHandler implements ICoverHandler {

    private TileEntity tile;
    protected List<String> validCovers;

    //TODO
    protected CoverInstance[] covers = new CoverInstance[] {
        Data.COVER_EMPTY, Data.COVER_EMPTY, Data.COVER_EMPTY, Data.COVER_EMPTY, Data.COVER_EMPTY, Data.COVER_EMPTY
    };

    public CoverHandler(TileEntity tile, Cover... covers) {
        this.tile = tile;
        validCovers = new ObjectArrayList<>();
        validCovers.add(Data.COVERNONE.getId());
        for (Cover cover : covers) {
            validCovers.add(cover.getId());
        }
    }

    @Override
    public void onUpdate() {
        for (int i = 0; i < covers.length; i++) {
            if (covers[i].isEmpty()) continue;
            covers[i].onUpdate(Ref.DIRECTIONS[i]);
        }
    }

    @Override
    public boolean onPlace(Direction side, @Nonnull Cover cover) {
        int i = side.getIndex();
        //TODO: Do not allow putting on front face.
        if (((TileEntityMachine)getTile()).getFacing() == Direction.byIndex(i)) {
            return false;
        }
        if (!isValid(side, covers[i].backing(), cover)) return false;
        //Emplace cover, calls onPlace!
        covers[i] = new CoverInstance(cover, this.getTile(), side);
        //TODO add cover.onPlace and cover.onRemove to customize sounds
        tile.getWorld().playSound(null, tile.getPos(), SoundEvents.BLOCK_METAL_PLACE, SoundCategory.BLOCKS, 1.0F, 1.0F);
        Utils.markTileForRenderUpdate(getTile());
        return true;
    }

    @Override
    public void onRemove() {
        for (int i = 0; i < covers.length; i++) {
            if (covers[i].isEmpty()) continue;
            covers[i].onRemove(Ref.DIRECTIONS[i]);
        }
    }

    @Override
    public CoverInstance getCover(Direction side) {
        return covers[side.getIndex()];
    }

    public CoverInstance[] getAll() {
        return covers;
    }

    @Override
    public boolean onInteract(@Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull Direction side, @Nullable AntimatterToolType type) {
        CoverInstance cover = getCover(side);
        //Allow placing cover on block interaction, if cover is empty.
        if (cover.isEmpty() && player.getHeldItem(hand).getItem() instanceof ItemCover) {
            ItemStack stack = player.getHeldItem(hand);
            ItemCover item = (ItemCover) stack.getItem();
            if (this.onPlace(side, item.getCover())) {
                if (!player.isCreative()) {
                    stack.shrink(1);
                }
                return true;
            }
        }
        if (cover.isEmpty()) return false;
        return cover.onInteract(player, hand, side, type);
    }

    @Override
    public boolean hasCover(@Nonnull Direction side, @Nonnull Cover cover) {
        return getCover(side).isEqual(cover);
    }

    @Override
    public boolean isValid(@Nonnull Direction side, Cover existing, @Nonnull Cover replacement) {
        //The extending coverhandler should check validity on its own.
        return true;//(existing.isEmpty() || replacement.isEqual(Data.COVERNONE)) && validCovers.contains(replacement.getId());
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
        //    covers[i] = AntimatterAPI.get(Cover.class, compound.getString("Cover-".concat(Integer.toString(i))));
        }
    }
}
