package muramasa.antimatter.capability.machine;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.capability.fluid.FluidTanks;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import tesseract.Tesseract;
import tesseract.api.fluid.FluidData;
import tesseract.api.fluid.IFluidNode;
import tesseract.api.ITickHost;
import tesseract.api.ITickingController;
import tesseract.util.Dir;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static muramasa.antimatter.machine.MachineFlag.GENERATOR;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE;

public class MachineFluidHandler<T extends TileEntityMachine> implements IFluidNode<FluidStack>, IMachineHandler, ITickHost {

    protected final T tile;
    protected final FluidTanks inputTanks, outputTanks;
    protected final int[] priority = new int[]{0, 0, 0, 0, 0, 0}; // TODO

    protected int capacity, pressure;
    protected ITickingController controller;

    public MachineFluidHandler(T tile, int capacity, int pressure) {
        this.tile = tile;
        this.capacity = capacity;
        this.pressure = pressure;
        int inputCount = tile.getMachineType().getGui().getSlots(SlotType.FL_IN, tile.getMachineTier()).size();
        int outputCount = tile.getMachineType().getGui().getSlots(SlotType.FL_OUT, tile.getMachineTier()).size();
        if (inputCount > 0) {
            this.inputTanks = FluidTanks.create(tile, ContentEvent.FLUID_INPUT_CHANGED, builder -> {
                for (int i = 0; i < inputCount; i++) {
                    builder.tank(capacity);
                }
                return builder;
            });
        }
        else {
            this.inputTanks = null;
        }
        if (outputCount > 0) {
            this.outputTanks = FluidTanks.create(tile, ContentEvent.FLUID_OUTPUT_CHANGED, builder -> {
                for (int i = 0; i < outputCount; i++) {
                    builder.tank(capacity);
                }
                return builder;
            });
        }
        else {
            this.outputTanks = null;
        }
    }

    public MachineFluidHandler(T tile) {
        this(tile, 8000 * (1 + tile.getMachineTier().getIntegerId()), 1000 * (250 + tile.getMachineTier().getIntegerId()));
    }

    @Override
    public void init() {
        Tesseract.FLUID.registerNode(tile.getDimension(), tile.getPos().toLong(), this);
    }

    public void onUpdate() {
        if (controller != null) {
            controller.tick();
        }
    }

    public void onRemove() {
        Tesseract.FLUID.remove(tile.getDimension(), tile.getPos().toLong());
    }

    public void onReset() {
        if (tile.isServerSide()) {
            Tesseract.FLUID.remove(tile.getDimension(), tile.getPos().toLong());
            Tesseract.FLUID.registerNode(tile.getDimension(), tile.getPos().toLong(), this);
        }
    }

    //TODO IN THIS METHOD: Slot 0 is hardcoded for output, refactor this.
    protected void insertFromCell(int slot) {
        // MachineItemHandler handler = tile.itemHandler;
        tile.itemHandler.ifPresent(handler -> {
            ItemStack stack = handler.getCellHandler().getStackInSlot(slot);
            //One at a time.
            for (int i = 0; i < stack.getCount(); i++) {
                //Fluid caps.
                LazyOptional<IFluidHandlerItem> fhandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
                //First, validate that an empty item can be
                ItemStack changedBucket = fhandler.map(ihandler -> {
                    FluidStack fluid = ihandler.getFluidInTank(0);
                    if (!checkValidFluid(fluid)) {
                        //I know it is not supposed to be null but i dont know how to do it otherwise, dont want a lazyoptional return, like Empty.
                        return null;
                    }
                    //tempHandler - essentially simulate draining a copy and see if it fits in output, otherwise dont drain it.
                    LazyOptional<IFluidHandlerItem> tempHandler = ihandler.getContainer().copy().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
                    if (tempHandler.map(temp -> {
                        temp.drain(this.inputWrapper.fill(fluid, SIMULATE), EXECUTE);
                        if (handler.getOutputHandler().insertItem(0,temp.getContainer(), true) != ItemStack.EMPTY) return false;
                        return true;
                    }).orElse(false)) {
                        ihandler.drain(this.inputWrapper.fill(fluid, EXECUTE), EXECUTE);
                        return ihandler.getContainer();
                    }
                    return null;
                }).orElse(null);
                //changedBucket - a changed bucket.
                if (changedBucket != null) {
                    changedBucket.copy();
                    changedBucket.setCount(1);
                    handler.getCellHandler().extractItem(slot,1,false);
                    handler.getOutputHandler().insertItem(0, changedBucket, false);
                } else {
                    break;
                }
            }
        });
    }

