package muramasa.gtu.api.recipe;

import net.minecraft.item.ItemStack;

public class OreDictOutput implements IRecipeObject<ItemStack> {

    ItemStack stack;

    public OreDictOutput(String name) {
        stack = RecipeHelper.getFirstOreDict(name);
    }

    @Override
    public ItemStack getInternal() {
        return stack;
    }
}
