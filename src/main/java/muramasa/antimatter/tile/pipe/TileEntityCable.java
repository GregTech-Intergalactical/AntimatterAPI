package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.pipe.types.Cable;
import muramasa.antimatter.pipe.types.PipeType;
import tesseract.Tesseract;
import tesseract.api.gt.IGTCable;
import tesseract.util.Dir;

public class TileEntityCable extends TileEntityPipe implements IGTCable {

    public TileEntityCable(PipeType<?> type) {
        super(type);
    }

    @Override
    public void onServerLoad() {
        Tesseract.GT_ENERGY.registerConnector(getDimension(), pos.toLong(), this); // this is connector class
    }

    @Override
    public void refreshConnection() {
        if (isServerSide()) {
            Tesseract.GT_ENERGY.remove(getDimension(), pos.toLong());
            Tesseract.GT_ENERGY.registerConnector(getDimension(), pos.toLong(), this); // this is connector class
        } else {
            super.refreshConnection();
        }
    }

    @Override
    public void onServerRemove() {
        Tesseract.GT_ENERGY.remove(getDimension(), pos.toLong());
        super.onServerRemove();
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
    public boolean connects(Dir direction) {
        return canConnect(direction.getIndex());
    }
}
