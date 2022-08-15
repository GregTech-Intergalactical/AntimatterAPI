package muramasa.antimatter.integration.rei;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;
import muramasa.antimatter.Ref;
import muramasa.antimatter.integration.jeirei.AntimatterJEIREIPlugin;
import muramasa.antimatter.integration.rei.category.RecipeMapCategory;
import muramasa.antimatter.integration.rei.category.RecipeMapDisplay;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.recipe.IRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraftforge.fluids.FluidStack;
import tesseract.FluidPlatformUtils;

import java.util.List;
import java.util.Set;

public class AntimatterREIPlugin implements REIClientPlugin {
    @Override
    public String getPluginProviderName() {
        return Ref.ID + ":rei";
    }

    @Override
    public void registerCategories(CategoryRegistry registry) {
        Set<ResourceLocation> registeredMachineCats = new ObjectOpenHashSet<>();

        AntimatterJEIREIPlugin.getREGISTRY().forEach((id, tuple) -> {
            if (!registeredMachineCats.contains(tuple.map.getLoc())) {
                RecipeMapCategory category = new RecipeMapCategory(tuple.map, tuple.gui, tuple.tier, tuple.model);
                registry.add(category);
                registry.addWorkstations(category.getCategoryIdentifier(), (EntryStack<?>) category.getIcon());
                registeredMachineCats.add(tuple.map.getLoc());
            }
        });
    }

    @Override
    public void registerDisplays(DisplayRegistry registry) {
        // regular recipes
        //registry.registerFiller(IRecipe.class, RecipeMapDisplay::new);
        AntimatterJEIREIPlugin.getREGISTRY().forEach((id, tuple) -> {
            tuple.map.getRecipes(true).forEach(object -> {
                String mapId = object.mapId;
                registry.add(new RecipeMapDisplay(object));
            });
        });
    }

    public static void showCategory(Machine<?>... types) {

    }

    public static void uses(FluidStack val, boolean USE) {

    }

    public static <T> void addModDescriptor(List<Component> tooltip, T t) {

    }

    public static dev.architectury.fluid.FluidStack toREIFLuidStack(FluidStack from){
        return dev.architectury.fluid.FluidStack.create(from.getFluid(), from.getRealAmount(), from.getTag());
    }

    public static FluidStack fromREIFluidStack(dev.architectury.fluid.FluidStack from){
        FluidStack stack = FluidPlatformUtils.createFluidStack(from.getFluid(), from.getAmount());
        stack.setTag(from.getTag());
        return stack;
    }
}
