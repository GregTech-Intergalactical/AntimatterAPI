package muramasa.antimatter.capability.machine;

import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import tesseract.api.gt.GTTransaction;
import tesseract.api.gt.IEnergyHandler;
import tesseract.api.gt.IGTNode;

import java.util.Arrays;

public class MultiMachineEnergyHandler<T extends TileEntityMultiMachine<T>> extends MachineEnergyHandler<T> {

    protected MachineEnergyHandler<?>[] inputs;//= new IEnergyHandler[0];
    protected MachineEnergyHandler<?>[] outputs; //= new IEnergyHandler[0];

    protected long cachedCapacity;

    public MultiMachineEnergyHandler(T tile, long energy, long capacity, int voltage_in, int voltage_out, int amperage_in, int amperage_out) {
        super(tile, energy, capacity, voltage_in, voltage_out, amperage_in, amperage_out);
    }

    public MultiMachineEnergyHandler(T tile) {
        super(tile, 0, 0, 0, 0, 0, 0);
    }

    public void onStructureBuild() {
        cacheInputs();
        cacheOutputs();

        //Amps in, amps out etc for this handler does not matter.
        //all handlers should be of same voltage.
        IEnergyHandler handler = anyHandler();
        if (handler != null) {
            this.voltageIn = handler.getInputVoltage();
            this.voltageOut = handler.getOutputVoltage();
        }
        this.cachedCapacity = super.getCapacity() + Arrays.stream(inputs).mapToLong(IGTNode::getCapacity).sum() + Arrays.stream(outputs).mapToLong(IGTNode::getCapacity).sum();
    }

    private void cacheInputs() {
        this.inputs = tile.getComponents("hatch_energy").stream().filter(t -> t.getEnergyHandler().isPresent()).map(t -> (t.getEnergyHandler().resolve().get())).toArray(MachineEnergyHandler<?>[]::new);
    }

    private void cacheOutputs() {
        this.outputs = tile.getComponents("hatch_dynamo").stream().filter(t -> t.getEnergyHandler().isPresent()).map(t -> (t.getEnergyHandler().resolve().get())).toArray(MachineEnergyHandler<?>[]::new);
    }

    private IEnergyHandler anyHandler() {
        if (inputs != null && inputs.length > 0) {
            return inputs[0];
        }
        if (outputs != null && outputs.length > 0) {
            return outputs[0];
        }
        return null;
    }

    public void invalidate() {
        this.cachedCapacity = super.getCapacity();
        this.inputs = null;
        this.outputs = null;
    }

   /* @Override
    public long insertInternal(long maxReceive, boolean simulate, boolean force) {
        if (outputs == null) cacheOutputs();
        long inserted = super.insertInternal(maxReceive, simulate, force);
        if (inserted == 0 && outputs != null) {
            for (MachineEnergyHandler<?> handler : outputs) {
                inserted += handler.insertInternal(maxReceive - inserted, simulate, force);
                //Output voltage as a dynamo outputs energy.
                if (!simulate && inserted > handler.getOutputVoltage()) {
                    System.out.println("BOOM");
                }
                if (inserted >= maxReceive)
                    return inserted;
            }
        }
        return inserted;
    }*/

    @Override
    public long getEnergy() {
        return super.getEnergy() + (inputs == null ? 0 : Arrays.stream(inputs).mapToLong(IGTNode::getEnergy).sum()) + (outputs == null ? 0 : Arrays.stream(outputs).mapToLong(IGTNode::getEnergy).sum());
    }

    @Override
    public long getCapacity() {
        return cachedCapacity;
    }

    @Override
    public boolean canOutput() {
        return false;
    }

    @Override
    public boolean canInput() {
        return false;
    }

    @Override
    public boolean extractEnergy(GTTransaction.TransferData data) {
        boolean ok = super.extractEnergy(data);
        if (data.transaction.mode == GTTransaction.Mode.INTERNAL) {
            for (MachineEnergyHandler<?> handler : inputs) {
                ok |= handler.extractEnergy(data);
            }
        }
        return ok;
    }

    @Override
    public boolean addEnergy(GTTransaction.TransferData data) {
        boolean ok = super.addEnergy(data);
        if (data.transaction.mode == GTTransaction.Mode.INTERNAL) {
            for (MachineEnergyHandler<?> handler : outputs) {
                ok |= handler.addEnergy(data);
            }
        }
        return ok;
    }

    @Override
    public boolean insert(GTTransaction transaction) {
        return super.insert(transaction);
    }

    public Tier getAccumulatedPower() {
        if (inputs == null) return Tier.ULV;
        long voltage = Arrays.stream(inputs).mapToLong(t -> (long) t.getInputVoltage() * t.getInputAmperage()).sum();
        Tier tier = Tier.getTier((int) voltage);
        return voltage >= tier.getVoltage() ? tier : Tier.getTier((int) (voltage >> 2));
    }
}
