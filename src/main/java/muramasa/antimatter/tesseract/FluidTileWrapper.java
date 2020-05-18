package muramasa.antimatter.tesseract;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverOutput;
import net.minecraft.fluid.Fluid;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE;

public class FluidTileWrapper implements IFluidNode, ITileWrapper {

    // TODO: Add black/white lister filter mode
    private TileEntity tile;
    private IFluidHandler handler;
    private Set<Fluid>[] filter = new Set[]{new ObjectOpenHashSet<>(), new ObjectOpenHashSet<>(), new ObjectOpenHashSet<>(), new ObjectOpenHashSet<>(), new ObjectOpenHashSet<>(), new ObjectOpenHashSet<>()};
    private boolean[] output = new boolean[]{false, false, false, false, false, false};
    private boolean[] input = new boolean[]{false, false, false, false, false, false};
    private int[] priority = new int[]{0, 0, 0, 0, 0, 0};
    private boolean valid = true;

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
        if (side != null) {
            output[side.getIndex()] = false;
        } else {
            Tesseract.FLUID.remove(tile.getWorld().getDimension().getType().getId(), tile.getPos().toLong());
            valid = false;
        }
    }

    @Override
    public void onUpdate(@Nonnull Direction side, @Nullable Cover cover) {
        /*if (cover instanceof CoverFilter) {
            filter.put(side, ((CoverFilter<Fluid>)cover).getFilter());
        }*/
        if (cover instanceof CoverOutput) {
            output[side.getIndex()] = true;
        }
        input[side.getIndex()] = true;
    }

    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public int insert(@Nonnull FluidData data, boolean simulate) {
        FluidStack stack = (FluidStack) data.getStack();
        return getFirstValidTank(stack.getFluid()) != -1 ? handler.fill(stack, simulate ? SIMULATE : EXECUTE) : 0;
    }

    @Nullable
    @Override
    public FluidData extract(int tank, int amount, boolean simulate) {
        FluidStack fluid = handler.getFluidInTank(tank);
        if (fluid.getAmount() > amount) {
            fluid = fluid.copy();
            fluid.setAmount(amount);
        }
        FluidStack stack = handler.drain(fluid, simulate ? SIMULATE : EXECUTE);
        return stack.isEmpty() ? null : new FluidData(stack, stack.getFluid(), stack.getAmount(), stack.getFluid().getAttributes().getTemperature(), stack.getFluid().getAttributes().isGaseous());
    }

    @Override
    public int getAvailableTank(@Nonnull Dir direction) {
        Set<?> set = filter[direction.getIndex()];
        if (set.isEmpty()) {
            for (int i = 0; i < handler.getTanks(); i++) {
                FluidStack stack = handler.getFluidInTank(i);
                if (!stack.isEmpty()) {
                    return i;
                }
            }
        } else {
            for (int i = 0; i < handler.getTanks(); i++) {
                FluidStack stack = handler.getFluidInTank(i);
                if (!stack.isEmpty() && set.contains(stack.getFluid())) {
                    return i;
                }
            }
        }
        return -1;
    }

    @Override
    public int getOutputAmount(@Nonnull Dir direction) {
        return 1;
    }

    @Override
    public int getPriority(@Nonnull Dir direction) {
        return priority[direction.getIndex()];
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
    public boolean canOutput(@Nonnull Dir direction) {
        return output[direction.getIndex()];
    }

    @Override
    public boolean canInput(@Nonnull Object fluid, @Nonnull Dir direction) {
        return isFluidAvailable(fluid, direction.getIndex()) && getFirstValidTank(fluid) != -1;
    }

    @Override
    public boolean connects(@Nonnull Dir direction) {
        return true;
    }

    private boolean isFluidAvailable(@Nonnull Object fluid, int dir) {
        Set<?> filtered = filter[dir];
        return input[dir] && (filtered.isEmpty() || filtered.contains(fluid));
    }

    // Fast way to find available tank for fluid
    private int getFirstValidTank(@Nonnull Object fluid) {
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
}
