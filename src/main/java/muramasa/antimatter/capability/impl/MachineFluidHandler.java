package muramasa.antimatter.capability.impl;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import tesseract.Tesseract;
import tesseract.api.fluid.FluidData;
import tesseract.api.fluid.IFluidNode;
import tesseract.api.ITickHost;
import tesseract.api.ITickingController;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE;

public class MachineFluidHandler implements IFluidNode, ITickHost {

    protected static int DEFAULT_CAPACITY = 99999;
    protected static int DEFAULT_PRESSURE = 99999;

    protected TileEntityMachine tile;
    protected ITickingController controller;
    protected FluidTankWrapper inputWrapper, outputWrapper;
    protected int[] priority = new int[]{0, 0, 0, 0, 0, 0};
    protected int pressure;

    public MachineFluidHandler(TileEntityMachine tile, int capacity, int pressure) {
        this.tile = tile;
        this.pressure = pressure;
        int inputCount = tile.getMachineType().getGui().getSlots(SlotType.FL_IN, tile.getMachineTier()).size();
        int outputCount = tile.getMachineType().getGui().getSlots(SlotType.FL_OUT, tile.getMachineTier()).size();
        if (inputCount > 0) inputWrapper = new FluidTankWrapper(tile, inputCount, capacity, ContentEvent.FLUID_INPUT_CHANGED);
        if (outputCount > 0) outputWrapper = new FluidTankWrapper(tile, outputCount, capacity, ContentEvent.FLUID_OUTPUT_CHANGED);
        Tesseract.FLUID.registerNode(tile.getDimention(), tile.getPos().toLong(), this);
    }

    public MachineFluidHandler(TileEntityMachine tile) {
        this(tile, DEFAULT_CAPACITY, DEFAULT_PRESSURE);
    }

    public MachineFluidHandler(TileEntityMachine tile, int capacity, int pressure, CompoundNBT fluidData) {
        this(tile, capacity, pressure);
        if (fluidData != null) deserialize(fluidData);
    }

    public MachineFluidHandler(TileEntityMachine tile, CompoundNBT fluidData) {
        this(tile, DEFAULT_CAPACITY, DEFAULT_PRESSURE, fluidData);
    }

    public void onUpdate() {
        if (controller != null) controller.tick();
    }

    public void onRemove() {
        Tesseract.FLUID.remove(tile.getDimention(), tile.getPos().toLong());
    }

    /*public void onReset() {
        if (tile.isServerSide()) {
            TesseractAPI.removeFluid(tile.getDimention(), tile.getPos().toLong());
            TesseractAPI.registerFluidNode(tile.getDimention(), tile.getPos().toLong(), this);
        }
    }*/

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

    public FluidTankWrapper getWrapperForSide(Direction side) {
        return inputWrapper != null ? inputWrapper : outputWrapper;
    }

    /** Returns raw FluidStacks from all inputs, including nulls **/
    @Nullable
    public FluidStack[] getInputsRaw() {
        if (inputWrapper == null || inputWrapper.getTanks() == 0) return null;
        FluidStack[] inputs = new FluidStack[inputWrapper.getTanks()];
        for (int i = 0; i < inputs.length; i++) {
            inputs[i] = inputWrapper.getFluidInTank(i);
        }
        return inputs;
    }

    /** Returns raw FluidStacks from all outputs, including nulls **/
    @Nullable
    public FluidStack[] getOutputsRaw() {
        if (outputWrapper == null || outputWrapper.getTanks() == 0) return null;
        FluidStack[] outputs = new FluidStack[outputWrapper.getTanks()];
        for (int i = 0; i < outputs.length; i++) {
            outputs[i] = outputWrapper.getFluidInTank(i);
        }
        return outputs;
    }

    /** Directly sets inputs. Only to be used for special cases like fluid syncing **/
    public void setInputs(FluidStack[] inputs) {
        if (inputWrapper == null || inputs.length != inputWrapper.getTanks()) return;
        for (int i = 0; i < inputs.length; i++) {
            inputWrapper.setFluidToTank(i, inputs[i]);
        }
    }

