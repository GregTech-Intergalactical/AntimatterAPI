package muramasa.antimatter.pipe;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import muramasa.antimatter.capability.INodeHandler;
import muramasa.antimatter.capability.impl.EnergyNodeHandler;
import muramasa.antimatter.capability.impl.FluidNodeHandler;
import muramasa.antimatter.capability.impl.ItemNodeHandler;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber
public class PipeCache {

    private static Int2ObjectMap<Long2ObjectMap<INodeHandler[]>> LOOKUP = new Int2ObjectOpenHashMap<>();

    // TODO: Should be handled by pipes/cables
    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent e) {
        TileEntity tile = Utils.getTile(e.getWorld(), e.getPos());
        if (tile == null || tile instanceof TileEntityMachine) return;
        LOOKUP.computeIfAbsent(e.getWorld().getDimension().getType().getId(), Long2ObjectOpenHashMap::new)
            .put(e.getPos().toLong(),
                // They are automatically removed on the caps invalidation
                new INodeHandler[] {
                    EnergyNodeHandler.of(tile),
                    FluidNodeHandler.of(tile),
                    ItemNodeHandler.of(tile)
            }
        );
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload e) {
        LOOKUP.remove(e.getWorld().getDimension().getType().getId());
    }

    @Nullable
    public static INodeHandler[] get(World world, BlockPos pos) {
        Long2ObjectMap<INodeHandler[]> entry = LOOKUP.get(world.getDimension().getType().getId());
        return entry != null ? entry.get(pos.toLong()) : null;
    }

    /*
    public static void setElectric(World world, Direction direction, TileEntity tile) {
        DimensionEntry entry = LOOKUP.computeIfAbsent(world.getDimension().getType().getId(), e -> new DimensionEntry());
        entry.setElectric(direction, tile);
    }

    public static void setFluid(World world, Direction direction, TileEntity tile) {
        DimensionEntry entry = LOOKUP.computeIfAbsent(world.getDimension().getType().getId(), e -> new DimensionEntry());
        entry.setFluid(direction, tile);
    }

    public static void setItem(World world, Direction direction, TileEntity tile) {
        DimensionEntry entry = LOOKUP.computeIfAbsent(world.getDimension().getType().getId(), e -> new DimensionEntry());
        entry.setItem(direction, tile);
    }

    public static class DimensionEntry {

        private Long2ObjectMap<INodeHandler[]> NODE_BLOCK = new Long2ObjectOpenHashMap<>();

        public DimensionEntry() {
        }

        public void setElectric(Direction direction, TileEntity tile) {
            INodeHandler[] data = NODE_BLOCK.computeIfAbsent(tile.getPos().toLong(), e -> new INodeHandler[3]);
            if (data[0] == null || data[0].isEmpty()) data[0] = EnergyNode.of(tile);
            if (data[0] == null) return;
            data[0].onUpdate(direction, null);
        }

        public void setFluid(Direction direction, TileEntity tile) {
            INodeHandler[] data = NODE_BLOCK.computeIfAbsent(tile.getPos().toLong(), e -> new INodeHandler[3]);
            if (data[1] == null || data[1].isEmpty()) data[1] = FluidNode.of(tile);
            if (data[1] == null) return;
            data[1].onUpdate(direction, null);
        }

        public void setItem(Direction direction, TileEntity tile) {
            INodeHandler[] data = NODE_BLOCK.computeIfAbsent(tile.getPos().toLong(), e -> new INodeHandler[3]);
            if (data[2] == null || data[2].isEmpty()) data[2] = ItemNode.of(tile);
            if (data[2] == null) return;
            data[2].onUpdate(direction, null);
        }
    }*/
}
