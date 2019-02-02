package muramasa.itech.common.tileentities.overrides;

import muramasa.itech.api.capability.ITechCapabilities;
import muramasa.itech.api.capability.impl.*;
import muramasa.itech.api.enums.CoverType;
import muramasa.itech.api.enums.MachineState;
import muramasa.itech.api.recipe.Recipe;
import muramasa.itech.api.util.Utils;
import muramasa.itech.common.tileentities.base.TileEntityMachine;
import muramasa.itech.common.utils.Ref;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class TileEntityBasicMachine extends TileEntityMachine {

    /** Data from NBT **/
    private NBTTagCompound itemData;

    /** Capabilities **/
    private MachineStackHandler stackHandler;
    private MachineTankHandler inputTank, outputTank;
    private MachineEnergyHandler energyStorage;
    private MachineConfigHandler configHandler;
    private MachineCoverHandler coverHandler;

    /** Logic **/
    private int curProgress, maxProgress;
    private float clientProgress;
    private Recipe activeRecipe;
    private boolean shouldCheckRecipe;

    @Override
    public void init(String type, String tier) {
        super.init(type, tier);
        stackHandler = new MachineStackHandler(this);
        if (itemData != null) stackHandler.deserializeNBT(itemData);
        inputTank = new MachineTankHandler(this, 9999, new FluidStack(FluidRegistry.WATER, 1), true, false);
        outputTank = new MachineTankHandler(this, 9999, new FluidStack(FluidRegistry.WATER, 1), false, true);
        energyStorage = new MachineEnergyHandler(99999999);
        energyStorage.energy = 99999999;
        coverHandler = new MachineCoverHandler(this, CoverType.BLANK, CoverType.ENERGYPORT, CoverType.ITEMPORT, CoverType.FLUIDPORT);
        configHandler = new MachineConfigHandler(this);
    }

    @Override
    public void onFirstTick() { //Using first tick as this fires on both client & server, unlike onLoad
        super.onFirstTick();
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
            Recipe recipe = getMachineType().findRecipe(stackHandler.getInputs(), inputTank.getFluid());
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
            if (Utils.canStacksFit(activeRecipe.getOutputs(), stackHandler.getOutputs())) {
                //Add outputs and reset to process next recipe cycle
                stackHandler.addOutputs(activeRecipe.getOutputs());
                curProgress = 0;

                //Check if has enough stack count for next recipe cycle
                if (!Utils.doStacksMatchAndSizeValid(activeRecipe.getInputs(), stackHandler.getInputs())) {
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
                    stackHandler.consumeInputs(activeRecipe.getInputs());
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
    public void onContentsChanged(int slot) {
        if (getMachineState().allowRecipeTickOnContentUpdate()) {
            setMachineState(MachineState.FOUND_RECIPE);
        }
        shouldCheckRecipe = true;
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

    /** Setters **/
    @Override
    public void setMachineState(MachineState newState) {
        if (getMachineState().getOverlayId() != newState.getOverlayId() && newState.getOverlayId() == 0 || newState.getOverlayId() == 1) {
            markForRenderUpdate();
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
        if (compound.hasKey(Ref.KEY_FLUID_NAME_1)) {
//            Fluid fluid = FluidRegistry.getFluid(compound.getString(Ref.KEY_FLUID_NAME_1));
//            if (fluid != null) {
//                savedFluidStack1 = new FluidStack(fluid, compound.getInteger(Ref.KEY_FLUID_AMOUNT_1));
//            }
        }
        if (compound.hasKey(Ref.KEY_FLUID_NAME_2)) {
//            Fluid fluid = FluidRegistry.getFluid(compound.getString(Ref.KEY_FLUID_NAME_2));
//            if (fluid != null) {
//                savedFluidStack2 = new FluidStack(fluid, compound.getInteger(Ref.KEY_FLUID_AMOUNT_2));
//            }
        }
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
        if (inputTank != null && inputTank.getFluid() != null) {
            compound.setString(Ref.KEY_FLUID_NAME_1, FluidRegistry.getFluidName(inputTank.getFluid()));
            compound.setInteger(Ref.KEY_FLUID_AMOUNT_1, inputTank.getFluid().amount);
        }
        if (outputTank != null && outputTank.getFluid() != null) {
            compound.setString(Ref.KEY_FLUID_NAME_2, FluidRegistry.getFluidName(outputTank.getFluid()));
            compound.setInteger(Ref.KEY_FLUID_AMOUNT_2, outputTank.getFluid().amount);
        }
        if (getMachineState() != null) {
            compound.setInteger(Ref.KEY_MACHINE_TILE_STATE, getMachineState().getId());
        }
        return compound;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return facing == null || coverHandler.hasCover(facing, CoverType.ITEMPORT);
        } else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return facing == null || coverHandler.hasCover(facing, CoverType.FLUIDPORT);
        } else if (capability == ITechCapabilities.ENERGY) {
            return facing == null || coverHandler.hasCover(facing, CoverType.ENERGYPORT);
        } else if (capability == ITechCapabilities.COVERABLE) {
            return facing == null || !coverHandler.hasCover(facing, CoverType.NONE);
        } else if (capability == ITechCapabilities.CONFIGURABLE) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(stackHandler);
        } else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
//            if (facing == outputSide) {
//                return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(outputTank);
//            } else {
            return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(inputTank);
//            }
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
