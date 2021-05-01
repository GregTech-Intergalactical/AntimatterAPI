package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.pipe.PipeCoverHandler;
import muramasa.antimatter.pipe.types.FluidPipe;
import muramasa.antimatter.pipe.types.PipeType;
import muramasa.antimatter.tesseract.FluidTileWrapper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import tesseract.Tesseract;
import tesseract.api.capability.TesseractFluidCapability;
import tesseract.api.fluid.IFluidPipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

public class TileEntityFluidPipe extends TileEntityPipe implements IFluidPipe {

    public TileEntityFluidPipe(PipeType<?> type) {
        super(type);
        SIDE_CAPS = Arrays.stream(Ref.DIRS).map(t -> LazyOptional.of(() -> new TesseractFluidCapability(this, t))).toArray(LazyOptional[]::new);
    }

    @Override
    protected void initTesseract() {
        if (isServerSide()) Tesseract.FLUID.registerConnector(getWorld(), pos.toLong(), this); // this is connector class
        super.initTesseract();
    }

    @Override
    public boolean validateTile(TileEntity tile, Direction side) {
        return tile instanceof TileEntityFluidPipe || tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side).isPresent();
    }

    @Override
    public void refreshConnection() {
        if (isServerSide()) {
            if (Tesseract.FLUID.remove(getWorld(), pos.toLong())) {
                Tesseract.FLUID.registerConnector(getWorld(), pos.toLong(), this); // this is connector class
            }
        }
        super.refreshConnection();
    }

    @Override
    public void registerNode(BlockPos pos, Direction side, boolean remove) {
        if (!remove) {
            FluidTileWrapper.wrap(this, getWorld(), pos, side, () -> world.getTileEntity(pos));
        } else {
            Tesseract.FLUID.remove(getWorld(), pos.toLong());
        }
    }

    @Override
    public void onRemove() {
        if (isServerSide()) Tesseract.FLUID.remove(getWorld(), pos.toLong());
        super.onRemove();
    }

    @Override
    public boolean isGasProof() {
        return ((FluidPipe<?>)getPipeType()).isGasProof();
    }

    @Override
    public int getCapacity() {
        return ((FluidPipe<?>)getPipeType()).getCapacity(getPipeSize());
    }

    @Override
    public int getPressure() {
        return ((FluidPipe<?>)getPipeType()).getPressure(getPipeSize());
    }

    @Override
    public int getTemperature() {
        return ((FluidPipe<?>)getPipeType()).getTemperature();
    }

    @Override
    public boolean connects(Direction direction) {
        return canConnect(direction.getIndex());
    }

    @Override
    protected Capability<?> getCapability() {
        return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    }

    public static class TileEntityCoveredFluidPipe extends TileEntityFluidPipe implements ITickablePipe {

        public TileEntityCoveredFluidPipe(PipeType<?> type) {
            super(type);
        }

        @Override
        public LazyOptional<PipeCoverHandler<?>> getCoverHandler() {
            return this.coverHandler;
        }
    }

}
