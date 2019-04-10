package muramasa.gtu.api.tileentities.multi;

import muramasa.gtu.api.capability.IComponentHandler;
import muramasa.gtu.api.capability.impl.MachineFluidHandler;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.recipe.Recipe;
import muramasa.gtu.api.recipe.RecipeMap;
import muramasa.gtu.api.util.Utils;
import net.minecraftforge.fluids.FluidStack;

public class TileEntityFluidMultiMachine extends TileEntityMultiMachine {

    @Override
    public Recipe findRecipe() {
        return RecipeMap.findRecipeFluid(getType().getRecipeMap(), getStoredFluids());
    }

    @Override
    public void consumeInputs() {
        FluidStack[] toConsume = activeRecipe.getInputFluids();
        if (toConsume == null) return;
        MachineFluidHandler fluidHandler;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_FLUID_INPUT)) {
            fluidHandler = hatch.getFluidHandler();
            if (fluidHandler == null) continue;
            toConsume = fluidHandler.consumeAndReturnInputs(toConsume);
            if (toConsume.length == 0) break;
        }
    }

    @Override
    public boolean canOutput() {
        FluidStack[] toOutput = activeRecipe.getOutputFluids();
        if (toOutput == null) return true;
        MachineFluidHandler fluidHandler;
        int matchCount = 0;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_FLUID_OUTPUT)) {
            fluidHandler = hatch.getFluidHandler();
            if (fluidHandler == null) continue;
            matchCount += fluidHandler.getSpaceForOutputs(toOutput);
        }
        return matchCount >= toOutput.length;
    }

    @Override
    public void addOutputs() {
        FluidStack[] toOutput = activeRecipe.getOutputFluids();
        if (toOutput == null) return;
        MachineFluidHandler fluidHandler;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_FLUID_OUTPUT)) {
            fluidHandler = hatch.getFluidHandler();
            if (fluidHandler == null) continue;
            for (int i = 0; i < toOutput.length; i++) {
                System.out.println("Adding output...");
                fluidHandler.addOutputs(toOutput[i]);
            }
        }
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
