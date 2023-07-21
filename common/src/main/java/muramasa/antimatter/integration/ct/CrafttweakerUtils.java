package muramasa.antimatter.integration.ct;

import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import dev.architectury.injectables.annotations.ExpectPlatform;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;

public class CrafttweakerUtils {
    @ExpectPlatform
    public static FluidHolder fromIFluidStack(IFluidStack fluidStack){
        throw new AssertionError();
    }
}
