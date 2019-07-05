package muramasa.gtu.api.tileentities;

import muramasa.gtu.api.machines.ContentEvent;
import muramasa.gtu.api.machines.MachineState;
import muramasa.gtu.api.recipe.Recipe;
import muramasa.gtu.api.recipe.RecipeMap;
import muramasa.gtu.api.util.Utils;
import net.minecraft.util.EnumFacing;

import java.util.List;

import static muramasa.gtu.api.machines.MachineFlag.RECIPE;
import static muramasa.gtu.api.machines.MachineState.*;

public class TileEntityRecipeMachine extends TileEntityMachine {

    /** Logic **/
    protected Recipe activeRecipe;
    protected int curProgress, maxProgress;
    protected float clientProgress;

    @Override
    public void onServerUpdate() {
        if (getMachineState() == ACTIVE) tickMachineLoop();
        if (coverHandler != null) coverHandler.tick();
    }

    public void tickMachineLoop() {
        switch (getMachineState()) {
            case ACTIVE:
            case OUTPUT_FULL:
                setMachineState(tickRecipe());
                break;
        }
    }

    /** Recipe Methods **/
    public Recipe findRecipe() {
        if (itemHandler != null) return RecipeMap.findRecipeItem(getType().getRecipeMap(), getMaxInputVoltage(), itemHandler.getInputs());
        else if (fluidHandler != null) return RecipeMap.findRecipeFluid(getType().getRecipeMap(), getMaxInputVoltage(), fluidHandler.getInputs());
        return null;
    }

    public void checkRecipe() {
        if (getMachineState().allowRecipeCheck()) { //No active recipes, see of contents match one
            System.out.println("check recipe");
            if (!hadFirstTick()) return; //TODO fixme
            if ((activeRecipe = findRecipe()) != null) {
                curProgress = 0;
                maxProgress = activeRecipe.getDuration();
                setMachineState(ACTIVE);
                onRecipeFound();
            }
        }
    }

    public MachineState tickRecipe() {
        onRecipeTick();
        if (activeRecipe == null) return IDLE; //TODO this null check added if saved state triggers tickMachineLoop, but there is no recipe to process
        if (curProgress == maxProgress) { //End of current recipe cycle, deposit items
            if (!canOutput()) return OUTPUT_FULL; //Return and loop until outputs can be added
            curProgress = 0;
            addOutputs(); //Add outputs and reset to process next recipe cycle
            return !canRecipeContinue() ? IDLE : ACTIVE; //Check if has enough stack count for next recipe cycle
        } else {
            //Calculate per recipe tick so user has risk of losing items
            if (!consumeResourceForRecipe()) return curProgress == 0 ? NO_POWER : POWER_LOSS;
            if (curProgress == 0) consumeInputs(); //Consume recipe inputs on first recipe tick
            curProgress++;
            return ACTIVE;
        }
    }

    public void consumeInputs() {
        if (itemHandler != null) itemHandler.consumeInputs(activeRecipe.getInputItems());
        if (fluidHandler != null) fluidHandler.consumeInputs(activeRecipe.getInputFluids());
    }

    public boolean canOutput() {
        if ((itemHandler != null && !itemHandler.canOutputsFit(activeRecipe.getOutputItems())) ||
            (fluidHandler != null && !fluidHandler.canOutputsFit(activeRecipe.getOutputFluids()))) {
            return false;
        }
        return true;
    }

    public void addOutputs() {
        if (itemHandler != null) itemHandler.addOutputs(activeRecipe.getOutputItems());
        if (fluidHandler != null) fluidHandler.addOutputs(activeRecipe.getOutputFluids());
    }

    public boolean canRecipeContinue() {
        if ((itemHandler != null && !Utils.doItemsMatchAndSizeValid(activeRecipe.getInputItems(), itemHandler.getInputs())) ||
            (fluidHandler != null && !Utils.doFluidsMatchAndSizeValid(activeRecipe.getInputFluids(), fluidHandler.getInputs()))) {
            return false;
        }
        return true;
    }

    public boolean consumeResourceForRecipe() {
        if (energyHandler.extract(activeRecipe.getPower(), true) == activeRecipe.getPower()) {
            energyHandler.extract(activeRecipe.getPower(), false);
            return true;
        }
        return false;
    }

    /** Helpers **/
    public void resetMachine() {
        setMachineState(getDefaultMachineState());
        activeRecipe = null;
    }

    /** Events **/
    public void onRecipeFound() {
        //NOOP
    }

    public void onRecipeTick() {
        //NOOP
    }

    @Override
    public void onContentsChanged(ContentEvent type, int slot) {
        //TODO seems to fire before the inventory has actually changed?
        switch (type) {
            case ITEM_INPUT:
                if (getMachineState().allowLoopTick() || getMachineState() == NO_POWER) tickMachineLoop();
                if (getType().hasFlag(RECIPE)) checkRecipe();
                break;
            case ITEM_OUTPUT:
                if (getMachineState().allowLoopTick() || getMachineState() == NO_POWER) tickMachineLoop();
                break;
        }
        markDirty(); //TODO determine if needed
    }

    /** Getters **/
    public float getClientProgress() {
        return clientProgress;
    }

    @Override
    public int getCurProgress() {
        return curProgress;
    }

    @Override
    public int getMaxProgress() {
        return maxProgress;
    }

    /** Setters **/
    @Override
    public boolean setFacing(EnumFacing side) {
        return super.setFacing(side.getAxis() == EnumFacing.Axis.Y ? EnumFacing.NORTH : side);
    }

    public void setClientProgress(float progress) {
        clientProgress = progress;
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        info.add("Recipe: " + curProgress + " / " + maxProgress);
        return info;
    }
}
