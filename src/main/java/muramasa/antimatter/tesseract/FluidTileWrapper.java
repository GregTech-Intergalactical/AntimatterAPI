package muramasa.antimatter.tesseract;

import it.unimi.dsi.fastutil.objects.ObjectSets;
import muramasa.antimatter.Data;
import muramasa.antimatter.cover.*;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
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

public class FluidTileWrapper implements IFluidNode<FluidStack>, ITileWrapper {

    private TileEntity tile;
    private boolean removed;
    private IFluidHandler handler;

    private Cover[] covers = new Cover[] {
        Data.COVER_NONE, Data.COVER_NONE, Data.COVER_NONE, Data.COVER_NONE, Data.COVER_NONE, Data.COVER_NONE
    };

    private FluidTileWrapper(TileEntity tile, IFluidHandler handler) {
        this.tile = tile;
        this.handler = handler;
    }

    @Nullable
    public static FluidTileWrapper of(TileEntity tile) {
        LazyOptional<IFluidHandler> capability = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
        if (capability.isPresent()) {
            FluidTileWrapper node = new FluidTileWrapper(tile, capability.orElse(null));
            capability.addListener(x -> node.onRemove(null));
            Tesseract.FLUID.registerNode(tile.getWorld().getDimension().getType().getId(), tile.getPos().toLong(), node);
            return node;
        }
        return null;
    }

    @Override
    public void onRemove(@Nullable Direction side) {
        if (side == null) {
            if (tile.isRemoved()) {
                Tesseract.FLUID.remove(tile.getWorld().getDimension().getType().getId(), tile.getPos().toLong());
                removed = true;
            } else {
                // What if tile is recreate cap ?
            }
        } else {
            covers[side.getIndex()] = Data.COVER_NONE;
        }
    }

    @Override
    public void onUpdate(Direction side, Cover cover) {
        covers[side.getIndex()] = cover;
    }

    @Override
    public boolean isRemoved() {
        return removed;
    }

    @Override
    public int insert(FluidData data, boolean simulate) {
        FluidStack stack = (FluidStack) data.getStack();
        return getFirstValidTank(stack.getFluid()) != -1 ? handler.fill(stack, simulate ? SIMULATE : EXECUTE) : 0;
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
        Set<?> filtered = getFiltered(direction.getIndex());
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
        return covers[direction.getIndex()] instanceof CoverOutput;
    }

    @Override
    public boolean canInput(Object fluid, Dir direction) {
        return isFluidAvailable(fluid, direction.getIndex()) && getFirstValidTank(fluid) != -1;
    }

    @Override
    public boolean connects(Dir direction) {
        return true;
    }

    private boolean isFluidAvailable(Object fluid, int dir) {
        if (covers[dir] instanceof CoverTintable) return false;
        Set<?> filtered = getFiltered(dir);
        return filtered.isEmpty() || filtered.contains(fluid);
    }

    // Fast way to find available tank for fluid
    private int getFirstValidTank(Object fluid) {
        int tank = -1;
        for (int i = 0; i < handler.getTanks(); i++) {
            FluidStack stack = handler.getFluidInTank(i);
            if (stack.isEmpty()) {
                tank = i;
            } else {
                if (stack.getFluid().equals(fluid) && handler.getTankCapacity(i) > stack.getAmount()) {
                    return i;
                }
            }
        }
        return tank;
    }

    private Set<?> getFiltered(int index) {
        return covers[index] instanceof CoverFilter<?> ? ((CoverFilter<?>) covers[index]).getFilter() : ObjectSets.EMPTY_SET;
    }
}
