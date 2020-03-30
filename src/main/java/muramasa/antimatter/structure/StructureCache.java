package muramasa.antimatter.structure;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.List;

@Mod.EventBusSubscriber
public class StructureCache {

    private static Int2ObjectOpenHashMap<DimensionEntry> LOOKUP = new Int2ObjectOpenHashMap<>();

    public static boolean has(World world, BlockPos pos) {
        DimensionEntry entry = LOOKUP.get(world.getDimension().getType().getId());
        return entry != null && entry.get(pos) != null;
    }

    public static BlockPos get(World world, BlockPos pos) {
        DimensionEntry entry = LOOKUP.get(world.getDimension().getType().getId());
        if (entry == null) return null;
        return entry.get(pos);
    }

    public static void add(World world, BlockPos pos, List<BlockPos> structure) {
        DimensionEntry entry = LOOKUP.get(world.getDimension().getType().getId());
        if (entry == null) {
            entry = new DimensionEntry(world.getDimension().getType().getId());
            LOOKUP.put(world.getDimension().getType().getId(), entry);
        }
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

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent e) {
        DimensionEntry entry = LOOKUP.get(e.getWorld().getDimension().getType().getId());
        if (entry == null) return;
        BlockPos controllerPos = entry.get(e.getPos());
        if (controllerPos != null) invalidateController(e.getWorld(), controllerPos);
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent e) {
        DimensionEntry entry = LOOKUP.get(e.getWorld().getDimension().getType().getId());
        if (entry == null) return;
        BlockPos controllerPos = entry.get(e.getPos());
        if (controllerPos != null) invalidateController(e.getWorld(), controllerPos);
    }

    @SubscribeEvent
    public static void onBlockClickEvent(PlayerInteractEvent.RightClickBlock e) {
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
    }

    public static class DimensionEntry {

        private HashMap<BlockPos, BlockPos> STRUCTURE_TO_CONTROLLER = new HashMap<>(); //Structure Position -> Controller Position
        private HashMap<BlockPos, List<BlockPos>> CONTROLLER_TO_STRUCTURE = new HashMap<>(); //Controller Pos -> All Structure Positions

        public int dimension;

        public DimensionEntry(int dimension) {
            this.dimension = dimension;
        }

        public BlockPos get(BlockPos pos) {
            return STRUCTURE_TO_CONTROLLER.get(pos);
        }

        public void add(BlockPos pos, List<BlockPos> structure) {
            CONTROLLER_TO_STRUCTURE.put(pos, structure);
            structure.forEach(s -> STRUCTURE_TO_CONTROLLER.put(s, pos));
        }

        public void remove(BlockPos pos) {
            List<BlockPos> structure = CONTROLLER_TO_STRUCTURE.remove(pos);
            if (structure == null) return;
            structure.forEach(s -> STRUCTURE_TO_CONTROLLER.remove(s));
        }
    }
}
