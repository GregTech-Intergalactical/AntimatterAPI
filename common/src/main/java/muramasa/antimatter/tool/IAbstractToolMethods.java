package muramasa.antimatter.tool;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public interface IAbstractToolMethods {
    default <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        return amount;
    }

    static <T extends LivingEntity> int damageItemStatic(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        if (stack.getItem() instanceof IAbstractToolMethods me){
            return me.damageItem(stack, amount, entity, onBroken);
        }
        return amount;
    }
}
