package muramasa.gregtech.api.recipe;

import muramasa.gregtech.api.util.Utils;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;

public class RecipeMap {

    private LinkedHashMap<String, ArrayList<Recipe>> recipeLookup;

    public RecipeMap(int initialSize) {
        recipeLookup = new LinkedHashMap<>(initialSize);
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

    void add(Recipe recipe) {
        if (recipe.hasInputStacks() && recipe.hasInputFluids()) {
            String inputString;
            for (int i = 0; i < recipe.getInputStacks().length; i++) {
                inputString = Utils.getString(recipe.getInputStacks()[i], recipe.getInputFluids()[0]);
                ArrayList<Recipe> existing = recipeLookup.get(inputString);
                if (existing != null) {
                    existing.add(recipe);
                } else {
                    ArrayList<Recipe> list = new ArrayList<>(1);
                    list.add(recipe);
                    recipeLookup.put(inputString, list);
                }
            }
        } else {
            if (recipe.hasInputStacks()) {
                String inputString;
                for (int i = 0; i < recipe.getInputStacks().length; i++) {
                    inputString = Utils.getString(recipe.getInputStacks()[i]);
                    ArrayList<Recipe> existing = recipeLookup.get(inputString);
                    if (existing != null) {
                        existing.add(recipe);
                    } else {
                        ArrayList<Recipe> list = new ArrayList<>(1);
                        list.add(recipe);
                        recipeLookup.put(inputString, list);
                    }
                }
            } else if (recipe.hasInputFluids()){
                for (int i = 0; i < recipe.getInputFluids().length; i++) {
                    String inputString = Utils.getString(recipe.getInputFluids()[i]);
                    ArrayList<Recipe> existing = recipeLookup.get(inputString);
                    if (existing != null) {
                        existing.add(recipe);
                    } else {
                        ArrayList<Recipe> list = new ArrayList<>(1);
                        list.add(recipe);
                        recipeLookup.put(inputString, list);
                    }
                }
            }
        }
    }

    public static Recipe findRecipeItem(RecipeMap map, ItemStack[] stacks) {
        if (map == null) return null;
        if (Utils.areStacksValid(stacks)) {
            ArrayList<Recipe> matches = map.recipeLookup.get(Utils.getString(stacks[0]));
            if (matches == null) return null;
            int size = matches.size();
            for (int i = 0; i < size; i++) {
                if (stacks.length == matches.get(i).getInputStacks().length && Utils.doStacksMatchAndSizeValid(matches.get(i).getInputStacks(), stacks)) {
                    return matches.get(i);
                }
            }
        }
        return null;
    }

//    public static Recipe findRecipeFluid(RecipeMap map, FluidStack[] inputs) {
//        if (map == null) return null;
//        if (Utils.areFluidsValid(inputs)) {
//            ArrayList<Recipe> matches = map.recipeLookup.get(Utils.getString(inputs[0]));
//            if (matches == null) return null;
//            int size = matches.size();
//            for (int i = 0; i < size; i++) {
//                if (inputs.length == matches.get(i).getInputFluids().length && Utils.doStacksMatchAndSizeValid(matches.get(i).getInputFluids(), inputs)) {
//                    return matches.get(i);
//                }
//            }
//        }
//        return null;
//    }

    public static Recipe findRecipeItemFluid(RecipeMap map, ItemStack[] stacks, FluidStack[] fluids) {
        if (map == null) return null;
        if (Utils.areStacksValid(stacks) && Utils.areFluidsValid(fluids)) {
            ArrayList<Recipe> matches = map.recipeLookup.get(Utils.getString(stacks[0], fluids[0]));
            if (matches == null) return null;
            System.out.println("PASS MATCHES");
            int size = matches.size();
            for (int i = 0; i < size; i++) {
                if (stacks.length == matches.get(i).getInputStacks().length && Utils.doStacksMatchAndSizeValid(matches.get(i).getInputStacks(), stacks) && Utils.doFluidsMatchAndSizeValid(matches.get(i).getInputFluids(), fluids)) {
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
