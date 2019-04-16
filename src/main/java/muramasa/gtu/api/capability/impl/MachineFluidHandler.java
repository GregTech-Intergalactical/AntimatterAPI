package muramasa.gtu.api.capability.impl;

import muramasa.gtu.api.gui.SlotType;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.util.Utils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

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
        int inputCount = tile.getType().getGui().getSlots(SlotType.FL_IN, tile.getTier()).size();
        int outputCount = tile.getType().getGui().getSlots(SlotType.FL_OUT, tile.getTier()).size();
        if (inputCount > 0) inputWrapper = new FluidTankWrapper(tile, inputCount, capacity, true);
        if (outputCount > 0) outputWrapper = new FluidTankWrapper(tile, outputCount, capacity, false);
    }

    public MachineFluidHandler(TileEntityMachine tile) {
        this(tile, DEFAULT_CAPACITY);
    }

    public MachineFluidHandler(TileEntityMachine tile, int capacity, NBTTagCompound fluidData) {
        this(tile, capacity);
        if (fluidData != null) deserialize(fluidData);
    }

    public MachineFluidHandler(TileEntityMachine tile, NBTTagCompound fluidData) {
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

    public void consumeInputs(FluidStack... inputs) {
        if (inputWrapper == null) return;
        for (int i = 0; i < inputs.length; i++) {
            for (int j = 0; j < inputWrapper.tanks.length; j++) {
                if (Utils.equals(inputs[i], inputWrapper.tanks[j].getFluid())) {
                    inputWrapper.tanks[j].drain(inputs[i].amount, true);
                }
            }
        }
    }

    public void addInputs(FluidStack... fluids) {
        if (inputWrapper == null) return;
        for (int i = 0; i < fluids.length; i++) {
            for (int j = 0; j < inputWrapper.tanks.length; j++) {
                inputWrapper.tanks[j].fill(fluids[i], true);
            }
        }
    }

    public void addOutputs(FluidStack... fluids) {
        if (outputWrapper == null) return;
        for (int i = 0; i < fluids.length; i++) {
            for (int j = 0; j < outputWrapper.tanks.length; j++) {
                System.out.println("Adding output: " + fluids[i].getLocalizedName());
                outputWrapper.tanks[j].fill(fluids[i], true);
            }
        }
    }

    public boolean canOutputsFit(FluidStack[] a) {
        return getSpaceForOutputs(a) >= a.length;
    }

    public int getSpaceForOutputs(FluidStack[] a) {
        int matchCount = 0;
        if (outputWrapper == null) return matchCount;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < outputWrapper.tanks.length; j++) {
                if (outputWrapper.tanks[j].getFluid() == null || outputWrapper.tanks[j].fill(a[i], false) == a[i].amount) {
                    matchCount++;
                }
            }
        }
        return matchCount;
    }

    public FluidStack[] consumeAndReturnInputs(FluidStack... inputs) {
        if (inputWrapper == null) return new FluidStack[0];
        ArrayList<FluidStack> notConsumed = new ArrayList<>();
        for (int i = 0; i < inputs.length; i++) {
            for (int j = 0; j < inputWrapper.tanks.length; j++) {
                if (Utils.equals(inputs[i], inputWrapper.tanks[j].getFluid())) {
                    if (inputWrapper.tanks[j].getFluid().amount >= inputs[i].amount) {
                        inputWrapper.tanks[j].drain(inputs[i], true);
                    } else {
                        int leftOver = inputs[i].amount - inputWrapper.tanks[j].getFluid().amount;
                        notConsumed.add(Utils.ca(leftOver, inputs[i]));
                        inputWrapper.tanks[j].drain(Utils.ca(inputs[i].amount - leftOver, inputs[i]), true);
                    }
                } else {
                    notConsumed.add(inputs[i]);
                }
            }
        }
        return notConsumed.toArray(new FluidStack[0]);
    }

    public FluidStack[] exportAndReturnOutputs(FluidStack... outputs) {
        if (outputWrapper == null) return new FluidStack[0];
        ArrayList<FluidStack> notExported = new ArrayList<>();
        int result;
        for (int i = 0; i < outputs.length; i++) {
            for (int j = 0; j < outputWrapper.tanks.length; j++) {
                result = outputWrapper.tanks[j].fill(outputs[i].copy(), true);
                if (result == outputs[i].amount) break; //Filling was successful
                else outputs[i] = Utils.ca(result, outputs[i]);
                if (j == outputWrapper.tanks.length - 1) notExported.add(outputs[i]);
            }
        }
        return notExported.toArray(new FluidStack[0]);
    }

    /** NBT **/
    public NBTTagCompound serialize() {
        NBTTagCompound tag = new NBTTagCompound();
        if (inputWrapper != null) {
            NBTTagList list = new NBTTagList();
            for (int i = 0; i < inputWrapper.tanks.length; i++) {
                if (inputWrapper.tanks[i].getFluid() == null) continue;
                list.appendTag(inputWrapper.tanks[i].writeToNBT(new NBTTagCompound()));
            }
            tag.setTag("Input-Fluids", list);
        }
        if (outputWrapper != null) {
            NBTTagList list = new NBTTagList();
            for (int i = 0; i < outputWrapper.tanks.length; i++) {
                if (outputWrapper.tanks[i].getFluid() == null) continue;
                list.appendTag(outputWrapper.tanks[i].writeToNBT(new NBTTagCompound()));
            }
            tag.setTag("Output-Fluids", list);
        }
        return tag;
    }

    public void deserialize(NBTTagCompound tag) {
        if (inputWrapper != null) {
            NBTTagList list = tag.getTagList("Input-Fluids", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++) {
                if (i < inputWrapper.tanks.length) {
                    inputWrapper.tanks[i].setFluid(FluidStack.loadFluidStackFromNBT(list.getCompoundTagAt(i)));
                }
            }
        }
        if (outputWrapper != null) {
            NBTTagList list = tag.getTagList("Output-Fluids", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++) {
                if (i < outputWrapper.tanks.length) {
                    outputWrapper.tanks[i].setFluid(FluidStack.loadFluidStackFromNBT(list.getCompoundTagAt(i)));
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
                    builder.append(outputWrapper.tanks[i].getFluid().getLocalizedName()).append(" - ").append(outputWrapper.tanks[i].getFluid().amount);
                    if (i != outputWrapper.tanks.length - 1) {
                        builder.append("\n");
                    }
                }
            }
        }
        return builder.toString();
    }
}
