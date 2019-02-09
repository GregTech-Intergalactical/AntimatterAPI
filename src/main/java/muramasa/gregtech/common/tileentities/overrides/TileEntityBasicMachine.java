package muramasa.gregtech.common.tileentities.overrides;

import muramasa.gregtech.api.capability.ITechCapabilities;
import muramasa.gregtech.api.capability.impl.*;
import muramasa.gregtech.api.enums.CoverType;
import muramasa.gregtech.api.enums.ItemList;
import muramasa.gregtech.api.items.MetaItem;
import muramasa.gregtech.api.machines.MachineState;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.recipe.Recipe;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nullable;

import static muramasa.gregtech.api.machines.MachineFlag.*;
import static muramasa.gregtech.api.materials.Prefix.CELL;

public class TileEntityBasicMachine extends TileEntityMachine {

    /** Data from NBT **/
    protected NBTTagCompound itemData, fluidData;

    /** Capabilities **/
    protected MachineStackHandler stackHandler, cellHandler;
    protected MachineTankHandler inputTank, outputTank;
    protected MachineEnergyHandler energyStorage;
    protected MachineConfigHandler configHandler;
    protected MachineCoverHandler coverHandler;

    /** Logic **/
    protected int curProgress, maxProgress;
    protected float clientProgress;
    protected Recipe activeRecipe;
    protected boolean shouldCheckRecipe;

    @Override
    public void init(String type, String tier, int facing) {
        super.init(type, tier, facing);
        Machine machine = getMachineType();
        if (machine.hasFlag(ITEM)) {
            stackHandler = new MachineStackHandler(this, 0);
            if (itemData != null) stackHandler.deserializeNBT(itemData);
        }
        if (machine.hasFlag(FLUID_INPUT)) {
            inputTank = new MachineTankHandler(this, 9999, null, true, false);
            if (fluidData != null) inputTank.deserializeNBT(fluidData);
            cellHandler = new MachineStackHandler(this, 2, 2, 2);
        }
//        inputTank = new MachineTankHandler(this, 9999, new FluidStack(FluidRegistry.WATER, 1), true, false);
//        outputTank = new MachineTankHandler(this, 9999, new FluidStack(FluidRegistry.WATER, 1), false, true);
        if (machine.hasFlag(ENERGY)) {
            energyStorage = new MachineEnergyHandler(Tier.get(tier).getVoltage() * 64);
            energyStorage.energy = 99999999; //Temporary
        }
        if (machine.hasFlag(COVERABLE)) {
            coverHandler = new MachineCoverHandler(this, CoverType.BLANK, CoverType.ENERGY_PORT, CoverType.ITEM_PORT, CoverType.FLUID_PORT);
        }
        if (machine.hasFlag(CONFIGURABLE)) {
            configHandler = new MachineConfigHandler(this);
        }
    }

    @Override
    public void onServerUpdate() {
        if (shouldCheckRecipe) {
            checkRecipe();
            shouldCheckRecipe = false;
        }
        if (getMachineState() == MachineState.FOUND_RECIPE) {
            setMachineState(tickRecipe());
        }
    }

    public void checkRecipe() {
        if (getMachineState().allowRecipeCheck()) { //No active recipes, see of contents match one
            System.out.println("RECIPE CHECK");
            if (stackHandler.getInputs().length == 0) return; //Escape if machine inputs are empty
            Recipe recipe = findRecipe();
            if (recipe != null) {
                activeRecipe = recipe;
                curProgress = 0;
                maxProgress = recipe.getDuration();
                setMachineState(MachineState.FOUND_RECIPE);
            }
        }
    }

    public Recipe findRecipe() {
        System.out.println("FIND RECIPE");
        Machine machine = getMachineType();
        return machine.hasFlag(FLUID_INPUT) ? machine.findRecipe(stackHandler.getInputs(), inputTank.getFluid()) : machine.findRecipe(stackHandler.getInputs());
    }

