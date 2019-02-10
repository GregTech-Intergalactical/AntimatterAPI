package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.common.tileentities.base.TileEntityMachine;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;

public class MachineTankHandler {

    private static final int TANK_CAPACITY = 99999;

    private TileEntityMachine tile;
    private GTFluidTank[] inputTanks, outputTanks;

    public MachineTankHandler(TileEntityMachine tile, int inputCount, int outputCount) {
        this.tile = tile;
        inputTanks = new GTFluidTank[inputCount];
        outputTanks = new GTFluidTank[outputCount];
        for (int i = 0; i < inputCount; i++) {
            inputTanks[i] = new GTFluidTank(TANK_CAPACITY, true, false);
        }
        for (int i = 0; i < outputCount; i++) {
            outputTanks[i] = new GTFluidTank(TANK_CAPACITY, false, true);
        }
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
