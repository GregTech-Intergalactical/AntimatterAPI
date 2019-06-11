package muramasa.gtu.api.registration;

import net.minecraft.item.ItemStack;

public interface IItemBlock {

    default String getDisplayName(ItemStack stack) {
        return stack.getUnlocalizedName();
    }
}