    protected boolean checkValidFluid(FluidStack fluid) {
        if (tile.has(GENERATOR)) {
            Recipe recipe = tile.getMachineType().getRecipeMap().find(null, new FluidStack[]{fluid});
            if (recipe != null) {
                return true;
            }
        }
        return true;
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object ...data) {
        if (event instanceof ContentEvent) {
            switch ((ContentEvent)event) {
                case ITEM_CELL_CHANGED:
                    if (tile.itemHandler.map(MachineItemHandler::getCellCount).orElse(0) > 0 && data[0] instanceof Integer) {
                        insertFromCell((Integer) data[0]);
                    }
                    break;
            }
        }
    }

    public FluidTanks getInputTanks() {
        return inputTanks;
    }

    public FluidTanks getOutputTanks() {
        return outputTanks;
    }

    /** Helpers **/
    public FluidStack[] getInputs() {
        return getInputList().toArray(new FluidStack[0]);
    }

    public FluidStack[] getOutputs() {
        return getOutputList().toArray(new FluidStack[0]);
    }

    public FluidTanks getTankFromSide(Direction side) {
        return inputTanks != null ? inputTanks : outputTanks;
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
    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        if (inputWrapper != null) {
            ListNBT list = new ListNBT();
            for (int i = 0; i < inputWrapper.getTanks(); i++) {
                if (inputWrapper.getFluidInTank(i) == FluidStack.EMPTY) continue;
                list.add(inputWrapper.writeToNBT(i, new CompoundNBT()));
            }
            tag.put(Ref.TAG_MACHINE_INPUT_FLUID, list);
        }
        if (outputWrapper != null) {
            ListNBT list = new ListNBT();
            for (int i = 0; i < outputWrapper.getTanks(); i++) {
                if (outputWrapper.getFluidInTank(i) == FluidStack.EMPTY) continue;
                list.add(outputWrapper.writeToNBT(i, new CompoundNBT()));
            }
            tag.put(Ref.TAG_MACHINE_OUTPUT_FLUID, list);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        if (inputWrapper != null) {
            ListNBT list = nbt.getList(Ref.TAG_MACHINE_INPUT_FLUID, Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) {
                if (i < inputWrapper.getTanks()) {
                    inputWrapper.setFluidToTank(i, FluidStack.loadFluidStackFromNBT(list.getCompound(i)));
                }
            }
        }
        if (outputWrapper != null) {
            ListNBT list = nbt.getList(Ref.TAG_MACHINE_OUTPUT_FLUID, Constants.NBT.TAG_COMPOUND);
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
    public int insert(FluidData data, boolean simulate) {
        if (inputTanks == null) {
            return 0;
        }
        FluidStack stack = (FluidStack) data.getStack();
        return inputTanks.fill(stack, simulate ? SIMULATE : EXECUTE);
    }

    @Nullable
    @Override
    public FluidData<FluidStack> extract(int tank, int amount, boolean simulate) {
        if (outputTanks == null) {
            return null;
        }
        FluidStack stack = outputTanks.getFluidInTank(tank);
        /*
        if (fluid.getAmount() > amount) {
            fluid = fluid.copy();
            fluid.setAmount(amount);
        }
         */
        FluidStack drained = outputTanks.drain(stack, simulate ? SIMULATE : EXECUTE);
        return drained.isEmpty() ? null : new FluidData<>(drained, drained.getAmount(), drained.getFluid().getAttributes().getTemperature(), drained.getFluid().getAttributes().isGaseous());
    }

    @Override
    public int getAvailableTank(Dir direction) {
        return outputWrapper.getAvailableTank(direction.getIndex());
    }

    @Override
    public int getOutputAmount(Dir direction) {
        return pressure;
    }

    @Override
    public int getPriority(Dir direction) {
        return priority[direction.getIndex()];
    }

    @Override
    public boolean canOutput() {
        return outputTanks != null;
    }

    @Override
    public boolean canInput() {
        return inputTanks != null;
    }

    @Override
    public boolean canOutput(Dir direction) {
        return tile.getOutputFacing().getIndex() == direction.getIndex();
    }

    @Override
    public boolean canInput(Object fluid, Dir direction) {
        if (tile.getFacing().getIndex() == direction.getIndex()) return false;
        if (/*TODO: Can input into output* ||*/tile.getOutputFacing().getIndex() == direction.getIndex()) return false;
        return inputWrapper.isFluidAvailable(fluid, direction.getIndex()) && inputWrapper.getFirstValidTank(fluid) != -1;
    }

    @Override
    public boolean connects(Dir direction) {
        return tile.getFacing().getIndex() != direction.getIndex()/* && !(tile.getCover(Ref.DIRECTIONS[direction.getIndex()]) instanceof CoverMaterial)*/;
    }

    @Override
    public void reset(ITickingController oldController, ITickingController newController) {
        if (oldController == null || (controller == oldController && newController == null) || controller != oldController) {
            controller = newController;
        }
    }

}
