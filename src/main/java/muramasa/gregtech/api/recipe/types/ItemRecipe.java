package muramasa.gregtech.api.recipe.types;

import muramasa.gregtech.api.recipe.types.AbstractRecipe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class ItemRecipe extends AbstractRecipe {

    protected ItemStack[] inputs, outputs;

    public ItemRecipe(ItemStack[] inputs, ItemStack[] outputs, int duration, int power) {
        super(duration, power);
        this.inputs = inputs;
        this.outputs = outputs;
    }

    @Override
    ItemStack[] getItemInputs() {
        return inputs;
    }

    @Override
    ItemStack[] getItemOutputs() {
        return outputs;
    }

    @Override
    FluidStack[] getFluidInputs() {
        return new FluidStack[0];
    }

    @Override
    FluidStack[] getFluidOutputs() {
        return new FluidStack[0];
    }
}
