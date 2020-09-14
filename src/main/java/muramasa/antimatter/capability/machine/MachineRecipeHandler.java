package muramasa.antimatter.capability.machine;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;

import static muramasa.antimatter.machine.MachineState.*;

public class MachineRecipeHandler<T extends TileEntityMachine> implements IMachineHandler {

    protected final T tile;
    protected final boolean generator;
    protected final IIntArray progress = new IntArray(1);

    protected Recipe activeRecipe;
    protected int currentProgress, maxProgress;
    protected int overclock;

    public MachineRecipeHandler(T tile) {
        this.tile = tile;
        this.generator = tile.getMachineType().has(MachineFlag.GENERATOR);
    }

    public void setClientProgress() {
        this.progress.set(0, Float.floatToRawIntBits(this.currentProgress / (float) this.maxProgress));
    }

    @OnlyIn(Dist.CLIENT)
    public float getClientProgress() {
        return Float.intBitsToFloat(this.progress.get(0));
    }

    public void onServerUpdate() {
        switch (tile.getMachineState()) {
            case IDLE:
                break;
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
    }

    public Recipe findRecipe() {
        return tile.getMachineType().getRecipeMap().find(tile.getMachineTier(), tile.itemHandler, tile.fluidHandler);
    }

    public MachineState tickRecipe() {
        // tickingRecipe = true;
        // onRecipeTick(); TODO: MachineEvent
        if (this.activeRecipe == null) {
            if (tile.getMachineState() == null) {
                return IDLE;
            }
            checkRecipe();
            return tile.getMachineState();
        } else if (!canOutput()) {
            return OUTPUT_FULL;
        } else if (this.currentProgress == this.maxProgress) {
            this.currentProgress = 0;
            if (!generator) {
                this.consumeInputs();
            }
            tile.itemHandler.ifPresent(h -> {
                h.addOutputs(activeRecipe.getOutputItems());
                this.onMachineEvent(MachineEvent.ITEMS_OUTPUTTED);
            });
            tile.fluidHandler.ifPresent(h -> {
                h.addOutputs(activeRecipe.getOutputFluids());
                this.onMachineEvent(MachineEvent.FLUIDS_OUTPUTTED);
            });
            if (!canRecipeContinue()) {
                this.resetRecipe();
                return IDLE;
            } else {
                return ACTIVE;
            }
        } else {
            this.currentProgress++;
            return ACTIVE;
        }
    }

    public boolean checkRecipe() {
        if (tile.getMachineState().allowRecipeCheck()) {
            if ((activeRecipe = findRecipe()) != null) {
                if (activeRecipe.getPower() > tile.getMaxInputVoltage() && !this.generator) {
                    tile.setMachineState(INVALID_TIER);
                    return false;
                }
                activateRecipe();
                return true;
            }
        }
        return false;
    }

    public void activateRecipe() {
        //if (canOverclock)
        this.currentProgress = 0;
        this.overclock = 0;
        int voltage = tile.getMachineTier().getVoltage();
        if (voltage > activeRecipe.getPower()) {
            int tempOc = (voltage / Ref.V[Utils.getVoltageTier(activeRecipe.getPower())]);
            while (tempOc > 1) {
                tempOc >>= 2;
                this.overclock++;
            }
        }
        this.maxProgress = Math.max(1, activeRecipe.getDuration() / (1 << overclock));
        tile.setMachineState(ACTIVE);
        if (generator) {
            consumeInputs();
        }
        // onRecipeFound(); TODO: MachineEvent
    }

    protected void consumeInputs() {
        tile.energyHandler.ifPresent(e -> {
            tile.itemHandler.ifPresent(i -> i.consumeInputs(this.activeRecipe, false));
            if (generator) {
                e.insert((long) (tile.getMachineType().getMachineEfficiency() * tile.getMachineTier().getVoltage()), false);
            } else {
                long extract = activeRecipe.getPower() * (1 << overclock);
                if (e.extract(extract, true) >= extract) {
                    e.extract(extract, false);
                }
            }
        });
    }

    protected boolean canOutput() {
        return this.activeRecipe.hasOutputItems() && tile.itemHandler.map(i -> i.canOutputsFit(this.activeRecipe.getOutputItems())).orElse(false) || this.activeRecipe.hasOutputFluids() && tile.fluidHandler.map(f -> f.canOutputsFit(this.activeRecipe.getOutputFluids())).orElse(false);
    }

    protected boolean canRecipeContinue() {
        return tile.itemHandler.map(i -> i.consumeInputs(this.activeRecipe, true)).orElse(false) || Utils.doFluidsMatchAndSizeValid(activeRecipe.getInputFluids(), tile.fluidHandler.map(MachineFluidHandler::getInputs).orElse(new FluidStack[0]));
    }

    public void resetRecipe() {
        this.activeRecipe = null;
        this.currentProgress = 0;
        this.overclock = 0;
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        if (event instanceof ContentEvent) {
            switch ((ContentEvent) event) {
                case FLUID_INPUT_CHANGED:
                case ITEM_INPUT_CHANGED:
                    if (!this.checkRecipe() && tile.getMachineState() == ACTIVE || tile.getMachineState() == POWER_LOSS) {
                        tile.setMachineState(IDLE);
                    }
                    break;
                case FLUID_OUTPUT_CHANGED:
                case ITEM_OUTPUT_CHANGED:
                    if (this.activeRecipe != null && this.currentProgress == 0 && this.tile.getMachineState() == ACTIVE || this.tile.getMachineState() == POWER_LOSS) {
                        tile.setMachineState(IDLE);
                    }
                    break;
                    /*
                case ENERGY_SLOT_CHANGED:
                    if (tile.getMachineState() == IDLE) {
                        tile.setMachineState(NO_POWER);
                    }
                    if (tile.getMachineState() == POWER_LOSS) {
                        tile.setMachineState(ACTIVE);
                    }
                    break;
                     */
            }
        } else if (event instanceof MachineEvent) {
            switch ((MachineEvent) event) {
                case ENERGY_INPUTTED:
                    if (tile.getMachineState() == POWER_LOSS) {
                        tile.setMachineState(ACTIVE);
                    }
                    break;
                case ENERGY_DRAINED:
                    if (tile.getMachineState() == ACTIVE) {
                        tile.setMachineState(POWER_LOSS);
                    }
                    break;
            }
        }
    }

}
