package muramasa.antimatter.tesseract;

import muramasa.antimatter.tile.pipe.TileEntityPipe;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import tesseract.Tesseract;
import tesseract.api.fluid.IFluidNode;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class FluidTileWrapper implements IFluidNode {

    private final TileEntity tile;
    private final IFluidHandler handler;

    private FluidTileWrapper(TileEntity tile, IFluidHandler handler) {
        this.tile = tile;
        this.handler = handler;
    }

    @Nullable
    public static void wrap(TileEntityPipe pipe, World world, BlockPos pos, Direction side, Supplier<TileEntity> supplier) {
       Tesseract.FLUID.registerNode(world.getDimensionKey(),pos.toLong(), () -> {
            TileEntity tile = supplier.get();
            LazyOptional<IFluidHandler> capability = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side.getOpposite());
            if (capability.isPresent()) {
                FluidTileWrapper node = new FluidTileWrapper(tile, capability.orElse(null));
                capability.addListener(o -> pipe.onInvalidate(side));
                return node;
            }
            throw new RuntimeException("invalid capability");
        });
    }

    @Override
    public int getPriority(Dir direction) {
        return (!(handler instanceof IFluidNode) ? 0 : ((IFluidNode)handler).getPriority(direction));
    }

    @Override
    public boolean canOutput() {
        return (!(handler instanceof IFluidNode) || ((IFluidNode)handler).canOutput());
    }

    @Override
    public boolean canInput() {
        return (!(handler instanceof IFluidNode) || ((IFluidNode)handler).canInput());
    }

    @Override
    public boolean canInput(Dir direction) {
        return (!(handler instanceof IFluidNode) || ((IFluidNode)handler).canInput(direction));
    }

    @Override
    public boolean canOutput(Dir direction) {
        return (!(handler instanceof IFluidNode) || ((IFluidNode)handler).canOutput(direction));
    }

    @Override
    public boolean canInput(FluidStack fluid, Dir direction) {
        return (!(handler instanceof IFluidNode) || ((IFluidNode)handler).canInput(fluid, direction));
    }

    @Override
    public int getTanks() {
        return handler.getTanks();
    }

    @Nonnull
    @Override
    public FluidStack getFluidInTank(int tank) {
        return handler.getFluidInTank(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        return handler.getTankCapacity(tank);
    }

    @Override
    public boolean isFluidValid(int tank, @Nonnull FluidStack stack) {
        return handler.isFluidValid(tank, stack);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        return handler.fill(resource, action);
    }

    @Nonnull
    @Override
    public FluidStack drain(FluidStack resource, FluidAction action) {
        return handler.drain(resource, action);
    }

    @Nonnull
    @Override
    public FluidStack drain(int maxDrain, FluidAction action) {
        return handler.drain(maxDrain, action);
    }
}
