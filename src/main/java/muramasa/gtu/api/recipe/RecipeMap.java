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

    public static RecipeMap ORE_BY_PRODUCTS = new RecipeMap("ore_byproducts", 100);

//    public static RecipeMap SMELTING = new RecipeMap("smelting", 100);

    public static RecipeMap STEAM_FUELS = new RecipeMap("steam_fuels", "Fuel Value: ", " EU", 1);
    public static RecipeMap GAS_FUELS = new RecipeMap("gas_fuels", "Fuel Value: ", " EU", 20);
    public static RecipeMap COMBUSTION_FUELS = new RecipeMap("combustion_fuels", "Fuel Value: ", " EU", 20);
    public static RecipeMap NAQUADAH_FUELS = new RecipeMap("naquadah_fuels", "Fuel Value: ", " EU", 20);
    public static RecipeMap PLASMA_FUELS = new RecipeMap("plasma_fuels", "Fuel Value: ", " EU", 100);

    private HashMap<IRecipeObject, Recipe> LOOKUP;
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
        return map.LOOKUP.get(new RecipeInputItem(items));
    }

    @Nullable
    public static Recipe findRecipeFluid(RecipeMap map, long voltage, FluidStack[] fluids) {
        if (map == null || !Utils.areFluidsValid(fluids)) return null;
        return map.LOOKUP.get(new RecipeInputFluid(fluids));
    }
}
