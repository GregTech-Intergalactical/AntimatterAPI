package muramasa.gtu.api.recipe;

import net.minecraftforge.fluids.FluidStack;

public class FluidStackWrapper implements IRecipeObject<FluidStack> {

    private FluidStack fluid;
    private boolean nbt;

    public FluidStackWrapper(FluidStack fluid) {
        this.fluid = fluid;
        this.nbt = fluid.tag != null;
    }

    @Override
    public FluidStack getInternal() {
        return fluid;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FluidStackWrapper)) return false;
        FluidStackWrapper wrapper = (FluidStackWrapper) obj;
        if ((fluid.getFluid() == wrapper.fluid.getFluid()) ||
            (wrapper.fluid.amount >= fluid.amount) ||
            (nbt && FluidStack.areFluidStackTagsEqual(fluid, wrapper.fluid))) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + fluid.getFluid().hashCode();
        if (nbt) result = 31 * result + fluid.tag.hashCode();
        return result;
    }
}
