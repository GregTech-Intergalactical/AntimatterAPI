package muramasa.gtu.api.tileentities.multi;

import muramasa.gtu.api.recipe.Recipe;
import muramasa.gtu.api.recipe.RecipeMap;
import muramasa.gtu.api.util.Utils;

public class TileEntityItemMultiMachine extends TileEntityMultiMachine {

    @Override
    public Recipe findRecipe() {
        return RecipeMap.findRecipeItem(getType().getRecipeMap(), getStoredItems());
    }

    @Override
    public void consumeInputs() {
        consumeItems(activeRecipe.getInputItems());
    }

    @Override
    public boolean canOutput() {
        return canItemsFit(activeRecipe.getOutputItems());
    }

    @Override
    public void addOutputs() {
        outputItems(activeRecipe.getOutputItems());
    }

    @Override
    public boolean canRecipeContinue() {
        return Utils.doItemsMatchAndSizeValid(activeRecipe.getInputItems(), getStoredItems());
    }

    @Override
    public boolean consumeResourceForRecipe() {
        return true; //TODO
    }
}
