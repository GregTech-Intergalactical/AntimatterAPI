package muramasa.antimatter.structure;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongLists;
import it.unimi.dsi.fastutil.objects.*;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;
import java.util.function.LongConsumer;

/**
 * StructureCache represents an efficient cache of multiblock structures in Antimatter. It listens to block updates to send updates to controllers.
 * StructureCache also ensures MAX_SHARES in multiblocks are handled.
 * It also supports listeners for positions to keep track of valid multiblocks.
 */
@Mod.EventBusSubscriber
public class StructureCache {

    private static final Object2ObjectMap<World, DimensionEntry> LOOKUP = new Object2ObjectOpenHashMap<>();
    private static final Object2ObjectMap<World, Long2ObjectMap<Set<StructureHandle<?>>>> CALLBACKS = new Object2ObjectOpenHashMap<>();

    static {
        AntimatterAPI.registerBlockUpdateHandler((world, pos, oldState, newState, flags) -> {
            if (oldState == newState) return;  // TODO: better checks?
            //if no block update is actually queried, ignore it here.
            if ((flags & (1 << 0)) == 0) {
                return;
            }
            StructureCache.DimensionEntry entry = LOOKUP.get(world);
            if (entry == null) return;
            Object2BooleanMap<BlockPos> controllerPos = entry.get(pos);
            if (controllerPos != null && controllerPos.size() > 0) {
                controllerPos.forEach((p, valid) -> {
                    if (!p.equals(pos)) {
                        refreshController(world, p, pos);
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
    public static boolean validate(World world, BlockPos pos, LongList structure, int maxAmount) {
        DimensionEntry e = LOOKUP.get(world);
        if (e != null) {
            if (!has(world, pos)) {
                boolean ok = e.validate(pos, maxAmount, structure);
                if (ok) {
                    notifyListenersAdd(world, pos);
                }
                return ok;
            }
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
    public static int refCount(World world, BlockPos pos) {
        DimensionEntry entry = LOOKUP.get(world);
        if (entry == null) return 0;

        Object2BooleanMap<BlockPos> e = entry.get(pos);
        return e == null ? 0 : e.values().stream().mapToInt(t -> t ? 1 : 0).sum();
    }

    /**
     * Is there a structure using this position?
     *
     * @param world Controller world.
     * @param pos   Relevant position.
     * @return if it is active.
     */
    public static boolean has(World world, BlockPos pos) {
        DimensionEntry entry = LOOKUP.get(world);
        if (entry == null) return false;
        long p = pos.asLong();
        LongList l = entry.CONTROLLER_TO_STRUCTURE.get(p);
        if (l == null || l.size() == 0) return false;
        return entry.STRUCTURE_TO_CONTROLLER.get(l.iterator().nextLong()).getBoolean(pos);
    }

    /**
     * Returns all controller positions for the @pos parameter.
     *
     * @param world relevant world.
     * @param pos   structure position.
     * @return a mapping of positions, where boolean is the validity state. (True = formed).
     */
    @Nullable
    public static Object2BooleanMap<BlockPos> get(World world, BlockPos pos) {
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
    public static <T extends TileEntityBasicMultiMachine> T getAnyMulti(World world, BlockPos pos, Class<T> clazz) {
        DimensionEntry entry = LOOKUP.get(world);
        if (entry == null) return null;
        Object2BooleanMap<BlockPos> list = entry.get(pos);
        if (list == null || list.size() == 0) return null;
        for (Object2BooleanMap.Entry<BlockPos> e : list.object2BooleanEntrySet()) {
            TileEntity tile = world.getBlockEntity(e.getKey());
            if (tile != null && clazz.isInstance(tile) && e.getBooleanValue()) return (T) tile;
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
    public static void add(World world, BlockPos pos, LongList structure) {
        DimensionEntry entry = LOOKUP.computeIfAbsent(world, e -> new DimensionEntry());
        entry.add(pos, structure);
    }

    /**
     * Remove a controller from the structure cache, either valid or invalid.
     *
     * @param world the controller world.
     * @param pos   the controller position.
     */
    public static void remove(World world, BlockPos pos) {
        DimensionEntry entry = LOOKUP.get(world);
        if (entry == null) return;
        entry.remove(pos);
    }

    /**
     * Invalidates a multiblock, keeping it in the cache but setting it as invalid.
     *
     * @param world     controller world.
     * @param pos       controller position.
     * @param structure packed multi structure.
     */
    public static void invalidate(World world, BlockPos pos, LongList structure) {
        DimensionEntry entry = LOOKUP.get(world);
        if (entry != null) {
            if (has(world, pos)) {
                notifyListenersRemove(world, pos);
                entry.invalidate(pos, structure);
            }
        }
    }

    private static void refreshController(World world, BlockPos controller, BlockPos at) {
        TileEntity tile = world.getBlockEntity(controller);
        if (tile instanceof TileEntityBasicMultiMachine) ((TileEntityBasicMultiMachine) tile).onBlockUpdate(at);
    }

    /**
     * Adds a structure listener to the cache. This listener is notified if there is a multiblock present at pos, either added
     * or removed.
     *
     * @param handle the structurehandle to call.
     * @param world  the tile world.
     * @param pos    the position to listen at.
     */
    public static void addListener(StructureHandle<?> handle, World world, BlockPos pos) {
        Long2ObjectMap<Set<StructureHandle<?>>> map = CALLBACKS.computeIfAbsent(world, k -> new Long2ObjectOpenHashMap<>());
        Set<StructureHandle<?>> set = map.computeIfAbsent(pos.asLong(), k -> new ObjectOpenHashSet<>());
        set.add(handle);
        TileEntity tile = world.getBlockEntity(pos);
        if (tile != null) handle.structureCacheAddition(tile);
    }

    /**
     * Removes a structurelistener, stopping all callbacks.
     *
     * @param handle the handle
     * @param world  the relevant world.
     * @param pos    the blockpos.
     */
    public static void removeListener(StructureHandle<?> handle, World world, BlockPos pos) {
        Long2ObjectMap<Set<StructureHandle<?>>> map = CALLBACKS.get(world);
        if (map != null) {
            Set<StructureHandle<?>> set = map.get(pos.asLong());
            if (set != null) {
                set.remove(handle);
            }
        }
    }

    private static void notifyListenersAdd(World world, BlockPos pos) {
        Long2ObjectMap<Set<StructureHandle<?>>> map = CALLBACKS.get(world);
        if (map != null) {
            TileEntity tile = world.getBlockEntity(pos);
            map.getOrDefault(pos.asLong(), Collections.emptySet()).forEach(handle -> handle.structureCacheAddition(tile));
        }
    }

    private static void notifyListenersRemove(World world, BlockPos pos) {
        Long2ObjectMap<Set<StructureHandle<?>>> map = CALLBACKS.get(world);
        if (map != null) {
            map.getOrDefault(pos.asLong(), Collections.emptySet()).forEach(StructureHandle::structureCacheRemoval);
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload e) {
        LOOKUP.remove((World) e.getWorld());
        Long2ObjectMap<Set<StructureHandle<?>>> map = CALLBACKS.remove((World) e.getWorld());
        if (map != null)
            map.forEach((k, v) -> v.forEach(StructureHandle::structureCacheRemoval));
    }

    public static class DimensionEntry {

        private final Long2ObjectMap<Object2BooleanMap<BlockPos>> STRUCTURE_TO_CONTROLLER = new Long2ObjectOpenHashMap<>(); //Structure Position -> Controller Position
        private final Long2ObjectMap<LongList> CONTROLLER_TO_STRUCTURE = new Long2ObjectOpenHashMap<>(); //Controller Pos -> All Structure Positions

        public DimensionEntry() {
            //STRUCTURE_TO_CONTROLLER.setDefaultReturnValue(Object2BooleanMaps.empty());
            CONTROLLER_TO_STRUCTURE.defaultReturnValue(LongLists.EMPTY_LIST);
        }

        @Nullable
        public Object2BooleanMap<BlockPos> get(BlockPos pos) {
            return STRUCTURE_TO_CONTROLLER.get(pos.asLong());
        }

        public void add(BlockPos pos, LongList structure) {
            long at = pos.asLong();
            CONTROLLER_TO_STRUCTURE.put(at, structure);
            for (long s : structure) {
                STRUCTURE_TO_CONTROLLER.compute(s, (k, v) -> {
                    if (v == null) {
                        v = new Object2BooleanOpenHashMap<>();
                    }
                    v.put(pos, false);
                    return v;
                });
            }
        }

        public boolean validate(BlockPos pos, int maxAmount, LongList structure) {
            long at = pos.asLong();
            int i = structure.stream().mapToInt(t -> {
                Object2BooleanMap<BlockPos> map = this.STRUCTURE_TO_CONTROLLER.get((long) t);
                if (map == null) {
                    Antimatter.LOGGER.warn("Invalid state in StructureCache, map should not be null");//throw new RuntimeException("Invalid state in StructureCache, map should not be null");
                    return Integer.MAX_VALUE;
                }
                return map.values().stream().mapToInt(j -> j ? 1 : 0).sum();
            }).max().orElse(0);
            if (i <= maxAmount) {
                LongList old = this.CONTROLLER_TO_STRUCTURE.remove(at);
                old.forEach((LongConsumer) l -> this.STRUCTURE_TO_CONTROLLER.compute(l, (k, v) -> {
                    if (v == null) return null;
                    if (v.size() == 1) return null;
                    v.remove(pos);
                    return v;
                }));
                this.CONTROLLER_TO_STRUCTURE.put(at, structure);
                structure.forEach((LongConsumer) t -> this.STRUCTURE_TO_CONTROLLER.compute(t, (k, v) -> {
                    if (v == null) {
                        v = new Object2BooleanOpenHashMap<>();
                    }
                    v.put(pos, true);
                    return v;
                }));
                return true;
            }
            return false;
        }

        public void invalidate(BlockPos pos, LongList structure) {
            long at = pos.asLong();
            LongList old = this.CONTROLLER_TO_STRUCTURE.put(at, structure);
            old.forEach((LongConsumer) l -> this.STRUCTURE_TO_CONTROLLER.compute(l, (k, v) -> {
                if (v == null) return null;
                if (v.size() == 1) return null;
                v.remove(pos);
                return v;
            }));
            for (long s : structure) {
                STRUCTURE_TO_CONTROLLER.compute(s, (k, v) -> {
                    if (v == null) {
                        v = new Object2BooleanOpenHashMap<>();
                    }
                    v.put(pos, false);
                    return v;
                });
            }
        }

        public void remove(BlockPos pos) {
            long at = pos.asLong();
            LongList structure = CONTROLLER_TO_STRUCTURE.remove(at);
            for (long s : structure) {
                STRUCTURE_TO_CONTROLLER.compute(s, (k, v) -> {
                    if (v == null) return null;
                    if (v.size() == 1) return null;
                    v.remove(pos);
                    return v;
                });
            }
        }
    }
}
