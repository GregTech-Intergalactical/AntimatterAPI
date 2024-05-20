package muramasa.antimatter.integration.ct.fabric;

import com.blamejared.crafttweaker.api.fluid.IFluidStack;
import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import earth.terrarium.botarium.common.fluid.utils.FluidHooks;
import net.minecraft.nbt.CompoundTag;

public class CrafttweakerUtilsImpl {
    public static FluidHolder fromIFluidStack(IFluidStack fluidStack){
        return FluidHooks.newFluidHolder(fluidStack.getFluid(), fluidStack.getAmount(), fluidStack.getTag().getInternal() instanceof CompoundTag tag ? tag : null);
    }
}
