package muramasa.gtu.api.recipe;

import net.minecraft.item.ItemStack;

public class ItemWrapper {

    private ItemStack item;
    private boolean count, nbt;
    private int hash = 1;

    public ItemWrapper(ItemStack item) {
        this.item = item;
        count = item.getCount() > 1;
        nbt = item.hasTagCompound();
        hash = 31 * hash + item.getItem().getRegistryName().toString().hashCode();
        if (item.getItemDamage() > 0) hash = 31 * hash + item.getItemDamage(); //TODO 1.13+: Remove damage
        if (nbt) hash = 31 * hash + item.getTagCompound().hashCode();
    }

    public ItemStack get() {
        return item.copy();
    }

    public int getCount() {
        return item.getCount();
    }

    public int getHash() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ItemWrapper)) return false;
        ItemWrapper other = (ItemWrapper) obj;
        if ((count && other.item.getCount() < item.getCount()) ||
            (nbt && !ItemStack.areItemStackTagsEqual(other.item, item))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
