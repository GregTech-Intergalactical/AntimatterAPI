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
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

import static muramasa.gregtech.api.machines.MachineFlag.*;
import static muramasa.gregtech.api.materials.Prefix.CELL;

public class TileEntityBasicMachine extends TileEntityMachine {

    /** Data from NBT **/
    protected NBTTagCompound itemInputData, itemOutputData, itemCellData, fluidData;

    /** Capabilities **/
    protected MachineStackHandler stackHandler;
    protected MachineTankHandler tankHandler;
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
            if (itemInputData != null) stackHandler.deserializeInput(itemInputData);
            if (itemOutputData != null) stackHandler.deserializeOutput(itemOutputData);
            if (itemCellData != null) stackHandler.deserializeCell(itemCellData);
        }
        if (machine.hasFlag(FLUID)) {
            tankHandler = new MachineTankHandler(this, machine.getFluidInputCount(), machine.getFluidOutputCount());
//            inputTank = new MachineTankHandler(this, 9999, null, true, false);
//            if (fluidData != null) inputTank.deserializeNBT(fluidData);
//            cellHandler = new MachineStackHandlerOld(this, 2, 2, 2);
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
        Machine machine = getMachineType();
        if (machine.hasFlag(FLUID)) {
            return machine.findRecipe(stackHandler.getInputs(), tankHandler.getInputs());
        } else {
            return machine.findRecipe(stackHandler.getInputs());
        }
    }

    public MachineState tickRecipe() { //TODO do count check here instead of checkRecipe being called on every contents update
        if (curProgress == maxProgress) { //End of current recipe cycle, deposit items
            if (canOutput()) {
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

    public boolean canOutput() {
        /*if (!getMachineType().hasFlag(FLUID)) {
            return stackHandler.canStacksFit(activeRecipe.getInputStacks());
        } else {
            return Utils.canStacksFit(activeRecipe.getOutputStacks(), stackHandler.getOutputs()) && tankHandler.canOutput(activeRecipe.getOutputFluids());
        }*/
        return stackHandler.canStacksFit(activeRecipe.getOutputStacks());
    }

    /** Events **/
    @Override
    public void onContentsChanged(int type, int slot) {
        if (type == 0) {
            if (getMachineState().allowRecipeTickOnContentUpdate()) {
                setMachineState(MachineState.FOUND_RECIPE);
            }
            shouldCheckRecipe = true;
        } else if (type == 2) {
            handleCellSlotUpdate(slot);
        }
    }

    public void handleCellSlotUpdate(int slot) {
        if (slot == 0) { //Input slot
            ItemStack stack = stackHandler.getCellInput();
            if (MetaItem.hasPrefix(stack, CELL)) {
                Material material = MetaItem.getMaterial(stack);
                if (material != null && material.getLiquid() != null) {
                    tankHandler.addInputs(new FluidStack(material.getLiquid(), 1000));
                }
            } else if (ItemList.Empty_Cell.isItemEqual(stack)) {
                tankHandler.getInput(0).setFluid(null);
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

//    public IItemHandler getStackHandler() {
//        return stackHandler;
//    }

//    public IItemHandler getCellHandler() {
//        return cellHandler;
//    }

    public MachineStackHandler getStackHandler() {
        return stackHandler;
    }

    public MachineTankHandler getFluidHandler() {
        return tankHandler;
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
        itemInputData = (NBTTagCompound) compound.getTag(Ref.KEY_MACHINE_TILE_ITEMS_INPUT);
        itemOutputData = (NBTTagCompound) compound.getTag(Ref.KEY_MACHINE_TILE_ITEMS_OUTPUT);
        if (getMachineType().hasFlag(FLUID)) {
            itemCellData = (NBTTagCompound) compound.getTag(Ref.KEY_MACHINE_TILE_ITEMS_CELL);
        }
        if (compound.hasKey(Ref.KEY_MACHINE_TILE_STATE)) {
            setMachineState(MachineState.VALUES[compound.getInteger(Ref.KEY_MACHINE_TILE_STATE)]);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (stackHandler != null) { //this should never happen...
            compound.setTag(Ref.KEY_MACHINE_TILE_ITEMS_INPUT, stackHandler.serializeInput());
            compound.setTag(Ref.KEY_MACHINE_TILE_ITEMS_OUTPUT, stackHandler.serializeOutput());
            if (getMachineType().hasFlag(FLUID)) {
                compound.setTag(Ref.KEY_MACHINE_TILE_ITEMS_CELL, stackHandler.serializeCell());
            }
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
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(stackHandler.getOutputHandler());
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
