package muramasa.antimatter.capability;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverInstance;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;

public class CoverHandler<T extends TileEntity> implements ICoverHandler<T> {

    private final LazyOptional<ICoverHandler<T>> handler = LazyOptional.of(() -> this);

    private T tile;
    protected Object2ObjectMap<Direction, CoverInstance<T>> covers = new Object2ObjectOpenHashMap<>();
    protected List<String> validCovers = new ObjectArrayList<>();

    public CoverHandler(T tile, Cover... validCovers) {
        this.tile = tile;
        this.validCovers.add(Data.COVERNONE.getId());
        Arrays.stream(validCovers).forEach(c -> this.validCovers.add(c.getId()));
        Arrays.stream(Ref.DIRS).forEach(d -> covers.put(d, new CoverInstance<>(Data.COVERNONE, tile)));
    }

    @Override
    public boolean set(Direction side, @Nonnull Cover newCover) {
        covers.get(side).onRemove(side);
        covers.put(side, new CoverInstance<>(newCover, getTile(), side)); //Emplace newCover, calls onPlace!
        //TODO add newCover.onPlace and newCover.onRemove to customize sounds
        tile.getWorld().playSound(null, tile.getPos(), SoundEvents.BLOCK_METAL_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        Utils.markTileForRenderUpdate(getTile());
        tile.markDirty();
        return true;
    }

    @Override
    public CoverInstance<T> get(Direction side) {
        return covers.get(side); //Should never return null, as COVER_NONE is inserted for every direction
    }

    public CoverInstance<?>[] getAll() {
        return covers.values().toArray(new CoverInstance[0]);
    }

    @Override
    public Direction getTileFacing() {
        return Direction.NORTH;
    }

    @Override
    public T getTile() {
        if (tile == null) throw new NullPointerException("CoverHandler cannot have a null tile");
        return tile;
    }

    @Override
    public void onUpdate() {
        covers.forEach((s, c) -> c.getCover().onUpdate(c, s));
    }

    @Override
    public void onRemove() {
        covers.forEach((s, c) -> c.onRemove(s));
    }

    @Override
    public boolean onInteract(PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type) {
        return false;
    }

    @Override
    public boolean placeCover(PlayerEntity player, Direction side, ItemStack stack, Cover cover) {
        if (!get(side).isEmpty() || !set(side, cover)) return false;
        if (!player.isCreative()) stack.shrink(1);
        return true;
    }

    @Override
    public boolean removeCover(PlayerEntity player, Direction side) {
        System.out.println(get(side).getId());
        Cover oldCover = get(side).getCover();
        if (get(side).isEmpty() || !set(side, Data.COVERNONE)) return false;
        if (!player.isCreative()) player.dropItem(oldCover.getDroppedStack(), false);
        player.playSound(SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
        return true;
    }

    @Override
    public boolean hasCover(@Nonnull Direction side, @Nonnull Cover cover) {
        return get(side).isEqual(cover);
    }

    @Override
    public boolean isValid(@Nonnull Direction side, @Nonnull Cover replacement) {
        return (get(side).isEmpty() || replacement.isEqual(Data.COVERNONE)) && validCovers.contains(replacement.getId());
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        byte[] sides = new byte[1];
        covers.forEach((s, c) -> {
            if (!c.isEmpty()) { //Don't store EMPTY covers unnecessarily
                sides[0] |= (1 << s.getIndex());
                CompoundNBT nbt = c.serialize();
                nbt.putString(Ref.TAG_MACHINE_COVER_ID, c.getId());
                tag.put(Ref.TAG_MACHINE_COVER_NAME.concat(Integer.toString(s.getIndex())), nbt);
            }
        });
        tag.putByte(Ref.TAG_MACHINE_COVER_SIDE, sides[0]);
        tag.putInt(Ref.TAG_MACHINE_COVER_FACING, getTileFacing().getIndex());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        byte sides = nbt.getByte(Ref.TAG_MACHINE_COVER_SIDE);
        for (int i = 0; i < Ref.DIRS.length; i++) {
            if ((sides & (1 << i)) > 0) {
                CompoundNBT cover = nbt.getCompound(Ref.TAG_MACHINE_COVER_NAME.concat(Integer.toString(i)));
                CoverInstance<T> c = new CoverInstance<>(AntimatterAPI.get(Cover.class, cover.getString(Ref.TAG_MACHINE_COVER_ID)), tile);
                c.deserialize(cover);
                covers.put(Ref.DIRS[i], c);
            } else {
                covers.put(Ref.DIRS[i], new CoverInstance<>(Data.COVERNONE, this.tile));
            }
        }
        World w = tile.getWorld();
        if (w != null && w.isRemote) {
            Utils.markTileForRenderUpdate(this.tile);
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return handler.cast();
    }

}
