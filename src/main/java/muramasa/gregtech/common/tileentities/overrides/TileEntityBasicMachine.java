package muramasa.gregtech.common.tileentities.overrides;

import muramasa.gregtech.api.GregTechAPI;
import muramasa.gregtech.api.capability.GTCapabilities;
import muramasa.gregtech.api.capability.impl.*;
import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.enums.ItemType;
import muramasa.gregtech.api.gui.SlotType;
import muramasa.gregtech.api.items.MaterialItem;
import muramasa.gregtech.api.machines.MachineState;
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

    /** Capabilities **/
    protected MachineItemHandler itemHandler;
    protected MachineFluidHandler fluidHandler;
    protected MachineEnergyHandler energyStorage;
    protected ConfigHandler configHandler;
    protected MachineCoverHandler coverHandler;

    /** Logic **/
    protected int curProgress, maxProgress;
    protected float clientProgress;
    protected Recipe activeRecipe;
    protected boolean shouldCheckRecipe;

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        Machine machine = getType();
        if (machine.hasFlag(ITEM)) {
            itemHandler = new MachineItemHandler(this, itemData);
        }
        if (machine.hasFlag(FLUID)) {
            fluidHandler = new MachineFluidHandler(this, fluidData);
//            inputTank = new MachineTankHandler(this, 9999, null, true, false);
//            if (fluidData != null) inputTank.deserializeNBT(fluidData);
//            cellHandler = new MachineStackHandlerOld(this, 2, 2, 2);
        }
//        inputTank = new MachineTankHandler(this, 9999, new FluidStack(FluidRegistry.WATER, 1), true, false);
//        outputTank = new MachineTankHandler(this, 9999, new FluidStack(FluidRegistry.WATER, 1), false, true);
        if (machine.hasFlag(ENERGY)) {
            energyStorage = new MachineEnergyHandler(getTier());
            energyStorage.energy = 99999999; //Temporary
        }
        if (machine.hasFlag(COVERABLE)) {
            coverHandler = new MachineCoverHandler(this, GregTechAPI.CoverBehaviourPlate, GregTechAPI.CoverBehaviourItem, GregTechAPI.CoverBehaviourFluid, GregTechAPI.CoverBehaviourEnergy);
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
            onRecipeTick();
        }
        if (coverHandler != null) {
            coverHandler.tick();
        }
    }

    public void checkRecipe() {
        if (getMachineState().allowRecipeCheck()) { //No active recipes, see of contents match one
            Recipe recipe = findRecipe();
            if (recipe != null) {
                activeRecipe = recipe;
                curProgress = 0;
                maxProgress = recipe.getDuration();
                setMachineState(MachineState.FOUND_RECIPE);
                onRecipeFound();
            }
        }
    }

    public Recipe findRecipe() {
        return getType().findRecipe(itemHandler, fluidHandler);
    }

    public MachineState tickRecipe() { //TODO do count check here instead of checkRecipe being called on every contents tick
        //TODO this null check added if saved state triggers tickRecipe, but there is no recipe to process
        if (activeRecipe == null) return MachineState.IDLE;
        if (curProgress == maxProgress) { //End of current recipe cycle, deposit items
            if (!canOutput()) {
                return MachineState.OUTPUT_FULL; //Return and loop until outputs can be added
            }

            //Add outputs and reset to process next recipe cycle
            addOutputs();
            curProgress = 0;

            //Check if has enough stack count for next recipe cycle
            if (!canRecipeContinue()) {
                return MachineState.IDLE;
            } else {
                return MachineState.FOUND_RECIPE;
            }
        } else {
            //Calculate per recipe tick so user has risk of losing items
            if (consumeResourceForRecipe()) { //Has enough power to process recipe
                if (curProgress == 0) { //Consume recipe inputs on first recipe tick
                    consumeInputs();
                }
                curProgress++;
                return MachineState.FOUND_RECIPE;
            } else {
                return curProgress == 0 ? MachineState.NO_POWER : MachineState.POWER_LOSS;
            }
        }
    }

    public ItemStack[] getStoredInputs() {
        return itemHandler.getInputs();
    }

    public void consumeInputs() {
        itemHandler.consumeInputs(activeRecipe.getInputStacks());
        if (activeRecipe.hasInputFluids()) {
            //TODO?
        }
    }

    public boolean canOutput() {
        /*if (!getType().hasFlag(FLUID)) {
            return itemHandler.canStacksFit(activeRecipe.getInputStacks());
        } else {
            return Utils.canStacksFit(activeRecipe.getOutputStacks(), itemHandler.getOutputs()) && fluidHandler.canOutput(activeRecipe.getOutputFluids());
        }*/
        return itemHandler.canStacksFit(activeRecipe.getOutputStacks());
    }

    public void addOutputs() {
        itemHandler.addOutputs(activeRecipe.getOutputStacks());
    }

    public boolean canRecipeContinue() {
        return Utils.doStacksMatchAndSizeValid(activeRecipe.getInputStacks(), itemHandler.getInputs());
    }

    public boolean consumeResourceForRecipe() {
        if (energyStorage != null && activeRecipe != null && energyStorage.energy >= activeRecipe.getPower()) {
            energyStorage.energy -= Math.max(energyStorage.energy -= activeRecipe.getPower(), 0);
            return true;
        }
        return false;
    }

    /** Events **/
    public void onRecipeFound() {
        //NOOP
    }

    public void onRecipeTick() {
        //NOOP
    }

    @Override
    public void onContentsChanged(int type, int slot) {
        if (type == 0) {
            if (getMachineState().allowRecipeTickOnContentUpdate()) {
                //TODO maybe avoid using FOUND, change?
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
                    System.out.println(Machines.ORE_WASHER.getGui().getCount(SlotType.FL_IN));
                    fluidHandler.addInputs(new FluidStack(material.getLiquid(), 1000));
                }
            } else if (ItemType.EmptyCell.isEqual(stack)) {
                System.out.println("Empty Cell");
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

    @Override
    public MachineItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
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
        if (compound.hasKey(Ref.KEY_MACHINE_TILE_STATE)) {
            //TODO saving state needed? if recipe is saved, serverUpdate should handle it.
            super.setMachineState(MachineState.VALUES[compound.getInteger(Ref.KEY_MACHINE_TILE_STATE)]);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        if (getMachineState() != null) {
            compound.setInteger(Ref.KEY_MACHINE_TILE_STATE, getMachineState().getId());
        }
        return compound;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (getType().hasFlag(ITEM) && capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return facing == null || coverHandler.hasCover(facing, GregTechAPI.CoverBehaviourItem);
        } else if (getType().hasFlag(ENERGY) && capability == GTCapabilities.ENERGY) {
            return facing == null || coverHandler.hasCover(facing, GregTechAPI.CoverBehaviourEnergy);
        } else if (getType().hasFlag(COVERABLE) && capability == GTCapabilities.COVERABLE) {
            return facing == null || (!coverHandler.get(facing).isEmpty());
        } else if (getType().hasFlag(CONFIGURABLE) && capability == GTCapabilities.CONFIGURABLE) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(itemHandler.getOutputHandler());
        } else if (capability == GTCapabilities.ENERGY) {
            return GTCapabilities.ENERGY.cast(energyStorage);
        } else if (capability == GTCapabilities.COVERABLE) {
            return GTCapabilities.COVERABLE.cast(coverHandler);
        } else if (capability == GTCapabilities.CONFIGURABLE) {
            return GTCapabilities.CONFIGURABLE.cast(configHandler);
        }
        return super.getCapability(capability, facing);
    }
}
