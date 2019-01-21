package muramasa.itech.common.tileentities;

import muramasa.itech.api.capability.ITechCapabilities;
import muramasa.itech.api.capability.implementations.*;
import muramasa.itech.api.enums.CoverType;
import muramasa.itech.api.machines.MachineList;
import muramasa.itech.api.machines.objects.Tier;
import muramasa.itech.api.machines.types.BasicMachine;
import muramasa.itech.api.recipe.Recipe;
import muramasa.itech.api.util.Utils;
import muramasa.itech.common.blocks.BlockMachines;
import muramasa.itech.common.utils.Ref;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;

import javax.annotation.Nullable;

public class TileEntityMachine extends TileEntityTickable {

    /** Data from NBT **/
    private String typeFromNBT = "", tierFromNBT = "";
    private int facing = 2;
    private NBTTagCompound itemData;
    private FluidStack savedFluidStack1, savedFluidStack2;

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
    public void onLoad() {

    }

    public TileEntityMachine() {
//        System.out.println("TYPE: " + typeFromNBT);
    }

    public void init(String type, String tier) {
        typeFromNBT = type;
        tierFromNBT = tier;
        stackHandler = new MachineStackHandler(this, MachineList.getBasic(type));
        if (itemData != null) stackHandler.deserializeNBT(itemData);
        inputTank = new MachineTankHandler(this, 9999, savedFluidStack1, true, false);
        outputTank = new MachineTankHandler(this, 9999, savedFluidStack2, false, true);
        energyStorage = new MachineEnergyHandler(99999999);
        energyStorage.energy = 99999999;
        coverHandler = new MachineCoverHandler(this, CoverType.BLANK, CoverType.ENERGYPORT, CoverType.ITEMPORT, CoverType.FLUIDPORT);
        configHandler = new MachineConfigHandler(this);
    }

    @Override
    public void onFirstTick() { //Using first tick as this fires on both client & server, unlike onLoad
        if (typeFromNBT.isEmpty() || tierFromNBT.isEmpty()) {
            typeFromNBT = MachineList.ALLOYSMELTER.getName();
            tierFromNBT = Tier.LV.getName();
        }
        init(typeFromNBT, tierFromNBT);
        if (facing > 2) {
            rotate(EnumFacing.VALUES[facing]);
        }
    }

    @Override
    public void update() {
        super.update();
        if (isClientSide()) {
//            System.out.println(clientProgress);
        }

        if (isClientSide()) return;
        advanceRecipe();
        if (shouldCheckRecipe) {
            checkRecipe();
            shouldCheckRecipe = false;
        }
    }

    private void checkRecipe() {
        if (activeRecipe == null) { //No active recipes, see of contents match one

            Recipe recipe = MachineList.getBasic(typeFromNBT).findRecipe(stackHandler.getInputStacks(), inputTank.getFluid());
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

    private void advanceRecipe() {
        if (activeRecipe != null){ //Found a valid recipe, process it
            System.out.println(energyStorage.energy);
            if (curProgress == maxProgress) {
                stackHandler.addOutputs(activeRecipe.getOutputs());
                curProgress = 0;
                activeRecipe = null;
            } else if (energyStorage.energy >= activeRecipe.getDuration() * activeRecipe.getPower()) {
                energyStorage.energy = Math.max(energyStorage.energy -= activeRecipe.getDuration() * activeRecipe.getPower(), 0);
                curProgress++;
            }
        }
    }

    public void onContentsChanged(int slot) {
        if (!stackHandler.isInputEmpty() && !shouldCheckRecipe) {
            shouldCheckRecipe = true;
        }
    }

    public BasicMachine getMachineType() {
        return MachineList.getBasic(getType());
    }

    public String getType() {
        return typeFromNBT;
    }

    public String getTier() {
        return tierFromNBT;
    }

    public void rotate(EnumFacing side) { //Rotate the front to look in a given direction
        if (side.getAxis() != EnumFacing.Axis.Y) {
            setState(getState().withProperty(BlockMachines.FACING, side));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey(Ref.KEY_MACHINE_TILE_TYPE) && compound.hasKey(Ref.KEY_MACHINE_TILE_TIER)) {
            typeFromNBT = compound.getString(Ref.KEY_MACHINE_TILE_TYPE);
            tierFromNBT = compound.getString(Ref.KEY_MACHINE_TILE_TIER);
        }
        itemData = (NBTTagCompound) compound.getTag(Ref.KEY_MACHINE_TILE_ITEMS);
        if (compound.hasKey(Ref.KEY_FLUID_NAME_1)) {
            Fluid fluid = FluidRegistry.getFluid(compound.getString(Ref.KEY_FLUID_NAME_1));
            if (fluid != null) {
                savedFluidStack1 = new FluidStack(fluid, compound.getInteger(Ref.KEY_FLUID_AMOUNT_1));
            }
        }
        if (compound.hasKey(Ref.KEY_FLUID_NAME_2)) {
            Fluid fluid = FluidRegistry.getFluid(compound.getString(Ref.KEY_FLUID_NAME_2));
            if (fluid != null) {
                savedFluidStack2 = new FluidStack(fluid, compound.getInteger(Ref.KEY_FLUID_AMOUNT_2));
            }
        }
        if (compound.hasKey(Ref.KEY_MACHINE_TILE_FACING)) {
            facing = compound.getInteger(Ref.KEY_MACHINE_TILE_FACING);
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setString(Ref.KEY_MACHINE_TILE_TYPE, getType());
        compound.setString(Ref.KEY_MACHINE_TILE_TIER, getTier());
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
        compound.setInteger(Ref.KEY_MACHINE_TILE_FACING, getState().getValue(BlockMachines.FACING).getIndex());
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

    /** Getters **/
    public MachineStackHandler getStackHandler() {
        return stackHandler;
    }

    public float getClientProgress() {
        return clientProgress;
    }

    public int getCurProgress() {
        return curProgress;
    }

    public int getMaxProgress() {
        return maxProgress;
    }

    /** Setters **/
    public void setClientProgress(float newClientProgress) {
        clientProgress = newClientProgress;
    }
}
