package muramasa.antimatter.recipe;

import net.minecraft.item.ItemStack;

import java.util.Collections;
import java.util.Set;

public class ItemWrapper {

    public ItemStack item;
    protected boolean count, nbt;
    protected int hash;

    public ItemWrapper(ItemStack item, Set<RecipeTag> tags) {
        this.item = item;
        count = item.getCount() > 1;
        nbt = item.hasTag() && !tags.contains(RecipeTag.IGNORE_NBT);
        long tempHash = 1; //long hash used to handle many inputs with nbt hashes
        tempHash = 31 * tempHash + item.getItem().getRegistryName().toString().hashCode();
        if (nbt) tempHash = 31 * tempHash + item.getTag().hashCode();
        hash = (int) (tempHash ^ (tempHash >>> 32)); //int version of the hash for the actual comparision
    }

    public ItemWrapper(ItemStack item) {
        this(item, Collections.emptySet());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof ItemWrapper)) return false;
        ItemWrapper other = (ItemWrapper) obj;
        //TODO: this might be a bad way to do it but i'm not sure which equals() is called when the Set<ItemWrapper> checks for equality.
        //TODO: This can be removed if the set doesn't use this comparison (see RecipeTagMap.itemsPresent)
        if (other instanceof RecipeTagMap.TagItemWrapper) {
            return (ItemStack.areItemsEqual(item, other.item) && (!nbt || ItemStack.areItemStackTagsEqual(item, other.item)));
        }
        return (!count || other.item.getCount() >= item.getCount()) && (ItemStack.areItemsEqual(item, other.item) && (!nbt || ItemStack.areItemStackTagsEqual(item, other.item)));
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
