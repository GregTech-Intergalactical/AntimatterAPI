package muramasa.gtu.api.tileentities.multi;

import muramasa.gtu.api.capability.IComponentHandler;
import muramasa.gtu.api.capability.impl.MachineItemHandler;
import muramasa.gtu.api.data.Machines;
import muramasa.gtu.api.recipe.Recipe;
import muramasa.gtu.api.recipe.RecipeMap;
import muramasa.gtu.api.util.Utils;
import net.minecraft.item.ItemStack;

public class TileEntityItemMultiMachine extends TileEntityMultiMachine {

    @Override
    public Recipe findRecipe() {
        return RecipeMap.findRecipeItem(getType().getRecipeMap(), getStoredItems());
    }

    /** Consumes inputs from all input hatches. Assumes doItemsMatchAndSizeValid has been used **/
    @Override
    public void consumeInputs() {
        ItemStack[] toConsume = activeRecipe.getInputItems();
        if (toConsume == null) return;
        MachineItemHandler itemHandler;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_ITEM_INPUT)) {
            itemHandler = hatch.getItemHandler();
            if (itemHandler == null) continue;
            toConsume = itemHandler.consumeAndReturnInputs(toConsume);
            if (toConsume.length == 0) break;
        }
    }

    /** Tests if outputs can fit across all output hatches **/
    @Override
    public boolean canOutput() {
        ItemStack[] toOutput = activeRecipe.getOutputItems();
        if (toOutput == null) return true;
        MachineItemHandler itemHandler;
        int matchCount = 0;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_ITEM_OUTPUT)) {
            itemHandler = hatch.getItemHandler();
            if (itemHandler == null) continue;
            matchCount += itemHandler.getSpaceForOutputs(toOutput);
        }
        return matchCount >= toOutput.length;
    }

    /** Export stacks to hatches regardless of space. Assumes canOutputsFit has been used **/
    @Override
    public void addOutputs() {
        ItemStack[] toOutput = activeRecipe.getOutputItems();
        if (toOutput == null) return;
        MachineItemHandler itemHandler;
        for (IComponentHandler hatch : getComponents(Machines.HATCH_ITEM_OUTPUT)) {
            itemHandler = hatch.getItemHandler();
            if (itemHandler == null) continue;
            for (int i = 0; i < toOutput.length; i++) {
                System.out.println("Adding output...");
                itemHandler.addOutputs(toOutput[i]);
            }
        }
    }

    @Override
    public boolean canRecipeContinue() {
        return Utils.doItemsMatchAndSizeValid(activeRecipe.getInputItems(), getStoredItems());
    }

    @Override
    public boolean consumeResourceForRecipe() {
        return true; //TODO
    }
}
