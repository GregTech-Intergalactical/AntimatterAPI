package muramasa.gregtech.api.recipe;

import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.api.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class RecipeBuilder {

    private RecipeMap recipeMap;
    private ItemStack[] stacksInput, stacksOutput;
    private FluidStack[] fluidsInput, fluidsOutput;
    private int[] chances;

    public void add(int duration, int power, int special) {
        if (stacksInput != null && !Utils.areStacksValid(stacksInput)) return;
        if (stacksOutput != null && !Utils.areStacksValid(stacksOutput)) return;
        if (fluidsInput != null && !Utils.areFluidsValid(fluidsInput)) return;
        if (fluidsOutput != null && !Utils.areFluidsValid(fluidsOutput)) return;

        //TODO validate item/fluid inputs/outputs do not exceed machine gui values
        //TODO get a recipe build method to machine type so it can be overriden?
        Recipe recipe = new Recipe(stacksInput, stacksOutput, fluidsInput, fluidsOutput, duration, power, special);
        if (chances != null) recipe.addChances(chances);
        recipeMap.add(recipe);
    }

    public void add(int duration, int power) {
        add(duration, power, 0);
    }

    public void add(int duration) {
        add(duration, 0);
    }

    public RecipeBuilder get(Machine type) {
        stacksInput = stacksOutput = null;
        fluidsInput = fluidsOutput = null;
        recipeMap = type.getRecipeMap();
        return this;
    }

    public RecipeBuilder ii(ItemStack... stacks) {
        stacksInput = stacks;
        return this;
    }

    public RecipeBuilder ii(List<ItemStack> stacks) {
        stacksInput = stacks.toArray(new ItemStack[0]);
        return this;
    }

    public RecipeBuilder io(ItemStack... stacks) {
        stacksOutput = stacks;
        return this;
    }

    public RecipeBuilder io(List<ItemStack> stacks) {
        stacksOutput = stacks.toArray(new ItemStack[0]);
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

    /** 10 = 10%, 75 = 75% etc **/
    public RecipeBuilder chances(int... values) {
        chances = values;
        return this;
    }
}
