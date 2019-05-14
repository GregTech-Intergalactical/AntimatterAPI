package muramasa.gtu.api.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

//TODO 1.13+: Remove damage
public class ItemStackWrapper implements IRecipeObject<ItemStack> {

    private ItemStack stack;
    private boolean size = false, damage = false, nbt = false;

    public ItemStackWrapper(ItemStack stack) {
        this.stack = stack;
    }

    @Override
    public ItemStack getInternal() {
        return stack;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ItemStackWrapper)) return false;
        ItemStackWrapper wrapper = (ItemStackWrapper) obj;

        if ((stack.getItem() != wrapper.stack.getItem()) ||
            (size && stack.getCount() != wrapper.stack.getCount()) ||
            (damage && stack.getItemDamage() != wrapper.stack.getItemDamage()) ||
            (nbt && !ItemStack.areItemStackTagsEqual(stack, wrapper.stack))
        ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Item.getIdFromItem(stack.getItem());
        result = prime * result + (size ? stack.getCount() : 0);
        result = prime * result + (damage ? stack.getItemDamage() : 0);
        result = prime * result + (nbt && stack.hasTagCompound() ? stack.getTagCompound().hashCode() : 0);
        return result;
    }
}
