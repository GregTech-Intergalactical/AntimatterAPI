package muramasa.antimatter.capability;

import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.base.FluidSnapshot;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import muramasa.antimatter.capability.fluid.FluidTank;
import muramasa.antimatter.capability.fluid.FluidTanks;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.blockentity.BlockEntityBase;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tesseract.FluidPlatformUtils;
import tesseract.api.fluid.IFluidNode;

import java.util.*;

public abstract class FluidHandler<T extends BlockEntityBase & IMachineHandler> implements IMachineHandler, IFluidNode {
    @Getter
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
            tanks.put(FluidDirection.INPUT, FluidTanks.create(tile, SlotType.FL_IN, b -> {
                for (int i = 0; i < inputCount; i++) {
                    b.tank(capacity);
                }
                return b;
            }));
        }
        if (outputCount > 0) {
            tanks.put(FluidDirection.OUTPUT, FluidTanks.create(tile, SlotType.FL_OUT, b -> {
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

    @Override
    public int getSize() {
        return this.tanks.values().stream().mapToInt(FluidTanks::getSize).sum();
    }

    @NotNull
    @Override
    public FluidHolder getFluidInTank(int tank) {
        return getTank(tank).getStoredFluid();
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

        boolean isOutput = tank >= input.getSize();

        if (!isOutput) {
            return input;
        } else {
            return output;
        }
    }

    public int offsetTank(int tank) {
        FluidTanks in = getInputTanks();
        if (in != null && tank >= getInputTanks().getSize())
            return tank - in.getSize();
        return tank;
    }

    @Override
    public long getTankCapacity(int tank) {
        return getTanks(tank).getTankCapacity(offsetTank(tank));
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidHolder stack) {
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

    @Override
    public long insertFluid(FluidHolder fluid, boolean simulate) {
        FluidTanks input = getInputTanks();
        if (input != null && !empty(input)) {
            return getInputTanks().insertFluid(fluid, simulate);
        }
        return 0;
    }

    protected boolean empty(FluidTanks tank) {
        return tank.getSize() == 0;
    }

    public long fillOutput(FluidHolder stack, boolean simulate) {
        if (getOutputTanks() != null) {
            return getOutputTanks().insertFluid(stack, simulate);
        }
        return 0;
    }

    @Override
    public FluidHolder extractFluid(FluidHolder fluid, boolean simulate) {
        if (getOutputTanks() != null) {
            return getOutputTanks().extractFluid(fluid, simulate);
        }
        return FluidHooks.emptyFluid();
    }

    @Override
    public FluidHolder extractFluid(long toExtract, boolean simulate) {
        if (getOutputTanks() != null){
            for (int i = 0; i < getOutputTanks().getSize(); i++) {
                FluidHolder toExtractFluid = getOutputTanks().getFluidInTank(i);
                FluidHolder fluid = getOutputTanks().extractFluid(toExtractFluid.copyWithAmount(Math.min(toExtractFluid.getFluidAmount(), toExtract)), simulate);
                if (!fluid.isEmpty()) return fluid;
            }
        }
        return FluidHooks.emptyFluid();
    }

    /**
     * Drains from the input tanks rather than output tanks. Useful for recipes.
     *
     * @param stack  stack to drain.
     * @param simulate execute/simulate
     * @return the drained stack
     */
    @NotNull
    @Override
    public FluidHolder drainInput(FluidHolder stack, boolean simulate) {
        if (getInputTanks() != null) {
            return getInputTanks().extractFluid(stack, simulate);
        }
        return FluidHooks.emptyFluid();
    }

    public FluidHolder drainInput(long maxDrain, boolean simulate) {
        if (getInputTanks() != null){
            for (int i = 0; i < getInputTanks().getSize(); i++) {
                FluidHolder fluid = getInputTanks().extractFluid(getInputTanks().getFluidInTank(i), simulate);
                if (!fluid.isEmpty()) return fluid;
            }
        }
        return FluidHooks.emptyFluid();
    }

    @Override
    public void setFluid(int slot, FluidHolder fluid) {
        getTank(slot).setFluid(0, fluid);
    }

    protected boolean checkValidFluid(FluidHolder fluid) {
        return true;
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
    }

    /**
     * Helpers
     **/
    @NotNull
    public FluidHolder[] getInputs() {
        FluidTanks tanks = getInputTanks();
        return tanks == null ? new FluidHolder[0] : tanks.getFluids().toArray(FluidHolder[]::new);
    }

    public FluidHolder[] getOutputs() {
        FluidTanks tanks = getOutputTanks();
        return tanks == null ? new FluidHolder[0] : tanks.getFluids().toArray(FluidHolder[]::new);
    }

    @Override
    public List<FluidHolder> getFluids() {
        List<FluidHolder> list = new ArrayList<>();
        list.addAll(Arrays.asList(getInputs()));
        list.addAll(Arrays.asList(getOutputs()));
        return list;
    }

    @Override
    public boolean isEmpty() {
        return getAllTanks().isEmpty();
    }

    @Override
    public void clearContent() {
        getAllTanks().clearContent();
    }

    @Override
    public FluidSnapshot createSnapshot() {
        return new FluidHandlerSnapshot(this);
    }

    @Override
    public long extractFromSlot(FluidHolder fluidHolder, FluidHolder toInsert, Runnable snapshot) {
        if (Objects.equals(fluidHolder.getCompound(), toInsert.getCompound()) && fluidHolder.getFluid().isSame(toInsert.getFluid())) {
            long extracted = Mth.clamp(toInsert.getFluidAmount(), 0, fluidHolder.getFluidAmount());
            snapshot.run();
            fluidHolder.setAmount(fluidHolder.getFluidAmount() - extracted);
            if(fluidHolder.getFluidAmount() == 0) fluidHolder.setFluid(Fluids.EMPTY);
            return extracted;
        }
        return 0;
    }

    @Override
    public void fromContainer(FluidContainer container) {

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
    public boolean allowsExtraction() {
        return getOutputTanks() != null;
    }

    @Override
    public boolean allowsInsertion() {
        return getInputTanks() != null;
    }

    @Override
    public boolean canInput(Direction direction) {
        return allowsInsertion();
    }

    @Override
    public boolean canInput(FluidHolder fluid, Direction direction) {
        return true;
    }

    @Override
    public boolean canOutput(Direction direction) {
        return allowsExtraction();
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
            for (int i = 0; i < getInputTanks().getSize(); i++) {
                FluidHolder stack = getInputTanks().getFluidInTank(i);
                if (!stack.isEmpty()) {
                    builder.append(FluidPlatformUtils.INSTANCE.getFluidId(stack.getFluid())).append(" - ").append(stack.getFluidAmount());
                    if (i != getInputTanks().getSize() - 1) {
                        builder.append("\n");
                    }
                }
            }
        }
        if (this.tanks.containsKey(FluidDirection.OUTPUT)) {
            builder.append("Outputs:\n");
            for (int i = 0; i < getOutputTanks().getSize(); i++) {
                FluidHolder stack = getOutputTanks().getFluidInTank(i);
                if (!stack.isEmpty()) {
                    builder.append(FluidPlatformUtils.INSTANCE.getFluidId(stack.getFluid())).append(" - ").append(stack.getFluidAmount());
                    if (i != getOutputTanks().getSize() - 1) {
                        builder.append("\n");
                    }
                }
            }
        }
        return builder.toString();
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        tanks.forEach((k, v) -> {
            if (!nbt.contains(k.toString()))
                return;
            v.deserialize(nbt.getList(k.toString(), Tag.TAG_COMPOUND));
        });
    }

    @Override
    public CompoundTag serialize(CompoundTag nbt) {
        tanks.forEach((k, v) -> {
            nbt.put(k.name(), v.serialize());
        });
        return nbt;
    }

    public enum FluidDirection {
        INPUT,
        OUTPUT
    }
}
