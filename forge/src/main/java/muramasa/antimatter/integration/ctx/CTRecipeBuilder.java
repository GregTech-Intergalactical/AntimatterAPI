//package muramasa.gtu.integration.ctx;
//
//import crafttweaker.api.item.IIngredient;
//import crafttweaker.api.liquid.ILiquidStack;
//import muramasa.antimatter.recipe.map.RecipeBuilder;
//import stanhebben.zenscript.annotations.ZenMethod;
//
//public class CTRecipeBuilder {
//
//    private RecipeBuilder builder;
//
//    public CTRecipeBuilder(RecipeBuilder builder) {
//        this.builder = builder;
//    }
//
//    @ZenMethod
//    public CTRecipeBuilder ii(IIngredient... ingredients) {
//        builder.ii(GregTechTweaker.getItems(ingredients));
//        return this;
//    }
//
//    @ZenMethod
//    public CTRecipeBuilder io(IIngredient... ingredients) {
//        builder.io(GregTechTweaker.getItems(ingredients));
//        return this;
//    }
//
//    @ZenMethod
//    public CTRecipeBuilder fi(ILiquidStack... liquids) {
//        builder.fi(GregTechTweaker.getFluids(liquids));
//        return this;
//    }
//
//    @ZenMethod
//    public CTRecipeBuilder fo(ILiquidStack... liquids) {
//        builder.fo(GregTechTweaker.getFluids(liquids));
//        return this;
//    }
//
//    @ZenMethod
//    public CTRecipeBuilder add(long duration, long power) {
//        builder.add(duration, power);
//        return this;
//    }
//}