    /** Directly sets outputs. Only to be used for special cases like fluid syncing **/
    public void setOutputs(FluidStack[] outputs) {
        if (outputWrapper == null || outputs.length != outputWrapper.getTanks()) return;
        for (int i = 0; i < outputs.length; i++) {
            outputWrapper.setFluidToTank(i, outputs[i]);
        }
    }

    /** Returns list of input fluids, filtering nulls **/
    public List<FluidStack> getInputList() {
        if (inputWrapper == null) return Collections.emptyList();
        List<FluidStack> list = new ObjectArrayList<>();
        for (int i = 0; i < inputWrapper.getTanks(); i++) {
            FluidStack stack = inputWrapper.getFluidInTank(i);
            if (stack != FluidStack.EMPTY) list.add(stack);
        }
        return list;
    }

    /** Returns list of output fluids, filtering nulls **/
    public List<FluidStack> getOutputList() {
        if (outputWrapper == null) return Collections.emptyList();
        List<FluidStack> list = new ObjectArrayList<>();
        for (int i = 0; i < outputWrapper.getTanks(); i++) {
            FluidStack stack = outputWrapper.getFluidInTank(i);
            if (stack != FluidStack.EMPTY) list.add(stack);
        }
        return list;
    }

    //TODO called by Basic machines, should they use consumeAndReturn?
    public void consumeInputs(FluidStack... inputs) {
        if (inputWrapper == null) return;
        for (FluidStack input : inputs) {
            inputWrapper.drain(input, EXECUTE);
        }
    }

    public void addInputs(FluidStack... inputs) {
        if (inputWrapper == null) return;
        for (FluidStack input : inputs) {
            inputWrapper.fill(input, EXECUTE);
        }
    }

    public void addOutputs(FluidStack... outputs) {
        if (outputWrapper == null || outputs == null || outputs.length == 0) return;
        for (FluidStack output : outputs) {
            outputWrapper.fill(output, EXECUTE);
        }
    }

    public boolean canOutputsFit(FluidStack[] outputs) {
        return getSpaceForOutputs(outputs) >= outputs.length;
    }

    public int getSpaceForOutputs(FluidStack[] outputs) {
        int matchCount = 0;
        if (outputWrapper == null) return matchCount;
        for (FluidStack output : outputs) {
            if (outputWrapper.fill(output, SIMULATE) == output.getAmount()) matchCount++;
        }
        return matchCount;
    }

    public FluidStack[] consumeAndReturnInputs(FluidStack... inputs) {
        if (inputWrapper == null) return new FluidStack[0];
        List<FluidStack> notConsumed = new ObjectArrayList<>();
        FluidStack result;
        for (FluidStack input : inputs) {
            result = inputWrapper.drain(input, EXECUTE);
            if (result != FluidStack.EMPTY) {
                if (result.getAmount() != input.getAmount()) { //Fluid was partially consumed
                    notConsumed.add(Utils.ca(input.getAmount() - result.getAmount(), input));
                }
            } else {
                notConsumed.add(input); //Fluid not present in input tanks
            }
        }
        return notConsumed.toArray(new FluidStack[0]);
    }

    public FluidStack[] exportAndReturnOutputs(FluidStack... outputs) {
        if (outputWrapper == null) return new FluidStack[0];
        List<FluidStack> notExported = new ObjectArrayList<>();
        int result;
        for (int i = 0; i < outputs.length; i++) {
            result = outputWrapper.fill(outputs[i], EXECUTE);
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
            for (int i = 0; i < inputWrapper.getTanks(); i++) {
                if (inputWrapper.getFluidInTank(i) == FluidStack.EMPTY) continue;
                list.add(inputWrapper.writeToNBT(i, new CompoundNBT()));
            }
            tag.put("Input-Fluids", list);
        }
        if (outputWrapper != null) {
            ListNBT list = new ListNBT();
            for (int i = 0; i < outputWrapper.getTanks(); i++) {
                if (outputWrapper.getFluidInTank(i) == FluidStack.EMPTY) continue;
                list.add(outputWrapper.writeToNBT(i, new CompoundNBT()));
            }
            tag.put("Output-Fluids", list);
        }
        return tag;
    }

