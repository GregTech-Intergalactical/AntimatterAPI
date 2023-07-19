package muramasa.antimatter.machine;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;

import javax.annotation.Nullable;
import java.util.List;

@FunctionalInterface
public interface ITooltipInfo {
    void getTooltips(BlockMachine machine, ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag flag);
}
