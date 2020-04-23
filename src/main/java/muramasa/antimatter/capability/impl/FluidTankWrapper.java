package muramasa.antimatter.capability.impl;
;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;

public class FluidTankWrapper implements IFluidHandler {

    private FluidTank[] tanks;
    private boolean dirty = false;
    private Map<Dir, ObjectSet<?>> filter = new EnumMap<>(Dir.class);

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

        for (Dir direction : Dir.VALUES) {
            filter.put(direction, new ObjectLinkedOpenHashSet<>(count));
        }
    }

//    @Override
//    public IFluidTankProperties[] getTankProperties() {
//        IFluidTankProperties[] properties = new IFluidTankProperties[tanks.length];
//        for (int i = 0; i < tanks.length; i++) {
//            properties[i] = new FluidTankProperties(tanks[i].getFluid(), tanks[i].getCapacity(), tanks[i].canFill(), tanks[i].canDrain());
//        }
//        return properties;
//    }

    public int setFirstEmptyTank(FluidStack fluid) {
        FluidTank tank = getFirstEmptyTank();
        if (tank != null) {
            tank.setFluid(fluid);
            return fluid.getAmount();
        }
        return 0;
    }

    public FluidTank getFirstEmptyTank() {
        for (FluidTank tank : tanks) {
            if (!tank.isEmpty()) return tank;
        }
        return null;
    }

    @Nullable
    public FluidTank getAvailableTank(@Nonnull Dir direction) {
        ObjectSet<?> set = filter.get(direction);
        if (set.isEmpty()) return getFirstValidTank();
        for (FluidTank tank : tanks) {
            if (!tank.isEmpty() && set.contains(tank.getFluid().getFluid())) return tank;
        }
        return null;
    }

    @Nullable
    public FluidTank getFirstValidTank() {
        for (FluidTank tank : tanks) {
            if (!tank.isEmpty()) return tank;
        }
        return null;
    }

    @Nullable
    public FluidTank findFluidInTanks(FluidStack fluid) {
        for (FluidTank tank : tanks) {
            if (!tank.isEmpty() && Utils.equals(tank.getFluid(), fluid)) return tank;
        }
        return null;
    }

    /* Looks for Fluid */
    @Nullable
    public FluidTank findFluidInTanks(Object fluid) {
        for (FluidTank tank : tanks) {
            if (!tank.isEmpty() && tank.getFluid().getFluid().equals(fluid)) return tank;
        }
        return null;
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
    public CompoundNBT writeToNBT(int tank, CompoundNBT nbt) {
        tanks[tank].writeToNBT(nbt);
        return nbt;
    }

    @Override
    public int getTankCapacity(int tank) {
        return tanks[tank].getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return tanks[tank].isFluidValid(stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        FluidTank tank = findFluidInTanks(resource);
        if (tank != null) {
            return tank.fill(resource, action);
        } else {
            tank = getFirstEmptyTank();
            if (tank != null) return tank.fill(resource, action);
        }
        return 0;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        FluidTank tank = findFluidInTanks(resource);
        if (tank != null) return tank.drain(resource, action);
        return FluidStack.EMPTY;
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        FluidTank tank = getFirstValidTank();
        if (tank != null) return tank.drain(maxDrain, action);
        return FluidStack.EMPTY;
    }

    public boolean isDirty() {
        return dirty;
    }

    public boolean isFluidAvailable(@Nonnull Object fluid, @Nonnull Dir direction) {
        ObjectSet<?> filtered = filter.get(direction);
        return filtered.isEmpty() || filtered.contains(fluid);
    }
}
