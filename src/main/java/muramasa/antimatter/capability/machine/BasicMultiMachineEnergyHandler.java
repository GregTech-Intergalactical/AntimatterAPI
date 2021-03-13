package muramasa.antimatter.capability.machine;

import muramasa.antimatter.capability.IEnergyHandler;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.multi.TileEntityBasicMultiMachine;
import muramasa.antimatter.tile.multi.TileEntityMultiMachine;
import tesseract.api.gt.IGTNode;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class BasicMultiMachineEnergyHandler extends MachineEnergyHandler<TileEntityBasicMultiMachine> {

    protected IEnergyHandler[] inputs;//= new IEnergyHandler[0];
    protected IEnergyHandler[] outputs; //= new IEnergyHandler[0];

    protected long cachedCapacity;

    public BasicMultiMachineEnergyHandler(TileEntityMachine tile, int amps, boolean generator) {
        super((TileEntityBasicMultiMachine)tile, amps, generator);
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
        List<IEnergyHandler> list = tile.getComponents("hatch_energy").stream().filter(t -> t.getEnergyHandler().isPresent()).map(t -> t.getEnergyHandler().get()).collect(Collectors.toList());
        list.add(this);
        this.inputs = list.toArray(new IEnergyHandler[0]);
    }

    private void cacheOutputs() {
        List<IEnergyHandler> list = tile.getComponents("hatch_dynamo").stream().filter(t -> t.getEnergyHandler().isPresent()).map(t -> t.getEnergyHandler().get()).collect(Collectors.toList());
        list.add(this);
        this.outputs = list.toArray(new IEnergyHandler[0]);
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

    @Override
    public long insert(long maxReceive, boolean simulate) {
        if (outputs == null) cacheOutputs();
        long inserted = super.insert(maxReceive, simulate);
        if (inserted == 0 && outputs != null) {
            for (IEnergyHandler handler : outputs) {
                inserted += handler.insert(maxReceive-inserted, simulate);
                //Output voltage as a dynamo outputs energy.
                if (!simulate && inserted > handler.getOutputVoltage()) {
                    System.out.println("BOOM");
                }
                if (inserted >= maxReceive)
                    return inserted;
            }
        }
        return inserted;
    }

    @Override
    public long getEnergy() {
        return super.getEnergy() +(inputs == null ? 0 : Arrays.stream(inputs).mapToLong(IGTNode::getEnergy).sum() + (outputs == null ? 0 : Arrays.stream(outputs).mapToLong(IGTNode::getEnergy).sum()));
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
    public long extract(long maxReceive, boolean simulate) {
        if (inputs == null) cacheInputs();
        long extracted = super.extract(maxReceive, simulate);
        if (extracted == 0 && inputs != null) {
            for (IEnergyHandler handler : inputs) {
                extracted += handler.extract(maxReceive-extracted, simulate);
                if (extracted >= maxReceive)
                    return extracted;
            }
        }
        return extracted;
    }

    public Tier getAccumulatedPower() {
        if (inputs == null) return Tier.ULV;
        return Tier.getTier((int)(Arrays.stream(inputs).mapToLong(t -> (long) t.getInputVoltage() *t.getInputAmperage()).sum()));
    }
}
