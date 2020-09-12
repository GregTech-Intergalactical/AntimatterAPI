package muramasa.antimatter.capability.machine;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.ICapabilityHandler;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.Objects;

import static muramasa.antimatter.machine.MachineFlag.GENERATOR;
import static muramasa.antimatter.machine.MachineFlag.RECIPE;
import static muramasa.antimatter.machine.MachineState.*;

public class MachineRecipeHandler<T extends TileEntityMachine> implements IMachineHandler, ICapabilityHandler {

    protected T tile;
    protected Recipe activeRecipe;
    protected int curProgress, maxProgress;
    protected int overclock;

    //Consuming resources can call into the recipe handler, causing a loop.
    //For instance, consuming fluid in the fluid handlers calls back into the MachineRecipeHandler, deadlocking.
    //So just 'lock' during recipe ticking.
    private boolean tickingRecipe = false;

    public MachineRecipeHandler(T tile, CompoundNBT tag) {
        this.tile = tile;
        if (tag != null) deserialize(tag);
    }

    public void onUpdate() {
        if (tile.getMachineState() != IDLE) tickMachineLoop();
    }

    protected void tickMachineLoop() {

        //To avoid a feedback loop caused by events firing from inside the recipe handler
        //(such as consuming resources for a recipe and/or generator check so we dont overflow tick recipes.
        if (tickingRecipe) return;
        switch (tile.getMachineState()) {
            case ACTIVE:
            case OUTPUT_FULL:
                tile.setMachineState(tickRecipe());
                break;
            case POWER_LOSS:
            case NO_POWER:
                MachineState state = tickRecipe();
                if (state != ACTIVE) {
                    tile.setMachineState(IDLE);
                } else {
                    tile.setMachineState(state);
                }
                break;
        }
        tickingRecipe = false;
    }

    public Recipe findRecipe() {
        return tile.getMachineType().getRecipeMap().find(tile.itemHandler.orElse(null), tile.fluidHandler.orElse(null));
    }

    //called when a new recipe is found, to process overclocking
    public void activateRecipe() {
        //if (canOverclock)
        curProgress = 0;
        overclock = 0;
        if (this.tile.getMachineTier().getVoltage() > activeRecipe.getPower()) {
            int voltage = this.tile.getMachineTier().getVoltage();
            int tier = 0;
            //Dont use utils, because we allow overclocking from ulv.
            for (int i = 0; i < Ref.V.length; i++) {
                if (activeRecipe.getPower() <= Ref.V[i]) {
                    tier = i;
                    break;
                }
            }
            int tempoverclock = (this.tile.getMachineTier().getVoltage() / Ref.V[tier]);
            while (tempoverclock > 1) {
                tempoverclock >>= 2;
                overclock++;
            }
        }
        maxProgress = Math.max(1, activeRecipe.getDuration() / (1 << overclock));
        onRecipeFound();
    }

    public boolean checkRecipe() {
        if (tile.getMachineState().allowRecipeCheck()) { //No active recipes, see of contents match one
            System.out.println("check recipe");
            if (!tile.hadFirstTick()) return false; //TODO fixme
            if ((activeRecipe = findRecipe()) != null) {
                if (activeRecipe.getPower() > tile.getMaxInputVoltage()) {
                    return false;
                    //TODO machine tier cannot process recipe
                }
                if (tile.has(GENERATOR) && (!activeRecipe.hasInputFluids() || activeRecipe.getInputFluids().length != 1)) {
                    return false;
                }
                if (!canOutput()) return false;
                activateRecipe();
                //TODO: Rename NO_POWER? Default to no_power for now.
                tile.setMachineState(NO_POWER);
                return true;
            }
        }
        return false;
    }

    public MachineState tickRecipe() {
        tickingRecipe = true;
        onRecipeTick();
        if (activeRecipe == null)
            return IDLE; //TODO this null check added if saved state triggers tickMachineLoop, but there is no recipe to process
        if (curProgress == maxProgress) { //End of current recipe cycle, deposit items
            if (!canOutput()) return OUTPUT_FULL; //Return and loop until outputs can be added
            curProgress = 0;
            addOutputs(); //Add outputs and reset to process next recipe cycle
            if (!canRecipeContinue()) {
                resetRecipe();
                return IDLE;
            } else {
                return ACTIVE;
            }
        } else {
            //Calculate per recipe tick so user has risk of losing items
            if (!consumeResourceForRecipe()) return curProgress == 0 ? IDLE : POWER_LOSS;
            if (curProgress == 0 && !tile.has(GENERATOR)) consumeInputs(); //Consume recipe inputs on first recipe tick
            curProgress++;
            return ACTIVE;
        }
    }

    @Nullable
    public Recipe getActiveRecipe() {
        return activeRecipe;
    }

    public void consumeInputs() {
        tile.itemHandler.ifPresent(h -> h.consumeInputs(activeRecipe,false));
        tile.fluidHandler.ifPresent(h -> h.consumeInputs(activeRecipe.getInputFluids()));
    }

