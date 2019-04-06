package muramasa.gtu.api.tileentities;

import muramasa.gtu.api.util.Utils;

public class TileEntityFluidMachine extends TileEntityItemFluidMachine {

    @Override
    public void consumeInputs() {
        fluidHandler.consumeInputs(activeRecipe.getInputFluids());
    }

    @Override
    public boolean canOutput() {
        return fluidHandler.canFluidsFit(activeRecipe.getOutputFluids());
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
