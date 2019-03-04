package muramasa.gregtech.common.tileentities.overrides;

import muramasa.gregtech.api.util.Utils;

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
