package muramasa.gregtech.api.machines.types;

import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.recipe.Recipe;
import muramasa.gregtech.api.recipe.RecipeMap;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import static muramasa.gregtech.api.machines.MachineFlag.*;

public class ItemFluidMachine extends BasicMachine {

    public ItemFluidMachine(String name, MachineFlag... extraFlags) {
        super(name, extraFlags);
        addFlags(FLUID_INPUT);
    }

    @Override
    public Recipe findRecipe(ItemStack[] inputs, FluidStack... fluidInputs) {
        return RecipeMap.findRecipeItemFluid(recipeMap, inputs, fluidInputs);
    }
}
