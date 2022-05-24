package muramasa.antimatter.capability.fluid;

import com.sun.jna.platform.win32.COM.IStream;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.tile.TileEntityBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
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
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * Array of multiple instances of FluidTank
 */
public class FluidTanks implements IFluidHandler {

    public static <T extends TileEntityBase<T> & IMachineHandler> FluidTanks create(T tile, ContentEvent contentEvent,
                                                                                    UnaryOperator<Builder<T>> builder) {
        return builder.apply(new Builder<>(tile, contentEvent)).build();
    }

    private final FluidTank[] tanks;
    private final long totalCapacity;

    public FluidTanks(int tanks, long tankAmount) {
        this.tanks = new FluidTank[tanks];
        for (int i = 0; i < tanks; i++) {
            this.tanks[i] = new FluidTank(tankAmount);
        }
        this.totalCapacity = tanks * tankAmount;
    }

    public FluidTanks(int tanks, long tankAmount, Predicate<FluidStack> validator) {
        this.tanks = new FluidTank[tanks];
        for (int i = 0; i < tanks; i++) {
            this.tanks[i] = new FluidTank(tankAmount, validator);
        }
        this.totalCapacity = tanks * tankAmount;
    }

    public FluidTanks(long... tankAmounts) {
        this.tanks = new FluidTank[tankAmounts.length];
        for (int i = 0; i < this.tanks.length; i++) {
            this.tanks[i] = new FluidTank(tankAmounts[i]);
        }
        this.totalCapacity = LongStream.of(tankAmounts).sum();
    }

    public FluidTanks(Collection<FluidTank> tanks) {
        this.tanks = tanks.toArray(new FluidTank[0]);
        this.totalCapacity = tanks.stream().mapToLong(FluidTank::getCapacity).sum();
    }

    public FluidTanks(FluidTank... tanks) {
        this.tanks = tanks;
        this.totalCapacity = Arrays.stream(tanks).mapToLong(FluidTank::getCapacity).sum();
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
    public long getTankCapacity(int tank) {
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

    public long getTotalCapacity() {
        return this.totalCapacity;
    }

    @Override
    public long fill(FluidStack stack, FluidAction action) {
        for (int i = 0; i < tanks.length; i++) {
            long fill = getTank(i).fill(stack, action);
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

    public ListTag serializeNBT() {
        ListTag nbt = new ListTag();
        Arrays.stream(tanks).forEach(t -> nbt.add(t.getFluid().writeToNBT(new CompoundTag())));
        return nbt;
    }

    public void deserializeNBT(ListTag nbt) {
        int i = 0;
        for (Tag tank : nbt) {
            if (tank instanceof CompoundTag) {
                CompoundTag cnbt = (CompoundTag) tank;
                if (i > tanks.length - 1)
                    break;
                tanks[i++].setFluid(FluidStack.loadFluidStackFromNBT(cnbt));
            }
        }
    }

    @Nonnull
    @Override
    public FluidStack drain(long maxDrain, FluidAction action) {
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
