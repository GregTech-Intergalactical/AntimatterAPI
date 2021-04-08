package muramasa.antimatter.tile.pipe;

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
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityFluidPipe extends TileEntityPipe implements IFluidPipe {

    public TileEntityFluidPipe(PipeType<?> type) {
        super(type);
    }

   /* @Override
    public void onFirstTick() {
        super.onFirstTick();
        if (isServerSide()) Tesseract.FLUID.registerConnector(getDimension(), pos.toLong(), this); // this is connector class
    }*/

    @Override
    public void onLoad() {
        super.onLoad();
        if (isServerSide()) Tesseract.FLUID.registerConnector(getDimension(), pos.toLong(), this);
    }

    @Override
    public boolean validateTile(TileEntity tile, Direction side) {
        return tile instanceof TileEntityFluidPipe || tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side).isPresent();
    }

    @Override
    public void refreshConnection() {
        if (isServerSide()) {
            Tesseract.FLUID.remove(getDimension(), pos.toLong());
            Tesseract.FLUID.registerConnector(getDimension(), pos.toLong(), this); // this is connector class
        }
        super.refreshConnection();
    }

    @Override
    public void cacheNode(BlockPos pos, Direction side, boolean remove) {
        if (!remove) {
            FluidTileWrapper.wrap(getWorld(), pos, side, () -> world.getTileEntity(pos));
        } else {
            Tesseract.FLUID.remove(getWorld().getDimensionKey(), pos.toLong());
        }
    }

    @Override
    public void onRemove() {
        if (isServerSide()) Tesseract.FLUID.remove(getDimension(), pos.toLong());
        super.onRemove();
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (side == null) return LazyOptional.empty();
        if (!this.canConnect(side.getIndex())) return LazyOptional.empty();
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> new TesseractFluidCapability(this, side)).cast();
        }
        return LazyOptional.empty();
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
    public boolean connects(Dir direction) {
        return canConnect(direction.getIndex());
    }
}
