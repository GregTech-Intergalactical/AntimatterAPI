package muramasa.gtu.api.recipe;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

//TODO 1.13+: Remove damage
public class ItemStackWrapper implements IRecipeObject<ItemStack> {

    private ItemStack stack;
    private boolean count, damage, nbt;

    public ItemStackWrapper(ItemStack stack) {
        this.stack = stack;
        this.count = stack.getCount() > 1;
        this.damage = stack.getMaxDamage() > 0;
        this.nbt = stack.hasTagCompound();
    }

    @Override
    public ItemStack getInternal() {
        return stack;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ItemStackWrapper)) return false;
        ItemStackWrapper wrapper = (ItemStackWrapper) obj;
        if ((stack.getItem() == wrapper.stack.getItem()) ||
            (count && wrapper.stack.getCount() >= stack.getCount()) ||
            (damage && stack.getItemDamage() == wrapper.stack.getItemDamage()) ||
            (nbt && ItemStack.areItemStackTagsEqual(stack, wrapper.stack))) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Item.getIdFromItem(stack.getItem());
        if (damage) result = 31 * result + stack.getItemDamage();
        if (nbt) result = 31 * result + stack.getTagCompound().hashCode();
        return result;
    }
}
