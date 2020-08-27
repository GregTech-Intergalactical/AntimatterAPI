package muramasa.antimatter.capability;

import muramasa.antimatter.capability.IEnergyHandler;
import net.minecraftforge.energy.IEnergyStorage;
import tesseract.api.ITickingController;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EnergyHandler implements IEnergyStorage, IEnergyHandler {

    protected long energy, capacity;
    //Change to protected since amperage for buffers are dynamic.
    protected int voltage_in, voltage_out, amperage_in, amperage_out;

    public EnergyHandler(long energy, long capacity, int voltage_in, int voltage_out, int amperage_in, int amperage_out) {
        this.energy = energy;
        this.capacity = capacity;
        this.voltage_in = voltage_in;
        this.voltage_out = voltage_out;
        this.amperage_in = amperage_in;
        this.amperage_out = amperage_out;
    }

    /** Tesseract IElectricNode Implementations **/
    @Override
    public long insert(long maxReceive, boolean simulate) {
        if (!canInput()) return 0;

        long inserted = Math.min(capacity - energy, maxReceive);

        //TODO: Don't allow less than one packet.
        if (inserted < maxReceive) {
            return 0;
        }
        if (!simulate) energy += inserted;

        return inserted;
    }

    @Override
    public long extract(long maxExtract, boolean simulate) {
        //if (!canOutput()) return 0;

        long extracted = Math.min(energy, maxExtract);
        //TODO: Don't allow less than one packet.
        if (extracted < maxExtract) {
            return 0;
        }
        if (!simulate) energy -= extracted;

        return extracted;
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
        return /*amperage_in > 0 &&*/ voltage_in > 0;
    }

    @Override
    public boolean canOutput(@Nonnull Dir direction) {
        return canOutput();
    }

    @Override
    public boolean canOutput() {
        //TODO: Only singular packets?
        return /*amperage_out > 0 &&*/ voltage_out > 0;
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
    public boolean connects(@Nonnull Dir direction) {
        return true;
    }

    @Override
    public void reset(@Nullable ITickingController oldController, @Nullable ITickingController newController) {
    }
}
