package muramasa.antimatter.behaviour;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface IBlockDestroyed<T> extends IBehaviour<T> {

    @Override
    default String getId() {
        return "block_destroyed";
    }

    boolean onBlockDestroyed(T instance, ItemStack stack, Level world, BlockState state, BlockPos pos, LivingEntity entity);
}
