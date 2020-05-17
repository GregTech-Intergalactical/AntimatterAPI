package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.pipe.PipeCache;
import muramasa.antimatter.pipe.types.ItemPipe;
import muramasa.antimatter.pipe.types.PipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import tesseract.Tesseract;
import tesseract.api.item.IItemPipe;
import tesseract.api.ITickHost;
import tesseract.api.ITickingController;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static muramasa.antimatter.pipe.PipeType.ITEM;

public class TileEntityItemPipe extends TileEntityPipe implements IItemPipe, ITickHost {

    private ITickingController controller;

    public TileEntityItemPipe(PipeType<?> type) {
        super(type);
    }

    @Override
    public void onLoad() {
        if (isServerSide()) Tesseract.ITEM.registerConnector(getDimention(), pos.toLong(), this); // this is connector class
        super.onLoad();
    }

    @Override
    public void refreshConnection() {
        if (isServerSide()) {
            Tesseract.ITEM.remove(getDimention(), pos.toLong());
            Tesseract.ITEM.registerConnector(getDimention(), pos.toLong(), this); // this is connector class
        } else {
            super.refreshConnection();
        }
    }

    @Override
    public void onRemove() {
        if (isServerSide()) Tesseract.ITEM.remove(getDimention(), pos.toLong());
    }

    @Override
    public void onServerUpdate() {
        if (controller != null) controller.tick();
    }

    @Override
    public int getCapacity() {
        return ((ItemPipe<?>)getPipeType()).getCapacity(getPipeSize());
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
        PipeCache.update(ITEM, world, direction, neighbor, null);
    }

    @Override
    protected void onNeighborRemove(TileEntity neighbor, Direction direction) {
        PipeCache.remove(ITEM, world, direction, neighbor);
    }
}
