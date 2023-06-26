package muramasa.antimatter.behaviour;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface IItemRightClick<T> extends IBehaviour<T> {
    @Override
    default String getId(){
        return "item_right_click";
    }

    InteractionResultHolder<ItemStack> onRightClick(T instance, Level level, Player player, InteractionHand usedHand);
}
