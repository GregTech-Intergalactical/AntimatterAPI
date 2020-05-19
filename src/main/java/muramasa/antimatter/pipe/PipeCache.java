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
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Map;

@Mod.EventBusSubscriber
@ParametersAreNonnullByDefault
public class PipeCache {

    private static final Int2ObjectMap<DimensionEntry> LOOKUP = new Int2ObjectOpenHashMap<>();

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload e) {
        LOOKUP.remove(e.getWorld().getDimension().getType().getId());
    }

    public static void update(PipeType<?> type, IWorldReader world, Direction direction, TileEntity target, Cover cover) {
        DimensionEntry entry = LOOKUP.computeIfAbsent(world.getDimension().getType().getId(), e -> new DimensionEntry());
        entry.update(type, direction, target, cover);
    }

    public static void remove(PipeType<?> type, IWorldReader world, Direction direction, TileEntity target) {
        DimensionEntry entry = LOOKUP.computeIfAbsent(world.getDimension().getType().getId(), e -> new DimensionEntry());
        entry.remove(type, direction, target);
    }

    public static class DimensionEntry {

        private Long2ObjectMap<Map<String, ITileWrapper>> NODE_BLOCK = new Long2ObjectOpenHashMap<>();

        public DimensionEntry() {
        }

        public void update(PipeType<?> type, Direction direction, TileEntity target, Cover cover) {
            get(type, target).onUpdate(direction.getOpposite(), cover);
        }

        public void remove(PipeType<?> type, Direction direction, TileEntity target) {
            get(type, target).onRemove(direction.getOpposite());
        }

        @Nonnull
        private ITileWrapper get(PipeType<?> type, TileEntity target) {
            Map<String, ITileWrapper> map = NODE_BLOCK.computeIfAbsent(target.getPos().toLong(), e -> new Object2ObjectArrayMap<>(3));
            ITileWrapper wrap = map.get(type.getTypeName());
            if (wrap == null || wrap.isRemoved()) {
                wrap = type.getTileWrapper(target);
                map.put(type.getTypeName(), wrap);
            }
            return wrap;
        }
    }
}
