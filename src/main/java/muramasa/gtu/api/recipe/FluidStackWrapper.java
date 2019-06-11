package muramasa.gtu.api.recipe;

import net.minecraftforge.fluids.FluidStack;

public class FluidStackWrapper implements IRecipeObject<FluidStack> {

    private FluidStack fluid;
    private boolean exactCount = false, nbt = false;

    public FluidStackWrapper(FluidStack fluid) {
        this.fluid = fluid;
    }

    @Override
    public FluidStack getInternal() {
        return fluid;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FluidStackWrapper)) return false;
        FluidStackWrapper wrapper = (FluidStackWrapper) obj;
        if ((fluid.getFluid() != wrapper.fluid.getFluid()) ||
            (nbt && !FluidStack.areFluidStackTagsEqual(fluid, wrapper.fluid)) ||
            (exactCount ? fluid.amount != wrapper.fluid.amount : fluid.amount < wrapper.fluid.amount)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + fluid.getFluid().hashCode();
        if (exactCount) result = 31 * result + fluid.amount;
        if (nbt) result = 31 * result + fluid.tag.hashCode();
        return result;
    }
}
