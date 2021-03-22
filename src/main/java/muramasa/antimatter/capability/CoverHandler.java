package muramasa.antimatter.capability;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.cover.CoverStack;
import muramasa.antimatter.cover.ICover;
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
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static muramasa.antimatter.Data.*;

public class CoverHandler<T extends TileEntity> implements ICoverHandler<T> {

    private final LazyOptional<ICoverHandler<T>> handler = LazyOptional.of(() -> this);

    private final T tile;
    protected final Object2ObjectMap<Direction, CoverStack<T>> covers = new Object2ObjectOpenHashMap<>(6);
    protected final Object2ObjectMap<Class<?>, Set<Direction>> reverseLookup = new Object2ObjectOpenHashMap<>(6);
    protected List<String> validCovers = new ObjectArrayList<>();

    public CoverHandler(T tile, ICover... validCovers) {
        this.tile = tile;
        this.validCovers.add(Data.COVERNONE.getId());
        Arrays.stream(validCovers).forEach(c -> this.validCovers.add(c.getId()));
        Arrays.stream(Ref.DIRS).forEach(d -> {
            covers.put(d, new CoverStack<>(Data.COVERNONE, tile));
            buildLookup(COVERNONE, COVERNONE, d);
        });
    }

    @Override
    public boolean set(Direction side, @Nonnull ICover newCover) {
        CoverStack<T> old = covers.get(side);
        buildLookup(old.getCover(),newCover, side);
        CoverStack<T> stack = new CoverStack<>(newCover, getTile());
        return set(side, old, stack);
    }

    public boolean set(Direction side, CoverStack<T> old, CoverStack<T> stack) {
        covers.put(side, stack); //Emplace newCover, calls onPlace!
        old.onRemove(side);
        stack.onPlace(side);
        if (tile.getWorld() != null) {
            sync();
        }
        return true;
    }

    protected void sync() {
        if (tile.getWorld() != null && tile.getWorld().isRemote)
            Utils.markTileForRenderUpdate(getTile());
        if (tile.getWorld() != null && !tile.getWorld().isRemote)
            tile.markDirty();
            tile.getWorld().playSound(null, tile.getPos(), SoundEvents.BLOCK_METAL_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
    }

    protected void buildLookup(ICover oldCover, ICover newCover, Direction dir) {
        reverseLookup.compute(oldCover.getClass(), (k,v) -> {
            if (v == null) v = new ObjectOpenHashSet<>();
            v.remove(dir);
            return v;
        });
        reverseLookup.compute(newCover.getClass(), (k,v) -> {
            if (v == null) v = new ObjectOpenHashSet<>();
            v.add(dir);
            return v;
        });
    }

    public Set<Direction> lookup(Class<?> c) {
        return reverseLookup.get(c);
    }
    @Nullable
    public Direction lookupSingle(Class<?> c) {
        Set<Direction> set = reverseLookup.get(c);
        if (set != null && set.size() == 1) return set.iterator().next();
        return null;
    }

    @Override
    public CoverStack<T> get(Direction side) {
        return covers.getOrDefault(side, (CoverStack<T>) COVER_EMPTY); //Should never return null, as COVER_NONE is inserted for every direction
    }

    public CoverStack<?>[] getAll() {
        return covers.values().toArray(new CoverStack[0]);
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
    public boolean placeCover(PlayerEntity player, Direction side, ItemStack stack, ICover cover) {
        if (!get(side).isEmpty() || !set(side, cover)) return false;
        if (!player.isCreative()) stack.shrink(1);
        return true;
    }

    @Override
    public boolean removeCover(PlayerEntity player, Direction side, boolean drop) {
        ICover oldCover = get(side).getCover();
        if (oldCover.isEqual(COVEROUTPUT)) return false;
        if (get(side).isEmpty() || !set(side, Data.COVERNONE)) return false;
        if (drop && !player.isCreative()) player.dropItem(oldCover.getDroppedStack(), false);
        player.playSound(SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
        return true;
    }

    @Override
    public boolean hasCover(@Nonnull Direction side, @Nonnull ICover cover) {
        return get(side).isEqual(cover);
    }

    @Override
    public boolean isValid(@Nonnull Direction side, @Nonnull ICover replacement) {
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
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        byte sides = nbt.getByte(Ref.TAG_MACHINE_COVER_SIDE);
        for (int i = 0; i < Ref.DIRS.length; i++) {
            if ((sides & (1 << i)) > 0) {
                CompoundNBT cover = nbt.getCompound(Ref.TAG_MACHINE_COVER_NAME.concat(Integer.toString(i)));
                CoverStack<T> c = new CoverStack<>(AntimatterAPI.get(ICover.class, cover.getString(Ref.TAG_MACHINE_COVER_ID)), tile);
                c.deserialize(cover);
                buildLookup(covers.get(Ref.DIRS[i]).getCover(), c.getCover(), Ref.DIRS[i]);
                covers.put(Ref.DIRS[i], c);
            } else {
                buildLookup(covers.get(Ref.DIRS[i]).getCover(),COVERNONE, Ref.DIRS[i]);
                covers.put(Ref.DIRS[i], new CoverStack<>(Data.COVERNONE, this.tile));
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

    @Override
    public boolean moveCover(PlayerEntity entity,Direction oldSide, Direction newSide) {
        //Have to move the entire stack, due to possible tag data.
        CoverStack<T> newStack = get(newSide);
        CoverStack<T> oldStack = get(oldSide);
        if (!newStack.isEmpty() || oldStack.isEmpty()) return false;
        CoverStack<T> toPlace = new CoverStack<>(oldStack);
        if (!removeCover(entity, oldSide, false)) return false;

        set(newSide,newStack, toPlace);
        if (tile.getWorld() != null) {
            sync();
        }
        return true;
    }
}
