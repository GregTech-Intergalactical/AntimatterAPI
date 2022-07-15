package muramasa.antimatter.mixin.fabric;

import muramasa.antimatter.capability.EnergyHandler;
import org.spongepowered.asm.mixin.Mixin;
import tesseract.api.fabric.wrapper.IEnergyHandlerStorage;
import tesseract.api.gt.IEnergyHandler;

@Mixin(EnergyHandler.class)
public abstract class EnergyHandlerMixin implements IEnergyHandlerStorage {
    @Override
    public IEnergyHandler getEnergyHandler() {
        return (EnergyHandler)(Object)this;
    }
}
