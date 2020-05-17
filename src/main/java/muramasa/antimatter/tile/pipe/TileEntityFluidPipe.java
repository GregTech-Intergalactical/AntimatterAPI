package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.pipe.PipeCache;
import muramasa.antimatter.pipe.types.FluidPipe;
import muramasa.antimatter.pipe.types.PipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import tesseract.Tesseract;
import tesseract.api.fluid.IFluidPipe;
import tesseract.api.ITickHost;
import tesseract.api.ITickingController;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static muramasa.antimatter.pipe.PipeType.FLUID;

public class TileEntityFluidPipe extends TileEntityPipe implements IFluidPipe, ITickHost {

    private ITickingController controller;

    public TileEntityFluidPipe(PipeType<?> type) {
        super(type);
    }

    @Override
    public void onLoad() {
        if (isServerSide()) Tesseract.FLUID.registerConnector(getDimention(), pos.toLong(), this); // this is connector class
        super.onLoad();
    }

    @Override
    public void refreshConnection() {
        if (isServerSide()) {
            Tesseract.FLUID.remove(getDimention(), pos.toLong());
            Tesseract.FLUID.registerConnector(getDimention(), pos.toLong(), this); // this is connector class
        } else {
            super.refreshConnection();
        }
    }

    @Override
    public void onRemove() {
        if (isServerSide()) Tesseract.FLUID.remove(getDimention(), pos.toLong());
    }

    @Override
    public void onServerUpdate() {
        if (controller != null) controller.tick();
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
    public boolean connects(@Nonnull Dir direction) {
        return canConnect(direction.getIndex());
    }

    @Override
    public void reset(@Nullable ITickingController oldController, @Nullable ITickingController newController) {
        if (oldController == null || (controller == oldController && newController == null) || controller != oldController)
            controller = newController;
    }

    @Override
    protected void onNeighborUpdate(TileEntity neighbor, Direction direction) {
        PipeCache.update(FLUID, world, direction, neighbor, null);
    }

    @Override
    protected void onNeighborRemove(TileEntity neighbor, Direction direction) {
        PipeCache.remove(FLUID, world, direction, neighbor);
    }
}
