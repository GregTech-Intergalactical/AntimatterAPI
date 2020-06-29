package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.pipe.types.Cable;
import muramasa.antimatter.pipe.types.PipeType;
import tesseract.Tesseract;
import tesseract.api.electric.IElectricCable;
import tesseract.util.Dir;

import javax.annotation.Nonnull;

public class TileEntityCable extends TileEntityPipe implements IElectricCable {

    public TileEntityCable(PipeType<?> type) {
        super(type);
    }

    @Override
    public void onLoad() {
        if (isServerSide()) Tesseract.ELECTRIC.registerConnector(getDimension(), pos.toLong(), this); // this is connector class
        super.onLoad();
    }

    @Override
    public void refreshConnection() {
        if (isServerSide()) {
            Tesseract.ELECTRIC.remove(getDimension(), pos.toLong());
            Tesseract.ELECTRIC.registerConnector(getDimension(), pos.toLong(), this); // this is connector class
        } else {
            super.refreshConnection();
        }
    }

    @Override
    public void onRemove() {
        if (isServerSide()) Tesseract.ELECTRIC.remove(getDimension(), pos.toLong());
    }

    @Override
    public int getVoltage() {
        return ((Cable<?>)getPipeType()).getTier().getVoltage();
    }

    @Override
    public int getLoss() {
        return ((Cable<?>)getPipeType()).getLoss();
    }

    @Override
    public int getAmps() {
        return ((Cable<?>)getPipeType()).getAmps(getPipeSize());
    }

    @Override
    public boolean connects(@Nonnull Dir direction) {
        return canConnect(direction.getIndex());
    }
}
