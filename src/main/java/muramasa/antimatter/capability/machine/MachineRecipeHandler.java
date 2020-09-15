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

    public IIntArray getProgressData() {
        return progress;
    }

    public void setClientProgress() {
        setClientProgress(Float.floatToRawIntBits(this.currentProgress / (float) this.maxProgress));
    }

    public void setClientProgress(int value) {
        this.progress.set(0, value);
    }

    @OnlyIn(Dist.CLIENT)
    public float getClientProgress() {
        return Float.intBitsToFloat(this.progress.get(0));
    }

    @Override
    public void init() {
        checkRecipe();
    }

    public void onServerUpdate() {
        switch (tile.getMachineState()) {
            case IDLE:
                break;
            case ACTIVE:
            case OUTPUT_FULL:
                tile.setMachineState(tickRecipe());
                break;
            default:
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

    protected MachineState tickRecipe() {
        // tickingRecipe = true;
        // onRecipeTick(); TODO: MachineEvent
        if (this.activeRecipe == null) {
            /*
            if (tile.getMachineState() == null) {
                return IDLE;
            }
             */
            System.out.println("Check Recipe when active recipe is null");
            checkRecipe();
            return tile.getMachineState();
        } else if (this.currentProgress == this.maxProgress) {
            this.currentProgress = 0;
            System.out.println("Finishing recipe");
            if (!generator) {
                this.consumeInputs(); // Normal recipes will consume input when recipe is finished
            }
            tile.itemHandler.ifPresent(h -> {
                h.addOutputs(activeRecipe.getOutputItems());
                // this.onMachineEvent(MachineEvent.ITEMS_OUTPUTTED);
            });
            tile.fluidHandler.ifPresent(h -> {
                h.addOutputs(activeRecipe.getOutputFluids());
                // this.onMachineEvent(MachineEvent.FLUIDS_OUTPUTTED);
            });
            if (!canOutput()) {
                setClientProgress(0);
                return OUTPUT_FULL;
            } else if (!canRecipeContinue()) {
                this.resetRecipe();
                return IDLE;
            } else {
                activateRecipe();
                return ACTIVE;
            }
        } else if (!canOutput()) {
            return OUTPUT_FULL;
        } else {
            this.currentProgress++;
            setClientProgress();
            return tile.getMachineState();
        }
    }

    protected boolean checkRecipe() {
        if (tile.getMachineState().allowRecipeCheck()) {
            if ((activeRecipe = findRecipe()) != null) {
                if (activeRecipe.getPower() > tile.getMaxInputVoltage() && !this.generator) {
                    tile.setMachineState(INVALID_TIER);
                    return false;
                }
                activateRecipe();
                return true;
            }
            setClientProgress(0);
        }
        tile.setMachineState(IDLE);
        return false;
    }

    protected void activateRecipe() {
        //if (canOverclock)
        this.currentProgress = 0;
        this.overclock = 0;
        int voltage = tile.getMachineTier().getVoltage();
        long recipeVoltage = activeRecipe.getPower();
        if (voltage > recipeVoltage) {
            int tempOc = voltage / Ref.V[Utils.getVoltageTier(recipeVoltage)];
            while (tempOc > 1) {
                tempOc >>= 2;
                this.overclock++;
            }
        }
        this.maxProgress = Math.max(1, activeRecipe.getDuration() / (1 << overclock));
        tile.setMachineState(ACTIVE);
        if (generator) {
            consumeInputs(); // Power generation recipes will consume input when recipe is activated
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
        boolean outputItemFit = (this.activeRecipe.hasOutputItems() && tile.itemHandler.map(i -> i.canOutputsFit(this.activeRecipe.getOutputItems())).orElse(false));
        boolean outputFluidFit = (this.activeRecipe.hasOutputFluids() && tile.fluidHandler.map(f -> f.canOutputsFit(this.activeRecipe.getOutputFluids())).orElse(false));
        return outputItemFit || outputFluidFit;
    }

    protected boolean canRecipeContinue() {
        return tile.itemHandler.map(i -> i.consumeInputs(this.activeRecipe, true)).orElse(false) || Utils.doFluidsMatchAndSizeValid(activeRecipe.getInputFluids(), tile.fluidHandler.map(MachineFluidHandler::getInputs).orElse(new FluidStack[0]));
    }

    public void resetRecipe() {
        this.activeRecipe = null;
        this.currentProgress = 0;
        this.overclock = 0;
        setClientProgress(0);
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        if (event instanceof ContentEvent) {
            switch ((ContentEvent) event) {
                case FLUID_INPUT_CHANGED:
                case ITEM_INPUT_CHANGED:
                    if (tile.getMachineState().allowRecipeCheck()) {
                        this.checkRecipe();
                    }
                    break;
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
