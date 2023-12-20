package muramasa.antimatter.blockentity;

import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Map;

public class BlockEntityCache {
    public static final Map<Level, Long2ObjectMap<BlockEntity>> BLOCK_ENTITY_CACHE = new Object2ObjectOpenHashMap<>();

    public static void addBlockEntity(Level level, BlockPos pos, BlockEntity blockEntity){
        BLOCK_ENTITY_CACHE.computeIfAbsent(level, l -> new Long2ObjectOpenHashMap<>()).putIfAbsent(pos.asLong(), blockEntity);
    }

    public static void removeBlockEntity(Level level, BlockPos pos){
        BLOCK_ENTITY_CACHE.computeIfPresent(level, (l, m) -> {
            m.remove(pos.asLong());
            return m;
        });
    }

    public static BlockEntity getBlockEntity(Level level, BlockPos pos){
        if (level == null) return null;
        if (BLOCK_ENTITY_CACHE.containsKey(level)){
            Long2ObjectMap<BlockEntity> map = BLOCK_ENTITY_CACHE.get(level);
            if (map.containsKey(pos.asLong())){
                return map.get(pos.asLong());
            }
        }
        return level.getBlockEntity(pos);
    }
}
