package muramasa.antimatter.recipe.map;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.RecipeTag;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class RecipeBuilder {

    private RecipeMap<? extends RecipeBuilder> recipeMap;
    protected List<ItemStack> itemsOutput = new ObjectArrayList<>();
    protected List<RecipeIngredient> ingredientInput = new ObjectArrayList<>();
    protected List<FluidStack> fluidsInput, fluidsOutput = new ObjectArrayList<>();
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
     *
     * @return the recipe.
     */
    public Recipe build(int duration, long power, int special, int amps) {
        if (itemsOutput != null && itemsOutput.size() > 0 && !Utils.areItemsValid(itemsOutput.toArray(new ItemStack[0]))) {
            Utils.onInvalidData("RECIPE BUILDER ERROR - OUTPUT ITEMS INVALID!");
            return Utils.getEmptyRecipe();
        }
        if (fluidsInput != null && fluidsInput.size() > 0 && !Utils.areFluidsValid(fluidsInput.toArray(new FluidStack[0]))) {
            Utils.onInvalidData("RECIPE BUILDER ERROR - INPUT FLUIDS INVALID!");
            return Utils.getEmptyRecipe();
        }
        if (fluidsOutput != null && fluidsOutput.size() > 0 && !Utils.areFluidsValid(fluidsOutput.toArray(new FluidStack[0]))) {
            Utils.onInvalidData("RECIPE BUILDER ERROR - OUTPUT FLUIDS INVALID!");
            return Utils.getEmptyRecipe();
        }

        /*if (itemsOutput != null) {
            for (int i = 0; i < itemsOutput.size(); i++) {
                itemsOutput.add(i, Unifier.get(itemsOutput.get(i)));
            }
        }*/

        if (ingredientInput == null) ingredientInput = Collections.emptyList();
        if (amps < 1) amps = 1;
        Recipe recipe = new Recipe(
                ingredientInput,
                itemsOutput != null ? itemsOutput.toArray(new ItemStack[0]) : null,
                fluidsInput != null ? fluidsInput.toArray(new FluidStack[0]) : null,
                fluidsOutput != null ? fluidsOutput.toArray(new FluidStack[0]) : null,
                duration, power, special, amps
        );
        if (chances != null) recipe.addChances(chances);
        recipe.setHidden(hidden);
        recipe.addTags(new ObjectOpenHashSet<>(tags));

        return recipe;
    }

    public Recipe add(long duration, long power, long special) {
        return add(duration, power, special, 1);
    }

    public Recipe add(long duration, long power, long special, int amps) {
        this.duration = (int) duration;
        this.power = power;
        this.special = (int) special;
        this.amps = amps;
        return add();
    }

    public Recipe add(long duration, long power) {
        return add(duration, power, this.special);
    }

    public Recipe add(int duration) {
        return add(duration, 0, this.special);
    }

    public RecipeBuilder ii(RecipeIngredient... stacks) {
        ingredientInput.addAll(Arrays.asList(stacks));
        return this;
    }

    public RecipeBuilder ii(List<RecipeIngredient> stacks) {
        ingredientInput.addAll(stacks);
        return this;
    }

    public RecipeBuilder io(ItemStack... stacks) {
        itemsOutput.addAll(Arrays.asList(stacks));
        return this;
    }

    public RecipeBuilder io(List<ItemStack> stacks) {
        itemsOutput.addAll(stacks);
        return this;
    }

    public RecipeBuilder fi(FluidStack... stacks) {
        fluidsInput.addAll(Arrays.asList(stacks));
        return this;
    }

    public RecipeBuilder fi(List<FluidStack> stacks) {
        fluidsInput.addAll(stacks);
        return this;
    }

    public RecipeBuilder fo(FluidStack... stacks) {
        fluidsOutput.addAll(Arrays.asList(stacks));
        return this;
    }

    public RecipeBuilder fo(List<FluidStack> stacks) {
        fluidsOutput.addAll(stacks);
        return this;
    }

    /**
     * 10 = 10%, 75 = 75% etc
     **/
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
        itemsOutput = new ObjectArrayList<>();
        ingredientInput = new ObjectArrayList<>();
        fluidsInput = new ObjectArrayList<>();
        fluidsOutput = new ObjectArrayList<>();
        chances = null;
        duration = special = 0;
        power = 0;
        hidden = false;
        tags.clear();
    }

    public RecipeMap<?> getMap() {
        return recipeMap;
    }

    public void setMap(RecipeMap<?> recipeMap) {
        this.recipeMap = recipeMap;
    }
}
