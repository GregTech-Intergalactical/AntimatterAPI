package muramasa.itech.api.recipe;

import muramasa.itech.api.machines.Machine;
import muramasa.itech.api.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class RecipeMap {

    private static HashMap<String, RecipeMap> allRecipeMaps = new HashMap<>();

    private ArrayList<Recipe> recipes = new ArrayList<>();
    private HashMap<String, ArrayList<Recipe>> recipeLookupStack = new HashMap<>();
    private HashMap<String, ArrayList<Recipe>> recipeLookupFluidStack = new HashMap<>();

    public RecipeMap(Machine machineType) {
        allRecipeMaps.put(machineType.getName(), this);
    }

    public Collection<Recipe> getRecipes() {
        return recipes;
    }

    public static RecipeMap get(Machine type) {
        return get(type.getName());
    }

    public static RecipeMap get(String name) {
        return allRecipeMaps.get(name);
    }

    public static Collection<RecipeMap> getAll() {
        return allRecipeMaps.values();
    }

    void add(Recipe recipe) {
        if (recipe.getInputs().length > 0) {
            String inputString = Utils.getString(recipe.getInputs()[0]);
            if (recipeLookupStack.containsKey(inputString)) {
                recipeLookupStack.get(inputString).add(recipe);
            } else {
                ArrayList<Recipe> list = new ArrayList<>();
                list.add(recipe);
                recipeLookupStack.put(inputString, list);
                recipes.add(recipe);
            }
        } else if (recipe.getFluidInputs().length > 0){
            String inputString = Utils.getString(recipe.getFluidInputs()[0]);
            if (recipeLookupFluidStack.containsKey(inputString)) {
                recipeLookupFluidStack.get(inputString).add(recipe);
            } else {
                ArrayList<Recipe> list = new ArrayList<>();
                list.add(recipe);
                recipeLookupFluidStack.put(inputString, list);
                recipes.add(recipe);
            }
        }
    }



    public static Recipe findRecipeItem(String type, ItemStack[] inputs) {
        if (Utils.areStacksValid(inputs)) {
            RecipeMap map = allRecipeMaps.get(type);
            if (map == null) return null;
            ArrayList<Recipe> matches = map.recipeLookupStack.get(Utils.getString(inputs[0]));
            if (matches == null) return null;
            for (int i = 0; i < matches.size(); i++) {
                if (inputs.length == matches.get(i).getInputs().length && Utils.doStacksMatch(matches.get(i), inputs)) {
                    return matches.get(i);
                }
            }
        }
        return null;
    }

    //TODO needed? only two machines
    public static Recipe findRecipeFluid(String type, FluidStack[] fluidInputs) {
        if (Utils.areFluidsValid(fluidInputs)) {
            String mapString = Utils.getString(fluidInputs[0]);
            ArrayList<Recipe> recipeMatches = allRecipeMaps.get(type).recipeLookupFluidStack.get(mapString);
            if (recipeMatches != null) {
                if (recipeMatches.size() == 1) return recipeMatches.get(0);
                for (int i = 0; i < recipeMatches.size(); i++) {
                    if (Utils.doFluidsMatch(recipeMatches.get(i), fluidInputs)) {
                        return recipeMatches.get(i);
                    }
                }
            }
        }
        return null;
    }

    public static Recipe findRecipeBoth(String type, ItemStack[] inputs, FluidStack[] fluidInputs) {
        if (Utils.areStacksValid(inputs) && Utils.areFluidsValid(fluidInputs)) {
            String mapString = Utils.getString(inputs[0]);
            //TODO if a recipe has both inputs, combine unlocalised strings for map key
            ArrayList<Recipe> recipeMatches = allRecipeMaps.get(type).recipeLookupStack.get(mapString);
            if (recipeMatches != null) {
                if (recipeMatches.size() == 1) return recipeMatches.get(0);
                for (int i = 0; i < recipeMatches.size(); i++) {
                    if (Utils.doStacksMatch(recipeMatches.get(i), inputs) && Utils.doFluidsMatch(recipeMatches.get(i), fluidInputs)) {
                        return recipeMatches.get(i);
                    }
                }
            }
        }
        return null;
    }
}
