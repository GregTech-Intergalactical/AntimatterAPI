package muramasa.gtu.api.tileentities.multi;

import muramasa.gtu.api.recipe.Recipe;
import muramasa.gtu.api.recipe.RecipeMap;
import muramasa.gtu.api.util.Utils;

/** Allows a MultiMachine to handle GUI recipes, instead of using Hatches **/
public class TileEntityBasicItemMultiMachine extends TileEntityMultiMachine {

    @Override
    public Recipe findRecipe() {
        return RecipeMap.findRecipeItem(getType().getRecipeMap(), itemHandler.getInputs());
    }

    @Override
    public void consumeInputs() {
        itemHandler.consumeInputs(activeRecipe.getInputItems());
    }

    @Override
    public boolean canOutput() {
        return itemHandler.canOutputsFit(activeRecipe.getOutputItems());
    }

    @Override
    public void addOutputs() {
        itemHandler.addOutputs(activeRecipe.getOutputItems());
    }

    @Override
    public boolean canRecipeContinue() {
        return Utils.doItemsMatchAndSizeValid(activeRecipe.getInputItems(), itemHandler.getInputs());
    }

    @Override
    public boolean consumeResourceForRecipe() {
        return true;
    }
}
