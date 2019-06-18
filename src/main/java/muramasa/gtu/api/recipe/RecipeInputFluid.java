package muramasa.gtu.api.recipe;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import net.minecraftforge.fluids.FluidStack;

public class RecipeInputFluid implements IRecipeObject {

    private FluidWrapper[] fluids;
    private Int2IntArrayMap map = new Int2IntArrayMap();
    private int hash = 1;

    public RecipeInputFluid(FluidStack... fluids) {
        this.fluids = new FluidWrapper[fluids.length];
        for (int i = 0; i < fluids.length; i++) {
            this.fluids[i] = new FluidWrapper(fluids[i]);
            map.put(this.fluids[i].getHash(), i);
            hash += this.fluids[i].getHash();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RecipeInputFluid)) return false;
        RecipeInputFluid other = (RecipeInputFluid) obj;
        for (int i = 0; i < fluids.length; i++) {
            int recipeCount = other.fluids[other.map.get(fluids[i].getHash())].getCount();
            int invCount = fluids[i].getCount();
            if (invCount < recipeCount) return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
