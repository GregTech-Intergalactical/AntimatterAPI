package muramasa.antimatter.capability.fluid;

import earth.terrarium.botarium.common.fluid.base.FluidContainer;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.base.FluidSnapshot;
import earth.terrarium.botarium.common.fluid.impl.SimpleFluidSnapshot;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import tesseract.api.fluid.IFluidNode;

import javax.annotation.Nonnull;
import java.util.List;

public class FluidHandlerNullSideWrapper implements IFluidNode {
    IFluidNode fluidHandler;

    public FluidHandlerNullSideWrapper(IFluidNode fluidHandler) {
        this.fluidHandler = fluidHandler;
    }

    @Override
    public int getPriority(Direction direction) {
        return fluidHandler.getPriority(direction);
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
    public long insertFluid(FluidHolder fluid, boolean simulate) {
        return 0;
    }

    @Override
    public FluidHolder extractFluid(FluidHolder fluid, boolean simulate) {
        return FluidHooks.emptyFluid();
    }

    @Override
    public void setFluid(int slot, FluidHolder fluid) {

    }

    @Override
    public List<FluidHolder> getFluids() {
        return fluidHandler.getFluids();
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
        return new FluidHandlerNullSideWrapper(fluidHandler);
    }

    @Override
    public long getTankCapacity(int tankSlot) {
        return fluidHandler.getTankCapacity(tankSlot);
    }

    @Override
    public void fromContainer(FluidContainer container) {
        if (container instanceof FluidHandlerNullSideWrapper wrapper) fluidHandler = wrapper.fluidHandler;
    }

    @Override
    public long extractFromSlot(FluidHolder fluidHolder, FluidHolder toInsert, Runnable snapshot) {
        return 0;
    }

    @Override
    public boolean allowsInsertion() {
        return fluidHandler.allowsInsertion();
    }

    @Override
    public boolean allowsExtraction() {
        return fluidHandler.allowsExtraction();
    }

    @Override
    public FluidSnapshot createSnapshot() {
        return new SimpleFluidSnapshot(this);
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
        fluidHandler.clearContent();
    }
}
