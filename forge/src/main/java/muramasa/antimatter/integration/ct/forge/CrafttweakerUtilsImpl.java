package muramasa.antimatter.integration.ct.forge;

import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.forge.fluid.ForgeFluidHolder;
import muramasa.antimatter.integration.ct.CrafttweakerUtils;

public class CrafttweakerUtilsImpl implements CrafttweakerUtils {
    public FluidHolder fromIFluidStack(IFluidStack fluidStack){
        return new ForgeFluidHolder(fluidStack.getInternal());
    }
}
