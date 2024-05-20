package muramasa.antimatter.fluid.forge;

import earth.terrarium.botarium.common.registry.fluid.FluidData;
import earth.terrarium.botarium.forge.regsitry.fluid.BotariumFluidType;
import muramasa.antimatter.AntimatterAPI;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class FluidUtilsImpl {
    public static FlowingFluid createSourceFluid(FluidData data){
        return new AntimatterSourceFluid(data);
    }

    public static FlowingFluid createFlowingFluid(FluidData data){
        return new AntimatterFlowingFluid(data);
    }

    public static ForgeFlowingFluid.Properties propertiesFromFluidProperties(FluidData data){
        String id = data.getProperties().id().getPath();
        String domain = data.getProperties().id().getNamespace();
        FluidType type = AntimatterAPI.getOrDefault(FluidType.class, id, domain, () -> AntimatterAPI.register(FluidType.class, id, domain, BotariumFluidType.of(data.getProperties())));
        return new ForgeFlowingFluid.Properties(() -> type, () -> data.getStillFluid().get(), () -> data.getFlowingFluid().get())
                .bucket(() -> data.getBucket().get()).block(() -> data.getBlock().get());
    }
}
