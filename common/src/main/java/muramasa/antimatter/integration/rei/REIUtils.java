package muramasa.antimatter.integration.rei;

import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.client.view.ViewSearchBuilder;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.recipe.map.IRecipeMap;
import net.minecraft.network.chat.Component;
import tesseract.FluidPlatformUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static muramasa.antimatter.machine.MachineFlag.RECIPE;

public class REIUtils {
    static List<Consumer<CategoryRegistry>> EXTRA_CATEGORIES = new ArrayList<>();
    static List<Consumer<DisplayRegistry>> EXTRA_DISPLAYS = new ArrayList<>();

    public static FluidHolder fromREIFluidStack(dev.architectury.fluid.FluidStack from){
        FluidHolder stack = FluidPlatformUtils.createFluidStack(from.getFluid(), from.getAmount());
        stack.setCompound(from.getTag());
        return stack;
    }

    public static dev.architectury.fluid.FluidStack toREIFLuidStack(FluidHolder from){
        return dev.architectury.fluid.FluidStack.create(from.getFluid(), from.getFluidAmount(), from.getCompound());
    }

    public static <T> void addModDescriptor(List<Component> tooltip, T t) {

    }

    public static void addExtraDisplay(Consumer<DisplayRegistry> registry){
        EXTRA_DISPLAYS.add(registry);
    }

    public static void addExtraCategory(Consumer<CategoryRegistry> registry){
        EXTRA_CATEGORIES.add(registry);
    }

    public static void uses(FluidHolder val, boolean USE) {
        EntryStack<?> stack = EntryStack.of(VanillaEntryTypes.FLUID, toREIFLuidStack(val));
        if (USE) ViewSearchBuilder.builder().addUsagesFor(stack).open();
        else ViewSearchBuilder.builder().addRecipesFor(stack).open();
    }

    public static void showCategory(Machine<?> type, Tier tier) {
        if (!type.has(RECIPE)) return;
        IRecipeMap map = type.getRecipeMap(tier);
        if (map == null) return; //incase someone adds tier specific recipe maps without a fallback
        ViewSearchBuilder.builder().addCategories(List.of(CategoryIdentifier.of(map.getLoc()))).open();
    }
}
