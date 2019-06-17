package muramasa.gtu.api.recipe;

import muramasa.gtu.api.util.Utils;
import net.minecraftforge.fluids.FluidStack;

public class RecipeInputFluid implements IRecipeObject {

    private FluidStack[] fluids;
    private int hash;

    public RecipeInputFluid(FluidStack... fluids) {
        this.fluids = fluids;
        this.hash = hashCode(); //Initial hash
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RecipeInputFluid)) return false;
        RecipeInputFluid input = (RecipeInputFluid) obj;
        return Utils.doFluidsMatchAndSizeValid(fluids, input.fluids);
    }

    @Override
    public int hashCode() {
        if (hash == 0) {
            hash = 1;
            for (int i = 0; i < fluids.length; i++) {
                hash = 31 * hash + Utils.getFluidHash(fluids[i]);
            }
        }
        return hash;
    }
}
