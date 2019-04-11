package muramasa.gtu.api.tileentities;

import muramasa.gtu.api.machines.ContentUpdateType;
import muramasa.gtu.api.recipe.Recipe;
import muramasa.gtu.api.recipe.RecipeMap;
import muramasa.gtu.api.util.Utils;

public class TileEntityItemFluidMachine extends TileEntityItemMachine {

    @Override
    public Recipe findRecipe() {
        return RecipeMap.findRecipeItemFluid(getType().getRecipeMap(), itemHandler.getInputs(), fluidHandler.getInputs());
    }

    @Override
    public void consumeInputs() {
        super.consumeInputs();
        if (activeRecipe.hasInputFluids()) {
            fluidHandler.consumeInputs(activeRecipe.getInputFluids());
        }
    }

    @Override
    public boolean canOutput() {
        if (activeRecipe.hasOutputFluids()) {
            return super.canOutput() && fluidHandler.canOutputsFit(activeRecipe.getOutputFluids());
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

    @Override
    public void onContentsChanged(ContentUpdateType type, int slot, boolean empty) {
        super.onContentsChanged(type, slot, empty);
        if (empty && type == ContentUpdateType.ITEM_CELL) {
            if (slot == 0) { //Input slot
//                ItemStack stack = itemHandler.getCellInput();
//                if (stack.getItem() instanceof MaterialItem) {
//                    MaterialItem item = (MaterialItem) stack.getItem();
//                    if (item.getPrefix() == Prefix.Cell) {
//                        fluidHandler.addInputs(item.getMaterial().getLiquid(1000));
//                    } else if (item.getPrefix() == Prefix.CellGas) {
//                        fluidHandler.addInputs(item.getMaterial().getGas(1000));
//                    } else if (item.getPrefix() == Prefix.CellPlasma) {
//                        fluidHandler.addInputs(item.getMaterial().getPlasma(1000));
//                    }
//                    itemHandler.getCellInput().setCount(0);
//                } /*else if (ItemType.EmptyCell.isEqual(stack)) {
//                    System.out.println("Empty Cell");
//                    fluidHandler.getInput(0).setFluid(null);
//                }*/
            } else if (slot == 1) { //Output slot

            }
        }
    }
}
