package muramasa.antimatter.structure;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongList;
import it.unimi.dsi.fastutil.longs.LongLists;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Set;

@Mod.EventBusSubscriber
public class StructureCache {

    private static final Long2ObjectMap<DimensionEntry> LOOKUP = new Long2ObjectOpenHashMap<>();

    static {
        AntimatterAPI.registerBlockUpdateHandler((world, pos, oldState, newState) -> {
            if (oldState == newState) return;  // TODO: better checks?
            if (newState.isIn(Data.PROXY_INSTANCE)) return;
            StructureCache.DimensionEntry entry = LOOKUP.get(getDimId(world));
            if (entry == null) return;
            Set<BlockPos> controllerPos = entry.get(pos);
            if (controllerPos.size() > 0) {
                controllerPos.forEach(p -> {
                    if (!p.equals(pos)) {
                        invalidateController(world, p);
                    }
                });
            }
        });
    }

    public static int refCount(World world, BlockPos pos) {
        DimensionEntry entry = LOOKUP.get(getDimId(world));
        if (entry == null) return 0;
        return entry.get(pos).size();
    }

    public static boolean has(World world, BlockPos pos) {
        DimensionEntry entry = LOOKUP.get(getDimId(world));
        if (entry == null) return false;
        return entry.get(pos).size() > 0;
    }

    @Nullable
    public static Set<BlockPos> get(World world, BlockPos pos) {
        DimensionEntry entry = LOOKUP.get(getDimId(world));
        return entry != null ? entry.get(pos) : null;
    }

    @Nullable
    public static <T extends TileEntityMachine> T getAnyMulti(World world, BlockPos pos, Class<T> clazz) {
        DimensionEntry entry = LOOKUP.get(getDimId(world));
        if (entry == null) return null;
        Set<BlockPos> list = entry.get(pos);
        if (list.size() == 0) return null;
        for (BlockPos blockPos : list) {
            TileEntity tile = world.getTileEntity(blockPos);
            if (tile != null && clazz.isInstance(tile)) return (T) tile;
        }
        return null;
    }

    /**
     * Adds a structure to the cache.
     * @param world the world the structure is in.
     * @param pos controller position.
     * @param structure BlockPos-packed positions
     * @param maxAmount how many shared controllers per block.
     * @return whether or not structure was added successfully.
     */
    public static boolean add(World world, BlockPos pos, LongList structure, int maxAmount) {
        DimensionEntry entry = LOOKUP.computeIfAbsent(getDimId(world), e -> new DimensionEntry());
        boolean ok = entry.add(pos, structure, maxAmount);
        if (ok) {
            //Antimatter.LOGGER.info("Added Structure to Store!");
        } else {
            remove(world, pos);
        }
        return ok;
    }

    public static void remove(World world, BlockPos pos) {
        DimensionEntry entry = LOOKUP.get(getDimId(world));
        if (entry == null) return;
        entry.remove(pos);
        //Antimatter.LOGGER.info("Removed Structure to Store!");
    }
    //just to switch between server & client. You can use two maps but y tho
    private static long getDimId(World world) {
        RegistryKey<World> w = world.getDimensionKey();
        int offset = world instanceof ServerWorld ? 1 : 0;
        return (((long)w.getLocation().hashCode()) << 32) | (offset & 0xffffffffL);
    }

    private static void invalidateController(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityBasicMultiMachine) ((TileEntityBasicMultiMachine) tile).invalidateStructure();
        remove(world, pos);
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload e) {
        LOOKUP.remove(getDimId((World)e.getWorld()));
    }

//    @SubscribeEvent
//    public static void onBlockClickEvent(PlayerInteractEvent.RightClickBlock e) {
//        if (e.getEntityPlayer().isSneaking()) return;
//        DimensionEntry entry = LOOKUP.get(e.getWorld().getDimension().getType().getId());
//        if (entry == null) return;
//        BlockPos controllerPos = entry.get(e.getPos());
//        if (controllerPos == null) return;
//        Vec3d hit = e.getHitVec();
//        BlockState state = e.getWorld().getBlockState(e.getPos());
//        if (!state.getBlock().onBlockActivated(e.getWorld(), e.getPos(), state, e.getEntityPlayer(), e.getHand(), e.getFace(), (float) hit.x, (float) hit.y, (float) hit.z)) {
//            state = e.getWorld().getBlockState(controllerPos);
//            state.getBlock().onBlockActivated(e.getWorld(), controllerPos, state, e.getEntityPlayer(), e.getHand(), e.getFace(), (float) hit.x, (float) hit.y, (float) hit.z);
//        }
//        e.setCanceled(true);
//    }

    public static class DimensionEntry {

        private Long2ObjectMap<Set<BlockPos>> STRUCTURE_TO_CONTROLLER = new Long2ObjectOpenHashMap<>(); //Structure Position -> Controller Position
        private Long2ObjectMap<LongList> CONTROLLER_TO_STRUCTURE = new Long2ObjectOpenHashMap<>(); //Controller Pos -> All Structure Positions

        public DimensionEntry() {
            STRUCTURE_TO_CONTROLLER.defaultReturnValue(Collections.EMPTY_SET);
            CONTROLLER_TO_STRUCTURE.defaultReturnValue(LongLists.EMPTY_LIST);
        }

        @Nonnull
        public Set<BlockPos> get(BlockPos pos) {
            return STRUCTURE_TO_CONTROLLER.get(pos.toLong());
        }

        public boolean add(BlockPos pos, LongList structure, int maxAmount) {
            long at = pos.toLong();
            int[] counter = new int[]{0};
            CONTROLLER_TO_STRUCTURE.put(at, structure);
            for (long s : structure) {
                STRUCTURE_TO_CONTROLLER.compute(s, (k,v) -> {
                    if (v == null) {
                        v = new ObjectOpenHashSet<>();
                    }
                    counter[0] = Math.max(counter[0], v.size());
                    v.add(pos);
                    return v;
                });
            }
            return counter[0] <= maxAmount;
        }

        public void remove(BlockPos pos) {
            long at = pos.toLong();
            LongList structure = CONTROLLER_TO_STRUCTURE.remove(at);
            for (long s : structure) {
                STRUCTURE_TO_CONTROLLER.compute(s, (k,v) -> {
                    if (v == null) return null;
                    if (v.size() == 1) return null;
                    v.remove(pos);
                    return v;
                } );
            }
        }
    }
}
