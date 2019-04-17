package muramasa.gtu.integration.ctx;

import crafttweaker.api.item.IIngredient;
import crafttweaker.api.liquid.ILiquidStack;
import muramasa.gtu.api.recipe.RecipeBuilder;
import muramasa.gtu.api.recipe.RecipeMap;
import stanhebben.zenscript.annotations.ZenMethod;

public class CTRecipeBuilder {

    private RecipeBuilder RB;

    public CTRecipeBuilder(RecipeMap recipeMap) {
        RB = new RecipeBuilder().get(recipeMap);
    }

    @ZenMethod
    public CTRecipeBuilder ii(IIngredient... ingredients) {
        RB.ii(GregTechTweaker.getItems(ingredients));
        return this;
    }

    @ZenMethod
    public CTRecipeBuilder io(IIngredient... ingredients) {
        RB.io(GregTechTweaker.getItems(ingredients));
        return this;
    }

    @ZenMethod
    public CTRecipeBuilder fi(ILiquidStack... liquids) {
        RB.fi(GregTechTweaker.getFluids(liquids));
        return this;
    }

    @ZenMethod
    public CTRecipeBuilder fo(ILiquidStack... liquids) {
        RB.fo(GregTechTweaker.getFluids(liquids));
        return this;
    }

    @ZenMethod
    public CTRecipeBuilder add(long duration, long power) {
        RB.add(duration, power);
        return this;
    }
}
