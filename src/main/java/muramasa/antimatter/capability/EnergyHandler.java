package muramasa.antimatter.capability;

import muramasa.antimatter.Ref;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;
import tesseract.api.gt.GTConsumer;
import tesseract.api.gt.IEnergyHandler;
import tesseract.api.gt.IGTNode;
import tesseract.util.Dir;

public class EnergyHandler implements IEnergyStorage, IEnergyHandler {

    protected final long capacity;

    protected long energy;
    protected int voltageIn, voltageOut, amperageIn, amperageOut;

    protected GTConsumer.State state = new GTConsumer.State(this);

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
        //if (!canInput()) return 0;
        if (!checkVoltage(maxReceive, simulate)) {
            return 0;
        }
        long toInsert = Math.max(Math.min(capacity - energy, maxReceive), 0);
        if (getState().receive(true, 1, toInsert)) {
            if (!simulate) {
                energy += toInsert;
                getState().receive(false,  1, toInsert);
            }
        } else {
            return 0;
        }
        return toInsert;
    }

    protected boolean checkVoltage(long receive, boolean simulate) {
        return true;
    }

    public void onUpdate() {
        this.state.onTick();
    }

    @Override
    public long extract(long maxExtract, boolean simulate) {
        //if (!canOutput()) return 0;
        if (getState().extract(true, 1, maxExtract)) {
            long toExtract = Math.max(Math.min(energy, maxExtract), 0);
            if (!simulate) {
                energy -= toExtract;
                getState().extract(false, 1, maxExtract);
            }
            return toExtract;
        }
        return 0;
    }

    public void setOutputAmperage(int amperageOut) {
        this.amperageOut = amperageOut;
    }

    public void setInputAmperage(int amperageIn) {
        this.amperageIn = amperageIn;
    }

    public void setOutputVoltage(int voltageOut) {
        this.voltageOut = voltageOut;
    }

    public void setInputVoltage(int voltageIn) {
        this.voltageIn = voltageIn;
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
    public boolean canInput(Dir direction) {
        return canInput();
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
        long receive = insert(maxReceive, simulate);
        return receive > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) receive;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        long extract = extract(maxExtract, simulate);
        return extract > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) extract;
    }

    @Override
    public int getEnergyStored() {
        long energy = getEnergy();
        return energy > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) energy;
    }

    @Override
    public int getMaxEnergyStored() {
        long capacity = getCapacity();
        return capacity > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) capacity;
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

    public GTConsumer.State getState() {
        return state;
    }
}
