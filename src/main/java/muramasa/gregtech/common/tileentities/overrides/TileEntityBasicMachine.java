package muramasa.gregtech.common.tileentities.overrides;

import muramasa.gregtech.api.GregTechAPI;
import muramasa.gregtech.api.capability.ITechCapabilities;
import muramasa.gregtech.api.capability.impl.*;
import muramasa.gregtech.api.enums.ItemType;
import muramasa.gregtech.api.items.MaterialItem;
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

public class TileEntityBasicMachine extends TileEntityMachine {

    /** Data from NBT **/
    protected NBTTagCompound itemInputData, itemOutputData, itemCellData, fluidData;

    /** Capabilities **/
    protected MachineItemHandler itemHandler;
    protected MachineFluidHandler fluidHandler;
    protected MachineEnergyHandler energyStorage;
    protected ConfigHandler configHandler;
    protected CoverHandler coverHandler;

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
            itemHandler = new MachineItemHandler(this, 0);
            if (itemInputData != null) itemHandler.deserializeInput(itemInputData);
            if (itemOutputData != null) itemHandler.deserializeOutput(itemOutputData);
            if (itemCellData != null) itemHandler.deserializeCell(itemCellData);
        }
        if (machine.hasFlag(FLUID)) {
            fluidHandler = new MachineFluidHandler(this, machine.getFluidInputCount(), machine.getFluidOutputCount());
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
            coverHandler = new CoverHandler(this, GregTechAPI.CoverBehaviourPlate, GregTechAPI.CoverBehaviourItem, GregTechAPI.CoverBehaviourFluid, GregTechAPI.CoverBehaviourEnergy);
        }
        if (machine.hasFlag(CONFIGURABLE)) {
            configHandler = new ConfigHandler(this);
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
        if (coverHandler != null) {
            coverHandler.update();
        }
    }

    public void checkRecipe() {
        if (getMachineState().allowRecipeCheck()) { //No active recipes, see of contents match one
            if (itemHandler.getInputs().length == 0) return; //Escape if machine inputs are empty
            Recipe recipe = getMachineType().findRecipe(itemHandler, fluidHandler);
            if (recipe != null) {
                activeRecipe = recipe;
                curProgress = 0;
                maxProgress = recipe.getDuration();
                setMachineState(MachineState.FOUND_RECIPE);
            }
        }
    }

    public MachineState tickRecipe() { //TODO do count check here instead of checkRecipe being called on every contents update
        if (curProgress == maxProgress) { //End of current recipe cycle, deposit items
            if (!canOutput()) {
                return MachineState.OUTPUT_FULL; //Return and loop until outputs can be added
            }

            //Add outputs and reset to process next recipe cycle
            itemHandler.addOutputs(activeRecipe.getOutputStacks());
            curProgress = 0;

            //Check if has enough stack count for next recipe cycle
            if (!Utils.doStacksMatchAndSizeValid(activeRecipe.getInputStacks(), itemHandler.getInputs())) {
                return MachineState.IDLE;
            } else {
                return MachineState.FOUND_RECIPE;
            }
        } else {
            //Calculate per recipe tick so user has risk of losing items
            if (consumeResourceForRecipe()) { //Has enough power to process recipe
                if (curProgress == 0) { //Consume recipe inputs on first recipe tick
                    itemHandler.consumeInputs(activeRecipe.getInputStacks());
                    if (activeRecipe.hasInputFluids()) {

                    }
                }
                curProgress++;
                return MachineState.FOUND_RECIPE;
            } else {
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
            return itemHandler.canStacksFit(activeRecipe.getInputStacks());
        } else {
            return Utils.canStacksFit(activeRecipe.getOutputStacks(), itemHandler.getOutputs()) && fluidHandler.canOutput(activeRecipe.getOutputFluids());
        }*/
        return itemHandler.canStacksFit(activeRecipe.getOutputStacks());
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
            ItemStack stack = itemHandler.getCellInput();
            if (stack.getItem() instanceof MaterialItem) {
                Material material = ((MaterialItem) stack.getItem()).getMaterial();
                if (material != null && material.getLiquid() != null) {
                    fluidHandler.addInputs(new FluidStack(material.getLiquid(), 1000));
                }
            } else if (ItemType.EmptyCell.isEqual(stack)) {
                fluidHandler.getInput(0).setFluid(null);
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

    public MachineItemHandler getItemHandler() {
        return itemHandler;
    }

    public MachineFluidHandler getFluidHandler() {
        return fluidHandler;
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
        if (itemHandler != null) {
            compound.setTag(Ref.KEY_MACHINE_TILE_ITEMS_INPUT, itemHandler.serializeInput());
            compound.setTag(Ref.KEY_MACHINE_TILE_ITEMS_OUTPUT, itemHandler.serializeOutput());
            if (getMachineType().hasFlag(FLUID)) {
                compound.setTag(Ref.KEY_MACHINE_TILE_ITEMS_CELL, itemHandler.serializeCell());
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
            return facing == null || coverHandler.hasCover(facing, GregTechAPI.CoverBehaviourItem);
        } else if (machine.hasFlag(ENERGY) && capability == ITechCapabilities.ENERGY) {
            return facing == null || coverHandler.hasCover(facing, GregTechAPI.CoverBehaviourEnergy);
        } else if (machine.hasFlag(COVERABLE) && capability == ITechCapabilities.COVERABLE) {
            return true;
        } else if (machine.hasFlag(CONFIGURABLE) && capability == ITechCapabilities.CONFIGURABLE) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemHandler.getOutputHandler());
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
