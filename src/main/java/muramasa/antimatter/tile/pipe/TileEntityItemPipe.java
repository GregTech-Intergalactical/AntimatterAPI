package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.pipe.types.ItemPipe;
import muramasa.antimatter.pipe.types.PipeType;
import net.minecraft.world.World;
import tesseract.TesseractAPI;
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

        World world = getWorld();
        if (world != null && !world.isRemote())
            TesseractAPI.registerItemPipe(world.getDimension().getType().getId(), pos.toLong(), this);
    }

    @Override
    public void remove() {
        World world = getWorld();
        if (world != null && !world.isRemote())
            TesseractAPI.removeItem(world.getDimension().getType().getId(), getPos().toLong());
        super.remove();
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
        return true;
    }

    @Override
    public void reset(@Nullable ITickingController oldController, @Nullable ITickingController newController) {
        if (oldController == null || (controller == oldController && newController == null) || controller != oldController)
            controller = newController;
    }
}
