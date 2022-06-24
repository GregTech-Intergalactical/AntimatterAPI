package muramasa.antimatter.mixin.fabric;

import muramasa.antimatter.capability.FluidHandler;
import net.fabricatedforgeapi.fluid.IFluidHandlerStorage;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(FluidHandler.class)
public class FluidHandlerMixin implements IFluidHandlerStorage {
    @Override
    public IFluidHandler getHandler() {
        return (FluidHandler) (Object)this;
    }
}
