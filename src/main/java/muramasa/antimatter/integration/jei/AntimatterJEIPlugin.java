package muramasa.antimatter.integration.jei;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
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
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.recipe.RecipeMap;
import net.minecraft.util.ResourceLocation;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static muramasa.antimatter.machine.MachineFlag.RECIPE;


@JeiPlugin
public class AntimatterJEIPlugin implements IModPlugin {

    protected static class RegistryValue {
        RecipeMap map;
        GuiData gui;
        Tier tier;
        String machine;

        public RegistryValue(RecipeMap map, GuiData gui, Tier tier, String machine) {
            this.map = map;
            this.gui = gui;
            this.tier = tier;
            this.machine = machine;
        }
    }

    private static IJeiRuntime runtime;
    private static Object2ObjectMap<String, RegistryValue> REGISTRY = new Object2ObjectLinkedOpenHashMap<>();

    public AntimatterJEIPlugin() {
        Antimatter.LOGGER.debug("AntimatterJEIPlugin created");
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Ref.ID, "jei");
    }

    public static void registerCategory(RecipeMap<?> map, GuiData gui, Tier tier, String itemModel) {
        REGISTRY.put(map.getId(), new RegistryValue(map,gui,tier,itemModel));//new Tuple<>(map, new Tuple<>(gui, tier)));
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

        Set<String> registeredMachineCats = new ObjectOpenHashSet<>();
        //TODO redo JEI categories to revolve around maps instead of machines

      /*  for (Machine type : MachineFlag.RECIPE.getTypes()) {
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
            if (!registeredMachineCats.contains(tuple.map.getId())) registry.addRecipeCategories(new RecipeMapCategory(tuple.map,tuple.gui,tuple.tier,tuple.machine));
        });
    }
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        REGISTRY.forEach((id, tuple) -> {
            registration.addRecipes(tuple.map.getRecipes(true), new ResourceLocation(Ref.ID, id));
        });
    }

    /*@Override
    public void registerIngredients(IModIngredientRegistration registration) {
        registration.register(RecipeMapCategory.TagType, Collections.emptyList(), null, new TaggedItemRenderer());
    }*/


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
    public static void showCategory(Machine... types) {
        if (runtime != null) {
            List<ResourceLocation> list = new LinkedList<>();
            for (int i = 0; i < types.length; i++) {
                if (!types[i].has(RECIPE)) continue;
                list.add(new ResourceLocation(Ref.ID, types[i].getRecipeMap().getId()));
            }
            runtime.getRecipesGui().showCategories(list);
        }
    }
}
