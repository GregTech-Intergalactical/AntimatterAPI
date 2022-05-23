package muramasa.antimatter.mixin.forge;

import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.capability.EnergyHandler;
import net.minecraftforge.energy.IEnergyStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tesseract.api.gt.GTTransaction;

@Mixin(EnergyHandler.class)
public abstract class EnergyHandlerMixin implements IEnergyStorage {
    @Shadow
    abstract boolean insert(GTTransaction transaction);
    @Shadow
    abstract GTTransaction extract(GTTransaction.Mode mode);
    @Shadow
    abstract boolean extractEnergy(GTTransaction.TransferData data);
    @Shadow
    abstract long getEnergy();
    @Shadow
    abstract long getCapacity();
    @Shadow
    abstract boolean canInput();
    @Shadow
    abstract boolean canOutput();

    /**
     * Forge IEnergyStorage Implementations
     **/
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        GTTransaction transaction = new GTTransaction((long) (maxReceive / AntimatterConfig.GAMEPLAY.EU_TO_FE_RATIO), a -> {
        });
        insert(transaction);
        if (!simulate) transaction.commit();
        return transaction.isValid() ? (int) transaction.getData().stream().mapToLong(t -> t.getEnergy((long) (t.getAmps(true) * AntimatterConfig.GAMEPLAY.EU_TO_FE_RATIO), true)).sum() : 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        GTTransaction transaction = extract(GTTransaction.Mode.INTERNAL);
        transaction.addData((long) (maxExtract / AntimatterConfig.GAMEPLAY.EU_TO_FE_RATIO), this::extractEnergy);
        if (!simulate) transaction.commit();
        return transaction.isValid() ? (int) transaction.getData().stream().mapToLong(t -> t.getEnergy((long) (t.getAmps(false) * AntimatterConfig.GAMEPLAY.EU_TO_FE_RATIO), false)).sum() : 0;
    }

    @Override
    public int getEnergyStored() {
        long energy = getEnergy();
        return energy > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) energy;
    }

    @Override
    public int getMaxEnergyStored() {
        long capacity = getCapacity();
        return capacity > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) capacity;
    }

    @Override
    public boolean canReceive() {
        return canInput();
    }

    @Override
    public boolean canExtract() {
        return canOutput();
    }
}
