package muramasa.gregtech.api.recipe;

import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.api.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

public class RecipeBuilder {

    private RecipeMap recipeMap;
    private ItemStack[] stacksInput, stacksOutput;
    private FluidStack[] fluidsInput, fluidsOutput;

    public RecipeBuilder(RecipeMap recipeMap) {
        this.recipeMap = recipeMap;
    }

    public static RecipeBuilder add(Machine type) {
        return new RecipeBuilder(type.getRecipeMap());
    }

    public void build(int duration, int power) {
        if (stacksInput != null && !Utils.areStacksValid(stacksInput)) return;
        if (stacksOutput != null && !Utils.areStacksValid(stacksOutput)) return;
        if (fluidsInput != null && !Utils.areFluidsValid(fluidsInput)) return;
        if (fluidsOutput != null && !Utils.areFluidsValid(fluidsOutput)) return;
        recipeMap.add(new Recipe(stacksInput, stacksOutput, fluidsInput, fluidsOutput, duration, power));
    }

    public RecipeBuilder ii(ItemStack... stacks) {
        stacksInput = stacks;
        return this;
    }

    public RecipeBuilder io(ItemStack... stacks) {
        stacksOutput = stacks;
        return this;
    }

    public RecipeBuilder fi(FluidStack... stacks) {
        fluidsInput = stacks;
        return this;
    }

    public RecipeBuilder fo(FluidStack... stacks) {
        fluidsOutput = stacks;
        return this;
    }
}
