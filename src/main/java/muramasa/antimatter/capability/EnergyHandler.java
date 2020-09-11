package muramasa.antimatter.capability;

import muramasa.antimatter.Ref;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import tesseract.util.Dir;

public class EnergyHandler implements IEnergyStorage, IEnergyHandler {

    protected final long capacity;
    protected final LazyOptional<IEnergyHandler> handler = LazyOptional.of(() -> this);

    protected long energy;
    protected int voltageIn, voltageOut, amperageIn, amperageOut;

    public EnergyHandler(long energy, long capacity, int voltageIn, int voltageOut, int amperageIn, int amperageOut) {
        this.energy = energy;
        this.capacity = capacity;
        this.voltageIn = voltageIn;
        this.voltageOut = voltageOut;
        this.amperageIn = amperageIn;
        this.amperageOut = amperageOut;
    }

    /** Tesseract IGTNode Implementations **/
    @Override
    public long insert(long maxReceive, boolean simulate) {
        if (!canInput()) {
            return 0;
        }
        long toInsert = Math.max(Math.min(capacity - energy, maxReceive), 0);
        if (!simulate) {
            energy += toInsert;
        }
        return toInsert;
    }

    @Override
    public long extract(long maxExtract, boolean simulate) {
        //if (!canOutput()) return 0;
        long toExtract = Math.max(Math.min(energy, maxExtract), 0);
        if (!simulate) {
            energy -= toExtract;
        }
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
        return amperageIn;
    }

    @Override
    public int getOutputAmperage() {
        return amperageOut;
    }

    @Override
    public int getInputVoltage() {
        return voltageIn;
    }

    @Override
    public int getOutputVoltage() {
        return voltageOut;
    }

    @Override
    public boolean canInput() {
        return voltageIn > 0;
    }

    @Override
    public boolean canOutput(Dir direction) {
        return canOutput();
    }

    @Override
    public boolean canOutput() {
        return voltageOut > 0;
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

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return AntimatterCaps.ENERGY_HANDLER_CAPABILITY.orEmpty(cap, this.handler);
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putLong(Ref.TAG_MACHINE_ENERGY, this.energy);
        tag.putInt(Ref.TAG_MACHINE_VOLTAGE_IN, this.voltageIn);
        tag.putInt(Ref.TAG_MACHINE_VOLTAGE_OUT, this.voltageOut);
        tag.putInt(Ref.TAG_MACHINE_AMPERAGE_IN, this.amperageIn);
        tag.putInt(Ref.TAG_MACHINE_AMPERAGE_OUT, this.amperageOut);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.energy = nbt.getLong(Ref.TAG_MACHINE_ENERGY);
        this.voltageIn = nbt.getInt(Ref.TAG_MACHINE_VOLTAGE_IN);
        this.voltageOut = nbt.getInt(Ref.TAG_MACHINE_VOLTAGE_OUT);
        this.amperageIn = nbt.getInt(Ref.TAG_MACHINE_AMPERAGE_IN);
        this.amperageOut = nbt.getInt(Ref.TAG_MACHINE_AMPERAGE_OUT);
    }

}
