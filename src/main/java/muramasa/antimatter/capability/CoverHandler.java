package muramasa.antimatter.capability;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.Ref;
import muramasa.antimatter.client.dynamic.DynamicTexturer;
import muramasa.antimatter.client.dynamic.DynamicTexturers;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

import static muramasa.antimatter.Data.ELECTRIC_WRENCH;
import static muramasa.antimatter.Data.WRENCH;

public class CoverHandler<T extends TileEntity> implements ICoverHandler<T> {

    private final LazyOptional<ICoverHandler<T>> handler = LazyOptional.of(() -> this);

    private final T tile;
    protected final Object2ObjectMap<Direction, ICover> covers = new Object2ObjectOpenHashMap<>(6);
    protected final Object2ObjectMap<CoverFactory, Set<Direction>> reverseLookup = new Object2ObjectOpenHashMap<>(6);
    protected List<ResourceLocation> validCovers = new ObjectArrayList<>();
    public final Map<Direction, DynamicTexturer<ICover, ICover.DynamicKey>> coverTexturer;

    public CoverHandler(T tile, ICover... validCovers) {
        this.tile = tile;
        this.validCovers.add(new ResourceLocation(ICover.empty.getDomain(), ICover.empty.getId()));
        Arrays.stream(validCovers).forEach(c -> this.validCovers.add(new ResourceLocation(c.getDomain(), c.getId())));
        Arrays.stream(Ref.DIRS).forEach(t -> this.set(t, ICover.empty, false));
        coverTexturer = new HashMap<>(6);// LazyHolder.of(() -> new
        // DynamicTexturer<>(DynamicTexturers.COVER_DYNAMIC_TEXTURER));
    }

    @OnlyIn(Dist.CLIENT)
    public DynamicTexturer<ICover, ICover.DynamicKey> getTexturer(Direction dir) {
        return coverTexturer.computeIfAbsent(dir, d -> new DynamicTexturer<>(DynamicTexturers.COVER_DYNAMIC_TEXTURER));
    }

    @Override
    public boolean set(Direction side, @Nonnull ICover newCover, boolean sync) {
        ICover old = covers.getOrDefault(side, ICover.empty);
        return set(side, old, newCover, sync);
    }

    public boolean set(Direction side, ICover old, ICover stack, boolean sync) {
        covers.put(side, stack); // Emplace newCover, calls onPlace!
        buildLookup(old.getFactory(), stack.getFactory(), side);
        old.onRemove();
        stack.onPlace();
        if (tile.getWorld() != null && sync) {
            sync();
        }
        return true;
    }

    protected void sync() {
        World world = tile.getWorld();
        if (world == null)
            return;
        if (!world.isRemote) {
            tile.markDirty();
            Utils.markTileForNBTSync(tile);
        } else {
            Utils.markTileForRenderUpdate(tile);
            tile.getWorld().playSound(null, tile.getPos(), SoundEvents.BLOCK_METAL_PLACE, SoundCategory.BLOCKS, 1.0f,
                    1.0f);
        }
    }

    protected void buildLookup(CoverFactory oldCover, CoverFactory newCover, Direction dir) {
        Set<Direction> set = reverseLookup.compute(oldCover, (k, v) -> {
            if (v == null)
                v = new ObjectOpenHashSet<>();
            v.remove(dir);
            return v;
        });
        reverseLookup.compute(newCover, (k, v) -> {
            if (v == null)
                v = new ObjectOpenHashSet<>();
            v.add(dir);
            return v;
        });
        if (set.isEmpty())
            reverseLookup.remove(oldCover);
    }

    public Set<Direction> lookup(CoverFactory c) {
        return reverseLookup.get(c);
    }

    @Nullable
    public Direction lookupSingle(CoverFactory c) {
        Set<Direction> set = reverseLookup.get(c);
        if (set != null && set.size() == 1)
            return set.iterator().next();
        return null;
    }

    @Override
    public ICover get(Direction side) {
        return covers.getOrDefault(side, ICover.empty); // Should never return null, as COVER_NONE is inserted for every
        // direction
    }

    public ICover[] getAll() {
        return covers.values().toArray(new ICover[0]);
    }

