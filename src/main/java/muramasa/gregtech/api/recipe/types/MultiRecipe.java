package muramasa.gregtech.api.recipe.types;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class MultiRecipe extends ItemRecipe {

    protected FluidStack[] fluidInputs, fluidOutputs;

    public MultiRecipe(ItemStack[] itemInputs, ItemStack[] itemOutputs, FluidStack[] fluidInputs, FluidStack[] fluidOutputs, int duration, int power) {
        super(itemInputs, itemOutputs, duration, power);
        this.fluidInputs = fluidInputs;
        this.fluidOutputs = fluidOutputs;
    }

    @Override
    public FluidStack[] getFluidInputs() {
        return fluidInputs;
    }

    @Override
    public FluidStack[] getFluidOutputs() {
        return fluidOutputs;
    }
}
