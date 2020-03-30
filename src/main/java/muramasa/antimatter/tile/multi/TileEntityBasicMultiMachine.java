package muramasa.antimatter.tile.multi;

import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.util.Utils;

/** Allows a MultiMachine to handle GUI recipes, instead of using Hatches **/
public class TileEntityBasicMultiMachine extends TileEntityMultiMachine {

    @Override
    public Recipe findRecipe() { //TODO support fluids?
        return getMachineType().getRecipeMap().find(itemHandler.get(), null);
    }

    @Override
    public void consumeInputs() {
        itemHandler.ifPresent(h -> h.consumeInputs(activeRecipe.getInputItems()));
    }

    @Override
    public boolean canOutput() {
        return itemHandler.isPresent() && itemHandler.get().canOutputsFit(activeRecipe.getOutputItems());
    }

    @Override
    public void addOutputs() {
        itemHandler.ifPresent(h -> h.addOutputs(activeRecipe.getOutputItems()));
    }

    @Override
    public boolean canRecipeContinue() {
        return itemHandler.isPresent() && Utils.doItemsMatchAndSizeValid(activeRecipe.getInputItems(), itemHandler.get().getInputs());
    }

    @Override
    public boolean consumeResourceForRecipe() {
        return true;
    }
}
