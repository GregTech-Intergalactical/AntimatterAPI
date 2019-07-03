package muramasa.gtu.api.structure;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import muramasa.gtu.GregTech;
import muramasa.gtu.api.tileentities.multi.TileEntityMultiMachine;
import muramasa.gtu.api.util.Utils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.List;

@Mod.EventBusSubscriber
public class StructureDatabase {

    private static Int2ObjectOpenHashMap<DimensionEntry> LOOKUP = new Int2ObjectOpenHashMap<>();

    public static void add(World world, BlockPos pos, List<BlockPos> structure) {
        DimensionEntry entry = LOOKUP.get(world.provider.getDimension());
        if (entry == null) {
            entry = new DimensionEntry(world.provider.getDimension());
            LOOKUP.put(world.provider.getDimension(), entry);
        }
        entry.add(pos, structure);
        GregTech.LOGGER.info("Added Structure to Store!");
    }

    public static void remove(World world, BlockPos pos) {
        DimensionEntry entry = LOOKUP.get(world.provider.getDimension());
        if (entry == null) return;
        entry.remove(pos);
        GregTech.LOGGER.info("Removed Structure to Store!");
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload e) {
        LOOKUP.remove(e.getWorld().provider.getDimension());
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent e) {
        DimensionEntry entry = LOOKUP.get(e.getWorld().provider.getDimension());
        if (entry == null) return;
        BlockPos controllerPos = entry.get(e.getPos());
        if (controllerPos != null) {
            TileEntity tile = Utils.getTile(e.getWorld(), controllerPos);
            if (tile instanceof TileEntityMultiMachine) ((TileEntityMultiMachine) tile).onStructureInvalid();
            remove(e.getWorld(), controllerPos);
        }
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
