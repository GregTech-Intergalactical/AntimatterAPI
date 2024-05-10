package muramasa.antimatter.capability.machine;

import muramasa.antimatter.blockentity.multi.BlockEntityMultiMachine;
import muramasa.antimatter.machine.Tier;
import tesseract.api.gt.IEnergyHandler;
import tesseract.api.gt.IGTNode;

import java.util.Arrays;

public class MultiMachineEnergyHandler<T extends BlockEntityMultiMachine<T>> extends MachineEnergyHandler<T> {

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
        this.inputs = tile.getComponentsByHandlerId(inputComponentString()).stream().filter(t -> t.getEnergyHandler().isPresent()).map(t -> (t.getEnergyHandler().get())).toArray(MachineEnergyHandler<?>[]::new);
    }

    protected String inputComponentString(){
        return "energy";
    }

    protected String outputComponentString(){
        return "dynamo";
    }

    private void cacheOutputs() {
        this.outputs = tile.getComponentsByHandlerId(outputComponentString()).stream().filter(t -> t.getEnergyHandler().isPresent()).map(t -> (t.getEnergyHandler().get())).toArray(MachineEnergyHandler<?>[]::new);
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
    public long extractEu(long voltage, boolean simulate) {
        if (inputs == null) return 0;
        long extracted = 0;
        long toExtract = Math.min(voltage, getEnergy());
        for (MachineEnergyHandler<?> handler : inputs) {
            long extract = handler.extractEu(toExtract, simulate);
            extracted+= extract;
            toExtract-= extract;
            if (toExtract <= 0) break;
        }
        if (toExtract > 0){
            extracted+= super.extractEu(voltage, simulate);
        }
        return extracted;
    }

    @Override
    public long insertInternal(long voltage, boolean simulate) {
        if (outputs == null) return 0;
        long inserted = 0;
        long toInsert = Math.min(voltage, getCapacity() - getEnergy());
        for (MachineEnergyHandler<?> handler : outputs) {
            long insert = handler.insertInternal(toInsert, simulate);
            inserted+= insert;
            toInsert-= insert;
            if (toInsert <= 0) break;
        }
        if (toInsert > 0){
            inserted+= super.insertInternal(voltage, simulate);
        }
        return inserted;
    }

    public Tier getAccumulatedPower() {
        if (inputs == null) return Tier.ULV;
        long voltage = Arrays.stream(inputs).mapToLong(t -> (long) t.getInputVoltage() * t.getInputAmperage()).sum();
        Tier tier = Tier.getTier(voltage);
        return voltage >= tier.getVoltage() ? tier : Tier.getTier((voltage >> 2));
    }
}
