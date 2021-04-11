package muramasa.antimatter.integration.jei.extension;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.extensions.IRecipeCategoryExtension;
import muramasa.antimatter.recipe.material.MaterialRecipe;

public class JEIMaterialRecipeExtension implements IRecipeCategoryExtension {

    protected final MaterialRecipe recipe;

    public JEIMaterialRecipeExtension(MaterialRecipe recipe) {
        this.recipe = recipe;
    }
    @Override
    public void setIngredients(IIngredients ingredients) {
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
    }
}
