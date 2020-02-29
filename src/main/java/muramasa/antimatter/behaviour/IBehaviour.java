package muramasa.antimatter.behaviour;

import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;

public interface IBehaviour<T> {

    default ActionResultType onItemUse(T instance, ItemUseContext c) {
        return ActionResultType.PASS;
    }
}
