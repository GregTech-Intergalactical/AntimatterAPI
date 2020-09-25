package muramasa.antimatter.tesseract;

import muramasa.antimatter.cover.CoverInstance;
import muramasa.antimatter.cover.CoverOutput;
import muramasa.antimatter.cover.CoverTintable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import tesseract.Tesseract;
import tesseract.api.fluid.FluidData;
import tesseract.api.fluid.IFluidNode;
import tesseract.util.Dir;

import javax.annotation.Nullable;
import java.util.Set;

import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE;

public class FluidTileWrapper extends TileWrapper<IFluidHandler> implements IFluidNode<FluidStack> {

    public FluidTileWrapper(TileEntity tile) {
        super(tile, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
    }

    @Override
    public void onInit() {
        Tesseract.FLUID.registerNode(tile.getWorld().getDimension().getType().getId(), tile.getPos().toLong(), this);
    }

    @Override
    public void onRemove() {
        Tesseract.FLUID.remove(tile.getWorld().getDimension().getType().getId(), tile.getPos().toLong());
    }

    @Override
    public int insert(FluidData<FluidStack> data, boolean simulate) {
        FluidStack stack = data.getStack();
        return getFirstValidTank(stack) != -1 ? handler.fill(stack, simulate ? SIMULATE : EXECUTE) : 0;
    }

    @Nullable
    @Override
    public FluidData<FluidStack> extract(int tank, int amount, boolean simulate) {
        FluidStack fluid = handler.getFluidInTank(tank);
        if (fluid.getAmount() > amount) {
            fluid = fluid.copy();
            fluid.setAmount(amount);
        }
        FluidStack stack = handler.drain(fluid, simulate ? SIMULATE : EXECUTE);
        return stack.isEmpty() ? null : new FluidData<>(stack, stack.getAmount(), stack.getFluid().getAttributes().getTemperature(), stack.getFluid().getAttributes().isGaseous());
    }

    @Override
    public int getAvailableTank(Dir direction) {
        Set<?> filtered = getFilterAt(direction.getIndex());
        if (filtered.isEmpty()) {
            for (int i = 0; i < handler.getTanks(); i++) {
                FluidStack stack = handler.getFluidInTank(i);
                if (!stack.isEmpty()) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < handler.getTanks(); i++) {
                FluidStack stack = handler.getFluidInTank(i);
                if (!stack.isEmpty() && filtered.contains(stack.getFluid())) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public int getOutputAmount(Dir direction) {
        return 1;
    }

    @Override
    public int getPriority(Dir direction) {
        return 0;
    }

    @Override
    public boolean canOutput() {
        return handler != null;
    }

    @Override
    public boolean canInput() {
        return handler != null;
    }

    @Override
    public boolean canOutput(Dir direction) {
        return getCoverAt(direction.getIndex()) instanceof CoverOutput;
    }

    @Override
    public boolean canInput(FluidStack fluid, Dir direction) {
        return isFluidAvailable(fluid, direction.getIndex()) && getFirstValidTank(fluid) != -1;
    }

    @Override
    public boolean connects(Dir direction) {
        return true;
    }

    private boolean isFluidAvailable(FluidStack fluid, int dir) {
        if (getCoverAt(dir) instanceof CoverTintable) return false;
        Set<?> filtered = getFilterAt(dir);
        return filtered.isEmpty() || filtered.contains(fluid.getFluid());
    }

    // Fast way to find available tank for fluid
    private int getFirstValidTank(FluidStack fluid) {
        int tank = -1;
        for (int i = 0; i < handler.getTanks(); i++) {
            FluidStack stack = handler.getFluidInTank(i);
            if (stack.isEmpty()) {
                tank = i;
            } else {
                if (stack.isFluidEqual(fluid) && handler.getTankCapacity(i) > stack.getAmount()) {
                    return i;
                }
            }
        }
        return tank;
    }
}
