package muramasa.gregtech.common.tileentities.overrides;

import muramasa.gregtech.api.recipe.Recipe;

public class TileEntitySteamMachine extends TileEntityBasicMachine {

    @Override
    public Recipe findRecipe() {
        return getMachineType().findRecipe(stackHandler.getInputs(), inputTank.getFluid());
    }

    @Override
    public boolean consumeResourceForRecipe() {
        //TODO handle steam instead of energy
        return super.consumeResourceForRecipe();
    }
}
