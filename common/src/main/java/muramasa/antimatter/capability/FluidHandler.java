package muramasa.antimatter.capability;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.capability.fluid.FluidTanks;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.tile.TileEntityBase;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import tesseract.FluidPlatformUtils;
import tesseract.Tesseract;
import tesseract.api.fluid.IFluidNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.EnumMap;

public abstract class FluidHandler<T extends TileEntityBase & IMachineHandler> implements IMachineHandler, IFluidNode {
    protected final T tile;
    protected final EnumMap<FluidDirection, FluidTanks> tanks = new EnumMap<>(FluidDirection.class);
    protected int capacity, pressure;

    /**
     * For GUI
     **/
    protected boolean dirty;

    public FluidHandler(T tile, int capacity, int pressure, int inputCount, int outputCount) {
        this.tile = tile;
        this.capacity = capacity;
        this.pressure = pressure;
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

    public void onRemove() {

    }

    public void onReset() {

    }

    public void onUpdate() {

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
        FluidTanks tanks = getTanks(tank);
        if (tanks == null)
            return null;
        return tanks.getTank(offsetTank(tank));
    }

    protected FluidTanks getTanks(int tank) {
        FluidTanks input = getInputTanks();
        FluidTanks output = getOutputTanks();
        boolean hasInput = input != null;
        boolean hasOutput = output != null;
        if (hasInput && !hasOutput) {
            return input;
        } else if (!hasInput && hasOutput) {
            return output;
        } else if (!hasOutput && !hasOutput) {
            return null;
        }

        boolean isOutput = tank >= input.getTanks();

        if (!isOutput) {
            return input;
        } else {
            return output;
        }
    }

    protected int offsetTank(int tank) {
        FluidTanks in = getInputTanks();
        if (in != null && tank >= getInputTanks().getTanks())
            return tank - in.getTanks();
        return tank;
    }

    @Override
    public long getTankCapacityInDroplets(int tank) {
        return getTanks(tank).getTankCapacityInDroplets(offsetTank(tank));
    }

    @Override
    public int getTankCapacity(int tank){
        return (int) (getTankCapacityInDroplets(tank) / Tesseract.dropletMultiplier);
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return getTank(tank).isFluidValid(stack);
    }

    public FluidTanks getAllTanks() {
        ObjectArrayList<FluidTank> list = new ObjectArrayList<>();
        if (getInputTanks() != null)
            list.addAll(Arrays.asList(getInputTanks().getBackingTanks()));
        if (getOutputTanks() != null)
            list.addAll(Arrays.asList(getOutputTanks().getBackingTanks()));
        return new FluidTanks(list);
    }

    public long fillLong(FluidStack stack, IFluidHandler.FluidAction action) {
        FluidTanks input = getInputTanks();
        if (input != null && !empty(input)) {
            return getInputTanks().fillDroplets(stack, action);
        }
        return 0;
    }

    @Override
    public int fill(FluidStack stack, FluidAction action){
        return (int) (this.fillDroplets(stack, action) / Tesseract.dropletMultiplier);
    }

    protected boolean empty(FluidTanks tank) {
        return tank.getTanks() == 0;
    }

    public long fillOutput(FluidStack stack, IFluidHandler.FluidAction action) {
        if (getOutputTanks() != null) {
            return getOutputTanks().fillDroplets(stack, action);
        }
        return 0;
    }

    @Nonnull
    public FluidStack drain(FluidStack stack, IFluidHandler.FluidAction action) {
        if (getOutputTanks() != null) {
            return getOutputTanks().drain(stack, action);
        }
        return FluidStack.EMPTY;
    }

    /**
     * Drains from the input tanks rather than output tanks. Useful for recipes.
     *
     * @param stack  stack to drain.
     * @param action execute/simulate
     * @return the drained stack
     */
    @Nonnull
    public FluidStack drainInput(FluidStack stack, IFluidHandler.FluidAction action) {
        if (getInputTanks() != null) {
            return getInputTanks().drain(stack, action);
        }
        return FluidStack.EMPTY;
    }

    public FluidStack drainInput(long maxDrain, IFluidHandler.FluidAction action) {
        if (getInputTanks() == null)
            return FluidStack.EMPTY;
        return getInputTanks().drain(maxDrain, action);
    }

    @Nonnull
    @Override
    public FluidStack drain(long maxDrain, IFluidHandler.FluidAction action) {
        if (getOutputTanks() != null) {
            return getOutputTanks().drain(maxDrain, action);
        }
        return FluidStack.EMPTY;
    }

    @Override
    public FluidStack drain(int amount, FluidAction action) {
        return drain((long) amount * Tesseract.dropletMultiplier, action);
    }

    protected boolean checkValidFluid(FluidStack fluid) {
        return true;
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
    }

    /**
     * Helpers
     **/
    @Nonnull
    public FluidStack[] getInputs() {
        FluidTanks tanks = getInputTanks();
        return tanks == null ? new FluidStack[0] : tanks.getFluids();
    }

    public FluidStack[] getOutputs() {
        FluidTanks tanks = getOutputTanks();
        return tanks == null ? new FluidStack[0] : tanks.getFluids();
    }

    @Nullable
    public FluidTanks getInputTanks() {
        return this.tanks.get(FluidDirection.INPUT);
    }

    @Nullable
    public FluidTanks getOutputTanks() {
        return this.tanks.get(FluidDirection.OUTPUT);
    }

    @Override
    public boolean canOutput() {
        return getOutputTanks() != null;
    }

    @Override
    public boolean canInput() {
        return getInputTanks() != null;
    }

    @Override
    public boolean canInput(Direction direction) {
        return canInput();
    }

    @Override
    public boolean canInput(FluidStack fluid, Direction direction) {
        return true;
    }

    @Override
    public boolean canOutput(Direction direction) {
        return canOutput();
    }

    @Override
    public int getPriority(Direction direction) {
        return 0;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        if (this.tanks.containsKey(FluidDirection.INPUT)) {
            builder.append("Inputs:\n");
            for (int i = 0; i < getInputTanks().getTanks(); i++) {
                FluidStack stack = getInputTanks().getFluidInTank(i);
                if (stack != FluidStack.EMPTY) {
                    builder.append(FluidPlatformUtils.getFluidId(stack.getFluid())).append(" - ").append(stack.getAmount());
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
                    builder.append(FluidPlatformUtils.getFluidId(stack.getFluid())).append(" - ").append(stack.getAmount());
                    if (i != getOutputTanks().getTanks() - 1) {
                        builder.append("\n");
                    }
                }
            }
        }
        return builder.toString();
    }

    public void deserializeNBT(CompoundTag nbt) {
        tanks.forEach((k, v) -> {
            if (!nbt.contains(k.toString()))
                return;
            v.deserializeNBT(nbt.getList(k.toString(), Tag.TAG_COMPOUND));
        });
    }

    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        tanks.forEach((k, v) -> {
            nbt.put(k.name(), v.serializeNBT());
        });
        return nbt;
    }

    public enum FluidDirection {
        INPUT,
        OUTPUT
    }
}
