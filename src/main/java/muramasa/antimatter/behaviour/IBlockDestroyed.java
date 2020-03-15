package muramasa.antimatter.behaviour;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBlockDestroyed<T> extends IBehaviour<T> {

    @Override
    default String getId() {
        return "block_destroyed";
    }

    boolean onBlockDestroyed(T instance, ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity entity);

}
