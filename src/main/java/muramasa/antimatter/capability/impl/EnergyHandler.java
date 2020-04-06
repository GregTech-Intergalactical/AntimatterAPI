package muramasa.antimatter.capability.impl;

import muramasa.antimatter.capability.IEnergyHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.energy.IEnergyStorage;
import tesseract.util.Dir;

public class EnergyHandler implements IEnergyHandler, IEnergyStorage {

    protected long energy, capacity, voltage_in, voltage_out, amperage_in, amperage_out;

    public EnergyHandler(long energy, long capacity, long voltage_in, long voltage_out, long amperage_in, long amperage_out) {
        this.energy = energy;
        this.capacity = capacity;
        this.voltage_in = voltage_in;
        this.voltage_out = voltage_out;
        this.amperage_in = amperage_in;
        this.amperage_out = amperage_out;
    }

    /** GTI IEnergyHandler Implementations **/
    @Override
    public long insert(long toInsert, boolean simulate) {
        if (!canInput()) return 0L;

        // Not check the min input due to dynamic amperage value
        long inserted = Math.min(capacity - energy, toInsert);
        if (!simulate) energy += inserted;

        return inserted;
    }

    @Override
    public long extract(long toExtract, boolean simulate) {
        if (!canExtract()) return 0L;

        // Not check the min input due to dynamic amperage value
        long extracted = Math.min(energy, toExtract);
        if (!simulate) energy -= extracted;

        return extracted;
    }

    @Override
    public long getPower() {
        return energy;
    }

    @Override
    public long getCapacity() {
        return capacity;
    }

    @Override
    public long getInputAmperage() {
        return amperage_in;
    }

    @Override
    public long getOutputAmperage() {
        return amperage_out;
    }

    @Override
    public long getInputVoltage() {
        return voltage_in;
    }

    @Override
    public long getOutputVoltage() {
        return voltage_out;
    }

    @Override
    public boolean canInput() {
        return voltage_in > 0L;
    }

    @Override
    public boolean canOutput() {
        return voltage_out > 0L;
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
        return (int) getPower();
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
