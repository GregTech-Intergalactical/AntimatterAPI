package muramasa.gtu.api.recipe;

import com.google.common.collect.Lists;
import muramasa.gtu.api.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nullable;
import java.util.*;

public class RecipeMap {

    public static RecipeMap ORE_BY_PRODUCTS = new RecipeMap("ore_byproducts", 100);

//    public static RecipeMap SMELTING = new RecipeMap("smelting", 100);

    public static RecipeMap STEAM_FUELS = new RecipeMap("steam_fuels", "Fuel Value: ", " EU", 1);
    public static RecipeMap GAS_FUELS = new RecipeMap("gas_fuels", "Fuel Value: ", " EU", 20);
    public static RecipeMap COMBUSTION_FUELS = new RecipeMap("combustion_fuels", "Fuel Value: ", " EU", 20);
    public static RecipeMap NAQUADAH_FUELS = new RecipeMap("naquadah_fuels", "Fuel Value: ", " EU", 20);
    public static RecipeMap PLASMA_FUELS = new RecipeMap("plasma_fuels", "Fuel Value: ", " EU", 100);

    private HashMap<IRecipeObject, List<Recipe>> LOOKUP;
    private String categoryId, categoryName;
    private String specialPre = "", specialPost = ""; //TODO move to lang

    public RecipeMap(String jeiCategoryId, int initialSize) {
        this.categoryId = "gt.recipe_map." + jeiCategoryId;
        this.categoryName = Utils.trans("jei.category." + jeiCategoryId + ".name");
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

    public Collection<Recipe> getRecipes(boolean filterHidden) {
        List<Recipe> recipes = new ArrayList<>();
        LOOKUP.values().forEach(l -> l.forEach(r -> {
            //TODO remove contains check, duplicate recipes are present in LOOKUP
            if (!recipes.contains(r) && !(r.isHidden() && filterHidden)) recipes.add(r);
        }));
        return recipes;
    }

    void add(Recipe recipe) {
        List<Recipe> existing;
        if (recipe.hasInputItems() && !recipe.hasInputFluids()) {
            for (int i = 0; i < recipe.getInputItems().length; i++) {
                ItemStackWrapper input = new ItemStackWrapper(recipe.getInputItems()[i]);
                if ((existing = LOOKUP.get(input)) != null) existing.add(recipe);
                else LOOKUP.put(input, Lists.newArrayList(recipe));
            }
        } else if (!recipe.hasInputItems() && recipe.hasInputFluids()) {
            for (int i = 0; i < recipe.getInputFluids().length; i++) {
                FluidStackWrapper input = new FluidStackWrapper(recipe.getInputFluids()[i]);
                if ((existing = LOOKUP.get(input)) != null) existing.add(recipe);
                else LOOKUP.put(input, Lists.newArrayList(recipe));
            }
        }
    }

    @Nullable
    //TODO take into account machine tier
    public static Recipe findRecipeItem(RecipeMap map, ItemStack[] items) {
        if (map == null || !Utils.areItemsValid(items)) return null;
        List<Recipe> matches = map.LOOKUP.get(new ItemStackWrapper(items[0]));
        if (matches == null) return null;
        int size = matches.size();
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
        List<Recipe> matches = map.LOOKUP.get(new FluidStackWrapper(fluids[0]));
        if (matches == null) return null;
        int size = matches.size();
        Recipe match;
        for (int i = 0; i < size; i++) {
            match = matches.get(i);
            if (!Utils.doFluidsMatchAndSizeValid(match.getInputFluids(), fluids)) continue;
            return match;
        }
        return null;
    }
}
