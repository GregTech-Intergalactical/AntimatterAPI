package muramasa.gregtech.api.capability.impl;

import muramasa.gregtech.api.capability.IEnergyStorage;
import muramasa.gregtech.common.tileentities.base.TileEntityMachine;

public class MachineEnergyHandler implements IEnergyStorage {

    protected TileEntityMachine tile;
    protected long energy;
    protected long capacity;
    protected long maxInsert;
    protected long maxExtract;

    public MachineEnergyHandler(TileEntityMachine tile) {
        capacity = tile.getTier().getVoltage() * 64;
        maxInsert = tile.getTier().getVoltage();
        maxExtract = tile.getTier().getVoltage();
        energy = Long.MAX_VALUE; //TODO temp
    }

    @Override
    public long insert(long toInsert, boolean simulate) {
        if (!canInsert()) return 0;

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
