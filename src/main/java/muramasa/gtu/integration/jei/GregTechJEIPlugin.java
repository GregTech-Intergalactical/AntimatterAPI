//package muramasa.gtu.integration.jei;
//
//import mezz.jei.api.IJeiRuntime;
//import mezz.jei.api.IModPlugin;
//import mezz.jei.api.IModRegistry;
//import mezz.jei.api.JEIPlugin;
//import mezz.jei.api.recipe.IRecipeCategoryRegistration;
//import muramasa.gtu.data.Guis;
//import muramasa.gtu.data.Machines;
//import muramasa.gtu.api.guiold.GuiData;
//import muramasa.antimatter.machines.MachineFlag;
//import muramasa.antimatter.machines.Tier;
//import muramasa.antimatter.machines.types.Machine;
//import muramasa.antimatter.recipe.Recipe;
//import muramasa.antimatter.recipe.RecipeMap;
//import muramasa.gtu.integration.jei.category.RecipeMapCategory;
//import muramasa.gtu.integration.jei.wrapper.RecipeWrapper;
//import net.minecraft.util.Tuple;
//
//import javax.annotation.Nullable;
//import java.util.*;
//
//import static muramasa.antimatter.machines.MachineFlag.*;
//
//@JEIPlugin
//public class GregTechJEIPlugin implements IModPlugin {
//
//    private static IJeiRuntime runtime;
//    private static HashMap<String, Tuple<RecipeMap, GuiData>> REGISTRY = new HashMap<>();
//
//    public static void registerCategory(RecipeMap map, GuiData gui) {
//        REGISTRY.put(map.getId(), new Tuple<>(map, gui));
//    }
//
//    @Nullable
//    public static Tuple<RecipeMap, GuiData> getRegisteredCategory(String name) {
//        return REGISTRY.get(name);
//    }
//
//    @Override
//    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
//        runtime = jeiRuntime;
//    }
//
//    @Override
//    public void registerCategories(IRecipeCategoryRegistration registry) {
//        RecipeMapCategory.setGuiHelper(registry.getJeiHelpers().getGuiHelper());
//
//        //TODO redo JEI categories to revolve around maps instead of machines
//        Set<String> registeredMachineCats = new HashSet<>();
//
//        for (Machine type : MachineFlag.RECIPE.getTypes()) {
//            if (registeredMachineCats.contains(type.getRecipeMap().getId())) continue;
//            if (type.hasFlag(BASIC)) {
//                if (REGISTRY.containsKey(type.getRecipeMap().getId())) continue;
//                if (type.hasFlag(STEAM)) {
//                    registry.addRecipeCategories(new RecipeMapCategory(Machines.get(type, type.getFirstTier()), Tier.BRONZE));
//                } else {
//                    registry.addRecipeCategories(new RecipeMapCategory(Machines.get(type, type.getFirstTier()), type.getGui().getHighestTier()));
//                }
//            } else if (type.hasFlag(MULTI)) {
//                if (type.getGui().hasSlots()) {
//                    registry.addRecipeCategories(new RecipeMapCategory(Machines.get(type, type.getFirstTier()), type.getGui().getHighestTier()));
//                } else {
//                    registry.addRecipeCategories(new RecipeMapCategory(Machines.get(type, type.getFirstTier()), Guis.MULTI_DISPLAY, type.getGui().getHighestTier()));
//                }
//            }
//            registeredMachineCats.add(type.getRecipeMap().getId());
//        }
//        REGISTRY.forEach((id, tuple) -> {
//            if (!registeredMachineCats.contains(tuple.getFirst().getId())) registry.addRecipeCategories(new RecipeMapCategory(tuple.getFirst(), tuple.getSecond(), Tier.LV));
//        });
//    }
//
//    @Override
//    public void register(IModRegistry registry) {
//        for (Machine type : MachineFlag.RECIPE.getTypes()) {
//            registry.addRecipes(type.getRecipeMap().getRecipes(true), type.getRecipeMap().getId());
//            registry.handleRecipes(Recipe.class, RecipeWrapper::new, type.getRecipeMap().getId());
//            for (Tier tier : type.getTiers()) {
//                registry.addRecipeCatalyst(new MachineStack(type, tier).asItemStack(), type.getRecipeMap().getId());
//            }
//        }
//        for (Tuple<RecipeMap, GuiData> pair : REGISTRY.values()) {
//            registry.addRecipes(pair.getFirst().getRecipes(true), pair.getFirst().getId());
//            registry.handleRecipes(Recipe.class, RecipeWrapper::new, pair.getFirst().getId());
//        }
//    }
//
//    public static void showCategory(Machine... types) {
//        if (runtime != null) {
//            List<String> list = new LinkedList<>();
//            for (int i = 0; i < types.length; i++) {
//                if (!types[i].hasFlag(RECIPE)) continue;
//                list.add(types[i].getRecipeMap().getId());
//            }
//            runtime.getRecipesGui().showCategories(list);
//        }
//    }
//}
