package muramasa.gtu.api.recipe;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import muramasa.gtu.api.util.Utils;
import net.minecraft.item.ItemStack;

public class RecipeInputItem implements IRecipeObject {

    private ItemStack[] items;
    private int[] itemHash;
    private Int2IntArrayMap hashMap = new Int2IntArrayMap();
    private int objectHash;

    public RecipeInputItem(ItemStack... items) {
        this.items = items;
        this.itemHash = new int[items.length];
        for (int i = 0; i < items.length; i++) {
            int hash = Utils.getItemHash(items[i]);
            itemHash[i] = hash;
            hashMap.put(itemHash[i], i);
            objectHash += hash;
            //objectHash ^= hash; //TODO broken?
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RecipeInputItem)) return false;
        RecipeInputItem input = (RecipeInputItem) obj;
        for (int i = 0; i < items.length; i++) {
            int recipeCount = input.items[input.hashMap.get(itemHash[i])].getCount();
            int invCount = items[i].getCount();
            if (invCount < recipeCount) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return objectHash;
    }
}
