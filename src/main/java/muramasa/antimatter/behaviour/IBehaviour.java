package muramasa.antimatter.behaviour;

import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBehaviour<T> {

    String getId();

    default ActionResultType onItemUse(T instance, ItemUseContext c) {
        return ActionResultType.PASS;
    }

    default boolean onBlockDestroyed(T instance, ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity entity) {
        return true;
    }
}
