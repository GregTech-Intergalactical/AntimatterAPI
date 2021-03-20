package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.pipe.types.FluidPipe;
import muramasa.antimatter.pipe.types.PipeType;
import tesseract.Tesseract;
import tesseract.api.fluid.IFluidPipe;
import tesseract.util.Dir;

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
    public void refreshConnection() {
        if (isServerSide()) {
            Tesseract.FLUID.remove(getDimension(), pos.toLong());
            Tesseract.FLUID.registerConnector(getDimension(), pos.toLong(), this); // this is connector class
        }
        super.refreshConnection();
    }

    @Override
    public void onRemove() {
        if (isServerSide()) Tesseract.FLUID.remove(getDimension(), pos.toLong());
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
    public boolean connects(Dir direction) {
        return canConnect(direction.getIndex());
    }
}
