package muramasa.antimatter.fluid.fabric;

import io.github.fabricators_of_create.porting_lib.util.FluidAttributes;
import io.github.fabricators_of_create.porting_lib.util.SimpleFlowableFluid;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.fluid.AntimatterFluidAttributes;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

import java.util.function.Consumer;

public class AntimatterFluidUtilsImpl {
    public static boolean isSource(Fluid fluid){
        return fluid instanceof SimpleFlowableFluid.Still;
    }
    public static void createSourceAndFlowingFluid(AntimatterFluid fluid, Consumer<Fluid> source, Consumer<FlowingFluid> flowing){
        SimpleFlowableFluid.Properties properties = new SimpleFlowableFluid.Properties(fluid::getFluid, fluid::getFlowingFluid, fromAntimatterBuilder(fluid.getAttributes())).bucket(fluid::getContainerItem).block(fluid::getFluidBlock);
        source.accept(new SimpleFlowableFluid.Still(properties));
        flowing.accept(new SimpleFlowableFluid.Flowing(properties));
    }

    @SuppressWarnings("removal")
    public static FluidAttributes.Builder fromAntimatterBuilder(AntimatterFluidAttributes fluidAttributes){
        FluidAttributes.Builder builder = FluidAttributes.builder(fluidAttributes.getStillTexture(), fluidAttributes.getFlowingTexture())
                .color(fluidAttributes.getColor())
                .density(fluidAttributes.getDensity())
                .overlay(fluidAttributes.getOverlayTexture())
                .luminosity(fluidAttributes.getLuminosity())
                .rarity(fluidAttributes.getRarity())
                .sound(fluidAttributes.getFillSound(), fluidAttributes.getEmptySound())
                .temperature(fluidAttributes.getTemperature())
                .translationKey(fluidAttributes.getTranslationKey())
                .viscosity(fluidAttributes.getViscosity());
        if (fluidAttributes.isGaseous()) builder.gaseous();
        return builder;
    }
}
