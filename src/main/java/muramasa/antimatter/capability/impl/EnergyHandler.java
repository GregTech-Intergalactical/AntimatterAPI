package muramasa.antimatter.capability.impl;

import muramasa.antimatter.capability.IEnergyHandler;
import net.minecraftforge.energy.IEnergyStorage;

public class EnergyHandler implements IEnergyHandler, IEnergyStorage {

    protected long energy, capacity, maxInsert, maxExtract;

    public EnergyHandler(long energy, long capacity, long maxInsert, long maxExtract) {
        this.energy = energy;
        this.capacity = capacity;
        this.maxInsert = maxInsert;
        this.maxExtract = maxExtract;
    }

    /** GTI IEnergyHandler Implementations **/
    @Override
    public long insert(long toInsert, boolean simulate) {
        if (!canInput()) return 0;

        long inserted = Math.min(capacity - energy, Math.min(this.maxInsert, toInsert));
        if (!simulate) energy += inserted;

        return inserted;
    }

    @Override
    public long extract(long toExtract, boolean simulate) {
        if (!canExtract()) return 0;

        long extracted = Math.min(energy, Math.min(this.maxExtract, toExtract));
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
    public long getMaxInsert() {
        return maxInsert;
    }

    @Override
    public long getMaxExtract() {
        return maxExtract;
    }

    @Override
    public boolean canInput() {
        return maxInsert > 0;
    }

    @Override
    public boolean canOutput() {
        return maxExtract > 0;
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
}
