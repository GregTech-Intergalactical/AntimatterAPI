package muramasa.antimatter.fluid;

import dev.architectury.injectables.annotations.ExpectPlatform;
import earth.terrarium.botarium.common.registry.fluid.FluidData;
import muramasa.antimatter.util.ImplLoader;
import net.minecraft.world.level.material.FlowingFluid;

public interface FluidUtils {
    FluidUtils INSTANCE = ImplLoader.load(FluidUtils.class);
    FlowingFluid createSourceFluid(FluidData data);

    FlowingFluid createFlowingFluid(FluidData data);
}
