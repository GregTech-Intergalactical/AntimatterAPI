package muramasa.gregtech.api.capability;

public interface IEnergyStorage {

    long receiveEnergy(long maxReceive, boolean simulate);

    long extractEnergy(long maxExtract, boolean simulate);

    long getEnergyStored();

    long getMaxEnergyStored();

    boolean canExtract();

    boolean canReceive();
}
