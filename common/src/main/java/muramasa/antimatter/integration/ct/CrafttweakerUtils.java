package muramasa.antimatter.integration.ct;

import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import dev.architectury.injectables.annotations.ExpectPlatform;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import muramasa.antimatter.util.ImplLoader;

public interface CrafttweakerUtils {
    CrafttweakerUtils INSTANCE = ImplLoader.load(CrafttweakerUtils.class);
    FluidHolder fromIFluidStack(IFluidStack fluidStack);
}
