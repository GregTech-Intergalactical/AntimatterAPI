package muramasa.gregtech.api.recipe;

import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.machines.Machine;
import muramasa.gregtech.api.util.Utils;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

public class RecipeMap {

    private LinkedHashMap<String, ArrayList<Recipe>> recipeLookup = new LinkedHashMap<>();

    public RecipeMap(Machine machineType) {
        //TODO? need a type reference?
    }

    public Collection<Recipe> getRecipes() {
        ArrayList<Recipe> recipes = new ArrayList<>();
        for (ArrayList<Recipe> subList : recipeLookup.values()) {
            for (Recipe recipe : subList) {
                if (!recipes.contains(recipe)) {
                    recipes.add(recipe);
                }
            }
        }
        return recipes;
    }

    public static RecipeMap get(Machine type) {
        return Machines.get(type.getName()).getRecipeMap();
    }

    void add(Recipe recipe) {
        if (recipe.getInputs().length > 0) {
            String inputString;
            for (int i = 0; i < recipe.getInputs().length; i++) {
                inputString = Utils.getString(recipe.getInputs()[i]);
                ArrayList<Recipe> existingList = recipeLookup.get(inputString);
                if (existingList != null) {
                    existingList.add(recipe);
                } else {
                    ArrayList<Recipe> list = new ArrayList<>();
                    list.add(recipe);
                    recipeLookup.put(inputString, list);
                }
            }
        } else if (recipe.getFluidInputs().length > 0){
            for (int i = 0; i < recipe.getFluidInputs().length; i++) {
                String inputString = Utils.getString(recipe.getFluidInputs()[0]);
                ArrayList<Recipe> existingList = recipeLookup.get(inputString);
                if (existingList != null) {
                    existingList.add(recipe);
                } else {
                    ArrayList<Recipe> list = new ArrayList<>();
                    list.add(recipe);
                    recipeLookup.put(inputString, list);
                }
            }
        }
    }

    //TODO fix assumption inputs is never empty
    public static Recipe findRecipeItem(RecipeMap map, ItemStack[] inputs) {
        if (Utils.areStacksValid(inputs)) {
            if (map == null) return null;
            ArrayList<Recipe> matches = map.recipeLookup.get(Utils.getString(inputs[0]));
            if (matches == null) return null;
            for (int i = 0; i < matches.size(); i++) {
                if (inputs.length == matches.get(i).getInputs().length && Utils.doStacksMatchAndSizeValid(matches.get(i).getInputs(), inputs)) {
                    return matches.get(i);
                }
            }
        }
        return null;
    }

//    //TODO needed? only two machines
//    public static Recipe findRecipeFluid(String type, FluidStack[] fluidInputs) {
//        //TODO is broken as findRecipeItem has been updated and this has not
//        if (Utils.areFluidsValid(fluidInputs)) {
//            String mapString = Utils.getString(fluidInputs[0]);
//            ArrayList<Recipe> recipeMatches = allRecipeMaps.get(type).recipeLookup.get(mapString);
//            if (recipeMatches != null) {
//                if (recipeMatches.size() == 1) return recipeMatches.get(0);
//                for (int i = 0; i < recipeMatches.size(); i++) {
//                    if (Utils.doFluidsMatch(recipeMatches.get(i), fluidInputs)) {
//                        return recipeMatches.get(i);
//                    }
//                }
//            }
//        }
//        return null;
//    }

//    public static Recipe findRecipeBoth(String type, ItemStack[] inputs, FluidStack[] fluidInputs) {
//        if (Utils.areStacksValid(inputs) && Utils.areFluidsValid(fluidInputs)) {
//            String mapString = Utils.getString(inputs[0]);
//            //TODO if a recipe has both inputs, combine unlocalised strings for map key
//            ArrayList<Recipe> recipeMatches = allRecipeMaps.get(type).recipeLookupStack.get(mapString);
//            if (recipeMatches != null) {
//                if (recipeMatches.size() == 1) return recipeMatches.get(0);
//                for (int i = 0; i < recipeMatches.size(); i++) {
//                    if (Utils.doStacksMatch(recipeMatches.get(i), inputs) && Utils.doFluidsMatch(recipeMatches.get(i), fluidInputs)) {
//                        return recipeMatches.get(i);
//                    }
//                }
//            }
//        }
//        return null;
//    }
}
