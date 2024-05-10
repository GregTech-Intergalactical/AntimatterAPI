package muramasa.antimatter.blockentity;

import earth.terrarium.botarium.common.fluid.base.PlatformFluidHandler;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import tesseract.TesseractCapUtils;

import java.util.Map;
import java.util.Optional;

public class BlockEntityCache {
    private static final Map<Level, Long2ObjectMap<BlockEntity>> BLOCK_ENTITY_CACHE = new Object2ObjectOpenHashMap<>();

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
        /*if (BLOCK_ENTITY_CACHE.containsKey(level)){
            Long2ObjectMap<BlockEntity> map = BLOCK_ENTITY_CACHE.get(level);
            if (map.containsKey(pos.asLong())){
                return map.get(pos.asLong());
            }
        }*/
        return level.getBlockEntity(pos);
    }

    public static Optional<PlatformFluidHandler> getFluidHandlerCached(Level level, BlockPos pos, Direction side){
        /*BlockEntity blockEntity = getBlockEntity(level, pos);
        if (blockEntity != null){
            return FluidHooks.safeGetBlockFluidManager(blockEntity, side);
        }*/
        return TesseractCapUtils.INSTANCE.getFluidHandler(level, pos, side);
    }
}
