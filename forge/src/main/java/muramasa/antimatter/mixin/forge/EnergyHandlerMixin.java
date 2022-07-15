package muramasa.antimatter.mixin.forge;

import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.capability.EnergyHandler;
import net.minecraftforge.energy.IEnergyStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tesseract.TesseractConfig;
import tesseract.api.gt.GTTransaction;
import tesseract.api.gt.IEnergyHandler;

@Mixin(EnergyHandler.class)
public abstract class EnergyHandlerMixin implements IEnergyStorage, IEnergyHandler {
    /**
     * Forge IEnergyStorage Implementations
     **/
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        GTTransaction transaction = new GTTransaction((long) (maxReceive / TesseractConfig.COMMON.EU_TO_FE_RATIO), a -> {
        });
        insert(transaction);
        if (!simulate) transaction.commit();
        return transaction.isValid() ? (int) transaction.getData().stream().mapToLong(t -> t.getEnergy((long) (t.getAmps(true) * TesseractConfig.COMMON.EU_TO_FE_RATIO), true)).sum() : 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        GTTransaction transaction = extract(GTTransaction.Mode.INTERNAL);
        transaction.addData((long) (maxExtract / TesseractConfig.COMMON.EU_TO_FE_RATIO), this::extractEnergy);
        if (!simulate) transaction.commit();
        return transaction.isValid() ? (int) transaction.getData().stream().mapToLong(t -> t.getEnergy((long) (t.getAmps(false) * TesseractConfig.COMMON.EU_TO_FE_RATIO), false)).sum() : 0;
    }

    @Override
    public int getEnergyStored() {
        long energy = (long) (getEnergy() * TesseractConfig.COMMON.EU_TO_FE_RATIO);
        return energy > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) energy;
    }

    @Override
    public int getMaxEnergyStored() {
        long capacity = (long) (getCapacity() * TesseractConfig.COMMON.EU_TO_FE_RATIO);
        return capacity > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) capacity;
    }

    @Override
    public boolean canReceive() {
        return TesseractConfig.COMMON.ENABLE_FE_OR_TRE_INPUT && canInput();
    }

    @Override
    public boolean canExtract() {
        return canOutput();
    }
}
