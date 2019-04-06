package muramasa.gtu.api.recipe;

import muramasa.gtu.api.machines.types.Machine;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.Ref;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public class RecipeBuilder {

    private RecipeMap recipeMap;
    private ItemStack[] stacksInput, stacksOutput;
    private FluidStack[] fluidsInput, fluidsOutput;
    private int[] chances;
    private int duration, special;
    private long power;
    private boolean hidden;

    public void add() {
        if (stacksInput != null && !Utils.areStacksValid(stacksInput)) {
            if (Ref.ENABLE_RECIPE_DEBUG_EXCEPTIONS) {
                throw new IllegalArgumentException("RECIPE BUILDER ERROR - INPUT STACKS INVALID!");
            } else {
                System.out.println("RECIPE BUILDER ERROR - INPUT STACKS INVALID!");
                return;
            }
        }
        if (stacksOutput != null && !Utils.areStacksValid(stacksOutput)) {
            if (Ref.ENABLE_RECIPE_DEBUG_EXCEPTIONS) {
                for (ItemStack stack : stacksOutput) {
                    System.out.println(stack != null ? stack.getDisplayName() : "NULL");
                }
                throw new IllegalArgumentException("RECIPE BUILDER ERROR - OUTPUT STACKS INVALID!");
            } else {
                System.out.println("RECIPE BUILDER ERROR - OUTPUT STACKS INVALID!");
                return;
            }
        }
        if (fluidsInput != null && !Utils.areFluidsValid(fluidsInput)) {
            if (Ref.ENABLE_RECIPE_DEBUG_EXCEPTIONS) {
                throw new IllegalArgumentException("RECIPE BUILDER ERROR - INPUT FLUIDS INVALID!");
            } else {
                System.out.println("RECIPE BUILDER ERROR - INPUT FLUIDS INVALID!");
                return;
            }
        }
        if (fluidsOutput != null && !Utils.areFluidsValid(fluidsOutput)) {
            if (Ref.ENABLE_RECIPE_DEBUG_EXCEPTIONS) {
                throw new IllegalArgumentException("RECIPE BUILDER ERROR - OUTPUT FLUIDS INVALID!");
            } else {
                System.out.println("RECIPE BUILDER ERROR - OUTPUT FLUIDS INVALID!");
                return;
            }
        }

        //TODO validate item/fluid inputs/outputs do not exceed machine gui values
        //TODO get a recipe build method to machine type so it can be overriden?

        //TODO FILTER EMPTY AND NULL STACKS
        Recipe recipe = new Recipe(
            stacksInput != null ? stacksInput.clone() : stacksInput,
            stacksOutput != null ? stacksOutput.clone() : stacksOutput,
            fluidsInput != null ? fluidsInput.clone() : fluidsInput,
            fluidsOutput != null ? fluidsOutput.clone() : fluidsOutput,
            duration, power, special
        );
        if (chances != null && chances.length == stacksOutput.length) recipe.addChances(chances);
        recipe.setHidden(hidden);
        recipeMap.add(recipe);
    }

    public void add(long duration, long power, long special) {
        this.duration = (int)duration;
        this.power = power;
        this.special = (int)special;
        add();
    }

    public void add(long duration, long power) {
        add(duration, power, 0);
    }

    public void add(int duration) {
        add(duration, 0, 0);
    }

    public RecipeBuilder get(Machine type) {
        return get(type.getRecipeMap());
    }

    public RecipeBuilder get(RecipeMap map) {
        stacksInput = stacksOutput = null;
        fluidsInput = fluidsOutput = null;
        chances = null;
        duration = special = 0;
        power = 0;
        hidden = false;
        recipeMap = map;
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

    public RecipeBuilder hide() {
        hidden = true;
        return this;
    }
}
