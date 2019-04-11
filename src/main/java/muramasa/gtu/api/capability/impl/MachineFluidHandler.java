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
        if (fluidData != null) deserialize(fluidData);
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
        return getInputList().toArray(new FluidStack[0]);
    }

    public FluidStack[] getOutputs() {
        return getOutputList().toArray(new FluidStack[0]);
    }

    public List<FluidStack> getInputList() {
        ArrayList<FluidStack> list = new ArrayList<>();
        for (int i = 0; i < inputWrapper.tanks.length; i++) {
            if (inputWrapper.tanks[i].getFluid() != null) list.add(inputWrapper.tanks[i].getFluid());
        }
        return list;
    }

    public List<FluidStack> getOutputList() {
        ArrayList<FluidStack> list = new ArrayList<>();
        for (int i = 0; i < outputWrapper.tanks.length; i++) {
            if (outputWrapper.tanks[i].getFluid() != null) list.add(outputWrapper.tanks[i].getFluid());
        }
        return list;
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
        for (int i = 0; i < fluids.length; i++) {
            for (int j = 0; j < inputWrapper.tanks.length; j++) {
                inputWrapper.tanks[j].fill(fluids[i], true);
            }
        }
    }

    public void addOutputs(FluidStack... fluids) {
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
