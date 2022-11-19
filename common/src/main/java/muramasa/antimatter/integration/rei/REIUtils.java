package muramasa.antimatter.integration.rei;

import muramasa.antimatter.machine.types.Machine;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;
import tesseract.FluidPlatformUtils;

import java.util.List;

public class REIUtils {
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

    public static void uses(FluidStack val, boolean USE) {

    }

    public static void showCategory(Machine<?>... types) {

    }
}
