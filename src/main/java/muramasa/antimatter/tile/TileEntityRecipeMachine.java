package muramasa.antimatter.tile;

import muramasa.antimatter.machine.ContentEvent;
import muramasa.antimatter.machine.MachineEvent;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.util.Utils;
import net.minecraft.util.Direction;

import java.util.List;

import static muramasa.antimatter.machine.MachineFlag.RECIPE;
import static muramasa.antimatter.machine.MachineState.*;

public class TileEntityRecipeMachine extends TileEntityMachine {

    /** Logic **/
    protected Recipe activeRecipe;
    protected int curProgress, maxProgress;
    protected float clientProgress;

    @Override
    public void onServerUpdate() {
        super.onServerUpdate();
        if (getMachineState() == ACTIVE) tickMachineLoop();
    }

    public void tickMachineLoop() {
//        if (getMachineState() == ACTIVE || getMachineState() == OUTPUT_FULL) {
//            setMachineState(tickRecipe());
//        } else if (getMachineState() == NO_POWER) {
//            setMachineState(IDLE);
//            checkRecipe();
//        }
        switch (getMachineState()) {
            case ACTIVE:
                setMachineState(tickRecipe());
                break;
            case OUTPUT_FULL:
                setMachineState(tickRecipe());
                break;
            case NO_POWER:
                checkRecipe();
                break;
        }
    }

    /** Recipe Methods **/
    public Recipe findRecipe() {
        return getMachineType().getRecipeMap().find(itemHandler.orElse(null), fluidHandler.orElse(null));
    }

    public void checkRecipe() {
        if (getMachineState().allowRecipeCheck()) { //No active recipes, see of contents match one
            System.out.println("check recipe");
            if (!hadFirstTick()) return; //TODO fixme
            if ((activeRecipe = findRecipe()) != null) {
                if (activeRecipe.getPower() > getMaxInputVoltage()) {
                    //TODO machine tier cannot process recipe
                }
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
        itemHandler.ifPresent(h -> h.consumeInputs(activeRecipe.getInputItems()));
        fluidHandler.ifPresent(h -> h.consumeInputs(activeRecipe.getInputFluids()));
    }

    public boolean canOutput() {
        if (itemHandler.isPresent() && activeRecipe.hasOutputItems() && !itemHandler.get().canOutputsFit(activeRecipe.getOutputItems())) return false;
        if (fluidHandler.isPresent() && activeRecipe.hasOutputFluids() && !fluidHandler.get().canOutputsFit(activeRecipe.getOutputFluids())) return false;
        return true;
    }

    public void addOutputs() {
        itemHandler.ifPresent(h -> h.addOutputs(activeRecipe.getOutputItems()));
        fluidHandler.ifPresent(h -> h.addOutputs(activeRecipe.getOutputFluids()));
        onMachineEvent(MachineEvent.ITEM_OUTPUT);
    }

    public boolean canRecipeContinue() {
        if (itemHandler.isPresent() && activeRecipe.hasInputItems() && !Utils.doItemsMatchAndSizeValid(activeRecipe.getInputItems(), itemHandler.get().getInputs())) return false;
        if (fluidHandler.isPresent() && activeRecipe.hasInputFluids() && !Utils.doFluidsMatchAndSizeValid(activeRecipe.getInputFluids(), fluidHandler.get().getInputs())) return false;
        return true;
    }

    public boolean consumeResourceForRecipe() {
        if (energyHandler.isPresent()) {
            if (energyHandler.get().extract(activeRecipe.getPower(), true) == activeRecipe.getPower()) {
                energyHandler.get().extract(activeRecipe.getPower(), false);
                return true;
            }
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
                if (getMachineType().hasFlag(RECIPE)) checkRecipe();
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
    public boolean setFacing(Direction side) {
        return super.setFacing(side.getAxis() == Direction.Axis.Y ? Direction.NORTH : side);
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
