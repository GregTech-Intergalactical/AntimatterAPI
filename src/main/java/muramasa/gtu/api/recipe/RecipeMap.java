package muramasa.gtu.api.recipe;

import muramasa.gtu.api.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

public class RecipeMap {

    private HashMap<IRecipeObject, Recipe> LOOKUP;
    private String categoryId;
    private RecipeBuilder builder;

    public RecipeMap(String categoryId, RecipeBuilder builder) {
        this.categoryId = "gt.recipe_map." + categoryId;
        this.builder = builder;
        this.builder.setMap(this);
        LOOKUP = new LinkedHashMap<>();
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return Utils.trans("jei.category." + categoryId + ".name");
    }

    public String getExtraString(String id) {
        return Utils.trans("jei.category." + categoryId + "." + id + ".name");
    }

    public RecipeBuilder RB() {
        return builder;
    }

    //TODO validate there are no duplicates
    public Collection<Recipe> getRecipes(boolean filterHidden) {
        return LOOKUP.values().stream().filter(r -> !(r.isHidden() && filterHidden)).collect(Collectors.toList());
    }

    void add(Recipe recipe) {
        IRecipeObject input = null;
        if (recipe.hasInputItems() && !recipe.hasInputFluids()) {
            input = new RecipeInputItem(recipe.getInputItems());
        } else if (!recipe.hasInputItems() && recipe.hasInputFluids()) {
            input = new RecipeInputFluid(recipe.getInputFluids());
        }
        if (LOOKUP.containsKey(input)) {
            Utils.printError("Duplicate recipe detected, skipping!: " + recipe);
            return;
        }
        LOOKUP.put(input, recipe);
    }

    @Nullable
    //TODO take into account machine tier
    public static Recipe findRecipeItem(RecipeMap map, long voltage, ItemStack[] items) {
        if (map == null || !Utils.areItemsValid(items)) return null;
        return (Recipe) map.LOOKUP.get(new RecipeInputItem(items));
    }

    @Nullable
    public static Recipe findRecipeFluid(RecipeMap map, long voltage, FluidStack[] fluids) {
        if (map == null || !Utils.areFluidsValid(fluids)) return null;
        return (Recipe) map.LOOKUP.get(new RecipeInputFluid(fluids));
    }
}
