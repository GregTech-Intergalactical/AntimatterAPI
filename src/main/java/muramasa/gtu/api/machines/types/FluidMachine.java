package muramasa.gtu.api.machines.types;

import muramasa.gtu.api.capability.impl.MachineFluidHandler;
import muramasa.gtu.api.capability.impl.MachineItemHandler;
import muramasa.gtu.api.recipe.Recipe;
import muramasa.gtu.api.recipe.RecipeMap;
import muramasa.gtu.api.tileentities.TileEntityFluidMachine;

import static muramasa.gtu.api.gui.SlotType.*;
import static muramasa.gtu.api.machines.MachineFlag.FLUID;

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
