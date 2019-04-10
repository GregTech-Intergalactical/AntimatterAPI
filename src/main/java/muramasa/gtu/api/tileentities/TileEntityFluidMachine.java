package muramasa.gtu.api.tileentities;

import muramasa.gtu.api.recipe.Recipe;
import muramasa.gtu.api.recipe.RecipeMap;
import muramasa.gtu.api.util.Utils;

//TODO extend BasicMachine
public class TileEntityFluidMachine extends TileEntityItemFluidMachine {

    @Override
    public Recipe findRecipe() {
        return RecipeMap.findRecipeFluid(getType().getRecipeMap(), fluidHandler.getInputs());
    }

    @Override
    public void consumeInputs() {
        fluidHandler.consumeInputs(activeRecipe.getInputFluids());
    }

    @Override
    public boolean canOutput() {
        return fluidHandler.canOutputsFit(activeRecipe.getOutputFluids());
    }

    @Override
    public void addOutputs() {
        fluidHandler.addOutputs(activeRecipe.getOutputFluids());
    }

    @Override
    public boolean canRecipeContinue() {
        return Utils.doFluidsMatchAndSizeValid(activeRecipe.getInputFluids(), fluidHandler.getInputs());
    }
}
