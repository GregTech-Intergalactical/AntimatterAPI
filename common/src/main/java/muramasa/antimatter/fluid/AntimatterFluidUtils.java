package muramasa.antimatter.fluid;

import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;

public class AntimatterFluidUtils {
    @ExpectPlatform
    public static Fluid createSourceFluid(AntimatterFluid fluid){
        throw new AssertionError();
    }

    @ExpectPlatform
    public static FlowingFluid createFlowingFluid(AntimatterFluid fluid){
        throw new AssertionError();
    }
}
