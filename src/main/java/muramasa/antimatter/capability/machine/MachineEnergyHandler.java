package muramasa.antimatter.capability.machine;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.capability.EnergyHandler;
import muramasa.antimatter.capability.ICapabilityHandler;
import muramasa.antimatter.capability.IEnergyHandler;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.machine.MachineFlag;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.nbt.CompoundNBT;
import tesseract.Tesseract;
import tesseract.api.ITickHost;
import tesseract.api.ITickingController;
import tesseract.util.Dir;

import java.util.List;

import static muramasa.antimatter.machine.MachineFlag.GENERATOR;

public class MachineEnergyHandler<T extends TileEntityMachine> extends EnergyHandler implements IMachineHandler, ICapabilityHandler, ITickHost {

    protected T tile;
    protected ITickingController controller;
    // Cached chargeable items from the energy handler. Updated on machine event as to not always extract caps.
    protected List<IEnergyHandler> cachedItems = new ObjectArrayList<>();

    public MachineEnergyHandler(T tile, long energy, long capacity, int voltage_in, int voltage_out, int amperage_in, int amperage_out) {
        super(energy, capacity, voltage_in, voltage_out, amperage_in, amperage_out);
        this.tile = tile;
    }

    public MachineEnergyHandler(T tile) {
        this(tile, 0, 0, 0, 0, 0, 0);
        if (tile.getMachineType().has(GENERATOR)) {
            capacity = tile.getMachineTier().getVoltage() * 40L;
            voltage_out = tile.getMachineTier().getVoltage();
            amperage_out = 1;
        } else {
            capacity = tile.getMachineTier().getVoltage() * 66L;
            voltage_in = tile.getMachineTier().getVoltage();
            amperage_out = 0;
        }
    }

    public void onInit() {
        if (tile.isServerSide()) Tesseract.GT_ENERGY.registerNode(tile.getDimension(), tile.getPos().toLong(), this);
    }

    public void onUpdate() {
        if (controller != null) controller.tick();
    }

    public void onRemove() {
        if (tile.isServerSide()) Tesseract.GT_ENERGY.remove(tile.getDimension(), tile.getPos().toLong());
    }

    public void onReset() {
        if (tile.isServerSide()) {
            Tesseract.GT_ENERGY.remove(tile.getDimension(), tile.getPos().toLong());
            Tesseract.GT_ENERGY.registerNode(tile.getDimension(), tile.getPos().toLong(), this);
        }
    }

    @Override
    public long insert(long maxReceive, boolean simulate) {
        long inserted = super.insert(maxReceive, simulate);
        if (inserted == 0 && canChargeItem()) {
            inserted = insertIntoItems(maxReceive,simulate);
        }
        if (!simulate) {
            tile.onMachineEvent(MachineEvent.ENERGY_INPUTTED, inserted);
        }
        return inserted;
    }

    protected long insertIntoItems(long maxReceive, boolean simulate) {
        for (IEnergyHandler handler : cachedItems) {
            long inserted = handler.insert(maxReceive, true);
            if (inserted > 0) {
                if (!simulate) handler.insert(maxReceive, false);
                return inserted;
            }
        }
        return 0;
    }

    public void setOutputAmperage(int amp) {
        amperage_out = amp;
    }

    public void setInputAmperage(int amp) {
        amperage_in = amp;
    }

    public void setOutputVoltage(int voltage) {
        voltage_out = voltage;
    }

    public void setInputVoltage(int voltage) {
        voltage_in = voltage;
    }

    public boolean canChargeItem() {
        return true;
    }

    public boolean canChargeFromItem() {
        return false;
    }

    @Override
    public long extract(long maxExtract, boolean simulate) {
        long extracted = super.extract(maxExtract, simulate);
        if (extracted == 0) {
            extracted = extractFromItems(maxExtract,simulate);
        }
        if (!simulate) {
            tile.onMachineEvent(MachineEvent.ENERGY_DRAINED, extracted);
        }
        return extracted;
    }

    protected long extractFromItems(long maxExtract, boolean simulate) {
        for (IEnergyHandler handler : cachedItems) {
            long extracted = handler.extract(maxExtract, true);
            if (extracted > 0) {
                if (!simulate) {
                    return handler.extract(maxExtract, false);
                } else {
                    return extracted;
                }
            }
        }
        return 0;
    }

    @Override
    public boolean connects(Dir direction) {
        // TODO: Finish connections when covers will be ready
        return tile.getFacing().getIndex() != direction.getIndex()/* && tile.getCover(Ref.DIRECTIONS[direction.getIndex()]).isEqual(Data.COVER_EMPTY)*/;
    }

    @Override
    public void reset(ITickingController oldController, ITickingController newController) {
        if (oldController == null || (controller == oldController && newController == null) || controller != oldController) {
            controller = newController;
        }
    }

    /** NBT **/
    // TODO: Finish
    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        tag.putLong("Energy", energy);
        tag.putLong("Voltage-In", voltage_in);
        tag.putLong("Voltage-Out", voltage_out);
        tag.putLong("Amperage-In", amperage_in);
        tag.putLong("Amperage-Out", amperage_out);
        return tag;
    }

    public void deserialize(CompoundNBT tag) {
        energy = tag.getLong("Energy");
        voltage_in = tag.getInt("Voltage-In");
        voltage_out = tag.getInt("Voltage-Out");
        amperage_in = tag.getInt("Amperage-In");
        amperage_out = tag.getInt("Amperage-Out");
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        if (event == ContentEvent.ENERGY_SLOT_CHANGED) tile.itemHandler.ifPresent(h -> cachedItems = h.getChargeableItems());
    }
}