package muramasa.gregtech.common.tileentities.overrides;

import muramasa.gregtech.api.util.Utils;

public class TileEntityItemFluidMachine extends TileEntityItemMachine {

    @Override
    public void consumeInputs() {
        if (activeRecipe.hasInputFluids()) {
            super.consumeInputs();
            fluidHandler.consumeInputs(activeRecipe.getInputFluids());
        }
        super.consumeInputs();
    }

    @Override
    public boolean canOutput() {
        if (activeRecipe.hasOutputFluids()) {
            return super.canOutput() && fluidHandler.canFluidsFit(activeRecipe.getOutputFluids());
        }
        return super.canOutput();
    }

    @Override
    public void addOutputs() {
        if (activeRecipe.hasOutputFluids()) {
            fluidHandler.addOutputs(activeRecipe.getOutputFluids());
        }
        super.addOutputs();
    }

    @Override
    public boolean canRecipeContinue() {
        if (activeRecipe.hasInputFluids()) {
            return super.canRecipeContinue() && Utils.doFluidsMatchAndSizeValid(activeRecipe.getInputFluids(), fluidHandler.getInputs());
        }
        return super.canRecipeContinue();
    }
}
