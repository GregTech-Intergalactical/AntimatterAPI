package muramasa.antimatter.capability;

import net.minecraftforge.energy.IEnergyStorage;
import tesseract.api.ITickingController;
import tesseract.util.Dir;

public class EnergyHandler implements IEnergyStorage, IEnergyHandler {

    protected long energy, capacity;
    protected int voltage_in, voltage_out, amperage_in, amperage_out;

    public EnergyHandler(long energy, long capacity, int voltage_in, int voltage_out, int amperage_in, int amperage_out) {
        this.energy = energy;
        this.capacity = capacity;
        this.voltage_in = voltage_in;
        this.voltage_out = voltage_out;
        this.amperage_in = amperage_in;
        this.amperage_out = amperage_out;
    }

    /** Tesseract IGTNode Implementations **/
    @Override
    public long insert(long maxReceive, boolean simulate) {
        if (!canInput()) return 0;

        long toInsert = Math.max(Math.min(capacity - energy, maxReceive), 0);
        if (!simulate) energy += toInsert;

        return toInsert;
    }

    @Override
    public long extract(long maxExtract, boolean simulate) {
        //if (!canOutput()) return 0;

        long toExtract = Math.max(Math.min(energy, maxExtract), 0);
        if (!simulate) energy -= toExtract;

        return toExtract;
    }

    @Override
    public long getEnergy() {
        return energy;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    public int getInputAmperage() {
        return amperage_in;
    }

    @Override
    public int getOutputAmperage() {
        return amperage_out;
    }

    @Override
    public int getInputVoltage() {
        return voltage_in;
    }

    @Override
    public int getOutputVoltage() {
        return voltage_out;
    }

    @Override
    public boolean canInput() {
        return voltage_in > 0;
    }

    @Override
    public boolean canOutput(Dir direction) {
        return canOutput();
    }

    @Override
    public boolean canOutput() {
        return voltage_out > 0;
    }

    /** Forge IEnergyStorage Implementations **/
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        return (int) insert(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return (int) extract(maxExtract, simulate);
    }

    @Override
    public int getEnergyStored() {
        return (int) getEnergy();
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) getCapacity();
    }

    @Override
    public boolean canReceive() {
        return canInput();
    }

    @Override
    public boolean canExtract() {
        return canOutput();
    }

    @Override
    public boolean connects(Dir direction) {
        return true;
    }
}
