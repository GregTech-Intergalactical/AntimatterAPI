package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.pipe.PipeCache;
import muramasa.antimatter.pipe.types.Cable;
import muramasa.antimatter.pipe.types.PipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import tesseract.Tesseract;
import tesseract.api.electric.IElectricCable;
import tesseract.util.Dir;

import javax.annotation.Nonnull;

import static muramasa.antimatter.pipe.PipeType.ELECTRIC;

public class TileEntityCable extends TileEntityPipe implements IElectricCable {

    public TileEntityCable(PipeType<?> type) {
        super(type);
    }

    @Override
    public void onLoad() {
        if (isServerSide()) Tesseract.ELECTRIC.registerConnector(getDimention(), pos.toLong(), this); // this is connector class
        super.onLoad();
    }

    @Override
    public void refreshConnection() {
        if (isServerSide()) {
            Tesseract.ELECTRIC.remove(getDimention(), pos.toLong());
            Tesseract.ELECTRIC.registerConnector(getDimention(), pos.toLong(), this); // this is connector class
        } else {
            super.refreshConnection();
        }
    }

    @Override
    public void onRemove() {
        if (isServerSide()) Tesseract.ELECTRIC.remove(getDimention(), pos.toLong());
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

    @Override
    protected void onNeighborUpdate(TileEntity neighbor, Direction direction) {
        PipeCache.update(ELECTRIC, world, direction, neighbor, null);
    }

    @Override
    protected void onNeighborRemove(TileEntity neighbor, Direction direction) {
        PipeCache.remove(ELECTRIC, world, direction, neighbor);
    }
}
