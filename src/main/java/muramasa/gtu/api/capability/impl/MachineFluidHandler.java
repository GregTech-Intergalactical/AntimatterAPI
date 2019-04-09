package muramasa.gtu.api.capability.impl;

import muramasa.gtu.api.gui.SlotType;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.util.Utils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;

public class MachineFluidHandler {

    protected static int DEFAULT_CAPACITY = 99999;

    protected TileEntityMachine tile;
    protected GTFluidTankWrapper inputWrapper, outputWrapper;

    public MachineFluidHandler(TileEntityMachine tile, int capacity) {
        this.tile = tile;
        inputWrapper = new GTFluidTankWrapper(tile, tile.getType().getGui().getSlots(SlotType.FL_IN, tile.getTier()).size(), capacity, true);
        outputWrapper = new GTFluidTankWrapper(tile, tile.getType().getGui().getSlots(SlotType.FL_OUT, tile.getTier()).size(), capacity, false);
    }

    public MachineFluidHandler(TileEntityMachine tile) {
        this(tile, DEFAULT_CAPACITY);
    }

    public MachineFluidHandler(TileEntityMachine tile, int capacity, NBTTagCompound fluidData) {
        this(tile, capacity);
        if (fluidData != null) {
            deserialize(fluidData);
        }
    }

    public MachineFluidHandler(TileEntityMachine tile, NBTTagCompound fluidData) {
        this(tile, DEFAULT_CAPACITY, fluidData);
    }

    public GTFluidTankWrapper getInputWrapper() {
        return inputWrapper;
    }

    public GTFluidTankWrapper getOutputWrapper() {
        return outputWrapper;
    }

    /** Helpers **/
    public FluidStack[] getInputs() {
        ArrayList<FluidStack> stacks = new ArrayList<>();
        for (int i = 0; i < inputWrapper.tanks.length; i++) {
            if (inputWrapper.tanks[i].getFluid() != null) {
                stacks.add(inputWrapper.tanks[i].getFluid());
            }
        }
        return stacks.toArray(new FluidStack[0]);
    }

    public FluidStack[] getOutputs() {
        ArrayList<FluidStack> stacks = new ArrayList<>();
        for (int i = 0; i < outputWrapper.tanks.length; i++) {
            if (outputWrapper.tanks[i].getFluid() != null) {
                stacks.add(outputWrapper.tanks[i].getFluid());
            }
        }
        return stacks.toArray(new FluidStack[0]);
    }

    public void consumeInputs(FluidStack... inputs) {
        for (int i = 0; i < inputs.length; i++) {
            for (int j = 0; j < inputWrapper.tanks.length; j++) {
                if (Utils.equals(inputs[i], inputWrapper.tanks[j].getFluid())) {
                    inputWrapper.tanks[j].drain(inputs[i].amount, true);
                }
            }
        }
    }

    public void addInputs(FluidStack... fluids) {
        //TODO fix
        for (int i = 0; i < fluids.length; i++) {
            for (int j = 0; j < inputWrapper.tanks.length; j++) {
                inputWrapper.tanks[j].fill(fluids[i], true);
            }
        }
    }

    public void addOutputs(FluidStack... fluids) {
        //TODO fix
        for (int i = 0; i < fluids.length; i++) {
            for (int j = 0; j < outputWrapper.tanks.length; j++) {
                outputWrapper.tanks[j].fill(fluids[i], true);
            }
        }
    }

    public boolean canFluidsFit(FluidStack[] a) {
        return getSpaceForFluids(a) >= a.length;
    }

    public int getSpaceForFluids(FluidStack[] a) {
        int matchCount = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < outputWrapper.tanks.length; j++) {
                if (outputWrapper.tanks[j].fill(a[i], false) == a[i].amount) {
                    matchCount++;
                }
            }
        }
        return matchCount;
//        int matchCount = 0;
//        for (int i = 0; i < a.length; i++) {
//            for (int j = 0; j < outputTanks.length; j++) {
//                if (outputTanks[j].getFluid() == null || (Utils.equals(a[i], outputTanks[j].getFluid()) && outputTanks[j].getFluid().amount + a[i].amount <= outputTanks[j].getCapacity())) {
//                    matchCount++;
//                    break;
//                }
//            }
//        }
//        return matchCount;
    }

    public FluidStack[] consumeAndReturnInputs(FluidStack... inputs) {
        return inputs; //TODO
    }

    public ArrayList<Integer> getInputIds() {
        ArrayList<Integer> ids = new ArrayList<>();
        FluidStack[] fluids = getInputs();
        for (int i = 0; i < fluids.length; i++) {
            ids.add(Utils.getIdByFluid(fluids[i].getFluid()));
        }
        return ids;
    }

    /** NBT **/
    public NBTTagCompound serialize() {
        NBTTagCompound nbt = new NBTTagCompound();
        return nbt; //TODO
    }

    public void deserialize(NBTTagCompound nbt) {
        //TODO
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (inputWrapper != null) {
            builder.append("Inputs:\n");
            for (int i = 0; i < inputWrapper.tanks.length; i++) {
                if (inputWrapper.tanks[i].getFluid() != null) {
                    builder.append(inputWrapper.tanks[i].getFluid().getLocalizedName()).append(" - ").append(inputWrapper.tanks[i].getFluid().amount);
                    if (i != inputWrapper.tanks.length - 1) {
                        builder.append("\n");
                    }
                }
            }
        }
        if (outputWrapper != null) {
            builder.append("Outputs:\n");
            for (int i = 0; i < outputWrapper.tanks.length; i++) {
                if (outputWrapper.tanks[i].getFluid() != null) {
                    builder.append(outputWrapper.tanks[i].getFluid().getLocalizedName()).append(" - ").append(inputWrapper.tanks[i].getFluid().amount);
                    if (i != outputWrapper.tanks.length - 1) {
                        builder.append("\n");
                    }
                }
            }
        }
        return builder.toString();
    }
}
