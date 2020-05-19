package muramasa.antimatter.structure;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.*;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class StructureCache {

    private static final Int2ObjectMap<DimensionEntry> LOOKUP = new Int2ObjectOpenHashMap<>();

    public static boolean has(World world, BlockPos pos) {
        DimensionEntry entry = LOOKUP.get(world.getDimension().getType().getId());
        return entry != null && entry.get(pos) != null;
    }

    @Nullable
    public static BlockPos get(World world, BlockPos pos) {
        DimensionEntry entry = LOOKUP.get(world.getDimension().getType().getId());
        return entry != null ? entry.get(pos) : null;
    }

    public static void add(World world, BlockPos pos, LongList structure) {
        DimensionEntry entry = LOOKUP.computeIfAbsent(world.getDimension().getType().getId(), e -> new DimensionEntry());
        entry.add(pos, structure);
        Antimatter.LOGGER.info("Added Structure to Store!");
    }

    public static void remove(IWorld world, BlockPos pos) {
        DimensionEntry entry = LOOKUP.get(world.getDimension().getType().getId());
        if (entry == null) return;
        entry.remove(pos);
        Antimatter.LOGGER.info("Removed Structure to Store!");
    }

    private static void invalidateController(IWorld world, BlockPos pos) {
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityMultiMachine) ((TileEntityMultiMachine) tile).invalidateStructure();
        remove(world, pos);
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload e) {
        LOOKUP.remove(e.getWorld().getDimension().getType().getId());
    }

    /**
     * COREMOD METHOD INSERTION: Runs every time when this is called:
     * @see ServerWorld#notifyBlockUpdate(BlockPos, BlockState, BlockState, int)
    */
    @SuppressWarnings("unused")
    public static void onNotifyBlockUpdate(ServerWorld world, BlockPos pos, BlockState oldState, BlockState newState) {
        if (oldState == newState) return;  // TODO: better checks?
        DimensionEntry entry = LOOKUP.get(world.dimension.getType().getId());
        if (entry == null) return;
        BlockPos controllerPos = entry.get(pos);
        if (controllerPos != null) invalidateController(world, controllerPos);
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
