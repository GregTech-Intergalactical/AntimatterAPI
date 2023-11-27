package muramasa.antimatter.behaviour;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

public interface IDestroySpeed<T> extends IBehaviour<T> {
    @Override
    default String getId() {
        return "destroy_speed";
    }

    float getDestroySpeed(T instance, float currentDestroySpeed, ItemStack stack, BlockState state);
}
