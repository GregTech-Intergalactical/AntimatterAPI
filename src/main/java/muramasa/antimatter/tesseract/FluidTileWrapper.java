package muramasa.antimatter.tesseract;

import it.unimi.dsi.fastutil.objects.ObjectSets;
import muramasa.antimatter.Data;
import muramasa.antimatter.cover.*;
import net.minecraft.fluid.Fluid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import tesseract.Tesseract;
import tesseract.api.fluid.FluidData;
import tesseract.api.fluid.IFluidNode;
import tesseract.util.Dir;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Supplier;

import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE;

public class FluidTileWrapper implements IFluidNode {

    private TileEntity tile;
    private IFluidHandler handler;

    private FluidTileWrapper(TileEntity tile, IFluidHandler handler) {
        this.tile = tile;
        this.handler = handler;
    }

    @Nullable
    public static void of(World world, BlockPos pos, Supplier<TileEntity> supplier) {
        Tesseract.FLUID.registerNode(world.getDimensionKey(),pos.toLong(), () -> {
            TileEntity tile = supplier.get();
            LazyOptional<IFluidHandler> capability = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
            if (capability.isPresent()) {
                FluidTileWrapper node = new FluidTileWrapper(tile, capability.orElse(null));
                capability.addListener(o -> node.onRemove(null));
                //Tesseract.ITEM.registerNode(tile.getWorld().getDimensionKey(), tile.getPos().toLong(), () -> node);
                return node;
            }
            throw new RuntimeException("invalid capability");
        });
    }

    public void onRemove(@Nullable Direction side) {
        if (side == null) {
            if (tile.isRemoved()) {
                Tesseract.FLUID.remove(tile.getWorld().getDimensionKey(), tile.getPos().toLong());
            }
        }
    }


    @Override
    public int insert(FluidStack stack, boolean simulate) {
        return getFirstValidTank(stack.getFluid()) != -1 ? handler.fill(stack, simulate ? SIMULATE : EXECUTE) : 0;
    }

    @Nullable
    @Override
    public FluidStack extract(int tank, int amount, boolean simulate) {
        FluidStack fluid = handler.getFluidInTank(tank);
        if (fluid.getAmount() > amount) {
            fluid = fluid.copy();
            fluid.setAmount(amount);
        }
        FluidStack stack = handler.drain(fluid, simulate ? SIMULATE : EXECUTE);
        return stack;
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
        return handler != null;
    }

    @Override
    public boolean canInput(FluidStack fluid, Dir direction) {
        return isFluidAvailable(fluid, direction.getIndex()) && getFirstValidTank(fluid.getFluid()) != -1;
    }

    @Override
    public boolean connects(Dir direction) {
        return true;
    }

    private boolean isFluidAvailable(FluidStack fluid, int dir) {
        Set<?> filtered = getFiltered(dir);
        return filtered.isEmpty() || filtered.contains(fluid);
    }

    // Fast way to find available tank for fluid
    private int getFirstValidTank(Fluid fluid) {
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
        return  ObjectSets.EMPTY_SET;
    }
}
