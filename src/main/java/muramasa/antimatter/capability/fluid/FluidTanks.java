package muramasa.antimatter.capability.fluid;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.tile.TileEntityBase;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.IntStream;

/**
 * Array of multiple instances of FluidTank
 */
public class FluidTanks implements IFluidHandler {

    public static <T extends TileEntityBase<T> & IMachineHandler> FluidTanks create(T tile, ContentEvent contentEvent,
            UnaryOperator<Builder<T>> builder) {
        return builder.apply(new Builder<>(tile, contentEvent)).build();
    }

    private final FluidTank[] tanks;
    private final int totalCapacity;

    public FluidTanks(int tanks, int tankAmount) {
        this.tanks = new FluidTank[tanks];
        for (int i = 0; i < tanks; i++) {
            this.tanks[i] = new FluidTank(tankAmount);
        }
        this.totalCapacity = tanks * tankAmount;
    }

    public FluidTanks(int tanks, int tankAmount, Predicate<FluidStack> validator) {
        this.tanks = new FluidTank[tanks];
        for (int i = 0; i < tanks; i++) {
            this.tanks[i] = new FluidTank(tankAmount, validator);
        }
        this.totalCapacity = tanks * tankAmount;
    }

    public FluidTanks(int... tankAmounts) {
        this.tanks = new FluidTank[tankAmounts.length];
        for (int i = 0; i < this.tanks.length; i++) {
            this.tanks[i] = new FluidTank(tankAmounts[i]);
        }
        this.totalCapacity = IntStream.of(tankAmounts).sum();
    }

    public FluidTanks(Collection<FluidTank> tanks) {
        this.tanks = tanks.toArray(new FluidTank[0]);
        this.totalCapacity = tanks.stream().mapToInt(FluidTank::getCapacity).sum();
    }

    public FluidTanks(FluidTank... tanks) {
        this.tanks = tanks;
        this.totalCapacity = Arrays.stream(tanks).mapToInt(FluidTank::getCapacity).sum();
    }

    public int getFirstAvailableTank(FluidStack stack, boolean drain) {
        int firstAvailable = -1;
        for (int i = 0; i < tanks.length; i++) {
            FluidTank tank = this.tanks[i];
            if (tank.isEmpty()) {
                firstAvailable = i;
                break;
            } else if ((drain && !tank.drain(stack, FluidAction.SIMULATE).isEmpty())
                    || (!drain && tank.fill(stack, FluidAction.SIMULATE) != 0)) {
                return i;
            }
        }
        return firstAvailable;
    }

    public FluidTank getTank(int tank) {
        return this.tanks[tank];
    }

    public FluidStack[] getFluids() {
        return Arrays.stream(this.tanks).map(FluidTank::getFluid).toArray(FluidStack[]::new);
    }

    public FluidTank[] getBackingTanks() {
        return tanks;
    }

    @Override
    public int getTanks() {
        return this.tanks.length;
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return this.tanks[tank].getFluid();
    }

    @Override
    public int getTankCapacity(int tank) {
        return this.tanks[tank].getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return this.tanks[tank].isFluidValid(stack);
    }

    public int getTotalFluidAmount() {
        int amount = 0;
        for (FluidTank tank : tanks) {
            amount += tank.getFluidAmount();
        }
        return amount;
    }

    public int getTotalCapacity() {
        return this.totalCapacity;
    }

    @Override
    public int fill(FluidStack stack, FluidAction action) {
        for (int i = 0; i < tanks.length; i++) {
            int fill = getTank(i).fill(stack, action);
            if (fill > 0)
                return fill;
        }
        return 0;
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack stack, FluidAction action) {
        for (int i = 0; i < tanks.length; i++) {
            FluidStack drain = getTank(i).drain(stack, action);
            if (!drain.isEmpty())
                return drain;
        }
        return FluidStack.EMPTY;
    }

    public ListNBT serializeNBT() {
        ListNBT nbt = new ListNBT();
        Arrays.stream(tanks).forEach(t -> nbt.add(t.getFluid().writeToNBT(new CompoundNBT())));
        return nbt;
    }

    public void deserializeNBT(ListNBT nbt) {
        int i = 0;
        for (INBT tank : nbt) {
            if (tank instanceof CompoundNBT) {
                CompoundNBT cnbt = (CompoundNBT) tank;
                if (i > tanks.length - 1)
                    break;
                tanks[i++].setFluid(FluidStack.loadFluidStackFromNBT(cnbt));
            }
        }
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        for (int i = 0; i < getTanks(); i++) {
            FluidTank tank = getTank(i);
            FluidStack stack = tank.drain(maxDrain, action);
            if (!stack.isEmpty()) {
                return stack;
            }
        }
        return FluidStack.EMPTY;
    }

    public static class Builder<T extends TileEntityBase & IMachineHandler> {

        private final T tile;
        private final List<FluidTank> tanks;
        private final ContentEvent contentEvent;

        private Builder(T tile, ContentEvent contentEvent) {
            this.tile = tile;
            this.tanks = new ObjectArrayList<>();
            this.contentEvent = contentEvent;
        }

        public Builder<T> tank(Predicate<FluidStack> validator, int amount) {
            this.tanks.add(new FluidTank(amount, validator) {
                @Override
                protected void onContentsChanged() {
                    tile.onMachineEvent(contentEvent, this.fluid);
                }
            });
            return this;
        }

        public Builder<T> tank(int amount) {
            this.tanks.add(new FluidTank(amount) {
                @Override
                protected void onContentsChanged() {
                    tile.onMachineEvent(contentEvent, this.fluid);
                }
            });
            return this;
        }

        private FluidTanks build() {
            return new FluidTanks(this.tanks);
        }

    }

}
