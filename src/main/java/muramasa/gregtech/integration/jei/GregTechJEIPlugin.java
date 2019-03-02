package muramasa.gregtech.integration.jei;

import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.MachineStack;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.api.recipe.Recipe;
import muramasa.gregtech.api.recipe.RecipeMap;
import muramasa.gregtech.integration.jei.category.MachineRecipeCategory;
import muramasa.gregtech.integration.jei.category.MultiMachineRecipeCategory;
import muramasa.gregtech.integration.jei.category.RecipeMapCategory;
import muramasa.gregtech.integration.jei.wrapper.RecipeWrapper;

import java.util.LinkedList;
import java.util.List;

import static muramasa.gregtech.api.machines.MachineFlag.*;

@JEIPlugin
public class GregTechJEIPlugin implements IModPlugin {

    private static IJeiRuntime runtime;

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        for (Machine type : MachineFlag.RECIPE.getTypes()) {
            if (type.hasFlag(BASIC)) {
                registry.addRecipeCategories(new MachineRecipeCategory(guiHelper, type));
            } else if (type.hasFlag(MULTI)){
                registry.addRecipeCategories(new MultiMachineRecipeCategory(guiHelper, type));
            }
        }
        registry.addRecipeCategories(new RecipeMapCategory(guiHelper, RecipeMap.ORE_BY_PRODUCTS));
    }

    @Override
    public void register(IModRegistry registry) {
        for (Machine type : MachineFlag.RECIPE.getTypes()) {
            registry.addRecipes(type.getRecipeMap().getRecipes(true), type.getRecipeMap().getCategoryId());
            registry.handleRecipes(Recipe.class, RecipeWrapper::new, type.getRecipeMap().getCategoryId());
            for (Tier tier : type.getTiers()) {
                registry.addRecipeCatalyst(new MachineStack(type, tier).asItemStack(), type.getRecipeMap().getCategoryId());
            }
        }
        registry.addRecipes(RecipeMap.ORE_BY_PRODUCTS.getRecipes(true), RecipeMap.ORE_BY_PRODUCTS.getCategoryId());
        registry.handleRecipes(Recipe.class, RecipeWrapper::new, RecipeMap.ORE_BY_PRODUCTS.getCategoryId());
    }

    public static void showCategory(Machine... types) {
        if (runtime != null) {
            List<String> list = new LinkedList<>();
            for (int i = 0; i < types.length; i++) {
                if (!types[i].hasFlag(RECIPE)) continue;
                list.add(types[i].getRecipeMap().getCategoryId());
            }
            runtime.getRecipesGui().showCategories(list);
        }
    }
}
