package muramasa.antimatter.integration.jei;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.helpers.IJeiHelpers;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.runtime.IJeiRuntime;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.integration.jei.category.RecipeMapCategory;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.recipe.map.RecipeMap;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fluids.FluidStack;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private static IJeiHelpers helpers;
    private static Object2ObjectMap<String, RegistryValue> REGISTRY = new Object2ObjectLinkedOpenHashMap<>();

    public AntimatterJEIPlugin() {
        Antimatter.LOGGER.debug("AntimatterJEIPlugin created");
    }

    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(Ref.ID, "jei");
    }

    public static void registerCategory(RecipeMap<?> map, GuiData gui, Tier tier, String itemModel) {
        REGISTRY.put(map.getId(), new RegistryValue(map,map.getGui() == null ? gui : map.getGui(),tier,itemModel));//new Tuple<>(map, new Tuple<>(gui, tier)));
    }


    @Override
    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
        runtime = jeiRuntime;
        //Remove fluid "blocks".
        runtime.getIngredientManager().removeIngredientsAtRuntime(VanillaTypes.ITEM, AntimatterAPI.all(AntimatterFluid.class).stream().map(t -> new ItemStack(Item.BLOCK_TO_ITEM.get(t.getFluidBlock()))).collect(Collectors.toList()));
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registry) {
        RecipeMapCategory.setGuiHelper(registry.getJeiHelpers().getGuiHelper());
        if (helpers == null) helpers = registry.getJeiHelpers();
        Set<String> registeredMachineCats = new ObjectOpenHashSet<>();

        REGISTRY.forEach((id, tuple) -> {
            if (!registeredMachineCats.contains(tuple.map.getId())) registry.addRecipeCategories(new RecipeMapCategory(tuple.map,tuple.gui,tuple.tier,tuple.machine));
        });
    }
    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        if (helpers == null) helpers = registration.getJeiHelpers();
        REGISTRY.forEach((id, tuple) -> {
            registration.addRecipes(tuple.map.getRecipes(true), new ResourceLocation(Ref.ID, id));
        });
    }

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
    //To perform a JEI lookup for fluid. Use defines direction.
    public static void uses(FluidStack val, boolean USE) {
        IFocus.Mode mode = !USE ? IFocus.Mode.OUTPUT : IFocus.Mode.INPUT;
        runtime.getRecipesGui().show(new IFocus<Object>() {
            @Override
            public Object getValue() {
                return val;
            }

            @Override
            public Mode getMode() {
                return mode;
            }
        });
    }

    public static IJeiRuntime getRuntime() {
        return runtime;
    }

    public static <T> void addModDescriptor(List<ITextComponent> tooltip, T t) {
        String text = helpers.getModIdHelper().getFormattedModNameForModId(getRuntime().getIngredientManager().getIngredientHelper(t).getDisplayModId(t));
        tooltip.add(new StringTextComponent(text));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        REGISTRY.forEach((id, tuple) -> {
            Machine<?> machine = Machine.get(tuple.machine);
            if (machine != Data.MACHINE_INVALID){
                machine.getTiers().forEach(t -> {
                    ItemStack stack = new ItemStack(machine.getItem(t));
                    if (!stack.isEmpty()){
                        registration.addRecipeCatalyst(stack, new ResourceLocation(Ref.ID, id));
                    } else {
                        Antimatter.LOGGER.error("machine " + tuple.machine + " has an empty item. Did you do the machine correctly?");
                    }
                });
            } //else {
          //      Antimatter.LOGGER.error("machine " + tuple.machine + " does not exist");
         //   }
        });
    }
}
