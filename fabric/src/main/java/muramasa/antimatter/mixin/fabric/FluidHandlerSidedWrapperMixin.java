package muramasa.antimatter.mixin.fabric;

import muramasa.antimatter.capability.fluid.FluidHandlerSidedWrapper;
import net.fabricatedforgeapi.fluid.IFluidHandlerStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FluidHandlerSidedWrapper.class)
public class FluidHandlerSidedWrapperMixin implements IFluidHandlerStorage {
    @Override
    public IFluidHandler getHandler() {
        return (FluidHandlerSidedWrapper)(Object)this;
    }
}
