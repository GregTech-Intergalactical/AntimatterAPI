package muramasa.gregtech.integration.jei;

import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import muramasa.gregtech.api.data.Guis;
import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.gui.GuiData;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.MachineStack;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.api.recipe.Recipe;
import muramasa.gregtech.api.recipe.RecipeMap;
import muramasa.gregtech.integration.jei.category.RecipeMapCategory;
import muramasa.gregtech.integration.jei.wrapper.RecipeWrapper;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static muramasa.gregtech.api.machines.MachineFlag.*;

@JEIPlugin
public class GregTechJEIPlugin implements IModPlugin {

    private static IJeiRuntime runtime;
    private static List<Pair<RecipeMap, GuiData>> REGISTRY = new ArrayList<>();

    public static void registerCategory(RecipeMap map, GuiData gui) {
        REGISTRY.add(new Pair<>(map, gui));
    }

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        RecipeMapCategory.setGuiHelper(registry.getJeiHelpers().getGuiHelper());
        for (Machine type : MachineFlag.RECIPE.getTypes()) {
            if (type.hasFlag(BASIC)) {
                registry.addRecipeCategories(new RecipeMapCategory(Machines.get(type, Tier.LV)));
            } else if (type.hasFlag(MULTI)){
                if (type.getGui().hasSlots()) {
                    registry.addRecipeCategories(new RecipeMapCategory(Machines.get(type, Tier.MULTI)));
                } else {
                    registry.addRecipeCategories(new RecipeMapCategory(Machines.get(type, Tier.MULTI), Guis.MULTI_DISPLAY));
                }
            }
        }
        for (Pair<RecipeMap, GuiData> pair : REGISTRY) {
            registry.addRecipeCategories(new RecipeMapCategory(pair.getA(), pair.getB()));
        }
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
        for (Pair<RecipeMap, GuiData> pair : REGISTRY) {
            registry.addRecipes(pair.getA().getRecipes(true), pair.getA().getCategoryId());
            registry.handleRecipes(Recipe.class, RecipeWrapper::new, pair.getA().getCategoryId());
        }
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
