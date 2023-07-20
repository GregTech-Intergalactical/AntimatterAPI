package muramasa.antimatter.integration.ct.forge;

import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import earth.terrarium.botarium.forge.fluid.ForgeFluidHolder;

public class CrafttweakerUtilsImpl {
    public static FluidHolder fromIFluidStack(IFluidStack fluidStack){
        return new ForgeFluidHolder(fluidStack.getInternal());
    }
}
