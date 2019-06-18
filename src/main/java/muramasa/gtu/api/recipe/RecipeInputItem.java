package muramasa.gtu.api.recipe;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import net.minecraft.item.ItemStack;

public class RecipeInputItem implements IRecipeObject {

    private ItemWrapper[] items;
    private Int2IntArrayMap map = new Int2IntArrayMap();
    private int hash = 1;

    public RecipeInputItem(ItemStack... items) {
        this.items = new ItemWrapper[items.length];
        for (int i = 0; i < items.length; i++) {
            this.items[i] = new ItemWrapper(items[i]);
            map.put(this.items[i].getHash(), i);
            hash += this.items[i].getHash();
            //hash ^= hash; //TODO broken?
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RecipeInputItem)) return false;
        RecipeInputItem other = (RecipeInputItem) obj;
        for (int i = 0; i < items.length; i++) {
            int recipeCount = other.items[other.map.get(items[i].getHash())].getCount();
            int invCount = items[i].getCount();
            if (invCount < recipeCount) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
