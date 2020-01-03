package muramasa.antimatter.recipe;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class RecipeInputFlat extends RecipeInput {

    //Alternate version of RecipeInput that only compares the raw hash
    //This is so input count does not effect checking for recipe map duplicates
    public RecipeInputFlat(ItemStack[] items, FluidStack[] fluids) {
        super(items, fluids);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof RecipeInput)) return false;
        RecipeInput other = (RecipeInput) obj;
        return hash == other.hash;
    }
}
