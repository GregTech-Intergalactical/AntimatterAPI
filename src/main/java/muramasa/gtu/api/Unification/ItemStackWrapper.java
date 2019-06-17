package muramasa.gtu.api.Unification;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

//TODO 1.13+: Remove damage
public class ItemStackWrapper {

    private ItemStack stack;
    private boolean count;
    private int hash;

    public ItemStackWrapper(ItemStack stack) {
        this.stack = stack;
        this.count = stack.getCount() > 1;
        hash = 1;
        hash = 31 * hash + Item.getIdFromItem(stack.getItem());
        if (stack.getItemDamage() > 0) hash = 31 * hash + stack.getItemDamage();
    }

    public ItemStack getStack() {
        return stack;
    }

    public int getHash() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ItemStackWrapper)) return false;
        ItemStackWrapper wrapper = (ItemStackWrapper) obj;
        return count && wrapper.stack.getCount() >= stack.getCount();
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
