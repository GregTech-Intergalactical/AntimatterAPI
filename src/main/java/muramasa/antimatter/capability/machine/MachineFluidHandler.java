package muramasa.antimatter.capability.machine;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
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
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import tesseract.Tesseract;
import tesseract.api.ITickingController;
import tesseract.api.fluid.FluidData;
import tesseract.api.fluid.IFluidNode;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static muramasa.antimatter.machine.MachineFlag.GENERATOR;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE;

public class MachineFluidHandler<T extends TileEntityMachine> implements IFluidNode<FluidStack>, IMachineHandler, IFluidHandler {

    protected final T tile;
    protected final EnumMap<FluidDirection, FluidTanks> tanks = new EnumMap<>(FluidDirection.class);
    protected final int[] priority = new int[]{0, 0, 0, 0, 0, 0}; // TODO

    protected int capacity, pressure;
    protected ITickingController controller;
    //To protect against callbacks into fillingCell.
    //TODO: Protect against these callback thingys. Dunno how.
    private boolean fillingCell = false;

    /** For GUI **/
    protected boolean dirty;

    protected void markDirty() {
        dirty = true;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void markSynced() {
        dirty = false;
    }

    public MachineFluidHandler(T tile, int capacity, int pressure) {
        this.tile = tile;
        this.capacity = capacity;
        this.pressure = pressure;
        int inputCount = tile.getMachineType().getGui().getSlots(SlotType.FL_IN, tile.getMachineTier()).size();
        int outputCount = tile.getMachineType().getGui().getSlots(SlotType.FL_OUT, tile.getMachineTier()).size();
        if (inputCount > 0) {
            tanks.put(FluidDirection.INPUT, FluidTanks.create(tile, ContentEvent.FLUID_INPUT_CHANGED, b -> {
                for (int i = 0; i < inputCount; i++) {
                    b.tank(capacity);
                }
                return b;
            }));
        }
        if (outputCount > 0) {
            tanks.put(FluidDirection.OUTPUT, FluidTanks.create(tile, ContentEvent.FLUID_OUTPUT_CHANGED, b -> {
                for (int i = 0; i < outputCount; i++) {
                    b.tank(capacity);
                }
                return b;
            }));
        }
    }

    public MachineFluidHandler(T tile) {
        this(tile, 8000 * (1 + tile.getMachineTier().getIntegerId()), 1000 * (250 + tile.getMachineTier().getIntegerId()));
    }

    @Override
    public void init() {
        registerNet();
    }

    public void onUpdate() {
        if (controller != null) {
            controller.tick();
        }
        fillCell(0, 1000);
    }

    public void onRemove() {
        deregisterNet();
    }

    public void onReset() {
        if (tile.isServerSide()) {
            refreshNet();
        }
    }

    public int getTanks() {
        return this.tanks.values().stream().mapToInt(FluidTanks::getTanks).sum();
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
         return getTank(tank).getFluid();
    }

    protected FluidTank getTank(int tank) {
        if (getInputTanks() == null) {
            if (getOutputTanks() != null) return getOutputTanks().getTank(tank);
        } else if (getInputTanks() != null && getOutputTanks() != null){
            if (tank > getInputTanks().getTanks()) return getOutputTanks().getTank(offsetTank(tank));
        } else if (getOutputTanks() == null && getInputTanks() != null) {
            return getInputTanks().getTank(tank);
        }
        return null;
    }

    protected FluidTanks getTanks(int tank) {
        if (getInputTanks() == null) {
            return getOutputTanks();
        } else if (getOutputTanks() == null) {
            return getInputTanks();
        } else {
            if (tank > getInputTanks().getTanks()) return getOutputTanks();
        }
        return getInputTanks();
    }

    protected int offsetTank(int tank) {
        if (getInputTanks() != null && tank > getInputTanks().getTanks()) return tank;
        if (getInputTanks() != null) return tank - getInputTanks().getTanks();
        return tank;
    }

    @Override
    public int getTankCapacity(int tank) {
        return getTanks(tank).getTankCapacity(offsetTank(tank));
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return getTank(tank).isFluidValid(stack);
    }


    public FluidTanks getAllTanks() {
        ArrayList<FluidTank> list = new ArrayList<>();
        if (getInputTanks() != null) list.addAll(Arrays.asList(getInputTanks().getBackingTanks()));
        if (getOutputTanks() != null) list.addAll(Arrays.asList(getOutputTanks().getBackingTanks()));
        return new FluidTanks(list);
    }

    public int fillCell(int cellSlot, int maxFill) {
        if (fillingCell) return 0;
        fillingCell = true;
        if (this.tanks.containsKey(FluidDirection.INPUT)) {
            tile.itemHandler.ifPresent(ih -> {
                if (ih.getCellInputHandler() == null) return;
                ItemStack cell = ih.getCellInputHandler().getStackInSlot(cellSlot);
                ItemStack toActOn = cell.copy();
                toActOn.setCount(1);
                toActOn.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(cfh -> {
                    ItemStack checkContainer = toActOn.copy().getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).map(t -> {
                                if (t.getFluidInTank(0).isEmpty()) {
                                    t.fill(FluidUtil.tryFluidTransfer(t,this.getAllTanks(), maxFill, false), EXECUTE);
                                } else {
                                    t.drain(maxFill, EXECUTE);
                                }
                                return t.getContainer();
                            }).orElse(null/* throw exception */);
                    if (!ih.getCellOutputHandler().insertItem(cellSlot,checkContainer,true).isEmpty()) return;

                    FluidStack stack;
                    if (cfh.getFluidInTank(0).isEmpty()) {
                        stack = FluidUtil.tryFluidTransfer(cfh,this.getAllTanks(), maxFill, true);
                    } else {
                        stack = FluidUtil.tryFluidTransfer(this.getAllTanks(),cfh, maxFill, true);
                    }
                    if (!stack.isEmpty()) {
                        ItemStack insert = cfh.getContainer();
                        insert.setCount(1);
                        ih.getCellOutputHandler().insertItem(cellSlot, insert, false);
                        ih.getCellInputHandler().extractItem(cellSlot, 1, false);
                    }
                });
            });
        }
        fillingCell = false;
        return 0;
    }

