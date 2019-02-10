package muramasa.gregtech.api.recipe.types;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class FluidRecipe extends AbstractRecipe {

    protected FluidStack[] inputs, outputs;

    public FluidRecipe(FluidStack[] inputs, FluidStack[] outputs, int duration, int power) {
        super(duration, power);

    }

    @Override
    ItemStack[] getItemInputs() {
        return new ItemStack[0];
    }

    @Override
    ItemStack[] getItemOutputs() {
        return new ItemStack[0];
    }

    @Override
    FluidStack[] getFluidInputs() {
        return inputs;
    }

    @Override
    FluidStack[] getFluidOutputs() {
        return outputs;
    }
}