    public boolean canOutput() {
        if (tile.itemHandler.isPresent() && activeRecipe.hasOutputItems() && !tile.itemHandler.get().canOutputsFit(activeRecipe.getOutputItems()))
            return false;
        if (tile.fluidHandler.isPresent() && activeRecipe.hasOutputFluids() && !tile.fluidHandler.get().canOutputsFit(activeRecipe.getOutputFluids()))
            return false;
        return true;
    }

    public void addOutputs() {
        tile.itemHandler.ifPresent(h -> {
            h.addOutputs(activeRecipe.getOutputItems());
            tile.onMachineEvent(MachineEvent.ITEMS_OUTPUTTED);
        });
        tile.fluidHandler.ifPresent(h -> h.addOutputs(activeRecipe.getOutputFluids()));
    }

    public boolean canRecipeContinue() {
        if (tile.itemHandler.isPresent() && !tile.itemHandler.get().consumeInputs(activeRecipe,true)) //!Utils.doItemsMatchAndSizeValid(activeRecipe.getInputItems(), tile.itemHandler.get().getInputs()))
            return false;
        if (tile.fluidHandler.isPresent() && !Utils.doFluidsMatchAndSizeValid(activeRecipe.getInputFluids(), tile.fluidHandler.get().getInputs()))
            return false;
        return true;
    }

    public boolean consumeResourceForRecipe() {
        if (tile.energyHandler.isPresent()) {
            if (!tile.has(GENERATOR)) {
                if (tile.energyHandler.get().extract((activeRecipe.getPower() * (1 << overclock)), true) >= activeRecipe.getPower() * (1 << overclock)) {
                    tile.energyHandler.get().extract((activeRecipe.getPower() * (1 << overclock)), false);
                    return true;
                }
            } else {
               return consumeGeneratorResources();
            }
        }
        return false;
    }

    protected boolean consumeGeneratorResources() {
        if (!activeRecipe.hasInputFluids()) {
            throw new RuntimeException("Missing fuel in active generator recipe!");
        }
        boolean shouldRun = tile.energyHandler.map(h -> h.insert((long)(tile.getMachineType().getMachineEfficiency()*(double)tile.getMachineTier().getVoltage()),true) > 0).orElse(false);
        if (!shouldRun) return false;
        long toConsume = (long) ((double)tile.getMachineTier().getVoltage() /(double)(activeRecipe.getPower() /(double) Objects.requireNonNull(activeRecipe.getInputFluids())[0].getAmount()));
        if (tile.fluidHandler.map(h -> {
            int amount = h.inputWrapper.drain(new FluidStack(activeRecipe.getInputFluids()[0],(int)toConsume), IFluidHandler.FluidAction.SIMULATE).getAmount();
            if (amount == toConsume) {
                h.inputWrapper.drain(new FluidStack(activeRecipe.getInputFluids()[0],(int)toConsume), IFluidHandler.FluidAction.EXECUTE);
                return true;
            }
            return false;
        }).orElse(false)) {
            //Input energy
            tile.energyHandler.ifPresent(handler -> {
                handler.insert((long)(tile.getMachineType().getMachineEfficiency()*(double)tile.getMachineTier().getVoltage()), false);
            });
            return true;
        }
        return false;
    }

    public void resetRecipe() {
        activeRecipe = null;
        overclock = 0;
    }

    /**
     * Events
     **/
    public void onRecipeFound() {
        //NOOP
    }

    public void onRecipeTick() {
        //NOOP
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        if (event instanceof ContentEvent) {
            //TODO seems to fire before the inventory has actually changed?
            switch ((ContentEvent) event) {
                case FLUID_INPUT_CHANGED:
                case ITEM_INPUT_CHANGED:
                case FLUID_OUTPUT_CHANGED:
                case ITEM_OUTPUT_CHANGED:
                    if ((tile.getMachineState() == IDLE) && tile.getMachineType().has(RECIPE)) checkRecipe();
                    if ((tile.getMachineState().allowLoopTick() || tile.getMachineState() == NO_POWER) && activeRecipe != null) tickMachineLoop();
                    break;
                case ENERGY_SLOT_CHANGED:
                    //Battery added, try to continue.
                    if (this.tile.getMachineState() == IDLE)
                        this.tile.setMachineState(NO_POWER);
                    if (this.tile.getMachineState() == POWER_LOSS)
                        this.tile.setMachineState(ACTIVE);
            }
            tile.markDirty(); //TODO determine if needed
        }
        if (event instanceof MachineEvent) {
            switch ((MachineEvent)event) {
                case ENERGY_INPUTTED:
                    if (this.tile.getMachineState() == IDLE && activeRecipe != null)
                        //NO_POWER is bad name i guess, by this i mean try to do a recipe check next tick.
                        this.tile.setMachineState(NO_POWER);
                    if (this.tile.getMachineState() == POWER_LOSS && activeRecipe != null)
                        this.tile.setMachineState(ACTIVE);
                    break;
                default:
                    break;
            }
        }
    }

    public int getCurProgress() {
        return curProgress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }


    /**
     * NBT
     **/
    // TODO: Finish
    @Override
    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        return tag;
    }

    @Override
    public void deserialize(CompoundNBT tag) {
    }

    @Override
    public Capability<?> getCapability() {
        return AntimatterCaps.RECIPE_HANDLER_CAPABILITY;
    }
}
