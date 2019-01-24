package muramasa.itech.common.tileentities.overrides;

import muramasa.itech.api.capability.ITechCapabilities;
import muramasa.itech.api.capability.implementations.*;
import muramasa.itech.api.enums.CoverType;
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
        stackHandler = new MachineStackHandler(this, getMachineType());
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
        advanceRecipe();
        if (shouldCheckRecipe) {
            checkRecipe();
            shouldCheckRecipe = false;
        }
    }

    public void checkRecipe() {
        if (activeRecipe == null) { //No active recipes, see of contents match one
            Recipe recipe = getMachineType().findRecipe(stackHandler.getInputStacks(), inputTank.getFluid());
            if (recipe != null && Utils.canStacksFit(recipe.getOutputs(), stackHandler.getOutputStacks())) {
                if (recipe.getInputs() != null) {
                    stackHandler.consumeInputs(recipe.getInputs());
                }
                if (recipe.getFluidInputs() != null) {
                    //consume fluids
                }
                activeRecipe = recipe;
                curProgress = 0;
                maxProgress = recipe.getDuration();
            }
        }
    }

    public void advanceRecipe() {
        if (activeRecipe != null) { //Found a valid recipe, process it
            System.out.println(energyStorage.energy);
            if (curProgress == maxProgress) {
                stackHandler.addOutputs(activeRecipe.getOutputs());
                curProgress = 0;
                activeRecipe = null;
            } else if (hasResourceForRecipe()) {
                energyStorage.energy = Math.max(energyStorage.energy -= activeRecipe.getTotalPower(), 0);
                curProgress++;
            }
        }
    }

    public boolean hasResourceForRecipe() {
        return energyStorage != null && activeRecipe != null && energyStorage.energy >= activeRecipe.getTotalPower();
    }

    /** Events **/
    @Override
    public void onContentsChanged(int slot) {
        if (!stackHandler.isInputEmpty() && !shouldCheckRecipe) {
            shouldCheckRecipe = true;
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

    /** Setters **/
    @Override
    public void setClientProgress(float newClientProgress) {
        clientProgress = newClientProgress;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        System.out.println("BASIC: NBT READ");
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