    @Override
    public T getTile() {
        if (tile == null)
            throw new NullPointerException("CoverHandler cannot have a null tile");
        return tile;
    }

    @Override
    public void onUpdate() {
        covers.forEach((s, c) -> {
            if (c.ticks()) {
                c.onUpdate();
            }
        });
    }

    @Override
    public void onRemove() {
        covers.forEach((s, c) -> c.onRemove());
    }

    @Override
    public boolean onInteract(PlayerEntity player, Hand hand, Direction side, @Nullable AntimatterToolType type) {
        return covers.get(side).onInteract(player, hand, side, type);
    }

    @Override
    public boolean placeCover(PlayerEntity player, Direction side, ItemStack stack, ICover cover) {
        if (!get(side).isEmpty() || !set(side, cover, true))
            return false;
        if (!player.isCreative())
            stack.shrink(1);
        return true;
    }

    @Override
    public boolean removeCover(PlayerEntity player, Direction side, boolean onlyRemove) {
        ICover oldCover = get(side);
        if (!onlyRemove && !canRemoveCover(oldCover))
            return false;
        if (get(side).isEmpty() || !set(side, ICover.empty, !onlyRemove))
            return false;
        if (!onlyRemove && !player.isCreative())
            player.dropItem(oldCover.getDroppedStack(), false);
        if (Utils.getToolType(player) != WRENCH && Utils.getToolType(player) != ELECTRIC_WRENCH) {
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
    public boolean hasCover(Class<? extends ICover> clazz) {
        return this.reverseLookup.containsKey(clazz);
    }

    @Override
    public boolean isValid(@Nonnull Direction side, @Nonnull ICover replacement) {
        return (get(side).isEmpty() || replacement.isEqual(ICover.empty)) && validCovers.contains(replacement.getId());
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        byte[] sides = new byte[1];
        covers.forEach((s, c) -> {
            if (!c.isEmpty()) { // Don't store EMPTY covers unnecessarily
                sides[0] |= (1 << s.getIndex());
                CoverFactory.writeCover(tag, c);
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
                ICover cover = CoverFactory.readCover(this, Direction.byIndex(i), nbt);
                buildLookup(covers.get(Ref.DIRS[i]).getFactory(), cover.getFactory(), Ref.DIRS[i]);
                covers.put(Ref.DIRS[i], cover);
            } else {
                buildLookup(covers.get(Ref.DIRS[i]).getFactory(), ICover.emptyFactory, Ref.DIRS[i]);
                covers.put(Ref.DIRS[i], ICover.empty);
            }
        }
        World w = tile.getWorld();
        if (w != null && w.isRemote) {
            Utils.markTileForRenderUpdate(this.tile);
        }
    }

    @Nonnull
    @Override
    public <U> LazyOptional<U> getCapability(@Nonnull Capability<U> cap, @Nullable Direction side) {
        return handler.cast();
    }

    @Override
    public boolean moveCover(PlayerEntity entity, Direction oldSide, Direction newSide) {
        // Have to move the entire stack, due to possible tag data.
        ICover newStack = get(newSide);
        ICover oldStack = get(oldSide);
        if (!newStack.isEmpty() || oldStack.isEmpty())
            return false;
        if (!removeCover(entity, oldSide, true))
            return false;
        CoverFactory factory = oldStack.getFactory();
        ICover copy = factory.get().get(this, oldStack.getTier(), newSide, factory);
        copy.deserialize(oldStack.serialize());
        boolean ok = set(newSide, newStack, copy, true);
        if (ok) {
            sync();
        }
        return ok;
    }

    /**
     * Checks whether a cover would block capability on this side.
     *
     * @param side side to check
     * @return a boolean whether or not capability was blocked.
     */
    public <U> boolean blocksCapability(Capability<U> capability, Direction side) {
        ICover stack = get(side);
        if (stack == null)
            return false;
        return stack.blocksCapability(capability, side);
    }

    /**
     * Returns a list of item stacks to be dropped upon machine removal.
     *
     * @return list.
     */
    public List<ItemStack> getDrops() {
        return this.covers.values().stream().map(ICover::getDroppedStack)
                .filter(droppedStack -> !droppedStack.isEmpty()).collect(Collectors.toList());
    }
}
