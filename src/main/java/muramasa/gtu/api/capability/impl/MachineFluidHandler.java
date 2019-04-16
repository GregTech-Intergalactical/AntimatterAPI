package muramasa.gtu.api.capability.impl;

import muramasa.gtu.api.gui.SlotType;
import muramasa.gtu.api.tileentities.TileEntityMachine;
import muramasa.gtu.api.util.Utils;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.List;

public class MachineFluidHandler {

    protected static int DEFAULT_CAPACITY = 99999;

    protected TileEntityMachine tile;
    protected FluidTankWrapper input, output;

    public MachineFluidHandler(TileEntityMachine tile, int capacity) {
        this.tile = tile;
        input = new FluidTankWrapper(tile, tile.getType().getGui().getSlots(SlotType.FL_IN, tile.getTier()).size(), capacity, true);
        output = new FluidTankWrapper(tile, tile.getType().getGui().getSlots(SlotType.FL_OUT, tile.getTier()).size(), capacity, false);
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
        return input;
    }

    public FluidTankWrapper getOutputWrapper() {
        return output;
    }

    /** Helpers **/
    public FluidStack[] getInputs() {
        return getInputList().toArray(new FluidStack[0]);
    }

    public FluidStack[] getOutputs() {
        return getOutputList().toArray(new FluidStack[0]);
    }

    /** Returns raw FluidStacks from all inputs, including nulls **/
    public FluidStack[] getInputsRaw() {
        FluidStack[] inputs = new FluidStack[input.tanks.length];
        for (int i = 0; i < inputs.length; i++) {
            inputs[i] = input.tanks[i].getFluid();
        }
        return inputs;
    }

    public FluidStack[] getOutputsRaw() {
        FluidStack[] outputs = new FluidStack[output.tanks.length];
        for (int i = 0; i < outputs.length; i++) {
            outputs[i] = output.tanks[i].getFluid();
        }
        return outputs;
    }

    public void setInputs(FluidStack[] inputs) {
        if (inputs.length != input.tanks.length) return;
        for (int i = 0; i < inputs.length; i++) {
            input.tanks[i].setFluid(inputs[i]);
        }
    }

    public void setOutputs(FluidStack[] outputs) {
        if (outputs.length != output.tanks.length) return;
        for (int i = 0; i < outputs.length; i++) {
            output.tanks[i].setFluid(outputs[i]);
        }
    }

    public List<FluidStack> getInputList() {
        ArrayList<FluidStack> list = new ArrayList<>();
        for (int i = 0; i < input.tanks.length; i++) {
            if (input.tanks[i].getFluid() != null) list.add(input.tanks[i].getFluid());
        }
        return list;
    }

    public List<FluidStack> getOutputList() {
        ArrayList<FluidStack> list = new ArrayList<>();
        for (int i = 0; i < output.tanks.length; i++) {
            if (output.tanks[i].getFluid() != null) list.add(output.tanks[i].getFluid());
        }
        return list;
    }

    public void consumeInputs(FluidStack... inputs) {
        for (int i = 0; i < inputs.length; i++) {
            for (int j = 0; j < input.tanks.length; j++) {
                if (Utils.equals(inputs[i], input.tanks[j].getFluid())) {
                    input.tanks[j].drain(inputs[i].amount, true);
                }
            }
        }
    }

    public void addInputs(FluidStack... fluids) {
        for (int i = 0; i < fluids.length; i++) {
            for (int j = 0; j < input.tanks.length; j++) {
                input.tanks[j].fill(fluids[i], true);
            }
        }
    }

    public void addOutputs(FluidStack... fluids) {
        for (int i = 0; i < fluids.length; i++) {
            for (int j = 0; j < output.tanks.length; j++) {
                System.out.println("Adding output: " + fluids[i].getLocalizedName());
                output.tanks[j].fill(fluids[i], true);
            }
        }
    }

    public boolean canOutputsFit(FluidStack[] a) {
        return getSpaceForOutputs(a) >= a.length;
    }

