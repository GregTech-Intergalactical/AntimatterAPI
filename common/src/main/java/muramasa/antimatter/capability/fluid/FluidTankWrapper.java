package muramasa.antimatter.capability.fluid;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import tesseract.TesseractGraphWrappers;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class FluidTankWrapper implements IFluidHandler {

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
    public int getTanks() {
        return tanks.length;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return tanks[tank].getFluid();
    }

    public void setFluidToTank(int tank, FluidStack stack) {
        tanks[tank].setFluid(stack);
    }

    @Nonnull
    public CompoundTag writeToNBT(int tank, CompoundTag nbt) {
        tanks[tank].writeToNBT(nbt);
        return nbt;
    }

    @Override
    public long getTankCapacityInDroplets(int tank) {
        return tanks[tank].getCapacityInDroplets();
    }

    @Override
    public int getTankCapacity(int tank) {
        return (int)(getTankCapacityInDroplets(tank) / TesseractGraphWrappers.dropletMultiplier);
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return tanks[tank].isFluidValid(stack);
    }

    @Override
    public long fillDroplets(FluidStack resource, FluidAction action) {
        int tank = getFirstValidTank(resource.getFluid());
        return tank != -1 ? getTank(tank).fillDroplets(resource, action) : 0;
    }

    @Override
    public int fill(FluidStack stack, FluidAction action) {
        return (int)(fillDroplets(stack, action) / TesseractGraphWrappers.dropletMultiplier);
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        FluidTank tank = findFluidInTanks(resource);
        return tank != null ? tank.drain(resource, action) : FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    @Deprecated
    public FluidStack drain(long maxDrain, FluidAction action) {
        FluidTank tank = getFirstValidTank();
        return tank != null ? tank.drain(maxDrain, action) : FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(int amount, FluidAction action) {
        return drain((long)amount * TesseractGraphWrappers.dropletMultiplier, action);
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
        for (int i = 0; i < getTanks(); i++) {
            FluidStack stack = getFluidInTank(i);
            if (stack.isEmpty()) {
                tank = i;
            } else {
                if (stack.getFluid().equals(fluid) && getTankCapacityInDroplets(i) > stack.getRealAmount()) {
                    return i;
                }
            }
        }
        return tank;
    }

    public int getAvailableTank(int dir) {
        Set<?> set = filter[dir];
        if (set.isEmpty()) {
            for (int i = 0; i < getTanks(); i++) {
                FluidStack stack = getFluidInTank(i);
                if (!stack.isEmpty()) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < getTanks(); i++) {
                FluidStack stack = getFluidInTank(i);
                if (!stack.isEmpty() && set.contains(stack.getFluid())) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Nullable
    private FluidTank findFluidInTanks(FluidStack fluid) {
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
}
