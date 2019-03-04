package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.gui.SlotType;
import muramasa.gregtech.api.util.Utils;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;

public class MachineFluidHandler {

    private static final int DEFAULT_CAPACITY = 99999;

    protected TileEntityMachine tile;
    protected GTFluidTank[] inputTanks, outputTanks;

    public MachineFluidHandler(TileEntityMachine tile, int capacity) {
        this.tile = tile;
        inputTanks = new GTFluidTank[tile.getType().getGui().getCount(SlotType.FL_IN)];
        outputTanks = new GTFluidTank[tile.getType().getGui().getCount(SlotType.FL_OUT)];
        for (int i = 0; i < inputTanks.length; i++) {
            inputTanks[i] = new GTFluidTank(capacity, true, false);
        }
        for (int i = 0; i < outputTanks.length; i++) {
            outputTanks[i] = new GTFluidTank(capacity, false, true);
        }
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

    public int getInputCount() {
        return inputTanks.length;
    }

    public int getOutputCount() {
        return outputTanks.length;
    }

    public GTFluidTank getInput(int i) {
        return inputTanks[i];
    }

    public GTFluidTank getOutput(int i) {
        return outputTanks[i];
    }

    public FluidStack[] getInputs() {
        ArrayList<FluidStack> stacks = new ArrayList<>();
        for (int i = 0; i < inputTanks.length; i++) {
            if (inputTanks[i].getFluid() != null) {
                stacks.add(inputTanks[i].getFluid());
            }
        }
        return stacks.toArray(new FluidStack[0]);
    }

    public FluidStack[] getOutputs() {
        ArrayList<FluidStack> stacks = new ArrayList<>();
        for (int i = 0; i < outputTanks.length; i++) {
            if (outputTanks[i].getFluid() != null) {
                stacks.add(outputTanks[i].getFluid());
            }
        }
        return stacks.toArray(new FluidStack[0]);
    }

    public void consumeInputs(FluidStack... inputs) {
        for (int i = 0; i < inputs.length; i++) {
            for (int j = 0; j < inputTanks.length; j++) {
                if (Utils.equals(inputs[i], inputTanks[j].getFluid())) {
                    System.out.println("FOUND FLUID");
                    inputTanks[j].drain(inputs[i].amount, true);
                }
            }
        }
    }

    public void addInputs(FluidStack... fluids) {
        //TODO fix
        for (int i = 0; i < fluids.length; i++) {
            for (int j = 0; j < inputTanks.length; j++) {
                inputTanks[j].fill(fluids[i], true);
            }
        }
    }

    public void addOutputs(FluidStack... fluids) {
        //TODO fix
        for (int i = 0; i < fluids.length; i++) {
            for (int j = 0; j < outputTanks.length; j++) {
                outputTanks[j].fill(fluids[i], true);
            }
        }
    }

    /** Helpers **/
    public boolean canFluidsFit(FluidStack[] a) {
        return getSpaceForFluids(a) >= a.length;
    }

    public int getSpaceForFluids(FluidStack[] a) {
        int matchCount = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < outputTanks.length; j++) {
                if (outputTanks[j].fill(a[i], false) == a[i].amount) {
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

    /** NBT **/
    public NBTTagCompound serialize() {
        NBTTagCompound nbt = new NBTTagCompound();
        return nbt; //TODO
    }

    public void deserialize(NBTTagCompound nbt) {
        //TODO
    }
}
