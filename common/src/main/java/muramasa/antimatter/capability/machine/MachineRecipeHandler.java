package muramasa.antimatter.capability.machine;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.Dispatch;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.machine.MachineState;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.recipe.IRecipe;
import muramasa.antimatter.recipe.IRecipeValidator;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.ingredient.FluidIngredient;
import muramasa.antimatter.recipe.map.IRecipeMap;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import tesseract.api.gt.GTTransaction;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static muramasa.antimatter.machine.MachineState.*;
import static muramasa.antimatter.machine.event.ContentEvent.ENERGY_SLOT_CHANGED;

//TODO: This needs some look into, a bit of spaghetti code sadly.
public class MachineRecipeHandler<T extends TileEntityMachine<T>> implements IMachineHandler, Dispatch.Sided<MachineRecipeHandler<?>> {

    protected final T tile;
    protected final boolean generator;
    protected IRecipe lastRecipe = null;
    /**
     * Indices:
     * 1 -> Progress of recipe
     */

    protected IRecipe activeRecipe;
    protected boolean consumedResources;
    protected int currentProgress,
            maxProgress;
    protected int overclock;

    //20 seconds per check.
    static final int WAIT_TIME = 20 * 20;
    static final int WAIT_TIME_POWER_LOSS = 20 * 5;
    static final int WAIT_TIME_OUTPUT_FULL = 20;
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
        this.generator = tile.getMachineType().has(MachineFlag.GENERATOR);
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

    public float getClientProgress() {
        return ((float) currentProgress / (float) maxProgress);
    }

