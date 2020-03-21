package muramasa.antimatter.integration.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.Ref;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.integration.jei.category.RecipeMapCategory;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.recipe.RecipeMap;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


@JeiPlugin
public class AntimatterJEIPlugin implements IModPlugin {

    private static IJeiRuntime runtime;
    private static HashMap<String, Tuple<RecipeMap, GuiData>> REGISTRY = new HashMap<>();

    public AntimatterJEIPlugin() {
        Antimatter.LOGGER.debug("AntimatterJEIPlugin created");
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Ref.ID, "jei");
    }

    public static void registerCategory(RecipeMap map, GuiData gui) {
        REGISTRY.put(map.getId(), new Tuple<>(map, gui));
    }

//    @Nullable
//    public static Tuple<RecipeMap, GuiData> getRegisteredCategory(String name) {
//        return REGISTRY.get(name);
//    }
//
    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        RecipeMapCategory.setGuiHelper(registry.getJeiHelpers().getGuiHelper());

        Set<String> registeredMachineCats = new HashSet<>();
        //TODO redo JEI categories to revolve around maps instead of machines
/*
        for (Machine type : MachineFlag.RECIPE.getTypes()) {
            if (registeredMachineCats.contains(type.getRecipeMap().getId())) continue;
            if (type.hasFlag(BASIC)) {
                if (REGISTRY.containsKey(type.getRecipeMap().getId())) continue;
                if (type.hasFlag(STEAM)) {
                    registry.addRecipeCategories(new RecipeMapCategory(Machines.get(type, type.getFirstTier()), Tier.BRONZE));
                } else {
                    registry.addRecipeCategories(new RecipeMapCategory(Machines.get(type, type.getFirstTier()), type.getGui().getHighestTier()));
                }
            } else if (type.hasFlag(MULTI)) {
                if (type.getGui().hasSlots()) {
                    registry.addRecipeCategories(new RecipeMapCategory(Machines.get(type, type.getFirstTier()), type.getGui().getHighestTier()));
                } else {
                    registry.addRecipeCategories(new RecipeMapCategory(Machines.get(type, type.getFirstTier()), Guis.MULTI_DISPLAY, type.getGui().getHighestTier()));
                }
            }
            registeredMachineCats.add(type.getRecipeMap().getId());
        }
 */
        REGISTRY.forEach((id, tuple) -> {
            if (!registeredMachineCats.contains(tuple.getA().getId())) registry.addRecipeCategories(new RecipeMapCategory(tuple.getA(), tuple.getB(), Tier.LV));
        });
    }
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        REGISTRY.forEach((id, tuple) -> {
            registration.addRecipes(tuple.getA().getRecipes(true), new ResourceLocation(Ref.ID, id));
        });
    }
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
}
