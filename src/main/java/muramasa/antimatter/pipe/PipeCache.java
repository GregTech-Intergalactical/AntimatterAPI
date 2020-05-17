package muramasa.antimatter.pipe;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import muramasa.antimatter.tesseract.ITileWrapper;
import muramasa.antimatter.tesseract.EnergyTileWrapper;
import muramasa.antimatter.tesseract.FluidTileWrapper;
import muramasa.antimatter.tesseract.ItemTileWrapper;
import muramasa.antimatter.cover.Cover;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

@Mod.EventBusSubscriber
public class PipeCache {

    private static Int2ObjectMap<DimensionEntry> LOOKUP = new Int2ObjectOpenHashMap<>();

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload e) {
        LOOKUP.remove(e.getWorld().getDimension().getType().getId());
    }

    public static void update(PipeType type, IWorldReader world, Direction direction, TileEntity tile, @Nullable Cover cover) {
        DimensionEntry entry = LOOKUP.computeIfAbsent(world.getDimension().getType().getId(), e -> new DimensionEntry());
        entry.update(type, direction, tile, cover);
    }

    public static void remove(PipeType type, IWorldReader world, Direction direction, TileEntity tile) {
        DimensionEntry entry = LOOKUP.computeIfAbsent(world.getDimension().getType().getId(), e -> new DimensionEntry());
        entry.remove(type, direction, tile);
    }

    @Nullable
    public static ITileWrapper get(PipeType type, World world, BlockPos pos) {
        DimensionEntry entry = LOOKUP.get(world.getDimension().getType().getId());
        return entry != null ? entry.get(type, pos) : null;
    }

    public static class DimensionEntry {

        private Long2ObjectMap<ITileWrapper[]> NODE_BLOCK = new Long2ObjectOpenHashMap<>();
        private static final BiFunction<PipeType, TileEntity, ITileWrapper> supplier = (PipeType type, TileEntity tile) -> {
            switch (type) {
                case ELECTRIC: return EnergyTileWrapper.of(tile);
                case FLUID: return FluidTileWrapper.of(tile);
                case ITEM: return ItemTileWrapper.of(tile);
                default: return null;
            }
        };

        public DimensionEntry() {
        }

        public void update(PipeType type, Direction direction, TileEntity tile, @Nullable Cover cover) {
            ITileWrapper[] data = NODE_BLOCK.computeIfAbsent(tile.getPos().toLong(), e -> new ITileWrapper[3]); // Can be replaced with EnumMap
            int id = type.ordinal();
            if (data[id] == null || !data[id].isValid()) data[id] = supplier.apply(type, tile);
            if (data[id] == null) return; // If wasn't initialized, then stop
            data[id].onUpdate(direction, cover);
        }

        public void remove(PipeType type, Direction direction, TileEntity tile) {
            ITileWrapper[] data = NODE_BLOCK.computeIfAbsent(tile.getPos().toLong(), e -> new ITileWrapper[3]); // Can be replaced with EnumMap
            int id = type.ordinal();
            if (data[id] == null) return; // If wasn't initialized, then stop
            data[id].onRemove(direction);
        }

        @Nullable
        public ITileWrapper get(PipeType type, BlockPos pos) {
            ITileWrapper[] data = NODE_BLOCK.get(pos.toLong());
            return data != null ? data[type.ordinal()] : null;
        }
    }
}
