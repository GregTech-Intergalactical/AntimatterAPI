package muramasa.antimatter.capability;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.dynamic.DynamicTexturer;
import muramasa.antimatter.client.dynamic.DynamicTexturers;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import speiger.src.collections.objects.lists.ObjectArrayList;
import speiger.src.collections.objects.maps.impl.hash.Object2ObjectOpenHashMap;
import speiger.src.collections.objects.maps.interfaces.Object2ObjectMap;
import speiger.src.collections.objects.sets.ObjectOpenHashSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static muramasa.antimatter.Data.COVERNONE;
import static muramasa.antimatter.Data.COVER_EMPTY;
import static muramasa.antimatter.Data.ELECTRIC_WRENCH;
import static muramasa.antimatter.Data.WRENCH;

public class CoverHandler<T extends TileEntity> implements ICoverHandler<T> {

    private final LazyOptional<ICoverHandler<T>> handler = LazyOptional.of(() -> this);

    private final T tile;
    protected final Object2ObjectMap<Direction, CoverStack<T>> covers = new Object2ObjectOpenHashMap<>(6);
    protected final Object2ObjectMap<Class<?>, Set<Direction>> reverseLookup = new Object2ObjectOpenHashMap<>(6);
    protected List<String> validCovers = new ObjectArrayList<>();
    public Map<Direction, DynamicTexturer<ICover, ICover.DynamicKey>> coverTexturer;

    public CoverHandler(T tile, ICover... validCovers) {
        this.tile = tile;
        this.validCovers.add(Data.COVERNONE.getId());
        Arrays.stream(validCovers).forEach(c -> this.validCovers.add(c.getId()));
        Arrays.stream(Ref.DIRS).forEach(d -> {
            covers.put(d, new CoverStack<>(Data.COVERNONE, tile, d));
            buildLookup(COVERNONE, COVERNONE, d);
        });
        coverTexturer = new HashMap<>(6);//LazyHolder.of(() -> new DynamicTexturer<>(DynamicTexturers.COVER_DYNAMIC_TEXTURER));
    }

    @OnlyIn(Dist.CLIENT)
    public DynamicTexturer<ICover, ICover.DynamicKey> getTexturer(Direction dir) {
        return coverTexturer.computeIfAbsent(dir, d -> new DynamicTexturer<>(DynamicTexturers.COVER_DYNAMIC_TEXTURER));
    }

    @Override
    public boolean set(Direction side, @Nonnull ICover newCover, boolean sync) {
        CoverStack<T> old = covers.get(side);
        CoverStack<T> stack = new CoverStack<>(newCover, getTile(), side);
        return set(side, old, stack, sync);
    }

    public boolean set(Direction side, CoverStack<T> old, CoverStack<T> stack, boolean sync) {
        covers.put(side, stack); //Emplace newCover, calls onPlace!
        buildLookup(old.getCover(),stack.getCover(), side);
        old.onRemove(side);
        stack.onPlace(side);
        if (tile.getWorld() != null && sync) {
            sync();
        }
        return true;
    }

    protected void sync() {
        World world = tile.getWorld();
        if (world == null) return;
        if (!world.isRemote) {
            tile.markDirty();
            Utils.markTileForNBTSync(tile);
        } else {
            Utils.markTileForRenderUpdate(tile);
            tile.getWorld().playSound(null, tile.getPos(), SoundEvents.BLOCK_METAL_PLACE, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
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
        covers.forEach((s, c) -> {
           if (c.getCover().ticks()) { c.getCover().onUpdate(c, s);};
        });
    }

    @Override
    public void onRemove() {
        covers.forEach((s, c) -> c.onRemove(s));
    }

    @Override
    public boolean onInteract(PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type) {
        return covers.get(side).onInteract(player, hand, side, type);
    }

    @Override
    public boolean placeCover(PlayerEntity player, Direction side, ItemStack stack, ICover cover) {
        if (!get(side).isEmpty() || !set(side, cover, true)) return false;
        if (!player.isCreative()) stack.shrink(1);
        return true;
    }

    @Override
    public boolean removeCover(PlayerEntity player, Direction side, boolean onlyRemove) {
        ICover oldCover = get(side).getCover();
        if (!onlyRemove && !canRemoveCover(oldCover)) return false;
        if (get(side).isEmpty() || !set(side, Data.COVERNONE, !onlyRemove)) return false;
        if (!onlyRemove && !player.isCreative()) player.dropItem(oldCover.getDroppedStack(), false);
        if (Utils.getToolType(player) != WRENCH && Utils.getToolType(player) != ELECTRIC_WRENCH){
            player.playSound(SoundEvents.ENTITY_ITEM_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
        } else {
            player.playSound(Ref.WRENCH, SoundCategory.BLOCKS, 1.0f, 1.0f);
        }
        return true;
    }

    protected boolean canRemoveCover(ICover cover) {
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
                CoverStack<T> c = new CoverStack<>(AntimatterAPI.get(ICover.class, cover.getString(Ref.TAG_MACHINE_COVER_ID)), tile, Ref.DIRS[i]);
                c.deserialize(cover);
                buildLookup(covers.get(Ref.DIRS[i]).getCover(), c.getCover(), Ref.DIRS[i]);
                covers.put(Ref.DIRS[i], c);
            } else {
                buildLookup(covers.get(Ref.DIRS[i]).getCover(),COVERNONE, Ref.DIRS[i]);
                covers.put(Ref.DIRS[i], new CoverStack<>(Data.COVERNONE, this.tile, Ref.DIRS[i]));
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
        CoverStack<T> toPlace = new CoverStack<>(oldStack, newSide);
        if (!removeCover(entity, oldSide, true)) return false;
        boolean ok = set(newSide,newStack, toPlace, true);
        if (ok) {
            sync();
        }
        return ok;
    }

    /**
     * Checks whether a cover would block capability on this side.
     * @param side side to check
     * @return a boolean whether or not capability was blocked.
     */
    public <U> boolean blocksCapability(Capability<U> capability, Direction side) {
        CoverStack<?> stack = get(side);
        if (stack.isEmpty()) return false;
        return stack.getCover().blocksCapability(stack, capability, side);
    }

    /**
     * Returns a list of item stacks to be dropped upon machine removal.
     * @return list.
     */
    public List<ItemStack> getDrops() {
        return this.covers.values().stream().filter(t -> !t.getCover().getDroppedStack().isEmpty()).map(t ->
            t.getCover().getDroppedStack()).collect(Collectors.toList());
    }
}
