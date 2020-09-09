package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.pipe.types.ItemPipe;
import muramasa.antimatter.pipe.types.PipeType;
import tesseract.Tesseract;
import tesseract.api.item.IItemPipe;
import tesseract.util.Dir;

public class TileEntityItemPipe extends TileEntityPipe implements IItemPipe {

    public TileEntityItemPipe(PipeType<?> type) {
        super(type);
    }

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        if (isServerSide()) Tesseract.ITEM.registerConnector(getDimension(), pos.toLong(), this); // this is connector class
    }

    @Override
    public void refreshConnection() {
        if (isServerSide()) {
            Tesseract.ITEM.remove(getDimension(), pos.toLong());
            Tesseract.ITEM.registerConnector(getDimension(), pos.toLong(), this); // this is connector class
        }
        super.refreshConnection();
    }

    @Override
    public void onRemove() {
        if (isServerSide()) Tesseract.ITEM.remove(getDimension(), pos.toLong());
        super.onRemove();
    }

    @Override
    public int getCapacity() {
        return ((ItemPipe<?>)getPipeType()).getCapacity(getPipeSize());
    }

    @Override
    public boolean connects(Dir direction) {
        return canConnect(direction.getIndex());
    }
}
