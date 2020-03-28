package muramasa.antimatter.capability.impl;

import muramasa.antimatter.capability.IEnergyHandler;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.energy.IEnergyStorage;
import tesseract.util.Dir;

public class EnergyHandler implements IEnergyHandler, IEnergyStorage {

    protected long energy, amperage, capacity, input, output;

    public EnergyHandler(long energy, long amperage, long capacity, long input, long output) {
        this.energy = energy;
        this.amperage = amperage;
        this.capacity = capacity;
        this.input = input;
        this.output = output;
    }

    /** GTI IEnergyHandler Implementations **/
    @Override
    public long insert(long toInsert, boolean simulate) {
        if (!canInput()) return 0;

        // Not check the min input due to dynamic amperage value
        long inserted = Math.min(capacity - energy, toInsert);
        if (!simulate) energy += inserted;

        return inserted;
    }

    @Override
    public long extract(long toExtract, boolean simulate) {
        if (!canExtract()) return 0;

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
        return amperage * 2;
    }

    @Override
    public long getOutputAmperage() {
        return amperage;
    }

    @Override
    public long getInputVoltage() {
        return input;
    }

    @Override
    public long getOutputVoltage() {
        return output;
    }

    @Override
    public boolean canInput() {
        return input > 0L;
    }

    @Override
    public boolean canOutput() {
        return output > 0L;
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
