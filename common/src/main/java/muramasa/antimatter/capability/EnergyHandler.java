package muramasa.antimatter.capability;

import muramasa.antimatter.Ref;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import tesseract.api.gt.GTConsumer;
import tesseract.api.gt.GTTransaction;
import tesseract.api.gt.IEnergyHandler;
import tesseract.api.gt.IGTNode;


public class EnergyHandler implements IEnergyHandler {

    protected final long capacity;

    protected long energy;
    protected long voltageIn, voltageOut, amperageIn, amperageOut;

    protected GTConsumer.State state = new GTConsumer.State(this);

    public EnergyHandler(long energy, long capacity, long voltageIn, long voltageOut, int amperageIn, int amperageOut) {
        this.energy = energy;
        this.capacity = capacity;
        this.voltageIn = voltageIn;
        this.voltageOut = voltageOut;
        this.amperageIn = amperageIn;
        this.amperageOut = amperageOut;
    }

    /**
     * Tesseract IGTNode Implementations
     **/

    protected boolean checkVoltage(long voltage) {
        return true;
    }

    public void onUpdate() {
        this.state.onTick();
    }

    public void setOutputAmperage(long amperageOut) {
        this.amperageOut = amperageOut;
    }

    public void setInputAmperage(long amperageIn) {
        this.amperageIn = amperageIn;
    }

    public void setOutputVoltage(long voltageOut) {
        this.voltageOut = voltageOut;
    }

    public void setInputVoltage(long voltageIn) {
        this.voltageIn = voltageIn;
    }

    @Override
    public long extractEu(long voltage, boolean simulate) {
        long toDrain = Math.min(voltage, this.energy);
        if (!simulate) this.energy -= toDrain;
        return toDrain;
    }

    @Override
    public long insertEu(long voltage, boolean simulate) {
        if (voltage < 0) return 0;
        if (!simulate && !checkVoltage(voltage)) return voltage;
        long toAdd = Math.min(voltage, this.capacity - this.energy);
        if (!simulate) this.energy += toAdd;
        return toAdd;
    }

    public long insertInternal(long voltage, boolean simulate) {
        if (voltage < 0) return 0;
        long toAdd = Math.min(voltage, this.capacity - this.energy);
        if (!simulate) this.energy += toAdd;
        return toAdd;
    }

    @Override
    public long insertAmps(long voltage, long amps, boolean simulate) {
        if (voltage < 0 || amps < 0) return 0;
        if (!simulate && !checkVoltage(voltage)) return amps;
        amps = Math.min(amps, this.availableAmpsInput(voltage));
        amps = Math.min(amps, (this.getCapacity() - this.getEnergy()) / this.getInputVoltage());
        return IEnergyHandler.super.insertAmps(voltage, amps, simulate);
    }

    protected void overVolt() {
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
    public long getInputAmperage() {
        return amperageIn;
    }

    @Override
    public long getOutputAmperage() {
        return amperageOut;
    }

    @Override
    public long getInputVoltage() {
        return voltageIn;
    }

    @Override
    public long getOutputVoltage() {
        return voltageOut;
    }

    @Override
    public boolean canInput() {
        return voltageIn > 0;
    }

    @Override
    public boolean canInput(Direction direction) {
        return canInput();
    }

    @Override
    public boolean canOutput(Direction direction) {
        return canOutput();
    }

    @Override
    public boolean canOutput() {
        return voltageOut > 0;
    }

    @Override
    public CompoundTag serialize(CompoundTag tag) {
        tag.putLong(Ref.TAG_MACHINE_ENERGY, this.energy);
        tag.putLong(Ref.TAG_MACHINE_VOLTAGE_IN, this.voltageIn);
        tag.putLong(Ref.TAG_MACHINE_VOLTAGE_OUT, this.voltageOut);
        tag.putLong(Ref.TAG_MACHINE_AMPERAGE_IN, this.amperageIn);
        tag.putLong(Ref.TAG_MACHINE_AMPERAGE_OUT, this.amperageOut);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        this.energy = nbt.getLong(Ref.TAG_MACHINE_ENERGY);
        this.voltageIn = nbt.getLong(Ref.TAG_MACHINE_VOLTAGE_IN);
        this.voltageOut = nbt.getLong(Ref.TAG_MACHINE_VOLTAGE_OUT);
        this.amperageIn = nbt.getLong(Ref.TAG_MACHINE_AMPERAGE_IN);
        this.amperageOut = nbt.getLong(Ref.TAG_MACHINE_AMPERAGE_OUT);
    }

    public GTConsumer.State getState() {
        return state;
    }
}
