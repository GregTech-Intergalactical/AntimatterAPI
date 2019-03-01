package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.capability.IEnergyStorage;
import muramasa.gregtech.api.machines.Tier;

public class MachineEnergyHandler implements IEnergyStorage {

    public long energy;
    private long capacity;
    private long maxInsert;
    private long maxExtract;

    public MachineEnergyHandler(Tier tier) {
        this.capacity = tier.getVoltage() * 64;
        this.maxInsert = tier.getVoltage();
        this.maxExtract = tier.getVoltage();
        this.energy = Math.max(0 , Math.min(capacity, energy));
    }

    @Override
    public long insert(long maxInsert, boolean simulate) {
        if (!canInsert()) return 0;

        long inserted = Math.min(capacity - energy, Math.min(this.maxInsert, maxInsert));
        if (!simulate) energy += inserted;

        return inserted;
    }

    @Override
    public long extract(long maxExtract, boolean simulate) {
        if (!canExtract()) return 0;

        long extracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate) energy -= extracted;

        return extracted;
    }

    @Override
    public long getEnergyStored() {
        return energy;
    }

    @Override
    public long getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canInsert() {
        return maxInsert > 0;
    }

    @Override
    public boolean canExtract() {
        return maxExtract > 0;
    }
}