    public int getSpaceForOutputs(FluidStack[] a) {
        int matchCount = 0;
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < output.tanks.length; j++) {
                if (output.tanks[j].getFluid() == null || output.tanks[j].fill(a[i], false) == a[i].amount) {
                    matchCount++;
                }
            }
        }
        return matchCount;
    }

    public FluidStack[] consumeAndReturnInputs(FluidStack... inputs) {
        ArrayList<FluidStack> notConsumed = new ArrayList<>();
        for (int i = 0; i < inputs.length; i++) {
            for (int j = 0; j < input.tanks.length; j++) {
                if (Utils.equals(inputs[i], input.tanks[j].getFluid())) {
                    if (input.tanks[j].getFluid().amount >= inputs[i].amount) {
                        input.tanks[j].drain(inputs[i], true);
                    } else {
                        int leftOver = inputs[i].amount - input.tanks[j].getFluid().amount;
                        notConsumed.add(Utils.ca(leftOver, inputs[i]));
                        input.tanks[j].drain(Utils.ca(inputs[i].amount - leftOver, inputs[i]), true);
                    }
                } else {
                    notConsumed.add(inputs[i]);
                }
            }
        }
        return notConsumed.toArray(new FluidStack[0]);
    }

    public FluidStack[] exportAndReturnOutputs(FluidStack... outputs) {
        ArrayList<FluidStack> notExported = new ArrayList<>();
        int result;
        for (int i = 0; i < outputs.length; i++) {
            for (int j = 0; j < output.tanks.length; j++) {
                result = output.tanks[j].fill(outputs[i].copy(), true);
                if (result == outputs[i].amount) break; //Filling was successful
                else outputs[i] = Utils.ca(result, outputs[i]);
                if (j == output.tanks.length - 1) notExported.add(outputs[i]);
            }
        }
        return notExported.toArray(new FluidStack[0]);
    }

    /** NBT **/
    public NBTTagCompound serialize() {
        NBTTagCompound tag = new NBTTagCompound();
        if (input != null) {
            NBTTagList list = new NBTTagList();
            for (int i = 0; i < input.tanks.length; i++) {
                if (input.tanks[i].getFluid() == null) continue;
                list.appendTag(input.tanks[i].writeToNBT(new NBTTagCompound()));
            }
            tag.setTag("Input-Fluids", list);
        }
        if (output != null) {
            NBTTagList list = new NBTTagList();
            for (int i = 0; i < output.tanks.length; i++) {
                if (output.tanks[i].getFluid() == null) continue;
                list.appendTag(output.tanks[i].writeToNBT(new NBTTagCompound()));
            }
            tag.setTag("Output-Fluids", list);
        }
        return tag;
    }

    public void deserialize(NBTTagCompound tag) {
        if (input != null) {
            NBTTagList list = tag.getTagList("Input-Fluids", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++) {
                if (i < input.tanks.length) {
                    input.tanks[i].setFluid(FluidStack.loadFluidStackFromNBT(list.getCompoundTagAt(i)));
                }
            }
        }
        if (output != null) {
            NBTTagList list = tag.getTagList("Output-Fluids", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.tagCount(); i++) {
                if (i < output.tanks.length) {
                    output.tanks[i].setFluid(FluidStack.loadFluidStackFromNBT(list.getCompoundTagAt(i)));
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (input != null) {
            builder.append("Inputs:\n");
            for (int i = 0; i < input.tanks.length; i++) {
                if (input.tanks[i].getFluid() != null) {
                    builder.append(input.tanks[i].getFluid().getLocalizedName()).append(" - ").append(input.tanks[i].getFluid().amount);
                    if (i != input.tanks.length - 1) {
                        builder.append("\n");
                    }
                }
            }
        }
        if (output != null) {
            builder.append("Outputs:\n");
            for (int i = 0; i < output.tanks.length; i++) {
                if (output.tanks[i].getFluid() != null) {
                    builder.append(output.tanks[i].getFluid().getLocalizedName()).append(" - ").append(output.tanks[i].getFluid().amount);
                    if (i != output.tanks.length - 1) {
                        builder.append("\n");
                    }
                }
            }
        }
        return builder.toString();
    }
}
