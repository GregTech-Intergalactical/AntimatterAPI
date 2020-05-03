package muramasa.antimatter.capability.enet;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.capability.INodeHandler;
import muramasa.antimatter.cover.Cover;
import net.minecraft.fluid.Fluid;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import tesseract.TesseractAPI;
import tesseract.api.fluid.FluidData;
import tesseract.api.fluid.IFluidNode;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE;
import static net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE;

public class FluidNode implements IFluidNode, INodeHandler {

    private TileEntity tile;
    private IFluidHandler handler;
    private Map<Dir, Set<Fluid>> filter = new EnumMap<>(Dir.class);
    private Map<Dir, Boolean> output = new EnumMap<>(Dir.class);
    //private boolean out = new Random().nextBoolean(); // TODO: For test
    private int capacity;

    public FluidNode(TileEntity tile) {
        this.tile = tile;
        this.handler = handler;

        LazyOptional<IFluidHandler> fluid = tile.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
        if (energy.isPresent()) {
            new FluidNode(tile);
        }

        for (Dir direction : Dir.VALUES) {
            filter.put(direction, new ObjectOpenHashSet<>());
            output.put(direction, false);
        }

        // Find the smallest capacity
        for (int i = 0; i < handler.getTanks(); i++) {
            capacity = Math.min(capacity, handler.getTankCapacity(i));
        }

        TesseractAPI.registerFluidNode(tile.getWorld().getDimension().getType().getId(), tile.getPos().toLong(), this);
    }

    @Override
    public void onRemove(Direction side) {
        TesseractAPI.removeFluid(tile.getWorld().getDimension().getType().getId(), tile.getPos().toLong());
    }

    @Override
    public void onUpdate(Direction side, Cover cover) {
        //if (cover instanceof CoverFilter)
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
        Set<?> set = filter.get(direction);
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
        return 0;
    }

    @Override
    public int getCapacity() {
        return capacity;
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
        return output.get(direction);
    }

    @Override
    public boolean canInput(@Nonnull Object fluid, Dir direction) {
        return isFluidAvailable(fluid, direction) && getFirstValidTank(fluid) != -1;
    }

    @Override
    public boolean connects(@Nonnull Dir direction) {
        return true;
    }

    private boolean isFluidAvailable(@Nonnull Object fluid, Dir direction) {
        Set<?> filtered = filter.get(direction);
        return filtered.isEmpty() || filtered.contains(fluid);
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
