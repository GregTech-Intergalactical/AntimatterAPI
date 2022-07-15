package muramasa.antimatter.mixin.forge;

import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.capability.EnergyHandler;
import net.minecraftforge.energy.IEnergyStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tesseract.TesseractConfig;
import tesseract.api.forge.wrapper.IEnergyHandlerStorage;
import tesseract.api.gt.GTTransaction;
import tesseract.api.gt.IEnergyHandler;

@Mixin(EnergyHandler.class)
public abstract class EnergyHandlerMixin implements IEnergyHandlerStorage {
    @Override
    public IEnergyHandler getEnergyHandler() {
        return (EnergyHandler)(Object)this;
    }
}
