package muramasa.antimatter.behaviour;

import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;

import java.util.List;

public interface IAddInformation<T> extends IBehaviour<T> {

    @Override
    default String getId() {
        return "add_information";
    }

    void onAddInformation(T instance, ItemStack stack, List<Component> tooltip, TooltipFlag flag);
}
