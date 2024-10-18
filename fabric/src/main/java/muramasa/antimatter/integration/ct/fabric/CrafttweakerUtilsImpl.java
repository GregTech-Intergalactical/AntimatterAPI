package muramasa.antimatter.integration.ct.fabric;

import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import muramasa.antimatter.integration.ct.CrafttweakerUtils;

public class CrafttweakerUtilsImpl implements CrafttweakerUtils {
    public FluidHolder fromIFluidStack(IFluidStack fluidStack){
        return FluidHooks.newFluidHolder(fluidStack.getFluid(), fluidStack.getAmount(), fluidStack.getTag().getInternal());
    }
}
