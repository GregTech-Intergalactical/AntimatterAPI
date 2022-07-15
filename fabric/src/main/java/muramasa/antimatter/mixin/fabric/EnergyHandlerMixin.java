package muramasa.antimatter.mixin.fabric;

import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.capability.EnergyHandler;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import team.reborn.energy.api.EnergyStorage;
import tesseract.TesseractConfig;
import tesseract.api.gt.GTTransaction;
import tesseract.api.gt.IEnergyHandler;

@Mixin(EnergyHandler.class)
public abstract class EnergyHandlerMixin implements EnergyStorage, IEnergyHandler {
    @Shadow @Final
    protected long capacity;
    @Shadow
    protected long voltageIn;
    /**
     * Team Reborn EnergyStorage Implementations
     **/
    @Override
    public long insert(long maxReceive, TransactionContext context) {
        GTTransaction transaction = new GTTransaction((long) (maxReceive / TesseractConfig.COMMON.EU_TO_TRE_RATIO), a -> {
        });
        insert(transaction);
        transaction.commit();
        return transaction.isValid() ? (int) transaction.getData().stream().mapToLong(t -> t.getEnergy((long) (t.getAmps(true) * TesseractConfig.COMMON.EU_TO_TRE_RATIO), true)).sum() : 0;
    }

    @Override
    public long extract(long maxExtract, TransactionContext simulate) {
        GTTransaction transaction = extract(GTTransaction.Mode.INTERNAL);
        transaction.addData((long) (maxExtract / TesseractConfig.COMMON.EU_TO_TRE_RATIO), this::extractEnergy);
        //if (!simulate) transaction.commit();
        transaction.commit();
        return transaction.isValid() ? (int) transaction.getData().stream().mapToLong(t -> t.getEnergy((long) (t.getAmps(false) * TesseractConfig.COMMON.EU_TO_TRE_RATIO), false)).sum() : 0;
    }

    @Override
    public long getAmount() {
        return (long) (getEnergy() * TesseractConfig.COMMON.EU_TO_TRE_RATIO);
    }

    @Override
    public long getCapacity() {
        return (long) (capacity * TesseractConfig.COMMON.EU_TO_TRE_RATIO);
    }

    @Override
    public boolean supportsInsertion() {
        return TesseractConfig.COMMON.ENABLE_FE_OR_TRE_INPUT && canInput();
    }

    @Override
    public boolean supportsExtraction() {
        return canOutput();
    }
}
