package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.pipe.types.ItemPipe;
import muramasa.antimatter.pipe.types.PipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.items.CapabilityItemHandler;
import tesseract.Tesseract;
import tesseract.api.item.IItemNode;
import tesseract.api.item.IItemPipe;
import tesseract.graph.ITickHost;
import tesseract.graph.ITickingController;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityItemPipe extends TileEntityPipe implements IItemPipe, ITickHost {

    private ITickingController controller;

    public TileEntityItemPipe(PipeType<?> type) {
        super(type);
    }

    @Override
    public void refreshConnections() {
        if (isServerSide()) Tesseract.ITEM.remove(getDimention(), pos.toLong());
        super.refreshConnections();
        if (isServerSide()) Tesseract.ITEM.registerNode(getDimention(), pos.toLong(), (IItemNode) this);
    }

    @Override
    public void onRemove() {
        Tesseract.ITEM.remove(getDimention(), pos.toLong());
    }

    @Override
    public void onServerUpdate() {
        if (controller != null) controller.tick();
    }

    @Override
    public boolean canConnect(TileEntity tile, Direction side) {
        return tile instanceof TileEntityItemPipe/* && getCover(side).isEqual(Data.COVER_NONE)*/ || tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).isPresent();
    }

    @Override
    public int getCapacity() {
        return ((ItemPipe<?>)getPipeType()).getCapacity(getPipeSize());
    }

    @Override
    public boolean connects(@Nonnull Dir direction) {
        return true;//Connectivity.has(connections, direction);
    }

    @Override
    public void reset(@Nullable ITickingController oldController, @Nullable ITickingController newController) {
        if (oldController == null || (controller == oldController && newController == null) || controller != oldController)
            controller = newController;
    }
}
