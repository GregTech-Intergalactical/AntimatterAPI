package muramasa.antimatter.integration.jei;

import dev.architectury.injectables.annotations.ExpectPlatform;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeSlotBuilder;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.ingredients.ITypedIngredient;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.*;
import mezz.jei.api.runtime.IJeiRuntime;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.integration.jei.category.MultiMachineInfoCategory;
import muramasa.antimatter.integration.jei.category.RecipeMapCategory;
import muramasa.antimatter.integration.jei.extension.JEIMaterialRecipeExtension;
import muramasa.antimatter.integration.jeirei.AntimatterJEIREIPlugin;
import muramasa.antimatter.item.ItemBattery;
import muramasa.antimatter.item.ItemMultiTextureBattery;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTypeItem;
import muramasa.antimatter.recipe.IRecipe;
import muramasa.antimatter.recipe.Recipe;
import muramasa.antimatter.recipe.map.IRecipeMap;
import muramasa.antimatter.recipe.map.RecipeMap;
import muramasa.antimatter.recipe.material.MaterialRecipe;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.util.Utils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import tesseract.TesseractCapUtils;
import tesseract.api.context.TesseractItemContext;
import tesseract.api.gt.IEnergyItem;
import tesseract.api.gt.IGTNode;
import tesseract.api.wrapper.ItemStackWrapper;

import java.util.*;
import java.util.stream.Collectors;

import static muramasa.antimatter.machine.MachineFlag.RECIPE;

