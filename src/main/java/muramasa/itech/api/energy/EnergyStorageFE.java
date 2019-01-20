package muramasa.itech.api.energy;

import net.minecraftforge.energy.IEnergyStorage;

public class EnergyStorageFE implements IEnergyStorage {

    private long energy;
    private long capacity;
    private long maxReceive;
    private long maxExtract;

    public EnergyStorageFE(long capacity) {
        this(capacity, (int)capacity, (int)capacity, 0);
    }

    public EnergyStorageFE(long capacity, int maxTransfer) {
        this(capacity, maxTransfer, maxTransfer, 0);
    }

    public EnergyStorageFE(long capacity, int maxReceive, int maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public EnergyStorageFE(long capacity, int maxReceive, int maxExtract, long energy) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.energy = Math.max(0 , Math.min(capacity, energy));
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) return 0;

        int energyReceived = (int) Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if (!simulate) energy += energyReceived;

        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) return 0;

        int energyExtracted = (int) Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if (!simulate) energy -= energyExtracted;

        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return (int) energy;
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) capacity;
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
