package muramasa.antimatter.tile.pipe;

import muramasa.antimatter.pipe.types.FluidPipe;
import muramasa.antimatter.pipe.types.PipeType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import tesseract.TesseractAPI;
import tesseract.api.fluid.IFluidPipe;
import tesseract.graph.Connectivity;
import tesseract.graph.ITickHost;
import tesseract.graph.ITickingController;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityFluidPipe extends TileEntityPipe implements IFluidPipe, ITickHost {

    private ITickingController controller;

    public TileEntityFluidPipe(PipeType<?> type) {
        super(type);
    }

    @Override
    public void refreshConnections() {
        if (isServerSide()) TesseractAPI.removeFluid(getDimention(), pos.toLong());
        super.refreshConnections();
        if (isServerSide()) TesseractAPI.registerFluidPipe(getDimention(), pos.toLong(), this);
    }

    @Override
    public void onRemove() {
        if (isServerSide()) TesseractAPI.removeFluid(getDimention(), pos.toLong());
    }

    @Override
    public void onServerUpdate() {
        if (controller != null) controller.tick();
    }

    @Override
    public boolean canConnect(TileEntity tile, Direction side) {
        return tile instanceof TileEntityFluidPipe/* && getCover(side).isEqual(Data.COVER_NONE)*/ || tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY).isPresent();
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
        return Connectivity.has(connections, direction);
    }

    @Override
    public void reset(@Nullable ITickingController oldController, @Nullable ITickingController newController) {
        if (oldController == null || (controller == oldController && newController == null) || controller != oldController)
            controller = newController;
    }
}
