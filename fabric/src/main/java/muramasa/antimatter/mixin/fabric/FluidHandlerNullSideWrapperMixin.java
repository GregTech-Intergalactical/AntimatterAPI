package muramasa.antimatter.mixin.fabric;

import muramasa.antimatter.capability.fluid.FluidHandlerNullSideWrapper;
import net.fabricatedforgeapi.fluid.IFluidHandlerStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FluidHandlerNullSideWrapper.class)
public class FluidHandlerNullSideWrapperMixin implements IFluidHandlerStorage {
    @Override
    public IFluidHandler getHandler() {
        return (FluidHandlerNullSideWrapper)(Object)this;
    }
}
