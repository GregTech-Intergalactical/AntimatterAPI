package muramasa.antimatter.structure;

import it.unimi.dsi.fastutil.longs.*;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Set;

/**
 * StructureCache represents an efficient cache of multiblock structures in Antimatter. It listens to block updates to send updates to controllers.
 * StructureCache also ensures MAX_SHARES in multiblocks are handled.
 * It also supports listeners for positions to keep track of valid multiblocks.
 */
public class StructureCache {

    private static final Object2ObjectMap<Level, DimensionEntry> LOOKUP = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectMap<Level, Long2ObjectMap<Set<StructureHandle<?>>>> CALLBACKS = new Object2ObjectOpenHashMap<>();

    static {
        AntimatterAPI.registerBlockUpdateHandler((world, pos, oldState, newState, flags) -> {
            if (oldState == newState) return;  // TODO: better checks?
            //if no block update is actually queried, ignore it here.
            if ((flags & (1)) == 0) {
                return;
            }
            DimensionEntry entry = LOOKUP.get(world);
            if (entry == null) return;
            LongSet controllerPos = entry.get(pos);
            if (controllerPos != null && controllerPos.size() > 0) {
                controllerPos.forEach((controller) -> {
                    if (controller != pos.asLong()) {
                        refreshController(world, BlockPos.of(controller), pos);
                    }
                });
            }
        });
    }

    /**
     * Validates a multiblock (To ensure max shares is not exceeded).
     *
     * @param world     tile world.
     * @param pos       controller position.
     * @param structure a packed list of multiblock positions.
     * @param maxAmount maximum number of shares allowed (0 == none).
     * @return if it was successfully added.
     */
    public static boolean validate(Level world, BlockPos pos, LongList structure, int maxAmount) {
        DimensionEntry e = LOOKUP.computeIfAbsent(world, w -> new DimensionEntry());
        if (!has(world, pos)) {
            boolean ok = e.validate(pos, maxAmount, structure);
            return ok;
        }
        return false;
    }

    /**
     * Returns the number of multiblocks using this position.
     *
     * @param world the controller world.
     * @param pos   the position.
     * @return the amount of usage.
     */
    public static int refCount(Level world, BlockPos pos) {
        DimensionEntry entry = LOOKUP.get(world);
        if (entry == null) return 0;

        LongSet e = entry.get(pos);
        return e == null ? 0 : e.size();
    }

    /**
     * Is there a structure using this position?
     *
     * @param world Controller world.
     * @param pos   Relevant position.
     * @return if it is active.
     */
    public static boolean has(Level world, BlockPos pos) {
        DimensionEntry entry = LOOKUP.get(world);
        if (entry == null) return false;
        long p = pos.asLong();
        LongList l = entry.CONTROLLER_TO_STRUCTURE.get(p);
        if (l == null || l.size() == 0) return false;
        return entry.STRUCTURE_TO_CONTROLLER.get(l.iterator().nextLong()).contains(p);
    }

    /**
     * Returns all controller positions for the @pos parameter.
     *
     * @param world relevant world.
     * @param pos   structure position.
     * @return a set of Positions in long form tof formed structures
     */
    @Nullable
    public static LongSet get(Level world, BlockPos pos) {
        DimensionEntry entry = LOOKUP.get(world);
        return entry != null ? entry.get(pos) : null;
    }

    /**
     * Attempts to get a multiblock that is of class clazz from the given structure position.
     *
     * @param world the controller world.
     * @param pos   the structure position.
     * @param clazz the tile class.
     * @param <T>   any relevant multi tile.
     * @return a nullable Tile Entity.
     */
    @Nullable
    public static <T extends TileEntityBasicMultiMachine> T getAnyMulti(Level world, BlockPos pos, Class<T> clazz) {
        DimensionEntry entry = LOOKUP.get(world);
        if (entry == null) return null;
        LongSet list = entry.get(pos);
        if (list == null || list.size() == 0) return null;
        for (long e : list) {
            BlockPos p = BlockPos.of(e);
            BlockEntity tile = world.getBlockEntity(p);
            if (clazz.isInstance(tile)) return (T) tile;
        }
        return null;
    }

    /**
     * Adds a structure to the cache.
     *
     * @param world     the world the structure is in.
     * @param pos       controller position.
     * @param structure BlockPos-packed positions
     */
    public static void add(Level world, BlockPos pos, LongList structure) {
        DimensionEntry entry = LOOKUP.computeIfAbsent(world, e -> new DimensionEntry());
        entry.add(pos, structure);
        notifyListenersAdd(world, pos);
    }

    /**
     * Remove a controller from the structure cache, either valid or invalid.
     *
     * @param world the controller world.
     * @param pos   the controller position.
     */
    public static void remove(Level world, BlockPos pos) {
        DimensionEntry entry = LOOKUP.get(world);
        if (entry == null) return;
        entry.remove(pos);
        notifyListenersRemove(world, pos);
    }

