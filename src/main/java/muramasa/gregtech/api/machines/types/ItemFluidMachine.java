package muramasa.gregtech.api.machines.types;

import muramasa.gregtech.api.capability.impl.MachineStackHandler;
import muramasa.gregtech.api.capability.impl.MachineTankHandler;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.recipe.Recipe;
import muramasa.gregtech.api.recipe.RecipeMap;

public class ItemFluidMachine extends BasicMachine {

    public ItemFluidMachine(String name, MachineFlag... extraFlags) {
        super(name, extraFlags);
//        addFlags(FLUID);
    }

    @Override
    public Recipe findRecipe(MachineStackHandler stackHandler, MachineTankHandler tankHandler) {
        return RecipeMap.findRecipeItemFluid(recipeMap, stackHandler.getInputs(), tankHandler.getInputs());
    }
}
