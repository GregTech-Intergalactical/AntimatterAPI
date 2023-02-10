package muramasa.antimatter.integration.rei;

import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import muramasa.antimatter.machine.types.Machine;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;
import tesseract.FluidPlatformUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class REIUtils {
    static List<Consumer<CategoryRegistry>> EXTRA_CATEGORIES = new ArrayList<>();
    static List<Consumer<DisplayRegistry>> EXTRA_DISPLAYS = new ArrayList<>();

    public static FluidStack fromREIFluidStack(dev.architectury.fluid.FluidStack from){
        FluidStack stack = FluidPlatformUtils.createFluidStack(from.getFluid(), from.getAmount());
        stack.setTag(from.getTag());
        return stack;
    }

    public static dev.architectury.fluid.FluidStack toREIFLuidStack(FluidStack from){
        return dev.architectury.fluid.FluidStack.create(from.getFluid(), from.getRealAmount(), from.getTag());
    }

    public static <T> void addModDescriptor(List<Component> tooltip, T t) {

    }

    public static void addExtraDisplay(Consumer<DisplayRegistry> registry){
        EXTRA_DISPLAYS.add(registry);
    }

    public static void addExtraCategory(Consumer<CategoryRegistry> registry){
        EXTRA_CATEGORIES.add(registry);
    }

    public static void uses(FluidStack val, boolean USE) {

    }

    public static void showCategory(Machine<?>... types) {

    }
}
