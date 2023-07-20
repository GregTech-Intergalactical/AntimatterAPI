package muramasa.antimatter.capability.fluid;

import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.base.FluidSnapshot;
import earth.terrarium.botarium.common.fluid.impl.SimpleFluidSnapshot;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.tile.TileEntityBase;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;
import tesseract.TesseractGraphWrappers;
import tesseract.api.fluid.FluidContainerHandler;
import tesseract.api.fluid.IFluidNode;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;
import java.util.stream.LongStream;

/**
 * Array of multiple instances of FluidTank
 */
public class FluidTanks implements FluidContainer, FluidContainerHandler {

    public static <T extends TileEntityBase<T> & IMachineHandler> FluidTanks create(T tile, ContentEvent contentEvent,
                                                                                    UnaryOperator<Builder<T>> builder) {
        return builder.apply(new Builder<>(tile, contentEvent)).build();
    }

    private final FluidTank[] tanks;
    private final long totalCapacity;

    public FluidTanks(int tanks, long tankAmountInMB) {
        this.tanks = new FluidTank[tanks];
        for (int i = 0; i < tanks; i++) {
            this.tanks[i] = new FluidTank(tankAmountInMB * TesseractGraphWrappers.dropletMultiplier);
        }
        this.totalCapacity = tanks * tankAmountInMB * TesseractGraphWrappers.dropletMultiplier;
    }

    public FluidTanks(int tanks, long tankAmountInMB, Predicate<FluidHolder> validator) {
        this.tanks = new FluidTank[tanks];
        for (int i = 0; i < tanks; i++) {
            this.tanks[i] = new FluidTank(tankAmountInMB * TesseractGraphWrappers.dropletMultiplier, validator);
        }
        this.totalCapacity = tanks * tankAmountInMB * TesseractGraphWrappers.dropletMultiplier;
    }

    public FluidTanks(long... tankAmountsInMB) {
        this.tanks = new FluidTank[tankAmountsInMB.length];
        for (int i = 0; i < this.tanks.length; i++) {
            this.tanks[i] = new FluidTank(tankAmountsInMB[i] * TesseractGraphWrappers.dropletMultiplier);
        }
        this.totalCapacity = LongStream.of(tankAmountsInMB).sum() * TesseractGraphWrappers.dropletMultiplier;
    }

    public FluidTanks(Collection<FluidTank> tanks) {
        this.tanks = tanks.toArray(new FluidTank[0]);
        this.totalCapacity = tanks.stream().mapToLong(FluidTank::getCapacity).sum();
    }

    public FluidTanks(@NotNull FluidTank... tanks) {
        this.tanks = tanks;
        this.totalCapacity = Arrays.stream(tanks).mapToLong(FluidTank::getCapacity).sum();
    }

    public int getFirstAvailableTank(FluidHolder stack, boolean drain) {
        int firstAvailable = -1;
        for (int i = 0; i < tanks.length; i++) {
            FluidTank tank = this.tanks[i];
            if (tank.isEmpty()) {
                firstAvailable = i;
                break;
            } else if ((drain && !tank.extractFluid(stack, true).isEmpty())
                    || (!drain && tank.insertFluid(stack, true) != 0)) {
                return i;
            }
        }
        return firstAvailable;
    }

    public FluidTank getTank(int tank) {
        return this.tanks[tank];
    }

    public List<FluidHolder> getFluids() {
        return Arrays.stream(this.tanks).map(FluidTank::getFluid).toList();
    }

    public FluidTank[] getBackingTanks() {
        return tanks;
    }


    @Override
    public int getSize() {
        return tanks.length;
    }

    @Override
    public boolean isEmpty() {
        boolean hasFluid = false;
        for (int i = 0; i < getSize(); i++) {
            if (!getTank(i).isEmpty()){
                hasFluid = true;
            }
        }
        return !hasFluid;
    }

    @Override
    public FluidContainer copy() {
        return null;
    }

    @Nonnull
    public FluidHolder getFluidInTank(int tank) {
        return this.tanks[tank].getFluid();
    }

    @Override
    public long getTankCapacity(int tank) {
        return this.tanks[tank].getCapacity();
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
        return true;
    }

    @Override
    public boolean allowsExtraction() {
        return true;
    }

    @Override
    public FluidSnapshot createSnapshot() {
        return new SimpleFluidSnapshot(this);
    }

    public boolean isFluidValid(int tank, @Nonnull FluidHolder stack) {
        return this.tanks[tank].isFluidValid(stack);
    }

    public long getTotalFluidAmount() {
        long amount = 0;
        for (FluidTank tank : tanks) {
            amount += tank.getFluid().getFluidAmount();
        }
        return amount;
    }

    public long getTotalCapacity() {
        return this.totalCapacity;
    }

    @Override
    public FluidContainer getFluidContainer() {
        return this;
    }

    @Override
    public long insertFluid(FluidHolder fluid, boolean simulate) {
        for (int i = 0; i < tanks.length; i++) {
            long fill = getTank(i).insertFluid(fluid, simulate);
            if (fill > 0)
                return fill;
        }
        return 0;
    }

    @Override
    public FluidHolder extractFluid(FluidHolder fluid, boolean simulate) {
        for (int i = 0; i < tanks.length; i++) {
            FluidHolder drain = getTank(i).extractFluid(fluid, simulate);
            if (!drain.isEmpty())
                return drain;
        }
        return FluidHooks.emptyFluid();
    }

    @Override
    public void setFluid(int slot, FluidHolder fluid) {
        tanks[slot].setFluid(0, fluid);
    }

    public ListTag serializeNBT() {
        ListTag nbt = new ListTag();
        Arrays.stream(tanks).forEach(t -> nbt.add(t.getFluid().serialize()));
        return nbt;
    }

    public void deserializeNBT(ListTag nbt) {
        int i = 0;
        for (Tag tank : nbt) {
            if (tank instanceof CompoundTag) {
                CompoundTag cnbt = (CompoundTag) tank;
                if (i > tanks.length - 1)
                    break;
                tanks[i++].setFluid(0, FluidHooks.fluidFromCompound(cnbt));
            }
        }
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
        for (FluidTank tank : tanks) {
            tank.clearContent();
        }
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

        public Builder<T> tank(Predicate<FluidHolder> validator, int amount) {
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