@SuppressWarnings("removal")
@JeiPlugin
public class AntimatterJEIPlugin implements IModPlugin {
    public static final Map<String, RecipeType<IRecipe>> RECIPE_TYPES = new Object2ObjectOpenHashMap<>();
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
    public void onRuntimeAvailable(@NotNull IJeiRuntime jeiRuntime) {
        if (AntimatterAPI.isModLoaded(Ref.MOD_REI)) return;
        runtime = jeiRuntime;
        //Remove fluid "blocks".
        List<ItemLike> list = new ArrayList<>();
        AntimatterJEIREIPlugin.getItemsToHide().forEach(c -> c.accept(list));
        if (!list.isEmpty()) {
            runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM, list.stream().map(i -> i.asItem().getDefaultInstance()).toList());
        }
        //runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM, AntimatterAPI.all(BlockSurfaceRock.class).stream().map(b -> new ItemStack(b, 1)).filter(t -> !t.isEmpty()).collect(Collectors.toList()));
        //runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM, AntimatterAPI.all(BlockOre.class).stream().filter(b -> b.getStoneType() != Data.STONE).map(b -> new ItemStack(b, 1)).collect(Collectors.toList()));
        //runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM, Data.MACHINE_INVALID.getTiers().stream().map(t -> Data.MACHINE_INVALID.getItem(t).getDefaultInstance()).collect(Collectors.toList()));
    }



    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        if (AntimatterAPI.isModLoaded(Ref.MOD_REI)) return;
        AntimatterAPI.all(Item.class).forEach(i -> {
            if (i instanceof IEnergyItem energyItem && energyItem.canCreate(new ItemStackWrapper(i.getDefaultInstance()))) {
                registration.registerSubtypeInterpreter(i, (s, c) -> {
                    long energy = TesseractCapUtils.INSTANCE.getEnergyHandlerItem(s).map(IGTNode::getEnergy).orElse(0L);
                    long capacity = TesseractCapUtils.INSTANCE.getEnergyHandlerItem(s).map(IGTNode::getCapacity).orElse(0L);
                    return "e:" + energy + "/" + capacity;
                });
            }
        });
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
                RecipeType<IRecipe> type = new RecipeType<>(tuple.map.getLoc(), IRecipe.class);
                RECIPE_TYPES.put(type.getUid().toString(), type);
                registry.addRecipeCategories(new RecipeMapCategory(tuple.map, type, tuple.gui, tuple.tier, tuple.workstations.isEmpty() ? null : tuple.workstations.get(0)));
                registeredMachineCats.add(tuple.map.getLoc());
            }
        });

        // multi machine
        registry.addRecipeCategories(new MultiMachineInfoCategory());
    }

    @Override
    public void registerRecipes(@NotNull IRecipeRegistration registration) {
        if (AntimatterAPI.isModLoaded(Ref.MOD_REI)) return;
        if (helpers == null) helpers = registration.getJeiHelpers();
        AntimatterJEIREIPlugin.getREGISTRY().forEach((id, tuple) -> {
            registration.addRecipes(RECIPE_TYPES.get(id.toString()), getRecipes(tuple.map));
        });
        MultiMachineInfoCategory.registerRecipes(registration);
    }

    private List<IRecipe> getRecipes(IRecipeMap recipeMap){
        RecipeManager manager = getRecipeManager();
        if (manager == null) return Collections.emptyList();
        List<IRecipe> recipes = new ArrayList<>(manager.getAllRecipesFor(Recipe.RECIPE_TYPE).stream().filter(r -> r.getMapId().equals(recipeMap.getId()) && !r.isHidden()).toList());
        if (recipeMap.getProxy() != null && recipeMap instanceof RecipeMap<?> map) {
            List<net.minecraft.world.item.crafting.Recipe<?>> proxyRecipes = (List<net.minecraft.world.item.crafting.Recipe<?>>) manager.getAllRecipesFor(recipeMap.getProxy().loc());
            proxyRecipes.forEach(recipe -> {
                IRecipe recipe1 = recipeMap.getProxy().handler().apply(recipe, map.RB());
                if (recipe1 != null && !recipe1.isHidden()){
                    recipes.add(recipe1);
                }
            });
        }
        return recipes;
    }

    private RecipeManager getRecipeManager(){
        if (AntimatterAPI.getSIDE().isServer()){
            return AntimatterPlatformUtils.getCurrentServer().getRecipeManager();
        } else {
            if (getWorld() == null) return null;
            return getWorld().getRecipeManager();
        }
    }

    @Environment(EnvType.CLIENT)
    ClientLevel getWorld(){
        return Minecraft.getInstance().level;
    }

    public static void showCategory(Machine<?> type, Tier tier) {
        if (runtime != null) {
            if (!type.has(RECIPE)) return;
            IRecipeMap map = type.getRecipeMap(tier);
            if (map == null) return; //incase someone adds tier specific recipe maps without a fallback
            runtime.getRecipesGui().showCategories(List.of(map.getLoc()));
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
    public static void uses(FluidHolder val, boolean USE) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static void addFluidIngredients(IRecipeSlotBuilder builder, List<FluidHolder> stacks){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Object getFluidObject(FluidHolder fluidHolder){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static FluidHolder getIngredient(ITypedIngredient<?> ingredient){
        throw new AssertionError();
    }

    public static IJeiRuntime getRuntime() {
        return runtime;
    }

    public static <T> void addModDescriptor(List<Component> tooltip, T t) {
        if (t == null || helpers == null) return;
        Object o = t;
        if (t instanceof FluidHolder holder) o = getFluidObject(holder);
        String text = helpers.getModIdHelper().getFormattedModNameForModId(getRuntime().getIngredientManager().getIngredientHelper(o).getDisplayModId(o));
        tooltip.add(Utils.literal(text));
    }

    @Override
    public void registerRecipeCatalysts(@NotNull IRecipeCatalystRegistration registration) {
        if (AntimatterAPI.isModLoaded(Ref.MOD_REI)) return;
        AntimatterJEIREIPlugin.getREGISTRY().forEach((id, tuple) -> {
            if (tuple.workstations.isEmpty()) return;
            tuple.workstations.forEach(s -> {
                ItemLike item = AntimatterPlatformUtils.getItemFromID(s);
                if (item == Items.AIR) return;
                registration.addRecipeCatalyst(new ItemStack(item), tuple.map.getLoc());
            });
        });
    }
}