    private static void refreshController(Level world, BlockPos controller, BlockPos at) {
        BlockEntity tile = world.getBlockEntity(controller);
        if (tile instanceof TileEntityMachine<?> machine) machine.onBlockUpdate(at);
    }

    /**
     * Adds a structure listener to the cache. This listener is notified if there is a multiblock present at pos, either added
     * or removed.
     *
     * @param handle the structurehandle to call.
     * @param world  the tile world.
     * @param pos    the position to listen at.
     */
    public static void addListener(StructureHandle<?> handle, Level world, BlockPos pos) {
        Long2ObjectMap<Set<StructureHandle<?>>> map = CALLBACKS.computeIfAbsent(world, k -> new Long2ObjectOpenHashMap<>());
        Set<StructureHandle<?>> set = map.computeIfAbsent(pos.asLong(), k -> new ObjectOpenHashSet<>());
        set.add(handle);
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile != null && has(world, pos)) {
            handle.structureCacheAddition(tile);
        }
    }

    /**
     * Removes a structurelistener, stopping all callbacks.
     *
     * @param handle the handle
     * @param world  the relevant world.
     * @param pos    the blockpos.
     */
    public static void removeListener(StructureHandle<?> handle, Level world, BlockPos pos) {
        Long2ObjectMap<Set<StructureHandle<?>>> map = CALLBACKS.get(world);
        if (map != null) {
            Set<StructureHandle<?>> set = map.get(pos.asLong());
            if (set != null) {
                set.remove(handle);
            }
        }
    }

    private static void notifyListenersAdd(Level world, BlockPos pos) {
        Long2ObjectMap<Set<StructureHandle<?>>> map = CALLBACKS.get(world);
        if (map != null) {
            BlockEntity tile = world.getBlockEntity(pos);
            map.getOrDefault(pos.asLong(), Collections.emptySet()).forEach(handle -> handle.structureCacheAddition(tile));
        }
    }

    private static void notifyListenersRemove(Level world, BlockPos pos) {
        Long2ObjectMap<Set<StructureHandle<?>>> map = CALLBACKS.get(world);
        if (map != null) {
            map.getOrDefault(pos.asLong(), Collections.emptySet()).forEach(StructureHandle::structureCacheRemoval);
        }
    }

    public static void onWorldUnload(LevelAccessor world) {
        LOOKUP.remove((Level) world);
        Long2ObjectMap<Set<StructureHandle<?>>> map = CALLBACKS.remove((Level) world);
        if (map != null)
            map.forEach((k, v) -> v.forEach(StructureHandle::structureCacheRemoval));
    }

    public static class DimensionEntry {

        private final Long2ObjectMap<LongSet> STRUCTURE_TO_CONTROLLER = new Long2ObjectOpenHashMap<>(); //Structure Position -> Controller Position
        private final Long2ObjectMap<LongList> CONTROLLER_TO_STRUCTURE = new Long2ObjectOpenHashMap<>(); //Controller Pos -> All Structure Positions

        public DimensionEntry() {
            //STRUCTURE_TO_CONTROLLER.setDefaultReturnValue(Object2BooleanMaps.empty());
            CONTROLLER_TO_STRUCTURE.defaultReturnValue(LongLists.EMPTY_LIST);
        }

        @Nullable
        public LongSet get(BlockPos pos) {
            return STRUCTURE_TO_CONTROLLER.get(pos.asLong());
        }

        public void add(BlockPos pos, LongList structure) {
            long at = pos.asLong();
            CONTROLLER_TO_STRUCTURE.put(at, structure);
            for (long s : structure) {
                STRUCTURE_TO_CONTROLLER.compute(s, (k, v) -> {
                    if (v == null) {
                        v = new LongOpenHashSet();
                    }
                    v.add(pos.asLong());
                    return v;
                });
            }
        }

        public boolean validate(BlockPos pos, int maxAmount, LongList structure) {
            long at = pos.asLong();
            int i = structure.stream().mapToInt(t -> {
                LongSet list = this.STRUCTURE_TO_CONTROLLER.get((long)t);
                if (list == null){
                    return 0;
                }
                return list.size();
            }).max().orElse(0);
            return i <= maxAmount;
        }

        public void remove(BlockPos pos) {
            long at = pos.asLong();
            LongList structure = CONTROLLER_TO_STRUCTURE.remove(at);
            for (long s : structure) {
                STRUCTURE_TO_CONTROLLER.compute(s, (k, v) -> {
                    if (v == null) return null;
                    if (v.size() == 0) return null;
                    v.remove(pos.asLong());
                    return v;
                });
            }
        }
    }
}