    public int fill(FluidStack stack, IFluidHandler.FluidAction action) {
        FluidTanks input = getInputTanks();
        if (input != null && !empty(input)) {
            return this.tanks.get(FluidDirection.INPUT).fill(stack, action);
        }
        return 0;
    }

    protected boolean empty(FluidTanks tank) {
        return tank.getTanks() == 0;
    }

    public int fillOutput(FluidStack stack, IFluidHandler.FluidAction action) {
        if (this.tanks.containsKey(FluidDirection.OUTPUT)) {
            return this.tanks.get(FluidDirection.OUTPUT).fill(stack, action);
        }
        return 0;
    }
    @Nonnull
    public FluidStack drain(FluidStack stack, IFluidHandler.FluidAction action) {
        if (this.tanks.containsKey(FluidDirection.OUTPUT)) {
            return this.tanks.get(FluidDirection.OUTPUT).drain(stack, action);
        }
        return FluidStack.EMPTY;
    }

    @Nonnull
    public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
        if (this.tanks.containsKey(FluidDirection.OUTPUT)) {
            return this.tanks.get(FluidDirection.OUTPUT).drain(maxDrain, action);
        }
        return FluidStack.EMPTY;
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
                        fillCell((Integer) data[0], 1000);
                    }
                    break;
                case FLUID_INPUT_CHANGED:
                    this.markDirty();
                case FLUID_OUTPUT_CHANGED:
                    this.markDirty();
                    break;
            }
        }
    }

    @Nullable
    public FluidTanks getInputTanks() {
        return this.tanks.get(FluidDirection.INPUT);
    }

    @Nullable
    public FluidTanks getOutputTanks() {
        return this.tanks.get(FluidDirection.OUTPUT);
    }

    /** Helpers **/
    @Nonnull
    public FluidStack[] getInputs() {
        FluidTanks tanks = getInputTanks();
        return tanks == null ? new FluidStack[0] : tanks.getFluids();
    }

    public FluidStack[] getOutputs() {
        FluidTanks tanks = getOutputTanks();
        return tanks == null ? new FluidStack[0] : tanks.getFluids();
    }

    public boolean canOutputsFit(FluidStack[] outputs) {
        return getSpaceForOutputs(outputs) >= outputs.length;
    }

    public int getSpaceForOutputs(FluidStack[] outputs) {
        int matchCount = 0;
        if (this.tanks.containsKey(FluidDirection.OUTPUT)) {
            for (FluidStack output : outputs) {
                if (fillOutput(output, SIMULATE) == output.getAmount()) {
                    matchCount++;
                }
            }
        }
        return matchCount;
    }

    public void addOutputs(FluidStack... fluids) {
        if (!this.tanks.containsKey(FluidDirection.OUTPUT)) {
            return;
        }
        if (fluids != null) {
            for (FluidStack input : fluids) {
                fillOutput(input,EXECUTE);
            }
        }
    }
    @Nonnull
    public List<FluidStack> consumeAndReturnInputs(List<FluidStack> inputs) {
        if (!this.tanks.containsKey(FluidDirection.INPUT)) {
            return Collections.emptyList();
        }
        List<FluidStack> notConsumed = new ObjectArrayList<>();
        FluidStack result;
        if (inputs != null) {
            for (FluidStack input : inputs) {
                result = drain(input, EXECUTE);
                if (result != FluidStack.EMPTY) {
                    if (result.getAmount() != input.getAmount()) { //Fluid was partially consumed
                        notConsumed.add(Utils.ca(input.getAmount() - result.getAmount(), input));
                    }
                } else {
                    notConsumed.add(input); //Fluid not present in input tanks
                }
            }
        }
        return notConsumed;
    }

    public FluidStack[] exportAndReturnOutputs(FluidStack... outputs) {
        if (!this.tanks.containsKey(FluidDirection.OUTPUT)) {
            return new FluidStack[0];
        }
        List<FluidStack> notExported = new ObjectArrayList<>();
        int result;
        for (int i = 0; i < outputs.length; i++) {
            result = fill(outputs[i], EXECUTE);
            if (result == 0) notExported.add(outputs[i]); //Valid space was not found
            else outputs[i] = Utils.ca(result, outputs[i]); //Fluid was partially exported
        }
        return notExported.toArray(new FluidStack[0]);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (this.tanks.containsKey(FluidDirection.INPUT)) {
            builder.append("Inputs:\n");
            for (int i = 0; i < getInputTanks().getTanks(); i++) {
                FluidStack stack = getInputTanks().getFluidInTank(i);
                if (stack != FluidStack.EMPTY) {
                    builder.append(stack.getFluid().getRegistryName()).append(" - ").append(stack.getAmount());
                    if (i != getInputTanks().getTanks() - 1) {
                        builder.append("\n");
                    }
                }
            }
        }
        if (this.tanks.containsKey(FluidDirection.OUTPUT)) {
            builder.append("Outputs:\n");
            for (int i = 0; i < getOutputTanks().getTanks(); i++) {
                FluidStack stack = getOutputTanks().getFluidInTank(i);
                if (stack != FluidStack.EMPTY) {
                    builder.append(stack.getFluid().getRegistryName()).append(" - ").append(stack.getAmount());
                    if (i != getOutputTanks().getTanks() - 1) {
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
        return fill((FluidStack) data.getStack(), simulate ? SIMULATE : EXECUTE);
    }

    @Nullable
    @Override
    public FluidData<FluidStack> extract(int tank, int amount, boolean simulate) {
        if (!this.tanks.containsKey(FluidDirection.OUTPUT)) {
            return null;
        }
        FluidTank t = getOutputTanks().getTank(tank);
        /*
        if (fluid.getAmount() > amount) {
            fluid = fluid.copy();
            fluid.setAmount(amount);
        }
         */
        FluidStack drained = t.drain(amount, simulate ? SIMULATE : EXECUTE);
        return drained.isEmpty() ? null : new FluidData<>(drained, drained.getAmount(), drained.getFluid().getAttributes().getTemperature(), drained.getFluid().getAttributes().isGaseous());
    }

    public void deserializeNBT(CompoundNBT nbt) {
        tanks.forEach((k,v) -> {
            v.deserializeNBT(nbt.getList(k.name(),Constants.NBT.TAG_COMPOUND));
        });
    }

    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        tanks.forEach((k,v) -> {
            nbt.put(k.name(), v.serializeNBT());
        });
        return nbt;
    }

    // TODO needed? Weird semantics
    @Override
    public int getAvailableTank(Dir direction) {
        return 0;
        // return outputWrapper.getAvailableTank(direction.getIndex());
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
        return this.tanks.containsKey(FluidDirection.OUTPUT);
    }

    @Override
    public boolean canInput() {
        return this.tanks.containsKey(FluidDirection.INPUT);
    }

    @Override
    public boolean canOutput(Dir direction) {
        return tile.getOutputFacing().getIndex() == direction.getIndex();
    }

    // TODO needed? Weird semantics
    @Override
    public boolean canInput(Object fluid, Dir direction) {
        if (tile.getFacing().getIndex() == direction.getIndex()) return false;
        if (/*TODO: Can input into output* ||*/tile.getOutputFacing().getIndex() == direction.getIndex()) return false;
        return true;
        // return inputWrapper.isFluidAvailable(fluid, direction.getIndex()) && inputWrapper.getFirstValidTank(fluid) != -1;
    }

    @Override
    public boolean connects(Dir direction) {
        return tile.getFacing().getIndex() != direction.getIndex()/* && !(tile.getCover(Ref.DIRECTIONS[direction.getIndex()]) instanceof CoverMaterial)*/;
    }

    @Override
    public void registerNet() {
        if (tile.getWorld() == null) return;
        Tesseract.FLUID.registerNode(tile.getDimension(), tile.getPos().toLong(), this);
    }

    @Override
    public void deregisterNet() {
        if (tile.getWorld() == null) return;
        Tesseract.FLUID.remove(tile.getDimension(), tile.getPos().toLong());
    }

    public enum FluidDirection {

        INPUT,
        OUTPUT

    }

}
