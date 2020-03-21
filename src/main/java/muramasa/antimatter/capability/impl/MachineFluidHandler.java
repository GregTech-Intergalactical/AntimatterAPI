package muramasa.antimatter.capability.impl;

import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MachineFluidHandler {

    protected static int DEFAULT_CAPACITY = 99999;

    protected TileEntityMachine tile;
    protected FluidTankWrapper inputWrapper, outputWrapper;

    public MachineFluidHandler(TileEntityMachine tile, int capacity) {
        this.tile = tile;
        int inputCount = tile.getMachineType().getGui().getSlots(SlotType.FL_IN, tile.getTier()).size();
        int outputCount = tile.getMachineType().getGui().getSlots(SlotType.FL_OUT, tile.getTier()).size();
        if (inputCount > 0) inputWrapper = new FluidTankWrapper(tile, inputCount, capacity, true);
        if (outputCount > 0) outputWrapper = new FluidTankWrapper(tile, outputCount, capacity, false);
    }

    public MachineFluidHandler(TileEntityMachine tile) {
        this(tile, DEFAULT_CAPACITY);
    }

    public MachineFluidHandler(TileEntityMachine tile, int capacity, CompoundNBT fluidData) {
        this(tile, capacity);
        if (fluidData != null) deserialize(fluidData);
    }

    public MachineFluidHandler(TileEntityMachine tile, CompoundNBT fluidData) {
        this(tile, DEFAULT_CAPACITY, fluidData);
    }

    public FluidTankWrapper getInputWrapper() {
        return inputWrapper;
    }

    public FluidTankWrapper getOutputWrapper() {
        return outputWrapper;
    }

    /** Helpers **/
    public FluidStack[] getInputs() {
        return getInputList().toArray(new FluidStack[0]);
    }

    public FluidStack[] getOutputs() {
        return getOutputList().toArray(new FluidStack[0]);
    }

    /** Returns raw FluidStacks from all inputs, including nulls **/
    @Nullable
    public FluidStack[] getInputsRaw() {
        if (inputWrapper == null || inputWrapper.tanks.length == 0) return null;
        FluidStack[] inputs = new FluidStack[inputWrapper.tanks.length];
        for (int i = 0; i < inputs.length; i++) {
            inputs[i] = inputWrapper.tanks[i].getFluid();
        }
        return inputs;
    }

    /** Returns raw FluidStacks from all outputs, including nulls **/
    @Nullable
    public FluidStack[] getOutputsRaw() {
        if (outputWrapper == null || outputWrapper.tanks.length == 0) return null;
        FluidStack[] outputs = new FluidStack[outputWrapper.tanks.length];
        for (int i = 0; i < outputs.length; i++) {
            outputs[i] = outputWrapper.tanks[i].getFluid();
        }
        return outputs;
    }

    /** Directly sets inputs. Only to be used for special cases like fluid syncing **/
    public void setInputs(FluidStack[] inputs) {
        if (inputWrapper == null || inputs.length != inputWrapper.tanks.length) return;
        for (int i = 0; i < inputs.length; i++) {
            inputWrapper.tanks[i].setFluid(inputs[i]);
        }
    }

    /** Directly sets outputs. Only to be used for special cases like fluid syncing **/
    public void setOutputs(FluidStack[] outputs) {
        if (outputWrapper == null || outputs.length != outputWrapper.tanks.length) return;
        for (int i = 0; i < outputs.length; i++) {
            outputWrapper.tanks[i].setFluid(outputs[i]);
        }
    }

    /** Returns list of input fluids, filtering nulls **/
    public List<FluidStack> getInputList() {
        if (inputWrapper == null) return Collections.emptyList();
        ArrayList<FluidStack> list = new ArrayList<>();
        for (int i = 0; i < inputWrapper.tanks.length; i++) {
            if (inputWrapper.tanks[i].getFluid() != null) list.add(inputWrapper.tanks[i].getFluid());
        }
        return list;
    }

    /** Returns list of output fluids, filtering nulls **/
    public List<FluidStack> getOutputList() {
        if (outputWrapper == null) return Collections.emptyList();
        ArrayList<FluidStack> list = new ArrayList<>();
        for (int i = 0; i < outputWrapper.tanks.length; i++) {
            if (outputWrapper.tanks[i].getFluid() != null) list.add(outputWrapper.tanks[i].getFluid());
        }
        return list;
    }

    //TODO called by Basic machines, should they use consumeAndReturn?
    public void consumeInputs(FluidStack... inputs) {
        if (inputWrapper == null) return;
        for (int i = 0; i < inputs.length; i++) {
            inputWrapper.drain(inputs[i], IFluidHandler.FluidAction.EXECUTE);
        }
    }

    public void addInputs(FluidStack... inputs) {
        if (inputWrapper == null) return;
        for (int i = 0; i < inputs.length; i++) {
            inputWrapper.fill(inputs[i], IFluidHandler.FluidAction.EXECUTE);
        }
    }

    public void addOutputs(FluidStack... outputs) {
        if (outputWrapper == null || outputs == null || outputs.length == 0) return;
        for (int i = 0; i < outputs.length; i++) {
            outputWrapper.fill(outputs[i], IFluidHandler.FluidAction.EXECUTE);
        }
    }

    public boolean canOutputsFit(FluidStack[] outputs) {
        return getSpaceForOutputs(outputs) >= outputs.length;
    }

    public int getSpaceForOutputs(FluidStack[] outputs) {
        int matchCount = 0;
        if (outputWrapper == null) return matchCount;
        for (int i = 0; i < outputs.length; i++) {
            if (outputWrapper.fill(outputs[i], IFluidHandler.FluidAction.SIMULATE) == outputs[i].getAmount()) matchCount++;
        }
        return matchCount;
    }

    public FluidStack[] consumeAndReturnInputs(FluidStack... inputs) {
        if (inputWrapper == null) return new FluidStack[0];
        ArrayList<FluidStack> notConsumed = new ArrayList<>();
        FluidStack result;
        for (int i = 0; i < inputs.length; i++) {
            result = inputWrapper.drain(inputs[i], IFluidHandler.FluidAction.EXECUTE);
            if (result != null) {
                if (result.getAmount() != inputs[i].getAmount()) { //Fluid was partially consumed
                    notConsumed.add(Utils.ca(inputs[i].getAmount() - result.getAmount(), inputs[i]));
                }
            } else {
                notConsumed.add(inputs[i]); //Fluid not present in input tanks
            }
        }

        return notConsumed.toArray(new FluidStack[0]);
    }

    public FluidStack[] exportAndReturnOutputs(FluidStack... outputs) {
        if (outputWrapper == null) return new FluidStack[0];
        ArrayList<FluidStack> notExported = new ArrayList<>();
        int result;
        for (int i = 0; i < outputs.length; i++) {
            result = outputWrapper.fill(outputs[i], IFluidHandler.FluidAction.EXECUTE);
            if (result == 0) notExported.add(outputs[i]); //Valid space was not found
            else outputs[i] = Utils.ca(result, outputs[i]); //Fluid was partially exported
        }
        return notExported.toArray(new FluidStack[0]);
    }

    /** NBT **/
    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        if (inputWrapper != null) {
            ListNBT list = new ListNBT();
            for (int i = 0; i < inputWrapper.tanks.length; i++) {
                if (inputWrapper.tanks[i].getFluid() == null) continue;
                list.add(inputWrapper.tanks[i].writeToNBT(new CompoundNBT()));
            }
            tag.put("Input-Fluids", list);
        }
        if (outputWrapper != null) {
            ListNBT list = new ListNBT();
            for (int i = 0; i < outputWrapper.tanks.length; i++) {
                if (outputWrapper.tanks[i].getFluid() == null) continue;
                list.add(outputWrapper.tanks[i].writeToNBT(new CompoundNBT()));
            }
            tag.put("Output-Fluids", list);
        }
        return tag;
    }

    public void deserialize(CompoundNBT tag) {
        if (inputWrapper != null) {
            ListNBT list = tag.getList("Input-Fluids", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                if (i < inputWrapper.tanks.length) {
                    inputWrapper.tanks[i].setFluid(FluidStack.loadFluidStackFromNBT(list.getCompound(i)));
                }
            }
        }
        if (outputWrapper != null) {
            ListNBT list = tag.getList("Output-Fluids", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                if (i < outputWrapper.tanks.length) {
                    outputWrapper.tanks[i].setFluid(FluidStack.loadFluidStackFromNBT(list.getCompound(i)));
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (inputWrapper != null) {
            builder.append("Inputs:\n");
            for (int i = 0; i < inputWrapper.tanks.length; i++) {
                if (inputWrapper.tanks[i].getFluid() != null) {
                    builder.append(inputWrapper.tanks[i].getFluid().getFluid().getRegistryName()).append(" - ").append(inputWrapper.tanks[i].getFluid().getAmount());
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
                    builder.append(outputWrapper.tanks[i].getFluid().getFluid().getRegistryName()).append(" - ").append(outputWrapper.tanks[i].getFluid().getAmount());
                    if (i != outputWrapper.tanks.length - 1) {
                        builder.append("\n");
                    }
                }
            }
        }
        return builder.toString();
    }
}