    public MachineState tickRecipe() { //TODO do count check here instead of checkRecipe being called on every contents update
        if (curProgress == maxProgress) { //End of current recipe cycle, deposit items
            if (Utils.canStacksFit(activeRecipe.getOutputStacks(), stackHandler.getOutputs())) {
                //Add outputs and reset to process next recipe cycle
                stackHandler.addOutputs(activeRecipe.getOutputStacks());
                curProgress = 0;

                //Check if has enough stack count for next recipe cycle
                if (!Utils.doStacksMatchAndSizeValid(activeRecipe.getInputStacks(), stackHandler.getInputs())) {
                    return MachineState.IDLE;
                } else {
                    return MachineState.FOUND_RECIPE;
                }
            } else {
                return MachineState.OUTPUT_FULL; //Return and loop until outputs can be added
            }
        } else {
            //Calculate per recipe tick so user has risk of losing items
            if (consumeResourceForRecipe()) { //Has enough power to process recipe
                if (curProgress == 0) { //Consume recipe inputs on first recipe tick
                    stackHandler.consumeInputs(activeRecipe.getInputStacks());
                    if (activeRecipe.hasInputFluids()) {
                        inputTank.drain(activeRecipe.getInputFluids()[0], true);
//                        if (inputTank.fluidStack.amount >= activeRecipe.getInputFluids()[0].amount) {
//                            inputTank.fluidStack.amount -= activeRecipe.getInputFluids()[0].amount;
//                        }
                        if (inputTank.fluidStack != null) {
                            System.out.println(inputTank.fluidStack.amount);
                        }
                    }
                }
                curProgress++;
                return MachineState.FOUND_RECIPE;
            } else {
                //TODO machine out of power/steam
                //TODO maybe not null recipe, but keep cache for user using hammer to restart?
                return curProgress == 0 ? MachineState.NO_POWER : MachineState.POWER_LOSS;
            }
        }
    }

    public boolean consumeResourceForRecipe() {
        if (energyStorage != null && activeRecipe != null && energyStorage.energy >= activeRecipe.getPower()) {
            energyStorage.energy -= Math.max(energyStorage.energy -= activeRecipe.getPower(), 0);
            return true;
        }
        return false;
    }

    /** Events **/
    @Override
    public void onContentsChanged(int type, int slot) {
        if (type == 0) {
            if (getMachineState().allowRecipeTickOnContentUpdate()) {
                setMachineState(MachineState.FOUND_RECIPE);
            }
            shouldCheckRecipe = true;
            System.out.println("CONTENT UPDATE");
        } else if (type == 2) {
            handleCellSlotUpdate(slot);
        }
    }

    public void handleCellSlotUpdate(int slot) {
        if (slot == 0) { //Input slot
            ItemStack stack = cellHandler.getStackInSlot(0);
            if (MetaItem.hasPrefix(stack, CELL)) {
                Material material = MetaItem.getMaterial(stack);
                if (material != null && material.getLiquid() != null) {
                    inputTank.fluidStack = new FluidStack(material.getLiquid(), 1000);
                }
            } else if (ItemList.Empty_Cell.isItemEqual(stack)) {
                inputTank.fluidStack = null;
            }
        } else if (slot == 1) { //Output slot

        }
    }

    /** Getters **/
    @Override
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

    public IItemHandler getStackHandler() {
        return stackHandler;
    }

    public IItemHandler getCellHandler() {
        return cellHandler;
    }

    public IFluidHandler getFluidHandler() {
        return inputTank;
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

    @Override
    public void setClientProgress(float newProgress) {
        clientProgress = newProgress;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        itemData = (NBTTagCompound) compound.getTag(Ref.KEY_MACHINE_TILE_ITEMS);
        if (compound.hasKey(Ref.KEY_MACHINE_TILE_STATE)) {
            setMachineState(MachineState.VALUES[compound.getInteger(Ref.KEY_MACHINE_TILE_STATE)]);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (stackHandler != null) { //this should never happen...
            compound.setTag(Ref.KEY_MACHINE_TILE_ITEMS, stackHandler.serializeNBT());
        }
        if (getMachineState() != null) {
            compound.setInteger(Ref.KEY_MACHINE_TILE_STATE, getMachineState().getId());
        }
        return compound;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        Machine machine = getMachineType();
        if (machine.hasFlag(ITEM) && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return facing == null || coverHandler.hasCover(facing, CoverType.ITEM_PORT);
        } else if (machine.hasFlag(ENERGY) && capability == ITechCapabilities.ENERGY) {
            return facing == null || coverHandler.hasCover(facing, CoverType.ENERGY_PORT);
        } else if (machine.hasFlag(COVERABLE) && capability == ITechCapabilities.COVERABLE) {
            return facing == null || !coverHandler.hasCover(facing, CoverType.NONE);
        } else if (machine.hasFlag(CONFIGURABLE) && capability == ITechCapabilities.CONFIGURABLE) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(stackHandler);
        } else if (capability == ITechCapabilities.ENERGY) {
            return ITechCapabilities.ENERGY.cast(energyStorage);
        } else if (capability == ITechCapabilities.COVERABLE) {
            return ITechCapabilities.COVERABLE.cast(coverHandler);
        } else if (capability == ITechCapabilities.CONFIGURABLE) {
            return ITechCapabilities.CONFIGURABLE.cast(configHandler);
        }
        return super.getCapability(capability, facing);
    }
}