    public void deserialize(CompoundNBT tag) {
        if (inputWrapper != null) {
            ListNBT list = tag.getList("Input-Fluids", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                if (i < inputWrapper.getTanks()) {
                    inputWrapper.setFluidToTank(i, FluidStack.loadFluidStackFromNBT(list.getCompound(i)));
                }
            }
        }
        if (outputWrapper != null) {
            ListNBT list = tag.getList("Output-Fluids", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                if (i < outputWrapper.getTanks()) {
                    outputWrapper.setFluidToTank(i, FluidStack.loadFluidStackFromNBT(list.getCompound(i)));
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (inputWrapper != null) {
            builder.append("Inputs:\n");
            for (int i = 0; i < inputWrapper.getTanks(); i++) {
                FluidStack stack = inputWrapper.getFluidInTank(i);
                if (stack != FluidStack.EMPTY) {
                    builder.append(stack.getFluid().getRegistryName()).append(" - ").append(stack.getAmount());
                    if (i != inputWrapper.getTanks() - 1) {
                        builder.append("\n");
                    }
                }
            }
        }
        if (outputWrapper != null) {
            builder.append("Outputs:\n");
            for (int i = 0; i < outputWrapper.getTanks(); i++) {
                FluidStack stack = outputWrapper.getFluidInTank(i);
                if (stack != FluidStack.EMPTY) {
                    builder.append(stack.getFluid().getRegistryName()).append(" - ").append(stack.getAmount());
                    if (i != outputWrapper.getTanks() - 1) {
                        builder.append("\n");
                    }
                }
            }
        }
        return builder.toString();
    }

    /** Tesseract IFluidNode Implementations **/
    @Override
    public int insert(@Nonnull FluidData data, boolean simulate) {
        FluidStack stack = (FluidStack) data.getStack();
        return inputWrapper.getFirstValidTank(stack.getFluid()) != -1 ? inputWrapper.fill(stack, simulate ? SIMULATE : EXECUTE) : 0;
    }

    @Nullable
    @Override
    public FluidData extract(int tank, int amount, boolean simulate) {
        FluidStack fluid = outputWrapper.getFluidInTank(tank);
        if (fluid.getAmount() > amount) {
            fluid = fluid.copy();
            fluid.setAmount(amount);
        }

        FluidStack stack = outputWrapper.drain(fluid, simulate ? SIMULATE : EXECUTE);
        return stack.isEmpty() ? null : new FluidData(stack, stack.getFluid(), stack.getAmount(), stack.getFluid().getAttributes().getTemperature(), stack.getFluid().getAttributes().isGaseous());
    }

    @Override
    public int getAvailableTank(@Nonnull Dir direction) {
        return outputWrapper.getAvailableTank(direction.getIndex());
    }

    @Override
    public int getOutputAmount(@Nonnull Dir direction) {
        return pressure;
    }

    @Override
    public int getPriority(@Nonnull Dir direction) {
        return priority[direction.getIndex()];
    }

    @Override
    public boolean canOutput() {
        return outputWrapper != null;
    }

    @Override
    public boolean canInput() {
        return inputWrapper != null;
    }

    @Override
    public boolean canOutput(@Nonnull Dir direction) {
        return tile.getOutputFacing().getIndex() == direction.getIndex();
    }

    @Override
    public boolean canInput(@Nonnull Object fluid, @Nonnull Dir direction) {
        if (tile.getFacing().getIndex() == direction.getIndex()) return false;
        if (/*TODO: Can input into output* ||*/tile.getOutputFacing().getIndex() == direction.getIndex()) return false;
        return inputWrapper.isFluidAvailable(fluid, direction.getIndex()) && inputWrapper.getFirstValidTank(fluid) != -1;
    }

    @Override
    public boolean connects(@Nonnull Dir direction) {
        return tile.getFacing().getIndex() != direction.getIndex()/* && !(tile.getCover(Ref.DIRECTIONS[direction.getIndex()]) instanceof CoverMaterial)*/;
    }

    @Override
    public void reset(@Nullable ITickingController oldController, @Nullable ITickingController newController) {
        if (oldController == null || (controller == oldController && newController == null) || controller != oldController)
            controller = newController;
    }
}
