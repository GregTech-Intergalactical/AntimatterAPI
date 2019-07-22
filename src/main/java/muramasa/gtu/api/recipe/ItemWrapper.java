package muramasa.gtu.api.recipe;

import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.Set;

public class ItemWrapper {

    public ItemStack item;
    private boolean count, nbt;
    private int hash;

    public ItemWrapper(ItemStack item, Set<RecipeTag> tags) {
        this.item = item;
        count = item.getCount() > 1;
        nbt = item.hasTagCompound() && !tags.contains(RecipeTag.IGNORE_NBT);
        long tempHash = 1; //long hash used to handle many inputs with nbt hashes
        tempHash = 31 * tempHash + item.getItem().getRegistryName().toString().hashCode();
        if (item.getItemDamage() > 0) tempHash = 31 * tempHash + item.getItemDamage(); //TODO 1.13+: Remove damage
        if (nbt) tempHash = 31 * tempHash + item.getTagCompound().hashCode();
        hash = (int) (tempHash ^ (tempHash >>> 32)); //int version of the hash for the actual comparision
    }

    public ItemWrapper(ItemStack item) {
        this(item, Collections.emptySet());
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
