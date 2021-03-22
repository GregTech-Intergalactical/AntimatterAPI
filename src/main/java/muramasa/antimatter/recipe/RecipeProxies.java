package muramasa.antimatter.recipe;

import muramasa.antimatter.recipe.ingredient.AntimatterIngredient;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;

import java.util.List;
import java.util.function.Function;

public class RecipeProxies {

    private static Function<IRecipe, Recipe> DEFAULT_PROXY = t -> {
        List<Ingredient> ingredients = t.getIngredients();
        Ingredient input = ingredients.get(0);
        ItemStack[] stacks = input.getMatchingStacks();
        LazyValue<AntimatterIngredient> ing = stacks.length == 1 ? AntimatterIngredient.of(stacks[0]) : AntimatterIngredient.of(1, input.getMatchingStacks());
        return new RecipeBuilder().ii(ing)
                .io(t.getRecipeOutput()).build(60, 8,0, 1);
    };
/*
    private static Function<IRecipe, Recipe> CRAFTING = t -> {
        if (t instanceof ShapedRecipe) return null;
        List<Ingredient> ingredients = t.getIngredients();
        if (ingredients.size() > 6 || ingredients.size() < 2) return null;
        List<ItemStack> list = new ObjectArrayList<>();
        List<LazyValue<AntimatterIngredient>> ings = new ObjectArrayList<>();
        for (Ingredient i : ingredients) {
            ItemStack[] stacks = i.getMatchingStacks();
            if (stacks.length == 0) return null;
            if (stacks.length == 1) {
                list.add(stacks[0]);
            } else {
                LazyValue<AntimatterIngredient> ing = AntimatterIngredient.of(1, stacks);
                ings.add(ing);
            }
        }
        ItemStack[] stacks = RecipeMap.uniqueItems(list.toArray(new ItemStack[0]));
        for (ItemStack stack : stacks) {
            ings.add(AntimatterIngredient.of(stack));
        }
        return new RecipeBuilder().ii(ings)
                .io(t.getRecipeOutput()).build(60, 8,0, 1);
    };
*/
    public static RecipeMap.Proxy FURNACE_PROXY = new RecipeMap.Proxy(IRecipeType.SMELTING, DEFAULT_PROXY);
    public static RecipeMap.Proxy BLASTING_PROXY = new RecipeMap.Proxy(IRecipeType.BLASTING, DEFAULT_PROXY);
    public static RecipeMap.Proxy SMOKING_PROXY = new RecipeMap.Proxy(IRecipeType.SMOKING, DEFAULT_PROXY);
    //public static RecipeMap.Proxy CRAFTING_PROXY = new RecipeMap.Proxy(IRecipeType.CRAFTING, CRAFTING);
}
