package muramasa.gtu.api.capability.impl;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;

import javax.annotation.Nonnull;

public class FluidHandlerItemCell extends FluidHandlerItemStackSimple {

    protected int maxTemp;

    public FluidHandlerItemCell(@Nonnull ItemStack container, int capacity, int maxTemp) {
        super(container, capacity);
        this.maxTemp = maxTemp;
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid) {
        return fluid.getFluid().getTemperature() <= maxTemp;
    }
}
