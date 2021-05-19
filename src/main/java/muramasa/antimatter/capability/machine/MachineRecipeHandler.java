package muramasa.antimatter.capability.machine;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.map.RecipeMap;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.IIntArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static muramasa.antimatter.machine.MachineState.*;
//TODO: This needs some look into, a bit of spaghetti code sadly.
public class MachineRecipeHandler<T extends TileEntityMachine> implements IMachineHandler {

    protected final T tile;
    protected final boolean generator;
    protected Recipe lastRecipe = null;
    /**
     * Indices:
     * 1 -> Progress of recipe
     */
    protected final IIntArray GUI_SYNC_DATA = new IIntArray() {

        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return MachineRecipeHandler.this.currentProgress;
                case 1:
                    return MachineRecipeHandler.this.maxProgress;
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0:
                    MachineRecipeHandler.this.currentProgress = value;
                    break;
                case 1:
                    MachineRecipeHandler.this.maxProgress = value;
                    break;
            }
        }

        @Override
        public int size() {
            return 2;
        }
    };

    protected Recipe activeRecipe;
    protected boolean consumedResources;
    protected int currentProgress, maxProgress;
    protected int overclock;

    //20 seconds per check.
    static final int WAIT_TIME = 20*20;
    static final int WAIT_TIME_POWER_LOSS = 20*5;
    protected int tickTimer = 0;

    //Consuming resources can call into the recipe handler, causing a loop.
    //For instance, consuming fluid in the fluid handlers calls back into the MachineRecipeHandler, deadlocking.
    //So just 'lock' during recipe ticking.
    private boolean tickingRecipe = false;

    //Items used to find recipe
    protected List<ItemStack> itemInputs = Collections.emptyList();
    protected List<FluidStack> fluidInputs = Collections.emptyList();

    public MachineRecipeHandler(T tile) {
        this.tile = tile;
        GUI_SYNC_DATA.set(0,0);
        this.generator = tile.getMachineType().has(MachineFlag.GENERATOR);
    }

    public IIntArray getProgressData() {
        return GUI_SYNC_DATA;
    }

    public void getInfo(List<String> builder) {
        if (activeRecipe != null) {
            if (tile.getMachineState() != ACTIVE) {
                builder.add("Active recipe but not running");
            }
            builder.add("Progress: " + currentProgress + "/" + maxProgress);
        } else {
            builder.add("No recipe active");
        }
    }

    public boolean hasRecipe() {
        return activeRecipe != null;
    }

    @OnlyIn(Dist.CLIENT)
    public float getClientProgress() {
        return ((float) currentProgress / (float) maxProgress);
    }

    @OnlyIn(Dist.CLIENT)
    public void setClientProgress(int progress) {
        this.currentProgress = progress;
    }

    @Override
    public void init() {
        checkRecipe();
    }

    public void onServerUpdate() {
        //First, a few timer related tasks that ensure the machine can recover from certain situations.
        if (activeRecipe == null && tickTimer >= WAIT_TIME) {
            tickTimer = 0;
            //Convert from power_loss to idle.
            checkRecipe();
        }
        else if (tile.getMachineState() == POWER_LOSS && tickTimer >= WAIT_TIME_POWER_LOSS) {
            tile.setMachineState(NO_POWER);
            tickTimer = 0;
        }
        else if (activeRecipe == null || tile.getMachineState() == POWER_LOSS) {
            tickTimer++;
            return;
        }
        if (tickingRecipe || activeRecipe == null) return;
        tickingRecipe = true;
        MachineState state;
        switch (tile.getMachineState()) {
            case ACTIVE:
                state = tickRecipe();
                tile.setMachineState(state);
                if (state != ACTIVE) {
                    tile.onRecipeStop();
                }
                break;
            case IDLE:
                break;
            case INVALID_STRUCTURE:
                break;
            case POWER_LOSS:
                break;
            case OUTPUT_FULL:
                break;
            default:
                state = tickRecipe();
                if (state != ACTIVE) {
                    tile.setMachineState(tile.getDefaultMachineState());
                    tile.onRecipeStop();
                } else {
                    tile.setMachineState(state);
                    tile.onRecipeActivated(activeRecipe);
                }
                break;
        }
        tickingRecipe = false;
    }

    public Recipe findRecipe() {
        if (lastRecipe != null) {
            activeRecipe = lastRecipe;
            if (canRecipeContinue()) {
                activeRecipe = null;
                return lastRecipe;
            }
        }
        RecipeMap<?> map = tile.getMachineType().getRecipeMap();
        return map != null ? map.find(tile.itemHandler, tile.fluidHandler, this::validateRecipe) : null;
    }

    protected Recipe cachedRecipe() {
        if (lastRecipe != null) {
            if (!lastRecipe.isValid()) {
                lastRecipe = null;
                return null;
            }
            Recipe old = activeRecipe;
            activeRecipe = lastRecipe;
            if (canRecipeContinue()) {
                activeRecipe = old;
                return lastRecipe;
            }
            activeRecipe = old;
        }
        return null;
    }

    protected int getOverclock() {
        int oc = 0;
        if (activeRecipe.getPower() > 0 && this.tile.getPowerLevel().getVoltage() > activeRecipe.getPower()) {
            long voltage = this.activeRecipe.getPower();
            int tier = 0;
            //Dont use utils, because we allow overclocking from ulv. (If we don't just change this).
            for (int i = 0; i < Ref.V.length; i++) {
                if (voltage <= Ref.V[i]) {
                    tier = i;
                    break;
                }
            }
            int tempoverclock = (this.tile.getPowerLevel().getVoltage() / Ref.V[tier]);
            while (tempoverclock > 1) {
                tempoverclock >>= 2;
                oc++;
            }
        }
        return oc;
    }

    protected long getPower() {
        return (activeRecipe.getPower() * (1L << overclock));
    }

    //called when a new recipe is found, to process overclocking
    protected void activateRecipe(boolean reset) {
        //if (canOverclock)
        consumedResources = false;
        maxProgress = activeRecipe.getDuration();
        overclock = getOverclock();
        maxProgress = Math.max(1, maxProgress >>= overclock);
        tickTimer = 0;
        if (reset) {
            currentProgress = 0;
            tile.onRecipeActivated(activeRecipe);
        }
        lastRecipe = activeRecipe;
    }

    protected void addOutputs() {
        if (activeRecipe.hasOutputItems()) {
            tile.itemHandler.ifPresent(h -> {
                //Roll the chances here. If they don't fit add flat (no chances).
                ItemStack[] out = activeRecipe.getOutputItems(true);
                if (h.canOutputsFit(out)) {
                    h.addOutputs(out);
                } else {
                    h.addOutputs(activeRecipe.getFlatOutputItems());
                }
                tile.onMachineEvent(MachineEvent.ITEMS_OUTPUTTED);
            });
        }
        if (activeRecipe.hasOutputFluids()) {
            tile.fluidHandler.ifPresent(h -> {
                h.addOutputs(activeRecipe.getOutputFluids());
                tile.onMachineEvent(MachineEvent.FLUIDS_OUTPUTTED);
            });
        }
    }

    protected MachineState recipeFinish() {
        addOutputs();
        this.itemInputs = new ObjectArrayList<>();
        this.fluidInputs = new ObjectArrayList<>();
        if (this.generator) {
            currentProgress = 0;
            return ACTIVE;
        }
        if (!canRecipeContinue()) {
            this.resetRecipe();
            checkRecipe();
            return activeRecipe != null ? ACTIVE : tile.getDefaultMachineState();
        } else {
            activateRecipe(true);
            return ACTIVE;
        }
    }

    protected MachineState tickRecipe() {
        if (this.activeRecipe == null) {
            System.out.println("Check Recipe when active recipe is null");
            return tile.getMachineState();
        }
        if (this.currentProgress == this.maxProgress) {
            if (!canOutput()) {
                return OUTPUT_FULL;
            }
            MachineState state = recipeFinish();
            if (state != ACTIVE) return state;
        }

        tile.onRecipePreTick();
        if (!consumeResourceForRecipe(false)) {
            if ((currentProgress == 0 && tile.getMachineState() == tile.getDefaultMachineState()) || generator) {
                //Cannot start a recipe :(
                resetRecipe();
                return tile.getDefaultMachineState();
            } else {
                //TODO: Hard-mode here?
                recipeFailure();
            }
            return POWER_LOSS;
        }
        if (currentProgress == 0 && !consumedResources && shouldConsumeResources())
            this.consumeInputs();
        this.currentProgress++;
        tile.onRecipePostTick();
        return ACTIVE;
    }

    protected boolean shouldConsumeResources() {
        return !generator;
    }

    protected void recipeFailure() {
        currentProgress = 0;
    }

    public boolean consumeResourceForRecipe(boolean simulate) {
        if (activeRecipe.getPower() > 0) {
            if (tile.energyHandler.isPresent()) {
                if (!generator) {
                    long power = getPower();
                    if (tile.energyHandler.map(t -> t.extractInternal(power, true, true)).orElse(0L) >= power) {
                        if (!simulate)
                            tile.energyHandler.map(t -> t.extractInternal(power, false, true));
                        return true;
                    } else {
                        return false;
                    }
                } else {
                    return consumeGeneratorResources(simulate);
                }
            }
        }
        return true;
    }

    protected boolean validateRecipe(Recipe r) {
        int voltage = this.generator ? Tier.getMax().getVoltage() : tile.getMachineType().amps()*tile.getMaxInputVoltage();
        boolean ok = voltage >= r.getPower()/ r.getAmps();
        List<ItemStack> consumed = this.tile.itemHandler.map(t -> t.consumeInputs(r, true)).orElse(Collections.emptyList());
        return ok && (consumed.size() > 0 || !r.hasInputItems());
    }

    protected boolean hasLoadedInput() {
        return itemInputs.size() > 0 || fluidInputs.size() > 0;
    }

    public void checkRecipe() {
        if (activeRecipe != null) {
            return;
        }
        //First lookup.
        if (!this.tile.hadFirstTick() && hasLoadedInput()) {
            if (!tile.getMachineState().allowRecipeCheck()) return;
            activeRecipe = tile.getMachineType().getRecipeMap().find(itemInputs.toArray(new ItemStack[0]), fluidInputs.toArray(new FluidStack[0]), r -> true);
            if (activeRecipe == null) return;
            activateRecipe(false);
            if (canOutput()) tile.setMachineState(ACTIVE);
            return;
        }
        if (tile.getMachineState().allowRecipeCheck()) {
            if ((activeRecipe = cachedRecipe()) != null || (activeRecipe = findRecipe()) != null) {
                if (!validateRecipe(activeRecipe)) {
                    tile.setMachineState(INVALID_TIER);
                    activeRecipe = null;
                    return;
                }
                if (!consumeResourceForRecipe(true) || !canRecipeContinue() || (generator && (!activeRecipe.hasInputFluids() || activeRecipe.getInputFluids().length != 1)) || !tile.onRecipeFound(activeRecipe)) {
                    activeRecipe = null;
                    tile.setMachineState(tile.getDefaultMachineState());
                    return;
                }
                activateRecipe(true);
                tile.setMachineState(ACTIVE);
                return;
            }
        }
    }

    public boolean accepts(ItemStack stack) {
        return this.tile.getMachineType().getRecipeMap().acceptsItem(stack);
    }

    public boolean accepts(FluidStack stack) {
        return this.tile.getMachineType().getRecipeMap().acceptsFluid(stack);
    }

    @Nullable
    public Recipe getActiveRecipe() {
        return activeRecipe;
    }

    public void consumeInputs() {
        if (!tile.hadFirstTick()) return;
        if (activeRecipe.hasInputItems()) {
            tile.itemHandler.ifPresent(h -> {
                this.itemInputs = h.consumeInputs(activeRecipe,false);
            });
        }
        if (activeRecipe.hasInputFluids()) {
            tile.fluidHandler.ifPresent(h -> {
                h.consumeAndReturnInputs(Arrays.asList(activeRecipe.getInputFluids()), false);
                this.fluidInputs = Arrays.asList(activeRecipe.getInputFluids());
            });
        }
        consumedResources = true;
    }

    public boolean canOutput() {
        //ignore chance for canOutput.
        if (tile.itemHandler.isPresent() && activeRecipe.hasOutputItems() && !tile.itemHandler.map(t -> t.canOutputsFit(activeRecipe.getFlatOutputItems())).orElse(false))
            return false;
        return !tile.fluidHandler.isPresent() || !activeRecipe.hasOutputFluids() || tile.fluidHandler.map(t -> t.canOutputsFit(activeRecipe.getOutputFluids())).orElse(false);
    }

    protected boolean canRecipeContinue() {
        return canOutput() && (!activeRecipe.hasInputItems() || tile.itemHandler.map(i -> i.consumeInputs(this.activeRecipe, true).size() > 0).orElse(false)) && (!activeRecipe.hasInputFluids() || Utils.doFluidsMatchAndSizeValid(activeRecipe.getInputFluids(), tile.fluidHandler.map(MachineFluidHandler::getInputs).orElse(new FluidStack[0])));
    }
    /*
      Helper to consume resources for a generator.
     */
    protected boolean consumeGeneratorResources(boolean simulate) {
        if (!activeRecipe.hasInputFluids()) {
            throw new RuntimeException("Missing fuel in active generator recipe!");
        }
        long toConsume = calculateGeneratorConsumption(tile.getMachineTier().getVoltage(), activeRecipe);
        long inserted = (long)((double)toConsume*activeRecipe.getPower()/activeRecipe.getInputFluids()[0].getAmount()*tile.getMachineType().getMachineEfficiency());

        final long t = inserted;
        long actual = tile.energyHandler.map(h -> h.insertInternal(t,true, true)).orElse(0L);
        //If there isn't enough room for an entire run reduce output.
        //E.g. if recipe is 24 eu per MB then you have to run 2x to match 48 eu/t
        //but eventually it will be too much so reduce output.
        while (actual < inserted && actual > 0) {
            toConsume--;
            inserted = (long)((double)toConsume*activeRecipe.getPower()/activeRecipe.getInputFluids()[0].getAmount()*tile.getMachineType().getMachineEfficiency());
            final long temp = inserted;
            actual = tile.energyHandler.map(h -> h.insertInternal(temp,true, true)).orElse(0L);
        }
        //If nothing to insert.
        if (actual == 0) return false;
        //because lambda don't like primitives
        final long actualConsume = toConsume;
        final long actualInsert = inserted;
        //make sure there are fluids avaialble
        if (tile.fluidHandler.map(h -> {
            int amount = h.getInputTanks().drain(new FluidStack(activeRecipe.getInputFluids()[0],(int)actualConsume), IFluidHandler.FluidAction.SIMULATE).getAmount();
            if (amount == actualConsume) {
                if (!simulate)
                    h.getInputTanks().drain(new FluidStack(activeRecipe.getInputFluids()[0],(int)actualConsume), IFluidHandler.FluidAction.EXECUTE);
                return true;
            }
            return false;
        }).orElse(false)) {
            //insert power!
            if (!simulate)
                tile.energyHandler.ifPresent(handler -> handler.insertInternal(actualInsert, false, true));
            return true;
        }
        return false;
    }

    protected long calculateGeneratorConsumption(int volt, Recipe r) {
        long power = r.getPower();
        int amount = r.getInputFluids()[0].getAmount();
        double offset =  (volt /((double)power/(double) amount));
        if (r.getDuration() > 1)
            offset /= r.getDuration();
        return Math.max(1, (long)(Math.ceil(offset)));
    }

    public void resetRecipe() {
        this.activeRecipe = null;
        this.consumedResources = false;
        this.currentProgress = 0;
        this.overclock = 0;
        this.maxProgress = 0;
        this.itemInputs = Collections.emptyList();
        this.fluidInputs = Collections.emptyList();
    }

    public void onMultiBlockStateChange(boolean isValid, boolean hardcore) {
        if (isValid) {
            if (tile.hadFirstTick()) {
                if (hasRecipe())
                    tile.setMachineState(MachineState.NO_POWER);
                else {
                    checkRecipe();
                }
            }
        } else {
            if (hardcore) {
                tile.onRecipeStop();
                resetRecipe();
            } else {
                tile.onRecipeStop();
                tile.resetMachine();
            }
        }
    }

    public void onRemove() {
        tile.onRecipeStop();
        resetRecipe();
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        if (tickingRecipe) return;
        if (event instanceof ContentEvent) {
                if (tile.getMachineState() == ACTIVE)
                    return;
                if ((event == ContentEvent.ITEM_OUTPUT_CHANGED || event == ContentEvent.FLUID_OUTPUT_CHANGED) && tile.getMachineState() == OUTPUT_FULL && canOutput()) {
                    tickingRecipe = true;
                    tile.setMachineState(recipeFinish());
                    tickingRecipe = false;
                    return;
                }
                if (tile.getMachineState().allowRecipeCheck()) {
                    if (activeRecipe != null) {
                        tile.setMachineState(NO_POWER);
                    } else if (tile.getMachineState() != POWER_LOSS) {
                        this.tickTimer = WAIT_TIME;
                    }
                }
        } else if (event instanceof MachineEvent) {
            switch ((MachineEvent) event) {
                case ENERGY_INPUTTED:
                    if (tile.getMachineState() == tile.getDefaultMachineState() && activeRecipe != null) {
                        tile.setMachineState(NO_POWER);
                    }
                    break;
                case ENERGY_DRAINED:
                    if (generator && tile.getMachineState() == tile.getDefaultMachineState()) {
                        if (activeRecipe != null) tile.setMachineState(NO_POWER);
                        else checkRecipe();
                    }
                    break;
            }
        }
    }

    /** NBT STUFF **/

    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        ListNBT item = new ListNBT();
        if (itemInputs.size() > 0) {
            itemInputs.forEach(t -> item.add(t.serializeNBT()));
        }
        ListNBT fluid = new ListNBT();
        if (fluidInputs.size() > 0) {
            fluidInputs.forEach(t -> fluid.add(t.writeToNBT(new CompoundNBT())));
        }
        nbt.put("I", item);
        nbt.put("F", fluid);
        nbt.putInt("P", currentProgress);
        return nbt;
    }

    public void deserializeNBT(CompoundNBT nbt) {
        itemInputs = new ObjectArrayList<>();
        fluidInputs = new ObjectArrayList<>();
        nbt.getList("I",10).forEach(t -> itemInputs.add(ItemStack.read((CompoundNBT) t)));
        nbt.getList("F",10).forEach(t -> fluidInputs.add(FluidStack.loadFluidStackFromNBT((CompoundNBT) t)));
        this.currentProgress = nbt.getInt("P");
    }
}
