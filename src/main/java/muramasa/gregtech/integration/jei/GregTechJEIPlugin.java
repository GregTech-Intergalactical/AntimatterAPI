package muramasa.gregtech.integration.jei;

import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import muramasa.gregtech.api.machines.MachineFlag;
import muramasa.gregtech.api.machines.MachineStack;
import muramasa.gregtech.api.machines.Tier;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.api.recipe.Recipe;
import muramasa.gregtech.integration.jei.categories.MachineRecipeCategory;
import muramasa.gregtech.integration.jei.categories.MultiMachineRecipeCategory;

import java.util.LinkedList;
import java.util.List;

import static muramasa.gregtech.api.machines.MachineFlag.BASIC;
import static muramasa.gregtech.api.machines.MachineFlag.MULTI;

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
            if (type.hasFlag(BASIC) /*|| (type.getSlots() != null && type.hasFlag(MULTI))*/) {
                registry.addRecipeCategories(new MachineRecipeCategory(guiHelper, type));
            } else if (type.hasFlag(MULTI)){
                registry.addRecipeCategories(new MultiMachineRecipeCategory(guiHelper, type));
            }
        }
    }

    @Override
    public void register(IModRegistry registry) {
        for (Machine type : MachineFlag.RECIPE.getTypes()) {
//            if (type.getSlots() != null) {
                registry.addRecipes(type.getRecipeMap().getRecipes(), type.getJeiCategoryID());
                registry.handleRecipes(Recipe.class, MachineRecipeWrapper::new, type.getJeiCategoryID());
//            }
            for (Tier tier : type.getTiers()) {
                registry.addRecipeCatalyst(new MachineStack(type, tier).asItemStack(), type.getJeiCategoryID());
            }
        }
    }

    public static void showCategory(Machine... type) {
        if (runtime != null) {
            List<String> list = new LinkedList<>();
            for (int i = 0; i < type.length; i++) {
                if (type[i].getJeiCategoryID() != null) {
                    list.add(type[i].getJeiCategoryID());
                }
            }
            runtime.getRecipesGui().showCategories(list);
        }
    }
}
