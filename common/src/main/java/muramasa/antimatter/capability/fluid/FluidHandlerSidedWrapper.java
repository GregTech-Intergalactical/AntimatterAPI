package muramasa.antimatter.capability.fluid;

import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.base.FluidSnapshot;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import muramasa.antimatter.capability.CoverHandler;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import tesseract.api.fluid.FluidContainerHandler;
import tesseract.api.fluid.IFluidNode;

import java.util.List;

public class FluidHandlerSidedWrapper implements IFluidNode, FluidContainerHandler {
    protected IFluidNode fluidHandler;
    protected Direction side;
    CoverHandler<?> coverHandler;

    public FluidHandlerSidedWrapper(IFluidNode fluidHandler, CoverHandler<?> coverHandler, Direction side) {
        this.fluidHandler = fluidHandler;
        this.coverHandler = coverHandler;
        this.side = side;
    }

    @Override
    public int getSize() {
        return fluidHandler.getSize();
    }

    @Override
    public boolean isEmpty() {
        return fluidHandler.isEmpty();
    }

    @Override
    public FluidContainer copy() {
        return new FluidHandlerSidedWrapper(fluidHandler, coverHandler, side);
    }

    @NotNull
    @Override
    public FluidHolder getFluidInTank(int tank) {
        return fluidHandler.getFluidInTank(tank);
    }

    @Override
    public long getTankCapacity(int tank) {
        return fluidHandler.getTankCapacity(tank);
    }

    @Override
    public void fromContainer(FluidContainer container) {
        if (container instanceof FluidHandlerSidedWrapper wrapper) {
            fluidHandler = wrapper.fluidHandler;
            coverHandler = wrapper.coverHandler;
            side = wrapper.side;
        }
    }

    @Override
    public long extractFromSlot(FluidHolder fluidHolder, FluidHolder toInsert, Runnable snapshot) {
        return fluidHandler.extractFromSlot(fluidHolder, toInsert, snapshot);
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidHolder stack) {
        return fluidHandler.isFluidValid(tank, stack);
    }

    @Override
    public FluidContainer getFluidContainer() {
        return this;
    }

    @Override
    public long insertFluid(FluidHolder resource, boolean simulate) {
        if (coverHandler != null) {
            if (coverHandler.get(side).blocksInput(FluidContainer.class, side)) {
                return 0;
            }
            long oldAmount = resource.getFluidAmount();
            if(coverHandler.onTransfer(resource, side, true, simulate)) return oldAmount - resource.getFluidAmount();
        }

        if (!fluidHandler.canInput(resource, side) || !fluidHandler.canInput(side)) {
            return 0;
        }
        return fluidHandler.insertFluid(resource, simulate);
    }

    @NotNull
    @Override
    public FluidHolder extractFluid(FluidHolder resource, boolean  simulate) {
        if (coverHandler != null && (coverHandler.get(side).blocksOutput(FluidContainer.class, side) || coverHandler.onTransfer(resource, side, false, simulate))) {
            return FluidHooks.emptyFluid();
        }
        if (!fluidHandler.canOutput(side)) return FluidHooks.emptyFluid();
        return fluidHandler.extractFluid(resource, simulate);
    }

    @Override
    public void setFluid(int slot, FluidHolder fluid) {
        fluidHandler.setFluid(slot, fluid);
    }

    @Override
    public List<FluidHolder> getFluids() {
        return fluidHandler.getFluids();
    }

    @Override
    public int getPriority(Direction direction) {
        return fluidHandler.getPriority(direction);
    }

    @Override
    public boolean allowsExtraction() {
        return fluidHandler.allowsExtraction();
    }

    @Override
    public FluidSnapshot createSnapshot() {
        return fluidHandler.createSnapshot();
    }

    @Override
    public boolean allowsInsertion() {
        return fluidHandler.allowsInsertion();
    }

    @Override
    public boolean canInput(Direction direction) {
        return fluidHandler.canInput(direction);
    }

    @Override
    public boolean canOutput(Direction direction) {
        return fluidHandler.canOutput(direction);
    }

    @Override
    public boolean canInput(FluidHolder fluid, Direction direction) {
        return fluidHandler.canInput(fluid, direction);
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        fluidHandler.deserialize(nbt);
    }

    @Override
    public CompoundTag serialize(CompoundTag nbt) {
        return fluidHandler.serialize(nbt);
    }

    @Override
    public void clearContent() {
        fluidHandler.clearContent();
    }

    @Override
    public void readSnapshot(FluidSnapshot snapshot) {
        fluidHandler.readSnapshot(snapshot);
    }
}
