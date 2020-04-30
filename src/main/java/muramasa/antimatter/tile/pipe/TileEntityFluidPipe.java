package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.pipe.types.FluidPipe;
import muramasa.antimatter.pipe.types.PipeType;
import net.minecraft.world.World;
import tesseract.TesseractAPI;
import tesseract.api.fluid.IFluidPipe;
import tesseract.graph.ITickHost;
import tesseract.graph.ITickingController;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityFluidPipe extends TileEntityPipe implements IFluidPipe, ITickHost {

    private ITickingController controller;

    public TileEntityFluidPipe(PipeType<?> type) {
        super(type);

        World world = getWorld();
        if (world != null && !world.isRemote())
            TesseractAPI.registerFluidPipe(world.getDimension().getType().getId(), pos.toLong(), this);
    }

    @Override
    public void remove() {
        World world = getWorld();
        if (world != null && !world.isRemote())
            TesseractAPI.removeFluid(world.getDimension().getType().getId(), getPos().toLong());
        super.remove();
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
        return true;
    }

    @Override
    public void reset(@Nullable ITickingController oldController, @Nullable ITickingController newController) {
        if (oldController == null || (controller == oldController && newController == null) || controller != oldController)
            controller = newController;
    }
}
