package muramasa.antimatter.capability.fluid;

import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.base.FluidSnapshot;
import earth.terrarium.botarium.common.fluid.impl.SimpleFluidSnapshot;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.level.material.Fluids;
import tesseract.api.fluid.FluidContainerHandler;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class FluidTank implements FluidContainer, FluidContainerHandler {
    protected Predicate<FluidHolder> validator;
    @Nonnull
    protected FluidHolder storedFluid = FluidHooks.emptyFluid();
    protected long capacity;
    public FluidTank(long capacity)
    {
        this(capacity, e -> true);
    }

    public FluidTank(long capacity, Predicate<FluidHolder> validator)
    {
        this.capacity = capacity;
        this.validator = validator;
    }

    public FluidTank setCapacity(long capacity)
    {
        this.capacity = capacity;
        return this;
    }

    public FluidTank setValidator(Predicate<FluidHolder> validator)
    {
        if (validator != null) {
            this.validator = validator;
        }
        return this;
    }

    public long getCapacity() {
        return capacity;
    }

    @Nonnull
    public FluidHolder getStoredFluid() {
        return storedFluid;
    }

    public boolean isFluidValid(FluidHolder stack)
    {
        return validator.test(stack);
    }

    @Override
    public FluidContainer getFluidContainer() {
        return this;
    }

    @Override
    public long insertFluid(FluidHolder fluid, boolean simulate) {
        if (validator.test(fluid)){
            if (this.storedFluid.isEmpty()) {
                FluidHolder insertedFluid = fluid.copyHolder();
                insertedFluid.setAmount(Mth.clamp(fluid.getFluidAmount(), 0, capacity));
                if (simulate) return insertedFluid.getFluidAmount();
                this.storedFluid = insertedFluid;
                onContentsChanged();
                return fluid.getFluidAmount();
            } else {
                if (this.storedFluid.matches(fluid)) {
                    long insertedAmount = Mth.clamp(fluid.getFluidAmount(), 0, capacity - this.storedFluid.getFluidAmount());
                    if (simulate) return insertedAmount;
                    this.storedFluid.setAmount(this.storedFluid.getFluidAmount() + insertedAmount);
                    onContentsChanged();
                    return insertedAmount;
                }
            }
        }
        return 0;
    }

    @Override
    public FluidHolder extractFluid(FluidHolder fluid, boolean simulate) {
        if (validator.test(fluid)) {
            FluidHolder toExtract = fluid.copyHolder();
            if (this.storedFluid.isEmpty()) {
                return FluidHooks.emptyFluid();
            } else if (this.storedFluid.matches(fluid)) {
                long extractedAmount = Mth.clamp(fluid.getFluidAmount(), 0, this.storedFluid.getFluidAmount());
                toExtract.setAmount(extractedAmount);
                if (simulate) return toExtract;
                this.storedFluid.setAmount(this.storedFluid.getFluidAmount() - extractedAmount);
                onContentsChanged();
                if (this.storedFluid.getFluidAmount() == 0) this.storedFluid = FluidHooks.emptyFluid();
                return toExtract;
            }
        }
        return FluidHooks.emptyFluid();
    }

    protected void onContentsChanged() {

    }

    @Override
    public void setFluid(int slot, FluidHolder fluid) {
        this.storedFluid = fluid;
    }

    @Override
    public List<FluidHolder> getFluids() {
        return List.of(storedFluid);
    }

    @Override
    public int getSize() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return storedFluid.isEmpty();
    }

    @Override
    public FluidContainer copy() {
        return new FluidTank(this.capacity, this.validator);
    }

    @Override
    public long getTankCapacity(int tankSlot) {
        return capacity;
    }

    @Override
    public void fromContainer(FluidContainer container) {
        this.storedFluid = container.getFluids().get(0);
        this.capacity = container.getTankCapacity(0);
        if (container instanceof FluidTank tank) this.validator = tank.validator;
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

    @Override
    public void deserialize(CompoundTag nbt) {
        this.storedFluid = FluidHooks.fluidFromCompound(nbt.getCompound("fluid"));
    }

    @Override
    public CompoundTag serialize(CompoundTag nbt) {
        nbt.put("fluid", storedFluid.serialize());
        return nbt;
    }

    @Override
    public void clearContent() {
        storedFluid = FluidHooks.emptyFluid();
    }
}
