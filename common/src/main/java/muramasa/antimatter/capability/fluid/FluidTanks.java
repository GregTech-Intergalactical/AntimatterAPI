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
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluids;
import org.jetbrains.annotations.NotNull;
import tesseract.TesseractGraphWrappers;
import tesseract.api.fluid.FluidContainerHandler;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
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
        int firstEmpty = -1;
        for (int i = 0; i < tanks.length; i++) {
            FluidTank tank = this.tanks[i];
            if (!drain){
                if (tank.isEmpty() && firstEmpty == -1){
                    firstEmpty = i;
                }
            }
            if (tank.getStoredFluid().matches(stack)) {
                firstAvailable = i;
                break;
            } else if ((drain && !tank.extractFluid(stack, true).isEmpty())
                    || (!drain && tank.insertFluid(stack, true) != 0)) {
                return i;
            }
        }
        if (firstAvailable == -1) return firstEmpty;
        return firstAvailable;
    }

    public FluidTank getTank(int tank) {
        return this.tanks[tank];
    }

    public List<FluidHolder> getFluids() {
        return Arrays.stream(this.tanks).map(FluidTank::getStoredFluid).toList();
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
        return this.tanks[tank].getStoredFluid();
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
            amount += tank.getStoredFluid().getFluidAmount();
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
        int tank = getFirstAvailableTank(fluid, true);
        if (tank == -1) return 0;
        return getTank(tank).insertFluid(fluid, simulate);
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

    public ListTag serialize() {
        ListTag nbt = new ListTag();
        Arrays.stream(tanks).forEach(t -> nbt.add(t.getStoredFluid().serialize()));
        return nbt;
    }

    public void deserialize(ListTag nbt) {
        int i = 0;
        for (Tag tank : nbt) {
            if (tank instanceof CompoundTag) {
                CompoundTag cnbt = (CompoundTag) tank;
                if (i > tanks.length - 1)
                    break;
                tanks[i++].setFluid(0, AntimatterPlatformUtils.fromTag(cnbt));
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

        public Builder<T> tank(Predicate<FluidHolder> validator, int amountInMB) {
            this.tanks.add(new FluidTank(amountInMB * TesseractGraphWrappers.dropletMultiplier, validator) {
                @Override
                protected void onContentsChanged() {
                    tile.onMachineEvent(contentEvent, this.storedFluid);
                }
            });
            return this;
        }

        public Builder<T> tank(int amountInMB) {
            this.tanks.add(new FluidTank(amountInMB * TesseractGraphWrappers.dropletMultiplier) {
                @Override
                protected void onContentsChanged() {
                    tile.onMachineEvent(contentEvent, this.storedFluid);
                }
            });
            return this;
        }

        private FluidTanks build() {
            return new FluidTanks(this.tanks);
        }

    }

}
