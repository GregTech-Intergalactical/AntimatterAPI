package muramasa.antimatter.integration.jei;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IVanillaCategoryExtensionRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.integration.jei.category.MultiMachineInfoCategory;
import muramasa.antimatter.integration.jei.category.RecipeMapCategory;
import muramasa.antimatter.integration.jei.extension.JEIMaterialRecipeExtension;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTypeItem;
import muramasa.antimatter.recipe.map.IRecipeMap;
import muramasa.antimatter.recipe.map.RecipeMap;
import muramasa.antimatter.recipe.material.MaterialRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static muramasa.antimatter.machine.MachineFlag.RECIPE;

@JeiPlugin
public class AntimatterJEIPlugin implements IModPlugin {

    protected static class RegistryValue {
        IRecipeMap map;
        GuiData gui;
        Tier tier;
        ResourceLocation model;

        public RegistryValue(IRecipeMap map, GuiData gui, Tier tier, ResourceLocation model) {
            this.map = map;
            this.gui = gui;
            this.tier = tier;
            this.model = model;
        }
    }

    private static IJeiRuntime runtime;
    private static IJeiHelpers helpers;
    private static final Object2ObjectMap<ResourceLocation, RegistryValue> REGISTRY = new Object2ObjectLinkedOpenHashMap<>();

    public AntimatterJEIPlugin() {
        Antimatter.LOGGER.info("Creating AntimatterAPI's JEI Plugin");
    }

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Ref.ID, "jei");
    }

    public static void registerCategory(IRecipeMap map, GuiData gui, Tier tier, ResourceLocation model, boolean override) {
        if (REGISTRY.containsKey(new ResourceLocation(map.getDomain(), map.getId())) && !override) {
            //    Antimatter.LOGGER.info("Attempted duplicate category registration: " + map.getId());
            RegistryValue value = REGISTRY.get(map.getLoc());
            if (value.model == null) value.model = model;
            return;
        }
        REGISTRY.put(new ResourceLocation(map.getDomain(), map.getId()), new RegistryValue(map, map.getGui() == null ? gui : map.getGui(), tier, model));//new Tuple<>(map, new Tuple<>(gui, tier)));
    }

    public static IJeiHelpers helpers() {
        return helpers;
    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
        //Remove fluid "blocks".
        runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM, AntimatterAPI.all(AntimatterFluid.class).stream().map(t -> new ItemStack(Item.BY_BLOCK.get(t.getFluidBlock()))).collect(Collectors.toList()));
        runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM, Collections.singletonList(new ItemStack(Data.PROXY_INSTANCE)));
        AntimatterAPI.all(MaterialTypeItem.class, t -> {
            if (!t.hidden()) return;
            List<ItemStack> stacks = (List<ItemStack>) t.all().stream().map(obj -> t.get((Material)obj, 1)).collect(Collectors.toList());
            if (stacks.isEmpty()) return;
          //  runtime.getIngredientManager().removeIngredientsAtRuntime();
       //     runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM, stacks);
        });
        //runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM, AntimatterAPI.all(BlockSurfaceRock.class).stream().map(b -> new ItemStack(b, 1)).filter(t -> !t.isEmpty()).collect(Collectors.toList()));
        //runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM, AntimatterAPI.all(BlockOre.class).stream().filter(b -> b.getStoneType() != Data.STONE).map(b -> new ItemStack(b, 1)).collect(Collectors.toList()));
        //runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM, Data.MACHINE_INVALID.getTiers().stream().map(t -> Data.MACHINE_INVALID.getItem(t).getDefaultInstance()).collect(Collectors.toList()));
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        RecipeMapCategory.setGuiHelper(registry.getJeiHelpers().getGuiHelper());
        MultiMachineInfoCategory.setGuiHelper(registry.getJeiHelpers().getGuiHelper());
        if (helpers == null) helpers = registry.getJeiHelpers();
        Set<ResourceLocation> registeredMachineCats = new ObjectOpenHashSet<>();

        REGISTRY.forEach((id, tuple) -> {
            if (!registeredMachineCats.contains(tuple.map.getLoc())) {
                registry.addRecipeCategories(new RecipeMapCategory(tuple.map, tuple.gui, tuple.tier, tuple.model));
                registeredMachineCats.add(tuple.map.getLoc());
            }
        });
        
        // multi machine
        registry.addRecipeCategories(new MultiMachineInfoCategory());
    }

    @Override
    public void registerRecipes(@Nonnull IRecipeRegistration registration) {
        if (helpers == null) helpers = registration.getJeiHelpers();
        REGISTRY.forEach((id, tuple) -> {
            registration.addRecipes(tuple.map.getRecipes(true), id);
        });
        MultiMachineInfoCategory.registerRecipes(registration);
    }

    public static void showCategory(Machine<?>... types) {
        if (runtime != null) {
            List<ResourceLocation> list = new LinkedList<>();
            for (int i = 0; i < types.length; i++) {
                if (!types[i].has(RECIPE)) continue;
                list.add(types[i].getRecipeMap().getLoc());
            }
            runtime.getRecipesGui().showCategories(list);
        }
    }

    @Override
    public void registerVanillaCategoryExtensions(IVanillaCategoryExtensionRegistration registration) {
        registration.getCraftingCategory().addCategoryExtension(MaterialRecipe.class, JEIMaterialRecipeExtension::new);
    }

    //To perform a JEI lookup for fluid. Use defines direction.
    public static void uses(FluidStack val, boolean USE) {
        FluidStack v = val.copy();
        runtime.getRecipesGui().show(new IFocus<FluidStack>() {
            @Override
            public ITypedIngredient<FluidStack> getTypedValue() {
                return new ITypedIngredient<>() {
                    @Override
                    public IIngredientType<FluidStack> getType() {
                        return VanillaTypes.FLUID;
                    }

                    @Override
                    public FluidStack getIngredient() {
                        return v;
                    }

                    @Override
                    public <V> Optional<V> getIngredient(IIngredientType<V> ingredientType) {
                        if (ingredientType == VanillaTypes.FLUID) return ((Optional<V>) Optional.of(v));
                        return Optional.empty();
                    }
                };
            }

            @Override
            public RecipeIngredientRole getRole() {
                return USE ? RecipeIngredientRole.INPUT : RecipeIngredientRole.OUTPUT;
            }

            @Override
            public <T> Optional<IFocus<T>> checkedCast(IIngredientType<T> ingredientType) {
                return Optional.empty();
            }

            @Override
            public Mode getMode() {
                return null;
            }

        });
    }

    public static IJeiRuntime getRuntime() {
        return runtime;
    }

    public static <T> void addModDescriptor(List<Component> tooltip, T t) {
        if (t == null || helpers == null) return;
        String text = helpers.getModIdHelper().getFormattedModNameForModId(getRuntime().getIngredientManager().getIngredientHelper(t).getDisplayModId(t));
        tooltip.add(new TextComponent(text));
    }

    @Override
    public void registerRecipeCatalysts(@Nonnull IRecipeCatalystRegistration registration) {
        AntimatterAPI.all(Machine.class, machine -> {
           IRecipeMap map = machine.getRecipeMap();
           if (map == null) return;
            ((Machine<?>)machine).getTiers().forEach(t -> {
                ItemStack stack = new ItemStack(machine.getItem(t));
                if (!stack.isEmpty()) {
                    registration.addRecipeCatalyst(stack, map.getLoc());
                }
            });
        });
    }
}
