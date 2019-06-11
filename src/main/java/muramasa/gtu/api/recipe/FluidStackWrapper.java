package muramasa.gtu.api.recipe;

import net.minecraftforge.fluids.FluidStack;

public class FluidStackWrapper implements IRecipeObject<FluidStack> {

    private FluidStack stack;

    public FluidStackWrapper(FluidStack fluid) {
        this.stack = fluid;
    }

    @Override
    public FluidStack getInternal() {
        return stack;
    }

    @Override
    public boolean equals(Object obj) {
        return stack.equals(obj);
    }

    @Override
    public int hashCode() {
        return stack.hashCode();
    }
}
