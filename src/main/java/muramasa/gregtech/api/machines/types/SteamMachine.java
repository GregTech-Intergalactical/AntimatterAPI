package muramasa.gregtech.api.machines.types;

import muramasa.gregtech.api.capability.impl.MachineFluidHandler;
import muramasa.gregtech.api.capability.impl.MachineItemHandler;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.recipe.Recipe;
import muramasa.gregtech.api.recipe.RecipeMap;

public class SteamMachine extends ItemFluidMachine {

    public SteamMachine(String name, Tier... tiers) {
        super(name);
        setTiers(tiers != null ? tiers : Tier.getSteam());
    }

    public SteamMachine(String name, Class tileClass) {
        this(name);
        setTileClass(tileClass);
    }

    @Override
    public Recipe findRecipe(MachineItemHandler stackHandler, MachineFluidHandler tankHandler) {
        return RecipeMap.findRecipeItemFluid(recipeMap, stackHandler.getInputs(), tankHandler.getInputs());
    }
}
