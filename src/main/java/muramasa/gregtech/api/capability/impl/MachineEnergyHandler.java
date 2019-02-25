package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.capability.IEnergyStorage;
import muramasa.gregtech.api.machines.Tier;

public class MachineEnergyHandler implements IEnergyStorage {

    public long energy;
    private long capacity;
    private long maxReceive;
    private long maxExtract;

    public MachineEnergyHandler(Tier tier) {
        this.capacity = tier.getVoltage() * 64;
        this.maxReceive = tier.getVoltage();
        this.maxExtract = tier.getVoltage();
        this.energy = Math.max(0 , Math.min(capacity, energy));
    }

    @Override
    public long receiveEnergy(long maxReceive, boolean simulate) {
        if (!canReceive()) return 0;

        long energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate) energy += energyReceived;

        return energyReceived;
    }

    @Override
    public long extractEnergy(long maxExtract, boolean simulate) {
        if (!canExtract()) return 0;

        long energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate) energy -= energyExtracted;

        return energyExtracted;
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
    public boolean canExtract() {
        return maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return maxReceive > 0;
    }
}
