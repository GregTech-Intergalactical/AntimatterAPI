package muramasa.itech.integration.jei;

import mezz.jei.api.*;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import muramasa.itech.api.enums.MachineFlag;
import muramasa.itech.api.machines.Machine;
import muramasa.itech.api.recipe.Recipe;
import muramasa.itech.api.recipe.RecipeMap;

import java.util.LinkedList;
import java.util.List;

@JEIPlugin
public class ITJEIPlugin implements IModPlugin {

    private static IJeiRuntime runtime;

    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        for (Machine type : MachineFlag.BASIC.getTypes()) {
            registry.addRecipeCategories(new MachineRecipeCategory(guiHelper, type));
        }
    }

    @Override
    public void register(IModRegistry registry) {
        for (Machine type : MachineFlag.BASIC.getTypes()) {
            registry.addRecipes(RecipeMap.get(type).getRecipes(), type.getJeiCategoryID());
            registry.handleRecipes(Recipe.class, MachineRecipeWrapper::new, type.getJeiCategoryID());
        }


//        for (MachineStack stack : MachineList.getAllStacks()) {
//            registry.addRecipeCatalyst(stack.getStackForm(), stack.getTypeFromNBT().getJeiCategoryID());
//        }
    }

    public static void showCategory(Machine... type) {
        if (runtime != null) {
            List<String> list = new LinkedList<>();
            for (int i = 0; i < type.length; i++) {
                list.add(type[i].getJeiCategoryID());
            }
            runtime.getRecipesGui().showCategories(list);
        }
    }
}
