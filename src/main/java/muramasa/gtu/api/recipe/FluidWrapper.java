package muramasa.gtu.api.recipe;

import net.minecraftforge.fluids.FluidStack;

public class FluidWrapper {

    private FluidStack fluid;
    private boolean count, nbt;
    private int hash = 1;

    public FluidWrapper(FluidStack fluid) {
        this.fluid = fluid;
        count = fluid.amount > 1;
        nbt = fluid.tag != null;
        hash = 31 * hash + fluid.getFluid().hashCode();
    }

    public FluidStack get() {
        return fluid.copy();
    }

    public int getCount() {
        return fluid.amount;
    }

    public int getHash() {
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FluidWrapper)) return false;
        FluidWrapper other = (FluidWrapper) obj;
        if ((count && other.fluid.amount < fluid.amount) &&
            (nbt || !FluidStack.areFluidStackTagsEqual(fluid, other.fluid))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
