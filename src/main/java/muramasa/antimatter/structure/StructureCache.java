package muramasa.antimatter.structure;

import it.unimi.dsi.fastutil.longs.*;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class StructureCache {

    private static final Long2ObjectMap<DimensionEntry> LOOKUP = new Long2ObjectOpenHashMap<>();

    static {
        AntimatterAPI.registerBlockUpdateHandler((world, pos, oldState, newState) -> {
            if (oldState == newState) return;  // TODO: better checks?
            StructureCache.DimensionEntry entry = LOOKUP.get(getDimId(world));
            if (entry == null) return;
            BlockPos controllerPos = entry.get(pos);
            if (controllerPos != null) {
                if (!controllerPos.equals(pos))
                    invalidateController(world, controllerPos);
            }
        });
    }


    public static boolean has(World world, BlockPos pos) {
        DimensionEntry entry = LOOKUP.get(getDimId(world));
        return entry != null && entry.get(pos) != null;
    }

    @Nullable
    public static BlockPos get(World world, BlockPos pos) {
        DimensionEntry entry = LOOKUP.get(getDimId(world));
        return entry != null ? entry.get(pos) : null;
    }

    public static void add(World world, BlockPos pos, LongList structure) {
        DimensionEntry entry = LOOKUP.computeIfAbsent(getDimId(world), e -> new DimensionEntry());
        entry.add(pos, structure);
        Antimatter.LOGGER.info("Added Structure to Store!");
    }

    public static void remove(World world, BlockPos pos) {
        DimensionEntry entry = LOOKUP.get(getDimId(world));
        if (entry == null) return;
        entry.remove(pos);
        Antimatter.LOGGER.info("Removed Structure to Store!");
    }
    //just to switch between server & client. You can use two maps but y tho
    private static long getDimId(World world) {
        RegistryKey<World> w = world.getDimensionKey();
        int offset = world instanceof ServerWorld ? 1 : 0;
        return (((long)w.getLocation().hashCode()) << 32) | (offset & 0xffffffffL);
    }

    private static void invalidateController(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityMultiMachine) ((TileEntityMultiMachine) tile).invalidateStructure();
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

        private Long2LongMap STRUCTURE_TO_CONTROLLER = new Long2LongOpenHashMap(); //Structure Position -> Controller Position
        private Long2ObjectMap<LongList> CONTROLLER_TO_STRUCTURE = new Long2ObjectOpenHashMap<>(); //Controller Pos -> All Structure Positions

        public DimensionEntry() {
            STRUCTURE_TO_CONTROLLER.defaultReturnValue(Long.MAX_VALUE);
            CONTROLLER_TO_STRUCTURE.defaultReturnValue(LongLists.EMPTY_LIST);
        }

        @Nullable
        public BlockPos get(BlockPos pos) {
            long at = STRUCTURE_TO_CONTROLLER.get(pos.toLong());
            return at != Long.MAX_VALUE ? BlockPos.fromLong(at) : null;
        }

        public void add(BlockPos pos, LongList structure) {
            long at = pos.toLong();
            CONTROLLER_TO_STRUCTURE.put(at, structure);
            for (long s : structure) {
                STRUCTURE_TO_CONTROLLER.put(s, at);
            }
        }

        public void remove(BlockPos pos) {
            long at = pos.toLong();
            LongList structure = CONTROLLER_TO_STRUCTURE.remove(at);
            for (long s : structure) {
                STRUCTURE_TO_CONTROLLER.remove(s);
            }
        }
    }
}
