package muramasa.antimatter.capability;

import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Ref;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.energy.IEnergyStorage;
import tesseract.api.gt.GTConsumer;
import tesseract.api.gt.GTTransaction;
import tesseract.api.gt.IEnergyHandler;


public class EnergyHandler implements IEnergyStorage, IEnergyHandler {

    protected final long capacity;

    protected long energy;
    protected long voltageIn, voltageOut, amperageIn, amperageOut;

    protected GTConsumer.State state = new GTConsumer.State(this);

    public EnergyHandler(long energy, long capacity, int voltageIn, int voltageOut, int amperageIn, int amperageOut) {
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

    protected boolean checkVoltage(GTTransaction.TransferData data) {
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
    public boolean extractEnergy(GTTransaction.TransferData data) {
        if (data.transaction.mode == GTTransaction.Mode.TRANSMIT) {
            long amps = Math.min(data.getAmps(false), this.availableAmpsOutput());
            amps = Math.min(amps, this.energy / this.getOutputVoltage());
            this.energy -= data.getEnergy(amps, false);
            this.getState().extract(false, amps);
            data.useAmps(false, amps);
            return amps > 0;
        } else {
            long toDrain = Math.min(data.getEu(), this.energy);
            this.energy -= data.drainEu(toDrain);
            return toDrain > 0;
        }
    }

    protected void overVolt() {

    }

    @Override
    public boolean addEnergy(GTTransaction.TransferData data) {
        if (data.transaction.mode == GTTransaction.Mode.TRANSMIT) {
            boolean ok = checkVoltage(data);
            if (!ok) {
                return false;
            }
            long amps = Math.min(data.getAmps(true), this.availableAmpsInput());
            amps = Math.min(amps, (this.capacity - this.energy) / this.getInputVoltage());
            this.energy += data.getEnergy(amps, true);
            data.useAmps(true, amps);
            this.getState().receive(false, amps);
            return amps > 0;
        } else {
            long toAdd = Math.min(data.getEu(), this.capacity - this.energy);
            this.energy += data.drainEu(toAdd);
            return toAdd > 0;
        }
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

    /**
     * Forge IEnergyStorage Implementations
     **/
    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        GTTransaction transaction = new GTTransaction((long) (maxReceive / AntimatterConfig.GAMEPLAY.EU_TO_FE_RATIO), a -> {
        });
        insert(transaction);
        if (!simulate) transaction.commit();
        return transaction.isValid() ? (int) transaction.getData().stream().mapToLong(t -> t.getEnergy((long) (t.getAmps(true) * AntimatterConfig.GAMEPLAY.EU_TO_FE_RATIO), true)).sum() : 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        GTTransaction transaction = extract(GTTransaction.Mode.INTERNAL);
        transaction.addData((long) (maxExtract / AntimatterConfig.GAMEPLAY.EU_TO_FE_RATIO), this::extractEnergy);
        if (!simulate) transaction.commit();
        return transaction.isValid() ? (int) transaction.getData().stream().mapToLong(t -> t.getEnergy((long) (t.getAmps(false) * AntimatterConfig.GAMEPLAY.EU_TO_FE_RATIO), false)).sum() : 0;
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
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putLong(Ref.TAG_MACHINE_ENERGY, this.energy);
        tag.putLong(Ref.TAG_MACHINE_VOLTAGE_IN, this.voltageIn);
        tag.putLong(Ref.TAG_MACHINE_VOLTAGE_OUT, this.voltageOut);
        tag.putLong(Ref.TAG_MACHINE_AMPERAGE_IN, this.amperageIn);
        tag.putLong(Ref.TAG_MACHINE_AMPERAGE_OUT, this.amperageOut);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
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
