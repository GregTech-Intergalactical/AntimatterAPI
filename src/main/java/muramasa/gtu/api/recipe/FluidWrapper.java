package muramasa.gtu.api.recipe;

import net.minecraftforge.fluids.FluidStack;

import java.util.Collections;
import java.util.Set;

public class FluidWrapper {

    private FluidStack fluid;
    private boolean count, nbt;
    private int hash;

    public FluidWrapper(FluidStack fluid, Set<RecipeTag> tags) {
        this.fluid = fluid;
        count = fluid.amount > 1;
        nbt = fluid.tag != null && !tags.contains(RecipeTag.IGNORE_NBT);
        long tempHash = 1; //long hash used to handle many inputs with nbt hashes
        tempHash = 31 * tempHash + fluid.getFluid().hashCode();
        if (nbt) tempHash = 31 * tempHash + fluid.tag.hashCode();
        hash = (int) (tempHash ^ (tempHash >>> 32)); //int version of the hash for the actual comparision
    }

    public FluidWrapper(FluidStack fluid) {
        this(fluid, Collections.emptySet());
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
            (nbt && !FluidStack.areFluidStackTagsEqual(fluid, other.fluid))) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
