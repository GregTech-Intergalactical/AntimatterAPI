package muramasa.gtu.api.registration;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface IHasItemBlock {

    default String getItemStackDisplayName(Block block, ItemStack stack) {
        return stack.getUnlocalizedName();
    }

    default void addInformation(ItemStack stack, List<String> tooltip) {
        //NOOP
    }
}
