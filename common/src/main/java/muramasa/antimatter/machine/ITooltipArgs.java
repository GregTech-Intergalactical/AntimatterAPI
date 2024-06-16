package muramasa.antimatter.machine;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@FunctionalInterface
public interface ITooltipArgs {
    Object[] getTooltipArgs(BlockMachine machine, ItemStack stack, @Nullable BlockGetter world, TooltipFlag flag, int i);
}
