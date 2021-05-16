package muramasa.antimatter.structure;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongLists;
import it.unimi.dsi.fastutil.objects.*;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.LongConsumer;

@Mod.EventBusSubscriber
public class StructureCache {

    private static final Object2ObjectMap<World, DimensionEntry> LOOKUP = new Object2ObjectOpenHashMap<>();

    static {
        AntimatterAPI.registerBlockUpdateHandler((world, pos, oldState, newState, flags) -> {
            if (oldState == newState) return;  // TODO: better checks?
            StructureCache.DimensionEntry entry = LOOKUP.get(world);
            if (entry == null) return;
            Object2BooleanMap<BlockPos> controllerPos = entry.get(pos);
            if (controllerPos.size() > 0) {
                controllerPos.forEach((p, valid) -> {
                    if (!p.equals(pos)) {
                        refreshController(world, p, pos);
                    }
                });
            }
        });
    }

    public static boolean validate(World world, BlockPos pos, LongList structure, int maxAmount) {
        DimensionEntry e = LOOKUP.get(world);
        if (e != null) {
            if (!has(world, pos)) {
                return e.validate(pos, maxAmount, structure);
            }
        }
        return false;
    }

    public static int refCount(World world, BlockPos pos) {
        DimensionEntry entry = LOOKUP.get(world);
        if (entry == null) return 0;
        return entry.get(pos).values().stream().mapToInt(t -> t ? 1 : 0).sum();
    }

    public static boolean has(World world, BlockPos pos) {
        DimensionEntry entry = LOOKUP.get(world);
        if (entry == null) return false;
        long p = pos.toLong();
        LongList l = entry.CONTROLLER_TO_STRUCTURE.get(p);
        if (l == null) return false;
        return entry.STRUCTURE_TO_CONTROLLER.get(l.iterator().nextLong()).getBoolean(pos);
    }

    @Nullable
    public static Object2BooleanMap<BlockPos> get(World world, BlockPos pos) {
        DimensionEntry entry = LOOKUP.get(world);
        return entry != null ? entry.get(pos) : null;
    }

    @Nullable
    public static <T extends TileEntityMachine> T getAnyMulti(World world, BlockPos pos, Class<T> clazz) {
        DimensionEntry entry = LOOKUP.get(world);
        if (entry == null) return null;
        Object2BooleanMap<BlockPos> list = entry.get(pos);
        if (list.size() == 0) return null;
        for (Object2BooleanMap.Entry<BlockPos> e : list.object2BooleanEntrySet()) {
            TileEntity tile = world.getTileEntity(e.getKey());
            if (tile != null && clazz.isInstance(tile) && e.getBooleanValue()) return (T) tile;
        }
        return null;
    }

    /**
     * Adds a structure to the cache.
     * @param world the world the structure is in.
     * @param pos controller position.
     * @param structure BlockPos-packed positions
     */
    public static void add(World world, BlockPos pos, LongList structure) {
        DimensionEntry entry = LOOKUP.computeIfAbsent(world, e -> new DimensionEntry());
        entry.add(pos, structure);
    }

    public static void remove(World world, BlockPos pos) {
        DimensionEntry entry = LOOKUP.get(world);
        if (entry == null) return;
        entry.remove(pos);
    }

    public static void invalidate(World world, BlockPos pos, LongList structure) {
        DimensionEntry entry = LOOKUP.get(world);
        if (entry != null) {
            if (has(world, pos))
                entry.invalidate(pos, structure);
        }
    }

    private static void refreshController(World world, BlockPos controller, BlockPos at) {
        TileEntity tile = world.getTileEntity(controller);
        if (tile instanceof TileEntityBasicMultiMachine) ((TileEntityBasicMultiMachine) tile).onBlockUpdate(at);
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload e) {
        LOOKUP.remove((World)e.getWorld());
    }

    public static class DimensionEntry {

        private final Long2ObjectMap<Object2BooleanMap<BlockPos>> STRUCTURE_TO_CONTROLLER = new Long2ObjectOpenHashMap<>(); //Structure Position -> Controller Position
        private final Long2ObjectMap<LongList> CONTROLLER_TO_STRUCTURE = new Long2ObjectOpenHashMap<>(); //Controller Pos -> All Structure Positions

        public DimensionEntry() {
            STRUCTURE_TO_CONTROLLER.defaultReturnValue(Object2BooleanMaps.emptyMap());
            CONTROLLER_TO_STRUCTURE.defaultReturnValue(LongLists.EMPTY_LIST);
        }

        @Nonnull
        public Object2BooleanMap<BlockPos> get(BlockPos pos) {
            return STRUCTURE_TO_CONTROLLER.get(pos.toLong());
        }

        public void add(BlockPos pos, LongList structure) {
            long at = pos.toLong();
            CONTROLLER_TO_STRUCTURE.put(at, structure);
            for (long s : structure) {
                STRUCTURE_TO_CONTROLLER.compute(s, (k,v) -> {
                    if (v == null) {
                        v = new Object2BooleanOpenHashMap<>();
                    }
                    v.put(pos, false);
                    return v;
                });
            }
        }

        public boolean validate(BlockPos pos, int maxAmount, LongList structure) {
            long at = pos.toLong();
            int i = structure.stream().mapToInt(t -> this.STRUCTURE_TO_CONTROLLER.get((long)t).values().stream().mapToInt(j -> j ? 1 : 0).sum()).max().orElse(0);
            if (i <= maxAmount) {
                LongList old = this.CONTROLLER_TO_STRUCTURE.remove(at);
                old.forEach((LongConsumer) l -> this.STRUCTURE_TO_CONTROLLER.compute(l, (k,v) -> {
                    if (v == null) return null;
                    if (v.size() == 1) return null;
                    v.removeBoolean(pos);
                    return v;
                }));
                this.CONTROLLER_TO_STRUCTURE.put(at, structure);
                structure.forEach((LongConsumer) t -> this.STRUCTURE_TO_CONTROLLER.compute(t, (k,v) -> {
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
            long at = pos.toLong();
            LongList old = this.CONTROLLER_TO_STRUCTURE.put(at, structure);
            old.forEach((LongConsumer) l -> this.STRUCTURE_TO_CONTROLLER.compute(l, (k,v) -> {
                if (v == null) return null;
                if (v.size() == 1) return null;
                v.removeBoolean(pos);
                return v;
            }));
            for (long s : structure) {
                STRUCTURE_TO_CONTROLLER.compute(s, (k,v) -> {
                    if (v == null) {
                        v = new Object2BooleanOpenHashMap<>();
                    }
                    v.put(pos, false);
                    return v;
                });
            }
        }

        public void remove(BlockPos pos) {
            long at = pos.toLong();
            LongList structure = CONTROLLER_TO_STRUCTURE.remove(at);
            for (long s : structure) {
                STRUCTURE_TO_CONTROLLER.compute(s, (k,v) -> {
                    if (v == null) return null;
                    if (v.size() == 1) return null;
                    v.removeBoolean(pos);
                    return v;
                } );
            }
        }
    }
}
