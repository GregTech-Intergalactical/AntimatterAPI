package muramasa.antimatter.item;

import net.minecraft.world.item.ItemStack;

public interface IContainerItem {
    boolean hasContainerItem(ItemStack stack);
    ItemStack getContainerItem(ItemStack itemStack);
}
