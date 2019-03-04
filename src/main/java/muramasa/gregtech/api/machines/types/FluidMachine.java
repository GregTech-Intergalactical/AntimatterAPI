package muramasa.gregtech.api.machines.types;

import muramasa.gregtech.api.capability.impl.MachineFluidHandler;
import muramasa.gregtech.api.capability.impl.MachineItemHandler;
import muramasa.gregtech.api.recipe.Recipe;
import muramasa.gregtech.api.recipe.RecipeMap;
import muramasa.gregtech.common.tileentities.overrides.TileEntityFluidMachine;

import static muramasa.gregtech.api.gui.SlotType.*;
import static muramasa.gregtech.api.machines.MachineFlag.FLUID;

public class FluidMachine extends BasicMachine {

    public FluidMachine(String name) {
        super(name, TileEntityFluidMachine.class);
        addFlags(FLUID);
        getGui().add(CELL_IN, 35, 63).add(CELL_OUT, 125, 63).add(FL_IN, 53, 63).add(FL_OUT, 107, 63);
    }

    public FluidMachine(String name, Class tileClass) {
        this(name);
        setTileClass(tileClass);
    }

    @Override
    public Recipe findRecipe(MachineItemHandler stackHandler, MachineFluidHandler tankHandler) {
        return RecipeMap.findRecipeFluid(recipeMap, tankHandler.getInputs());
    }
}
