package muramasa.antimatter.recipe.map;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.RecipeTag;
import muramasa.antimatter.recipe.Unifier;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RecipeBuilder {

    private RecipeMap recipeMap;
    protected ItemStack[] itemsOutput;
    protected List<RecipeIngredient> ingredientInput;
    protected FluidStack[] fluidsInput, fluidsOutput;
    protected int[] chances;
    protected int duration, special;
    protected long power;
    protected int amps;
    protected boolean hidden;
    protected Set<RecipeTag> tags = new ObjectOpenHashSet<>();

    public Recipe add() {
        Recipe r = build(duration, power, special, amps);
        addToMap(r);
        return r;
    }

    protected void addToMap(Recipe r) {
        recipeMap.add(r);
    }

    /**
     * Builds a recipe without adding it to a map.
     * @return the recipe.
     */
    public Recipe build(int duration, long power, int special, int amps) {
        if (itemsOutput != null && !Utils.areItemsValid(itemsOutput)) {
            Utils.onInvalidData("RECIPE BUILDER ERROR - OUTPUT ITEMS INVALID!");
            return Utils.getEmptyRecipe();
        }
        if (fluidsInput != null && !Utils.areFluidsValid(fluidsInput)) {
            Utils.onInvalidData("RECIPE BUILDER ERROR - INPUT FLUIDS INVALID!");
            return Utils.getEmptyRecipe();
        }
        if (fluidsOutput != null && !Utils.areFluidsValid(fluidsOutput)) {
            Utils.onInvalidData("RECIPE BUILDER ERROR - OUTPUT FLUIDS INVALID!");
            return Utils.getEmptyRecipe();
        }

        if (/*AntimatterConfig.RECIPE.ENABLE_RECIPE_UNIFICATION && */itemsOutput != null) {
            for (int i = 0; i < itemsOutput.length; i++) {
                itemsOutput[i] = Unifier.get(itemsOutput[i]);
            }
        }

        //TODO validate item/fluid inputs/outputs do not exceed machine gui values
        //TODO get a recipe build method to machine type so it can be overriden?
        //otherwise we get NPEs everywhere :S so keep this non-null
        if (ingredientInput == null) ingredientInput = Collections.emptyList();
        if (amps < 1) amps = 1;
        Recipe recipe = new Recipe(
                ingredientInput,
                itemsOutput != null ? itemsOutput.clone() : null,
                fluidsInput != null ? fluidsInput.clone() : null,
                fluidsOutput != null ? fluidsOutput.clone() : null,
                duration, power, special,amps
        );
        if (chances != null) recipe.addChances(chances);
        recipe.setHidden(hidden);
        recipe.addTags(new ObjectOpenHashSet<>(tags));

        return recipe;
    }

    public Recipe add(long duration, long power, long special) {
        return add(duration,power,special,1);
    }

    public Recipe add(long duration, long power, long special, int amps) {
        this.duration = (int)duration;
        this.power = power;
        this.special = (int)special;
        this.amps = amps;
        return add();
    }


    public Recipe add(long duration, long power) {
        return add(duration, power, 0);
    }

    public Recipe add(int duration) {
        return add(duration, 0, 0);
    }

    public RecipeBuilder ii(RecipeIngredient... stacks) {
        ingredientInput = Arrays.asList(stacks);
        return this;
    }

    public RecipeBuilder ii(List<RecipeIngredient> stacks) {
        ingredientInput = stacks;
        return this;
    }

    public RecipeBuilder io(ItemStack... stacks) {
        itemsOutput = stacks;
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

    public RecipeBuilder tags(RecipeTag... tags) {
        this.tags = new ObjectOpenHashSet<>(tags);
        return this;
    }

    public void clear() {
        itemsOutput = null;
        ingredientInput = null;
        fluidsInput = fluidsOutput = null;
        chances = null;
        duration = special = 0;
        power = 0;
        hidden = false;
        tags.clear();
    }

    public RecipeMap getMap() {
        return recipeMap;
    }

    public void setMap(RecipeMap recipeMap) {
        this.recipeMap = recipeMap;
    }
}
