package muramasa.antimatter.tile;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.machine.MachineFluidHandler;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.util.Utils;
import net.minecraft.util.IIntArray;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

import static muramasa.antimatter.machine.MachineState.*;

// TODO - read and write nbt when Recipe is serializable/deserializable
// TODO - Translate this to a Holder class that TileEntityMachine has when getMachineType().has(MachineFlag.RECIPE)
public class TileEntityRecipeMachine extends TileEntityMachine {

    protected Recipe activeRecipe;
    protected int currentProgress, maxProgress;
    protected int overclock;

    protected final boolean generator;

    protected final IIntArray progressData = new IIntArray() {

        @Override
        public int get(int index) {
            if (index == 0) {
                return Float.floatToRawIntBits(currentProgress / (float) maxProgress);
            }
            return -1;
        }

        @Override
        public void set(int index, int value) {
            if (index == 0) {
                currentProgress = Float.intBitsToFloat(value);
            }
        }

        @Override
        public int size() {
            return 1;
        }

    };

    public TileEntityRecipeMachine(Machine<?> type) {
        super(type);
        this.generator = type.has(MachineFlag.GENERATOR);
    }

    @Override
    public void resetMachine() {
        super.resetMachine();
        resetRecipe();
    }

    @Override
    public void onServerUpdate() {
        super.onServerUpdate();
        switch (machineState) {
            case IDLE:
                break;
            case ACTIVE:
            case OUTPUT_FULL:
                this.setMachineState(tickRecipe());
                break;
            case POWER_LOSS:
            case NO_POWER:
                MachineState state = tickRecipe();
                if (state != ACTIVE) {
                    this.setMachineState(IDLE);
                } else {
                    this.setMachineState(state);
                }
                break;
        }
    }

    public Recipe findRecipe() {
        return type.getRecipeMap().find(tier, this.itemHandler, this.fluidHandler);
    }

    public MachineState tickRecipe() {
        // tickingRecipe = true;
        // onRecipeTick(); TODO: MachineEvent
        if (this.activeRecipe == null) {
            if (machineState == null) {
                return IDLE;
            }
            checkRecipe();
            return machineState;
        } else if (!canOutput()) {
            return OUTPUT_FULL;
        } else if (this.currentProgress == this.maxProgress) {
            this.currentProgress = 0;
            if (!generator) {
                this.consumeInputs();
            }
            this.itemHandler.ifPresent(h -> {
                h.addOutputs(activeRecipe.getOutputItems());
                this.onMachineEvent(MachineEvent.ITEMS_OUTPUTTED);
            });
            this.fluidHandler.ifPresent(h -> {
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
        if (machineState.allowRecipeCheck()) {
            if ((activeRecipe = findRecipe()) != null) {
                if (activeRecipe.getPower() > getMaxInputVoltage() && !this.generator) {
                    this.setMachineState(INVALID_TIER);
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
        int voltage = getMachineTier().getVoltage();
        if (voltage > activeRecipe.getPower()) {
            int tempOc = (voltage / Ref.V[Utils.getVoltageTier(activeRecipe.getPower())]);
            while (tempOc > 1) {
                tempOc >>= 2;
                this.overclock++;
            }
        }
        this.maxProgress = Math.max(1, activeRecipe.getDuration() / (1 << overclock));
        setMachineState(ACTIVE);
        if (generator) {
            consumeInputs();
        }
        // onRecipeFound(); TODO: MachineEvent
    }

    protected void consumeInputs() {
        this.energyHandler.ifPresent(e -> {
            this.itemHandler.ifPresent(i -> i.consumeInputs(this.activeRecipe, false));
            if (generator) {
                e.insert((long) (type.getMachineEfficiency() * tier.getVoltage()), false);
            } else {
                long extract = activeRecipe.getPower() * (1 << overclock);
                if (e.extract(extract, true) >= extract) {
                    e.extract(extract, false);
                }
            }
        });
    }

    protected boolean canOutput() {
        return this.activeRecipe.hasOutputItems() && this.itemHandler.map(i -> i.canOutputsFit(this.activeRecipe.getOutputItems())).orElse(false) || this.activeRecipe.hasOutputFluids() && this.fluidHandler.map(f -> f.canOutputsFit(this.activeRecipe.getOutputFluids())).orElse(false);
    }

    protected boolean canRecipeContinue() {
        return this.itemHandler.map(i -> i.consumeInputs(this.activeRecipe, true)).orElse(false) || Utils.doFluidsMatchAndSizeValid(activeRecipe.getInputFluids(), this.fluidHandler.map(MachineFluidHandler::getInputs).orElse(new FluidStack[0]));
    }

    public IIntArray getProgressData() {
        return progressData;
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
                    if (!this.checkRecipe() && machineState == ACTIVE || machineState == POWER_LOSS) {
                        this.setMachineState(IDLE);
                    }
                    break;
                case FLUID_OUTPUT_CHANGED:
                case ITEM_OUTPUT_CHANGED:
                    if (this.activeRecipe != null && this.currentProgress == 0 && this.machineState == ACTIVE || this.machineState == POWER_LOSS) {
                        this.setMachineState(IDLE);
                    }
                    break;
                    /*
                case ENERGY_SLOT_CHANGED:
                    if (machineState == IDLE) {
                        this.setMachineState(NO_POWER);
                    }
                    if (machineState == POWER_LOSS) {
                        this.setMachineState(ACTIVE);
                    }
                    break;
                     */
            }
        } else if (event instanceof MachineEvent) {
            switch ((MachineEvent) event) {
                case ENERGY_INPUTTED:
                    if (machineState == POWER_LOSS) {
                        this.setMachineState(ACTIVE);
                    }
                    break;
                case ENERGY_DRAINED:
                    if (machineState == ACTIVE) {
                        this.setMachineState(POWER_LOSS);
                    }
                    break;
            }
        }
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        info.add("Recipe: " + this.currentProgress + " / " + this.maxProgress);
        return info;
    }

}
