package muramasa.antimatter.integration.jei;

import dev.architectury.injectables.annotations.ExpectPlatform;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockSurfaceRock;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.integration.jeirei.AntimatterJEIREIPlugin;
import muramasa.antimatter.integration.jei.category.MultiMachineInfoCategory;
import muramasa.antimatter.integration.jei.category.RecipeMapCategory;
import muramasa.antimatter.integration.jei.extension.JEIMaterialRecipeExtension;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTypeItem;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.recipe.map.IRecipeMap;
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
import java.util.Set;
import java.util.stream.Collectors;

import static muramasa.antimatter.machine.MachineFlag.RECIPE;

@SuppressWarnings("removal")
@JeiPlugin
public class AntimatterJEIPlugin implements IModPlugin {
    private static IJeiRuntime runtime;
    private static IJeiHelpers helpers;

    public AntimatterJEIPlugin() {
        Antimatter.LOGGER.info("Creating AntimatterAPI's JEI Plugin");
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Ref.ID, "jei");
    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime jeiRuntime) {
        if (AntimatterAPI.isModLoaded(Ref.MOD_REI)) return;
        runtime = jeiRuntime;
        //Remove fluid "blocks".
        runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM, AntimatterAPI.all(AntimatterFluid.class).stream().map(t -> new ItemStack(Item.BY_BLOCK.get(t.getFluidBlock()))).collect(Collectors.toList()));
        runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM, Collections.singletonList(new ItemStack(Data.PROXY_INSTANCE)));
        AntimatterAPI.all(MaterialTypeItem.class, t -> {
            if (!t.hidden()) return;
            List<ItemStack> stacks = (List<ItemStack>) t.all().stream().map(obj -> t.get((Material)obj, 1)).collect(Collectors.toList());
            if (stacks.isEmpty()) return;
            runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM, stacks);
        });
        runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM, AntimatterJEIREIPlugin.getItemsToHide().stream().map(i -> i.asItem().getDefaultInstance()).toList());
        //runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM, AntimatterAPI.all(BlockSurfaceRock.class).stream().map(b -> new ItemStack(b, 1)).filter(t -> !t.isEmpty()).collect(Collectors.toList()));
        //runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM, AntimatterAPI.all(BlockOre.class).stream().filter(b -> b.getStoneType() != Data.STONE).map(b -> new ItemStack(b, 1)).collect(Collectors.toList()));
        //runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM, Data.MACHINE_INVALID.getTiers().stream().map(t -> Data.MACHINE_INVALID.getItem(t).getDefaultInstance()).collect(Collectors.toList()));
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        if (AntimatterAPI.isModLoaded(Ref.MOD_REI)) return;
        RecipeMapCategory.setGuiHelper(registry.getJeiHelpers().getGuiHelper());
        MultiMachineInfoCategory.setGuiHelper(registry.getJeiHelpers().getGuiHelper());
        if (helpers == null) helpers = registry.getJeiHelpers();
        Set<ResourceLocation> registeredMachineCats = new ObjectOpenHashSet<>();

        AntimatterJEIREIPlugin.getREGISTRY().forEach((id, tuple) -> {
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
        if (AntimatterAPI.isModLoaded(Ref.MOD_REI)) return;
        if (helpers == null) helpers = registration.getJeiHelpers();
        AntimatterJEIREIPlugin.getREGISTRY().forEach((id, tuple) -> {
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
        if (AntimatterAPI.isModLoaded(Ref.MOD_REI)) return;
        registration.getCraftingCategory().addCategoryExtension(MaterialRecipe.class, JEIMaterialRecipeExtension::new);
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        AntimatterJEIREIPlugin.getREGISTRY().forEach((id, tuple) -> {
            registration.addRecipeTransferHandler(new MachineTransferHandler(tuple.map.getLoc()));
        });
    }

    @ExpectPlatform
    public static void uses(FluidStack val, boolean USE) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void addFluidIngredients(IRecipeSlotBuilder builder, List<FluidStack> stacks){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static FluidStack getIngredient(ITypedIngredient<?> ingredient){
        throw new AssertionError();
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
        if (AntimatterAPI.isModLoaded(Ref.MOD_REI)) return;
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
