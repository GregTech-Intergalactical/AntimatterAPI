package muramasa.gtu.api.capability;

public interface IEnergyHandler {

    long insert(long toInsert, boolean simulate);

    long extract(long toExtract, boolean simulate);

    long getPower();

    long getCapacity();

    long getMaxInsert();

    long getMaxExtract();

    boolean canInput();

    boolean canOutput();
}
