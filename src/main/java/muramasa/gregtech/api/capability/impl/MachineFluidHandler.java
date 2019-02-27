package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.gui.SlotType;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;

public class MachineFluidHandler {

    private static final int DEFAULT_CAPACITY = 99999;

    private TileEntityMachine tile;
    public GTFluidTank[] inputTanks, outputTanks;
    private int capacity;

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
            //TODO
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

//    public FluidStack getFirstInput() {
//        for (int i = 0; i < inputTanks.length; i++) {
//
//        }
//    }

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

    public boolean canInput(FluidStack... fluids) {
        int matchCount = 0;
        for (int i = 0; i < fluids.length; i++) {
            for (int j = 0; j < inputTanks.length; j++) {
                if (inputTanks[j].fill(fluids[i], false) == fluids[i].amount) {
                    matchCount++;
                }
            }
        }
        return matchCount >= fluids.length;
    }

    public boolean canOutput(FluidStack... fluids) {
        int matchCount = 0;
        for (int i = 0; i < fluids.length; i++) {
            for (int j = 0; j < outputTanks.length; j++) {
                if (outputTanks[j].fill(fluids[i], false) == fluids[i].amount) {
                    matchCount++;
                }
            }
        }
        return matchCount >= fluids.length;
    }

    public void addInputs(FluidStack... fluids) {
        for (int i = 0; i < fluids.length; i++) {
            for (int j = 0; j < inputTanks.length; j++) {
                inputTanks[j].fill(fluids[i], true);
            }
        }
    }

    public void addOutputs(FluidStack... fluids) {
        for (int i = 0; i < fluids.length; i++) {
            for (int j = 0; j < outputTanks.length; j++) {
                outputTanks[j].fill(fluids[i], true);
            }
        }
    }

    public void consumeInputs() {

    }

    public void consumeOutputs() {

    }

//    public FluidStack findAndConsumeNextInput() {
//
//    }
//
//    public FluidStack findAndConsumeNextOutput() {
//
//    }
}
