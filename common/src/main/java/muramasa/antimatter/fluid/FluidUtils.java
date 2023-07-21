package muramasa.antimatter.fluid;

import dev.architectury.injectables.annotations.ExpectPlatform;
import earth.terrarium.botarium.common.registry.fluid.FluidData;
import net.minecraft.world.level.material.FlowingFluid;

public class FluidUtils {
    @ExpectPlatform
    public static FlowingFluid createSourceFluid(FluidData data){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static FlowingFluid createFlowingFluid(FluidData data){
        throw new AssertionError();
    }
}
