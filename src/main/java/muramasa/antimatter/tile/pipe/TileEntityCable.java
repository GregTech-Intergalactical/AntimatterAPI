package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.pipe.types.Cable;
import muramasa.antimatter.pipe.types.PipeType;
import tesseract.TesseractAPI;
import tesseract.api.electric.IElectricCable;
import tesseract.util.Dir;

import javax.annotation.Nonnull;

public class TileEntityCable extends TileEntityPipe implements IElectricCable {

    public TileEntityCable(PipeType<?> type) {
        super(type);
    }

    @Override
    public void onInit() {
        super.onInit();
        TesseractAPI.registerElectricCable(getDimention(), pos.toLong(), this);
    }

    @Override
    public void onRemove() {
        TesseractAPI.removeElectric(getDimention(), pos.toLong());
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
        return true;
    }
}