    public int getCurrentProgress() {
        return currentProgress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    @Override
    public void init() {
        checkRecipe();
    }

    public void resetProgress(){
        this.currentProgress = 0;
    }

    public void onServerUpdate() {
        //First, a few timer related tasks that ensure the machine can recover from certain situations.
        if (tickingRecipe) return;
        if (tickTimer > 0) {
            if (tile.getMachineState() == IDLE) {
                tickTimer = 0;
            } else {
                tickTimer--;
                if (tickTimer > 0) {
                    return;
                }
            }
        }
        if (tile.getMachineState() == POWER_LOSS && activeRecipe != null) {
            tile.setMachineState(NO_POWER);
            tickTimer = 0;
        }
        if (tile.getMachineState() == OUTPUT_FULL) {
            if (canOutput()) {
                tile.setMachineState(recipeFinish());
                return;
            }
        }
        if (activeRecipe == null) return;
        tickingRecipe = true;
        MachineState state;
        switch (tile.getMachineState()) {
            case ACTIVE:
                state = tickRecipe();
                tile.setMachineState(state);
                break;
            case NO_POWER:
                state = tickRecipe();
                if (state != ACTIVE) {
                    tile.setMachineState(tile.getDefaultMachineState());
                } else {
                    tile.setMachineState(state);
                }
                break;
            default:
                break;
        }
        tickingRecipe = false;
    }

    public IRecipe findRecipe() {
        if (lastRecipe != null) {
            activeRecipe = lastRecipe;
            if (canRecipeContinue()) {
                activeRecipe = null;
                return lastRecipe;
            }
            activeRecipe = null;
        }
        IRecipeMap map = tile.getMachineType().getRecipeMap();
        return map != null ? map.find(tile.itemHandler, tile.fluidHandler, tile.getMachineTier(), this::validateRecipe) : null;
    }

    protected IRecipe cachedRecipe() {
        if (lastRecipe != null) {
            if (!lastRecipe.isValid()) {
                lastRecipe = null;
                return null;
            }
            IRecipe old = activeRecipe;
            activeRecipe = lastRecipe;
            if (canRecipeContinue()) {
                activeRecipe = old;
                return lastRecipe;
            }
            activeRecipe = old;
        }
        return null;
    }

    public int getOverclock() {
        if (activeRecipe == null) return 0;
        int oc = 0;
        if (activeRecipe.getPower() > 0 && this.tile.getPowerLevel().getVoltage() > activeRecipe.getPower()) {
            long voltage = this.activeRecipe.getPower();
            int tier = Utils.getVoltageTier(voltage);
            /*//Dont use utils, because we allow overclocking from ulv. (If we don't just change this).
            for (int i = 0; i < Ref.V.length; i++) {
                if (voltage <= Ref.V[i]) {
                    tier = i;
                    break;
                }
            }*/
            int tempoverclock = (this.tile.getPowerLevel().getVoltage() / Ref.V[tier]);
            while (tempoverclock > 1) {
                tempoverclock >>= 2;
                oc++;
            }
        }
        return oc;
    }

    public long getPower() {
        if (activeRecipe == null) return 0;
        if (overclock == 0 || tile.has(MachineFlag.RF)) return activeRecipe.getPower();
        //half the duration => overclock ^ 2.
        //so if overclock is 2 tiers, we have 1/4 the duration(200 -> 50) but for e.g. 8eu/t this would be
        //8*4*4 = 128eu/t.
        return (activeRecipe.getPower() * (1L << overclock) * (1L << overclock));
    }

    protected void calculateDurations() {
        maxProgress = activeRecipe.getDuration();
        if (!generator && !tile.has(MachineFlag.RF)) {
            overclock = getOverclock();
            this.maxProgress = Math.max(1, maxProgress >> overclock);
        }
    }

    protected void activateRecipe(boolean reset) {
        //if (canOverclock)
        consumedResources = false;
        tickTimer = 0;
        if (reset) {
            currentProgress = 0;
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
        tickTimer = 0;
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
            calculateDurations();
            activateRecipe(true);
            return ACTIVE;
        }
    }

    protected MachineState tickRecipe() {
        if (this.activeRecipe == null) {
            System.out.println("Check Recipe when active recipe is null");
            return tile.getMachineState();
        }
        if (this.currentProgress >= this.maxProgress) {
            if (!canOutput()) {
                tickTimer += WAIT_TIME_OUTPUT_FULL;
                return OUTPUT_FULL;
            }
            MachineState state = recipeFinish();
            if (state != ACTIVE) return state;
        }

        tile.onRecipePreTick();
        if (!consumeResourceForRecipe(false)) {
            if ((currentProgress == 0 && tile.getMachineState() == tile.getDefaultMachineState())) {
                //Cannot start a recipe :(
                return tile.getDefaultMachineState();
            } else {
                //TODO: Hard-mode here?
                recipeFailure();
            }
            if (!generator){
                tickTimer += WAIT_TIME_POWER_LOSS;
                if (tile.getMachineState() == ACTIVE && !tile.isMuffled()) tile.getLevel().playSound(null, tile.getBlockPos(), Ref.INTERRUPT, SoundSource.BLOCKS, 1.0f, 1.0f);
                return POWER_LOSS;
            } else {
                tickTimer += 10;
                return IDLE;
            }
        }
        if (currentProgress == 0 && !consumedResources && shouldConsumeResources()) {
            if (!this.consumeInputs()) {

            }
        }
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
                    GTTransaction transaction = tile.energyHandler.map(eh -> eh.extract(GTTransaction.Mode.INTERNAL)).orElse(null);
                    if (transaction != null && transaction.isValid()) {
                        if (simulate) {
                            return transaction.eu >= power;
                        } else if (transaction.eu >= power) {
                            transaction.addData(power, Utils.sink());
                            transaction.commit();
                            return true;
                        } else {
                            return false;
                        }
                    } else {
                        return false;
                    }
                } else {
                    return consumeGeneratorResources(simulate);
                }
            } else if (tile.rfHandler.isPresent()){
                if (!generator) {
                    long power = getPower();
                    long availableEnergy = tile.rfHandler.map(eh -> eh.getStoredEnergy()).orElse(0L);
                    if (availableEnergy > 0 && availableEnergy >= power){
                        if (!simulate){
                            tile.rfHandler.ifPresent(eh -> eh.setEnergy(availableEnergy - power));
                        }
                        return true;
                    }
                    return false;
                } else {
                    return consumeRFGeneratorResources(simulate);
                }
            } else {
                return false;
            }
        }
        return true;
    }

    protected boolean validateRecipe(IRecipe r) {
        long voltage = this.generator ? tile.getMaxOutputVoltage() : tile.getMachineType().amps() * tile.getMaxInputVoltage();
        boolean ok = voltage >= r.getPower() / r.getAmps();
        List<ItemStack> consumed = this.tile.itemHandler.map(t -> t.consumeInputs(r, true)).orElse(Collections.emptyList());
        for (IRecipeValidator validator : r.getValidators()) {
            if (!validator.validate(r, tile)) {
                return false;
            }
        }
        return ok && (consumed.size() > 0 || !r.hasInputItems() || consumedResources);
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
            activeRecipe = tile.getMachineType().getRecipeMap().find(itemInputs.toArray(new ItemStack[0]), fluidInputs.toArray(new FluidStack[0]), this.tile.getMachineTier(), this::validateRecipe);
            if (activeRecipe == null) return;
            calculateDurations();
            lastRecipe = activeRecipe;
            return;
        }
        if (tile.getMachineState().allowRecipeCheck()) {
            if ((activeRecipe = cachedRecipe()) != null || (activeRecipe = findRecipe()) != null) {
                if (!validateRecipe(activeRecipe)) {
                    tile.setMachineState(INVALID_TIER);
                    activeRecipe = null;
                    return;
                }
                calculateDurations();
                if (!consumeResourceForRecipe(true) || !canRecipeContinue() || (generator && (!activeRecipe.hasInputFluids() || activeRecipe.getInputFluids().size() != 1))) {
                    activeRecipe = null;
                    tile.setMachineState(tile.getDefaultMachineState());
                    //wait half a second after trying again.
                    tickTimer += 10;
                    return;
                }
                activateRecipe(true);
                tile.setMachineState(ACTIVE);
            }
        }
    }

    public boolean accepts(ItemStack stack) {
        IRecipeMap map = this.tile.getMachineType().getRecipeMap();
        return map == null || map.acceptsItem(stack);
    }

    public boolean accepts(FluidStack stack) {
        IRecipeMap map = this.tile.getMachineType().getRecipeMap();
        return map == null || map.acceptsFluid(stack);
    }

    @Nullable
    public IRecipe getActiveRecipe() {
        return activeRecipe;
    }

    public boolean consumeInputs() {
        boolean flag = true;
        if (!tile.hadFirstTick()) return true;
        if (activeRecipe.hasInputItems()) {
            flag &= tile.itemHandler.map(h -> {
                this.itemInputs = h.consumeInputs(activeRecipe, false);
                return !this.itemInputs.isEmpty();
            }).orElse(true);
        }
        if (activeRecipe.hasInputFluids()) {
            flag &= tile.fluidHandler.map(h -> {
                this.fluidInputs = h.consumeAndReturnInputs(activeRecipe.getInputFluids(), false);
                return !this.fluidInputs.isEmpty();
            }).orElse(true);
        }
        if (flag) consumedResources = true;
        return flag;
    }

    public boolean canOutput() {
        //ignore chance for canOutput.
        if (tile.itemHandler.isPresent() && activeRecipe.hasOutputItems() && !tile.itemHandler.map(t -> t.canOutputsFit(activeRecipe.getFlatOutputItems())).orElse(false))
            return false;
        return !tile.fluidHandler.isPresent() || !activeRecipe.hasOutputFluids() || tile.fluidHandler.map(t -> t.canOutputsFit(activeRecipe.getOutputFluids())).orElse(false);
    }

    protected boolean canRecipeContinue() {
        return canOutput() && (!activeRecipe.hasInputItems() || tile.itemHandler.map(i -> i.consumeInputs(this.activeRecipe, true).size() > 0).orElse(false)) && (!activeRecipe.hasInputFluids() || tile.fluidHandler.map(t -> t.consumeAndReturnInputs(activeRecipe.getInputFluids(), true).size() > 0).orElse(false));
    }

    protected boolean consumeRFGeneratorResources(boolean simulate){
        if (!activeRecipe.hasInputFluids()) {
            throw new RuntimeException("Missing fuel in active generator recipe!");
        }
        long toConsume = calculateGeneratorConsumption(activeRecipe);
        long actualConsume = toConsume;
        if (actualConsume == 0 || tile.fluidHandler.map(h -> {
            FluidIngredient in = activeRecipe.getInputFluids().get(0);
            long amount = in.drainedAmount((int) actualConsume, h, true, true); //h.getInputTanks().drain(new FluidStack(activeRecipe.getInputFluids().get(0).getStacks()[0], (int) actualConsume), IFluidHandler.FluidAction.SIMULATE).getAmount();
            if (amount == actualConsume) {
                if (!simulate)
                    in.drain(amount, h, true, false);
                return true;
            }
            return false;
        }).orElse(false)) {
            //insert power!
            if (!simulate) {
                tile.rfHandler.ifPresent(r -> {
                    r.setEnergy(r.getStoredEnergy() + activeRecipe.getPower());
                });
            }
            return true;
        }
        return false;
    }

    /*
      Helper to consume resources for a generator.
     */
    protected boolean consumeGeneratorResources(boolean simulate) {
        if (!activeRecipe.hasInputFluids()) {
            throw new RuntimeException("Missing fuel in active generator recipe!");
        }
        long toConsume = calculateGeneratorConsumption(tile.getMachineTier().getVoltage(), activeRecipe);
        long inserted;
        if (toConsume == 0)
            inserted = (long) ((double) activeRecipe.getPower() / activeRecipe.getInputFluids().get(0).getAmount() * tile.getMachineType().getMachineEfficiency());
        else
            inserted = (long) ((double) toConsume * activeRecipe.getPower() / activeRecipe.getInputFluids().get(0).getAmount() * tile.getMachineType().getMachineEfficiency());

        final long t = inserted;
        GTTransaction transaction = new GTTransaction(t, Utils.sink());
        if (!tile.energyHandler.map(eh -> eh.insert(transaction)).orElse(false)) {
            return false;
        }
        //Check leftover eu.
        long actual = t - transaction.eu;
        //If there isn't enough room for an entire run reduce output.
        //E.g. if recipe is 24 eu per MB then you have to run 2x to match 48 eu/t
        //but eventually it will be too much so reduce output.
        if (actual < inserted && toConsume == 0) return false;
        while (actual < inserted && actual > 0) {
            toConsume--;
            inserted = (long) ((double) toConsume * activeRecipe.getPower() / activeRecipe.getInputFluids().get(0).getAmount() * tile.getMachineType().getMachineEfficiency());
            actual = Math.min(inserted, transaction.eu);
        }
        //If nothing to insert.
        if (actual == 0) return false;
        //because lambda don't like primitives
        final long actualConsume = toConsume;
        //make sure there are fluids avaialble
        if (actualConsume == 0 || tile.fluidHandler.map(h -> {
            FluidIngredient in = activeRecipe.getInputFluids().get(0);
            long amount = in.drainedAmount((int) actualConsume, h, true, true); //h.getInputTanks().drain(new FluidStack(activeRecipe.getInputFluids().get(0).getStacks()[0], (int) actualConsume), IFluidHandler.FluidAction.SIMULATE).getAmount();
            if (amount == actualConsume) {
                if (!simulate)
                    in.drain(amount, h, true, false);
                return true;
            }
            return false;
        }).orElse(false)) {
            //insert power!
            if (!simulate) {
                transaction.commit();
            }
            return true;
        }
        return false;
    }

    protected long calculateGeneratorConsumption(int volt, IRecipe r) {
        long power = r.getPower();
        long amount = r.getInputFluids().get(0).getAmount();
        if (currentProgress > 0 && amount == 1) {
            return 0;
        }
        double offset = (volt / ((double) power / (double) amount));
        if (r.getDuration() > 1)
            offset /= r.getDuration();
        return Math.max(1, (long) (Math.ceil(offset)));
    }

    protected long calculateGeneratorConsumption(IRecipe r) {
        long amount = r.getInputFluids().get(0).getAmount();
        if (currentProgress > 0) {
            return 0;
        }
        return amount;
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
            if (activeRecipe != null) tile.onMachineStop();
            if (hardcore) {
                resetRecipe();
            }
            tile.resetMachine();
        }
    }

    public void onRemove() {
        resetRecipe();
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        if (tickingRecipe) return;
        if (event instanceof ContentEvent) {
            if (tile.getMachineState() == ACTIVE)
                return;
            if (tile.getMachineState() == POWER_LOSS) {
                return;
            }
            if (activeRecipe != null && !consumeResourceForRecipe(true)) {
                return;
            }
            if (event == ENERGY_SLOT_CHANGED) {
                if (tile.itemHandler.map(t -> t.inventories.get(SlotType.ENERGY).getStackInSlot((int) data[0]).isEmpty()).orElse(true)) {
                    return;
                }
            }
            if ((event == ContentEvent.ITEM_OUTPUT_CHANGED || event == ContentEvent.FLUID_OUTPUT_CHANGED) && tile.getMachineState() == OUTPUT_FULL && tickTimer == 0 && canOutput()) {
                tickingRecipe = true;
                tile.setMachineState(recipeFinish());
                tickingRecipe = false;
                return;
            }
            if (tile.getMachineState().allowRecipeCheck()) {
                if (activeRecipe != null) {
                    tile.setMachineState(NO_POWER);
                } else if (tile.getMachineState() != POWER_LOSS && tickTimer == 0) {
                    checkRecipe();
                } else if (event == ContentEvent.ITEM_INPUT_CHANGED || event == ContentEvent.FLUID_INPUT_CHANGED) {
                    checkRecipe();
                }
            }
        } else if (event instanceof MachineEvent) {
            switch ((MachineEvent) event) {
                case ENERGY_INPUTTED, HEAT_INPUTTED:
                    if (event == MachineEvent.HEAT_INPUTTED && !tile.has(MachineFlag.HEAT)) break;
                    if (tile.getMachineState() == tile.getDefaultMachineState() && activeRecipe != null) {
                        tile.setMachineState(NO_POWER);
                    }
                    if (tile.getMachineState().allowRecipeCheck() && tile.getMachineState() != POWER_LOSS && tickTimer == 0) {
                        checkRecipe();
                    }
                    break;
                case ENERGY_DRAINED, HEAT_DRAINED:
                    if (event == MachineEvent.HEAT_DRAINED && !tile.has(MachineFlag.HEAT)) break;
                    if (generator && tile.getMachineState() == tile.getDefaultMachineState()) {
                        if (activeRecipe != null) tile.setMachineState(NO_POWER);
                        else checkRecipe();
                    }
                    break;
            }
        }
    }

    /**
     * NBT STUFF
     **/

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        ListTag item = new ListTag();
        if (itemInputs.size() > 0) {
            itemInputs.forEach(t -> {
                item.add(t.save(new CompoundTag()));
            });
        }
        ListTag fluid = new ListTag();
        if (fluidInputs.size() > 0) {
            fluidInputs.forEach(t -> fluid.add(t.writeToNBT(new CompoundTag())));
        }
        nbt.put("I", item);
        nbt.putInt("T", tickTimer);
        nbt.put("F", fluid);
        nbt.putInt("P", currentProgress);
        nbt.putBoolean("C", consumedResources);
        if (activeRecipe != null){
            nbt.putString("AR", activeRecipe.getId().toString());
        }
        if (lastRecipe != null){
            nbt.putString("LR", lastRecipe.getId().toString());
        }
        return nbt;
    }

    public void deserializeNBT(CompoundTag nbt) {
        itemInputs = new ObjectArrayList<>();
        fluidInputs = new ObjectArrayList<>();
        nbt.getList("I", 10).forEach(t -> itemInputs.add(ItemStack.of((CompoundTag) t)));
        nbt.getList("F", 10).forEach(t -> fluidInputs.add(FluidStack.loadFluidStackFromNBT((CompoundTag) t)));
        this.currentProgress = nbt.getInt("P");
        this.tickTimer = nbt.getInt("T");
        this.consumedResources = nbt.getBoolean("C");
        this.activeRecipe = nbt.contains("AR") ? this.tile.getMachineType().getRecipeMap().findByID(new ResourceLocation(nbt.getString("AR"))) : null;
        this.lastRecipe = nbt.contains("LR") ? this.tile.getMachineType().getRecipeMap().findByID(new ResourceLocation(nbt.getString("LR"))) : null;
        if (this.activeRecipe != null) calculateDurations();
    }

    @Override
    public Optional<MachineRecipeHandler<?>> forSide(Direction side) {
        return Optional.of(this);
    }

    @Override
    public Optional<MachineRecipeHandler<?>> forNullSide() {
        return Optional.of(this);
    }
}
