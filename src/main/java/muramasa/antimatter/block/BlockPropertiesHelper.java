package muramasa.antimatter.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;

/**
 * Includes helper methods for cleaner properties instantiations.
 * Similar to those in {@link net.minecraft.world.level.block.Blocks}
 */
public class BlockPropertiesHelper {

    public static boolean never(BlockState state, BlockGetter getter, BlockPos pos, EntityType<?> type) {
        return false;
    }

    public static boolean never(BlockState state, BlockGetter getter, BlockPos pos) {
        return false;
    }

    public static boolean always(BlockState state, BlockGetter getter, BlockPos pos, EntityType<?> type) {
        return true;
    }

    public static boolean always(BlockState state, BlockGetter getter, BlockPos pos) {
        return true;
    }

    public static boolean ocelotOrParrot(BlockState state, BlockGetter getter, BlockPos pos, EntityType<?> type) {
        return type == EntityType.OCELOT || type == EntityType.PARROT;
    }

    public static BlockBehaviour.Properties leaves() {
        return BlockBehaviour.Properties.of(Material.LEAVES)
                .strength(0.2F)
                .randomTicks()
                .sound(SoundType.GRASS)
                .noOcclusion()
                .isValidSpawn(BlockPropertiesHelper::ocelotOrParrot)
                .isSuffocating(BlockPropertiesHelper::never)
                .isViewBlocking(BlockPropertiesHelper::never);
    }

    public static BlockBehaviour.Properties glass(DyeColor dyeColor) {
        return BlockBehaviour.Properties.of(Material.GLASS, dyeColor)
                .strength(0.3F)
                .sound(SoundType.GLASS)
                .noOcclusion()
                .isValidSpawn(BlockPropertiesHelper::never)
                .isRedstoneConductor(BlockPropertiesHelper::never)
                .isSuffocating(BlockPropertiesHelper::never)
                .isViewBlocking(BlockPropertiesHelper::never);
    }

    private BlockPropertiesHelper() { }

}
