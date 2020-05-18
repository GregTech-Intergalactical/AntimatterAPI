package muramasa.antimatter.pipe;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import muramasa.antimatter.pipe.types.PipeType;
import muramasa.antimatter.tesseract.ITileWrapper;
import muramasa.antimatter.cover.Cover;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@Mod.EventBusSubscriber
@ParametersAreNonnullByDefault
public class PipeCache {

    private static Int2ObjectMap<DimensionEntry> LOOKUP = new Int2ObjectOpenHashMap<>();

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload e) {
        LOOKUP.remove(e.getWorld().getDimension().getType().getId());
    }

    public static void update(PipeType<?> type, IWorldReader world, Direction direction, TileEntity tile, @Nullable Cover cover) {
        DimensionEntry entry = LOOKUP.computeIfAbsent(world.getDimension().getType().getId(), e -> new DimensionEntry());
        entry.update(type, direction, tile, cover);
    }

    public static void remove(PipeType<?> type, IWorldReader world, Direction direction, TileEntity tile) {
        DimensionEntry entry = LOOKUP.computeIfAbsent(world.getDimension().getType().getId(), e -> new DimensionEntry());
        entry.remove(type, direction, tile);
    }

    public static class DimensionEntry {

        private Long2ObjectMap<Map<String, ITileWrapper>> NODE_BLOCK = new Long2ObjectOpenHashMap<>();

        public DimensionEntry() {
        }

        public void update(PipeType<?> type, Direction direction, TileEntity tile, @Nullable Cover cover) {
            get(type, tile).onUpdate(direction, cover);
        }

        public void remove(PipeType<?> type, Direction direction, TileEntity tile) {
            get(type, tile).onRemove(direction);
        }

        @Nonnull
        private ITileWrapper get(PipeType<?> type, TileEntity tile) {
            Map<String, ITileWrapper> map = NODE_BLOCK.computeIfAbsent(tile.getPos().toLong(), e -> new Object2ObjectArrayMap<>()); // Can be replaced with EnumMap
            ITileWrapper wrapper = map.get(type.getTypeName());
            if (wrapper == null || !wrapper.isValid()) {
                wrapper = type.getTileWrapper(tile);
                map.put(type.getTypeName(), wrapper);
            }
            return wrapper;
        }
    }
}
