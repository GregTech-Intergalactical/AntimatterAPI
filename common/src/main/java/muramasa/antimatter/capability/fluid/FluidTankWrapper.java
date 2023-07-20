package muramasa.antimatter.capability.fluid;

import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.base.FluidSnapshot;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;
import tesseract.api.fluid.IFluidNode;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class FluidTankWrapper implements IFluidNode {

    // TODO: Add black/white lister filter mode
    private FluidTank[] tanks;
    private boolean dirty = false;
    private Set<Fluid>[] filter = new Set[]{new ObjectOpenHashSet<>(), new ObjectOpenHashSet<>(), new ObjectOpenHashSet<>(), new ObjectOpenHashSet<>(), new ObjectOpenHashSet<>(), new ObjectOpenHashSet<>()};

    public FluidTankWrapper(TileEntityMachine machine, int count, int capacity, ContentEvent event) {
        tanks = new FluidTank[count];
        for (int i = 0; i < count; i++) {
            tanks[i] = new FluidTank(capacity) {
                @Override
                protected void onContentsChanged() {
                    dirty = true;
                    machine.onMachineEvent(event);
                }
            };
        }
    }

    @Nonnull
    public FluidTank getTank(int tank) {
        return tanks[tank];
    }

    @Override
    public int getSize() {
        return tanks.length;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public FluidContainer copy() {
        return this;
    }

    @Nonnull
    @Override
    public FluidHolder getFluidInTank(int tank) {
        return tanks[tank].getFluid();
    }

    public void setFluid(int tank, FluidHolder stack) {
        tanks[tank].setFluid(0, stack);
    }

    @Override
    public List<FluidHolder> getFluids() {
        return Arrays.stream(this.tanks).map(FluidTank::getFluid).toList();
    }

    @Nonnull
    public CompoundTag writeToNBT(int tank, CompoundTag nbt) {
        tanks[tank].serialize(nbt);
        return nbt;
    }

    @Override
    public long getTankCapacity(int tank) {
        return tanks[tank].getCapacity();
    }

    @Override
    public void fromContainer(FluidContainer container) {

    }

    @Override
    public long extractFromSlot(FluidHolder fluidHolder, FluidHolder toInsert, Runnable snapshot) {
        return 0;
    }

    @Override
    public boolean allowsInsertion() {
        return false;
    }

    @Override
    public boolean allowsExtraction() {
        return false;
    }

    @Override
    public FluidSnapshot createSnapshot() {
        return null;
    }

    @Override
    public int getPriority(Direction direction) {
        return 0;
    }

    @Override
    public boolean canInput(Direction direction) {
        return false;
    }

    @Override
    public boolean canOutput(Direction direction) {
        return false;
    }

    @Override
    public boolean canInput(FluidHolder fluid, Direction direction) {
        return false;
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidHolder stack) {
        return tanks[tank].isFluidValid(stack);
    }

    @Override
    public long insertFluid(FluidHolder fluid, boolean simulate) {
        int tank = getFirstValidTank(fluid.getFluid());
        return tank != -1 ? getTank(tank).insertFluid(fluid, simulate) : 0;
    }

    @Override
    public FluidHolder extractFluid(FluidHolder fluid, boolean simulate) {
        FluidTank tank = findFluidInTanks(fluid);
        return tank != null ? tank.extractFluid(fluid, simulate) : FluidHooks.emptyFluid();
    }

    public boolean isDirty() {
        return dirty;
    }

    public boolean isFluidAvailable(Object fluid, int dir) {
        Set<?> filtered = filter[dir];
        return filtered.isEmpty() || filtered.contains(fluid);
    }

    // Fast way to find available tank for fluid
    public int getFirstValidTank(Object fluid) {
        int tank = -1;
        for (int i = 0; i < getSize(); i++) {
            FluidHolder stack = getFluidInTank(i);
            if (stack.isEmpty()) {
                tank = i;
            } else {
                if (stack.getFluid().equals(fluid) && getTankCapacity(i) > stack.getFluidAmount()) {
                    return i;
                }
            }
        }
        return tank;
    }

    public int getAvailableTank(int dir) {
        Set<?> set = filter[dir];
        if (set.isEmpty()) {
            for (int i = 0; i < getSize(); i++) {
                FluidHolder stack = getFluidInTank(i);
                if (!stack.isEmpty()) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < getSize(); i++) {
                FluidHolder stack = getFluidInTank(i);
                if (!stack.isEmpty() && set.contains(stack.getFluid())) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Nullable
    private FluidTank findFluidInTanks(FluidHolder fluid) {
        for (FluidTank tank : tanks) {
            if (!tank.isEmpty() && Utils.equals(tank.getFluid(), fluid)) {
                return tank;
            }
        }
        return null;
    }

    @Nullable
    private FluidTank getFirstValidTank() {
        for (FluidTank tank : tanks) {
            if (!tank.isEmpty()) {
                return tank;
            }
        }
        return null;
    }

    @Nullable
    private FluidTank getFirstEmptyTank() {
        for (FluidTank tank : tanks) {
            if (!tank.isEmpty()) {
                return tank;
            }
        }
        return null;
    }

    @Override
    public void deserialize(CompoundTag nbt) {

    }

    @Override
    public CompoundTag serialize(CompoundTag nbt) {
        return null;
    }

    @Override
    public void clearContent() {

    }
}
