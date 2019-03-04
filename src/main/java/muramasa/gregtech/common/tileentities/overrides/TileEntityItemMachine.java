package muramasa.gregtech.common.tileentities.overrides;

import muramasa.gregtech.api.util.Utils;

public class TileEntityItemMachine extends TileEntityBasicMachine {

    @Override
    public void consumeInputs() {
        itemHandler.consumeInputs(activeRecipe.getInputStacks());
    }

    @Override
    public boolean canOutput() {
        return itemHandler.canStacksFit(activeRecipe.getOutputStacks());
    }

    @Override
    public void addOutputs() {
        itemHandler.addOutputs(activeRecipe.getOutputStacks());
    }

    @Override
    public boolean canRecipeContinue() {
        return Utils.doStacksMatchAndSizeValid(activeRecipe.getInputStacks(), itemHandler.getInputs());
    }
}
