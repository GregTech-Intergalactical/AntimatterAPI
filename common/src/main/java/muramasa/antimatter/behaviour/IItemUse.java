package muramasa.antimatter.behaviour;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;

public interface IItemUse<T> extends IBehaviour<T> {

    @Override
    default String getId() {
        return "item_use";
    }

    InteractionResult onItemUse(T instance, UseOnContext c);
}
