package muramasa.gtu.api.tileentities;

import muramasa.gtu.api.machines.ContentUpdateType;
import muramasa.gtu.api.machines.MachineState;
import muramasa.gtu.api.recipe.Recipe;

import java.util.List;

import static muramasa.gtu.api.machines.MachineFlag.RECIPE;
import static muramasa.gtu.api.machines.MachineState.*;

public abstract class TileEntityBasicMachine extends TileEntityMachine {

    /** Logic **/
    protected Recipe activeRecipe;
    protected int curProgress, maxProgress;
    protected float clientProgress;

    @Override
    public void onServerUpdate() {
        if (getMachineState() == ACTIVE) tickMachineLoop();
        if (coverHandler != null) coverHandler.tick();
    }

    /** Recipe Methods **/
    public Recipe findRecipe() {
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

    public void tickMachineLoop() {
        switch (getMachineState()) {
            case ACTIVE:
            case OUTPUT_FULL:
                setMachineState(tickRecipe());
                break;
        }
    }

    public boolean consumeResourceForRecipe() {
        if (energyHandler.extract(activeRecipe.getPower(), true) == activeRecipe.getPower()) {
            energyHandler.extract(activeRecipe.getPower(), false);
            return true;
        }
        return false;
    }

    public void consumeInputs() {
        //NOOP
    }

    public boolean canOutput() {
        return true; //NOOP
    }

    public void addOutputs() {
        //NOOP
    }

    public boolean canRecipeContinue() {
        return true; //NOOP
    }

    /** Events **/
    public void onRecipeFound() {
        //NOOP
    }

    public void onRecipeTick() {
        //NOOP
    }

    @Override
    public void onContentsChanged(ContentUpdateType type, int slot, boolean empty) {
        if (empty) return;
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
    public void setMachineState(MachineState newState) {
        if (getMachineState().getOverlayId() != newState.getOverlayId() && (newState.getOverlayId() == 0 || newState.getOverlayId() == 1)) {
            markForRenderUpdate();
            System.out.println("RENDER UPDATE");
        }
        super.setMachineState(newState);
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
