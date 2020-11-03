package muramasa.antimatter.recipe;

import net.minecraftforge.fluids.FluidStack;

import java.util.Collections;
import java.util.Set;

public class FluidWrapper {

    public FluidStack fluid;
    private boolean count, nbt;
    private int hash;

    public FluidWrapper(FluidStack fluid, Set<RecipeTag> tags) {
        this.fluid = fluid;
        count = fluid.getAmount() > 1;
        nbt = fluid.hasTag() && !tags.contains(RecipeTag.IGNORE_NBT);
        long tempHash = 1; //long hash used to handle many inputs with nbt hashes
        tempHash = 31 * tempHash + fluid.getFluid().hashCode(); //TODO validate? potentially not persistent on relaunches?
        if (nbt) tempHash = 31 * tempHash + fluid.getTag().hashCode();
        hash = (int) (tempHash ^ (tempHash >>> 32)); //int version of the hash for the actual comparision
    }

    public FluidWrapper(FluidStack fluid) {
        this(fluid, Collections.emptySet());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FluidWrapper)) return false;
        FluidWrapper other = (FluidWrapper) obj;
        return ((fluid.getFluid() == other.fluid.getFluid()) && !count || other.fluid.getAmount() >= fluid.getAmount()) || (!nbt || FluidStack.areFluidStackTagsEqual(fluid, other.fluid));
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
