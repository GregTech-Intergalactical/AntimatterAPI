package muramasa.gtu.api.registration;

import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

public interface IHasItemBlock {

    default String getItemStackDisplayName(Block block, ItemStack stack) {
        return stack.getUnlocalizedName();
    }

    default List<String> addInformation(ItemStack stack) {
        return Collections.emptyList();
    }
}
