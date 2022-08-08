package muramasa.antimatter.fluid.forge;

import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.fluid.AntimatterFluidAttributes;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;

import java.util.function.Consumer;

public class AntimatterFluidUtilsImpl {
    public static boolean isSource(Fluid fluid){
        return fluid instanceof ForgeFlowingFluid.Source;
    }

    public static void createSourceAndFlowingFluid(AntimatterFluid fluid, Consumer<Fluid> source, Consumer<FlowingFluid> flowing){
        FluidAttributes.Builder builder = fromAntimatterBuilder(fluid.getAttributes());
        ForgeFlowingFluid.Properties properties = new ForgeFlowingFluid.Properties(fluid::getFluid, fluid::getFlowingFluid, builder).bucket(fluid::getContainerItem).block(fluid::getFluidBlock);
        source.accept(new ForgeFlowingFluid.Source(properties));
        flowing.accept(new ForgeFlowingFluid.Flowing(properties));
    }
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
