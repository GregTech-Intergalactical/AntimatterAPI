package muramasa.antimatter.tile.pipe;

import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.function.Consumer;

@Mod.EventBusSubscriber
public class PipeReferenceCounter {
    private static final Object2ObjectMap<RegistryKey<World>, Object2ObjectMap<Class<? extends TileEntityPipe>, Long2IntMap>> LOOKUP = new Object2ObjectOpenHashMap<>();
    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload e) {
        LOOKUP.remove(((World)e.getWorld()).getDimensionKey());
    }

    public static void add(RegistryKey<World> world, long pos, Class<? extends TileEntityPipe> pipeType, Consumer<Long> onRegister) {
        LOOKUP.compute(world, (k,v) -> {
            if (v == null) {
                v = new Object2ObjectOpenHashMap<>();
            }
            v.compute(pipeType, (a,b) -> {
                //can actually be null.
                if (b == null) {
                    b = new Long2IntOpenHashMap();
                }
                int n = b.compute(pos, (c,d) -> {
                    if (d == null) {
                        return 1;
                    }
                    return d + 1;
                });
                if (n == 1) onRegister.accept(pos);
                return b;
            });
            return v;
        });
    }

    public static void remove(RegistryKey<World> world, long pos, Class<? extends TileEntityPipe> pipeType, Consumer<Long> onRemove) {
        LOOKUP.compute(world, (k,v) -> {
            if (v == null) {
                v = new Object2ObjectOpenHashMap<>();
            }
            v.compute(pipeType, (a,b) -> {
                //can actually be null.
                if (b == null) {
                    b = new Long2IntOpenHashMap();
                }
                int n = b.compute(pos, (c,d) -> d - 1);
                if (n == 0) onRemove.accept(pos);
                return b;
            });
            return v;
        });
    }
}
