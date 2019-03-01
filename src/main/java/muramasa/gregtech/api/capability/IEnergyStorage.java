package muramasa.gregtech.api.capability;

public interface IEnergyStorage {

    long insert(long maxInsert, boolean simulate);

    long extract(long maxExtract, boolean simulate);

    long getEnergyStored();

    long getMaxEnergyStored();

    boolean canInsert();

    boolean canExtract();
}
