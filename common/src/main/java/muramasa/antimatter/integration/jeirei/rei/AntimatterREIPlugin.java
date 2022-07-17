package muramasa.antimatter.integration.jeirei.rei;

import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.integration.jeirei.jei.AntimatterJEIPlugin;
import muramasa.antimatter.machine.types.Machine;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fluids.FluidStack;
import tesseract.FluidPlatformUtils;

import java.util.List;

public class AntimatterREIPlugin implements REIClientPlugin {
    @Override
    public String getPluginProviderName() {
        return Ref.ID + ":rei";
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
