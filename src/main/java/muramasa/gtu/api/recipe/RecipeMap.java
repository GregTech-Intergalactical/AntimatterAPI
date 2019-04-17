package muramasa.gtu.api.recipe;

import com.google.common.collect.Lists;
import muramasa.gtu.api.util.GTLoc;
import muramasa.gtu.api.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.*;

public class RecipeMap {

    public static RecipeMap ORE_BY_PRODUCTS = new RecipeMap("ore_byproducts", 100);

    public static RecipeMap STEAM_FUELS = new RecipeMap("steam_fuels", "Fuel Value: ", " EU", 1);
    public static RecipeMap GAS_FUELS = new RecipeMap("gas_fuels", "Fuel Value: ", " EU", 20);
    public static RecipeMap COMBUSTION_FUELS = new RecipeMap("combustion_fuels", "Fuel Value: ", " EU", 20);
    public static RecipeMap NAQUADAH_FUELS = new RecipeMap("naquadah_fuels", "Fuel Value: ", " EU", 20);
    public static RecipeMap PLASMA_FUELS = new RecipeMap("plasma_fuels", "Fuel Value: ", " EU", 100);

    private LinkedHashMap<String, List<Recipe>> LOOKUP;
    private String categoryId, categoryName;
    private String specialPre = "", specialPost = "";

    public RecipeMap(String jeiCategoryId, int initialSize) {
        this.categoryId = "gt.recipe_map." + jeiCategoryId;
        this.categoryName = GTLoc.get("jei.category." + jeiCategoryId + ".name");
        LOOKUP = new LinkedHashMap<>(initialSize);
    }

    public RecipeMap(String jeiCategoryId, String specialPre, String specialPost, int initialSize) {
        this(jeiCategoryId, initialSize);
        this.specialPre = specialPre;
        this.specialPost = specialPost;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public static void register() {

    }

    public Collection<Recipe> getRecipes(boolean filterHidden) {
        List<Recipe> recipes = new ArrayList<>();
        LOOKUP.values().forEach(l -> l.forEach(r -> {
            //TODO remove contains check, duplicate recipes are present in LOOKUP
            if (!recipes.contains(r) && !(r.isHidden() && filterHidden)) recipes.add(r);
        }));
        return recipes;
    }

    void add(Recipe recipe) {
        String input;
        List<Recipe> existing;
        if (recipe.hasInputItems() && recipe.hasInputFluids()) {
            for (int i = 0; i < recipe.getInputItems().length; i++) {
                input = Utils.getString(recipe.getInputItems()[i], recipe.getInputFluids()[0]);
                if ((existing = LOOKUP.get(input)) != null) existing.add(recipe);
                else LOOKUP.put(input, Lists.newArrayList(recipe));
            }
        } else if (recipe.hasInputItems() && !recipe.hasInputFluids()){
            for (int i = 0; i < recipe.getInputItems().length; i++) {
                input = Utils.getString(recipe.getInputItems()[i]);
                if ((existing = LOOKUP.get(input)) != null) existing.add(recipe);
                else LOOKUP.put(input, Lists.newArrayList(recipe));
            }
        } else if (!recipe.hasInputItems() && recipe.hasInputFluids()) {
            for (int i = 0; i < recipe.getInputFluids().length; i++) {
                input = Utils.getString(recipe.getInputFluids()[i]);
                if ((existing = LOOKUP.get(input)) != null) existing.add(recipe);
                else LOOKUP.put(input, Lists.newArrayList(recipe));
            }
        }
    }

    @Nullable
    //TODO take into account machine tier
    public static Recipe findRecipeItem(RecipeMap map, ItemStack[] items) {
        if (map == null || !Utils.areItemsValid(items)) return null;
        List<Recipe> matches = map.LOOKUP.get(Utils.getString(items[0]));
        if (matches == null) return null;
        int size = matches.size();
        if (size == 1) return matches.get(0);
        Recipe match;
        for (int i = 0; i < size; i++) {
            match = matches.get(i);
            if (!Utils.doItemsMatchAndSizeValid(match.getInputItems(), items)) continue;
            return match;
        }
        return null;
    }

    @Nullable
    public static Recipe findRecipeFluid(RecipeMap map, FluidStack[] fluids) {
        if (map == null || !Utils.areFluidsValid(fluids)) return null;
        List<Recipe> matches = map.LOOKUP.get(Utils.getString(fluids[0]));
        if (matches == null) return null;
        int size = matches.size();
        if (size == 1) return matches.get(0);
        Recipe match;
        for (int i = 0; i < size; i++) {
            match = matches.get(i);
            if (!Utils.doFluidsMatchAndSizeValid(match.getInputFluids(), fluids)) continue;
            return match;
        }
        return null;
    }

    @Nullable
    public static Recipe findRecipeItemFluid(RecipeMap map, ItemStack[] items, FluidStack[] fluids) {
        if (map == null || !Utils.areItemsValid(items) || !Utils.areFluidsValid(fluids)) return null;
        List<Recipe> matches = map.LOOKUP.get(Utils.getString(items[0], fluids[0]));
        if (matches == null) return null;
        int size = matches.size();
        if (size == 1) return matches.get(0);
        Recipe match;
        for (int i = 0; i < size; i++) {
            match = matches.get(i);
            if (!Utils.doItemsMatchAndSizeValid(match.getInputItems(), items) || !Utils.doFluidsMatchAndSizeValid(match.getInputFluids(), fluids)) continue;
            return match;
        }
        return null;
    }
}
