package muramasa.antimatter.behaviour;

import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

public interface IItemUse<T> extends IBehaviour<T> {

    @Override
    default String getId() {
        return "item_use";
    }

    ActionResultType onItemUse(T instance, ItemUseContext c);

}
