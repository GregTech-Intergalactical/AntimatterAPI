package muramasa.gtu.api.tileentities.multi;

import muramasa.gtu.api.recipe.Recipe;
import muramasa.gtu.api.recipe.RecipeMap;
import muramasa.gtu.api.util.Utils;

public class TileEntityFluidMultiMachine extends TileEntityMultiMachine {

    @Override
    public Recipe findRecipe() {
        return RecipeMap.findRecipeFluid(getType().getRecipeMap(), getStoredFluids());
    }

    @Override
    public void consumeInputs() {
        consumeFluids(activeRecipe.getInputFluids());
    }

    @Override
    public boolean canOutput() {
        return canFluidsFit(activeRecipe.getOutputFluids());
    }

    @Override
    public void addOutputs() {
        outputFluids(activeRecipe.getOutputFluids());
    }

    @Override
    public boolean canRecipeContinue() {
        return Utils.doFluidsMatchAndSizeValid(activeRecipe.getInputFluids(), getStoredFluids());
    }

    @Override
    public boolean consumeResourceForRecipe() {
        return true; //TODO
    }
}
