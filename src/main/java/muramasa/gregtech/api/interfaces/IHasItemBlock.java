package muramasa.gregtech.api.interfaces;

import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.List;

public interface IHasItemBlock {

    default String getItemStackDisplayName(ItemStack stack) {
        return stack.getUnlocalizedName();
    }

    default List<String> addInformation(ItemStack stack) {
        return Collections.emptyList();
    }
}
