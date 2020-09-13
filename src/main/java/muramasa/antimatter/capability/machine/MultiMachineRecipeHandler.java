/*
package muramasa.antimatter.capability.machine;

import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.nbt.CompoundNBT;

public class MultiMachineRecipeHandler<T extends TileEntityMultiMachine> extends MachineRecipeHandler<T> {

    public MultiMachineRecipeHandler(TileEntityMachine tile) {
        super((T) tile);
    }

    @Override
    public Recipe findRecipe() {
        return tile.getMachineType().getRecipeMap().find(tile.getStoredItems(), tile.getStoredFluids());
    }

    @Override
    public void consumeInputs() {
        if (activeRecipe.hasInputItems()) tile.consumeItems(activeRecipe.getInputItems());
        if (activeRecipe.hasInputFluids()) tile.consumeFluids(activeRecipe.getInputFluids());
    }

    @Override
    public boolean canOutput() {
        if ((tile.has(MachineFlag.ITEM) && !tile.canItemsFit(activeRecipe.getOutputItems())) ||
            (tile.has(MachineFlag.FLUID) && !tile.canFluidsFit(activeRecipe.getOutputFluids()))) {
            return false;
        }
        return true;
    }

    @Override
    public void addOutputs() {
        if (tile.has(MachineFlag.ITEM)) tile.outputItems(activeRecipe.getOutputItems());
        if (tile.has(MachineFlag.FLUID)) tile.outputFluids(activeRecipe.getOutputFluids());
    }

    @Override
    public boolean canRecipeContinue() {
        if ((tile.has(MachineFlag.ITEM) && !Utils.doItemsMatchAndSizeValid(activeRecipe.getInputItems(), tile.getStoredItems())) ||
            (tile.has(MachineFlag.FLUID) && !Utils.doFluidsMatchAndSizeValid(activeRecipe.getInputFluids(), tile.getStoredFluids()))) {
            return false;
        }
        return true;
    }

    @Override
    public boolean consumeResourceForRecipe() {
        TODO breaks generators like combustion engine
       if (getStoredEnergy() >= activeRecipe.getPower()) {
           consumeEnergy(activeRecipe.getPower());
           return true;
       }
        return false;
        return true;
    }
}
 */
