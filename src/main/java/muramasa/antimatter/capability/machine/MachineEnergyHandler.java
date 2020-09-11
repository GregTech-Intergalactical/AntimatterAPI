package muramasa.antimatter.capability.machine;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.capability.*;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraftforge.common.capabilities.Capability;
import tesseract.Tesseract;
import tesseract.api.ITickHost;
import tesseract.api.ITickingController;
import tesseract.util.Dir;

import java.util.List;

import static muramasa.antimatter.machine.MachineFlag.GENERATOR;

// Server Only
public class MachineEnergyHandler<T extends TileEntityMachine> extends EnergyHandler implements IMachineHandler, ITickHost {

    protected final T tile;

    protected ITickingController controller;

    // Cached chargeable items from the energy handler. Updated on machine event as to not always extract caps.
    protected List<IEnergyHandler> cachedItems = new ObjectArrayList<>();

    public MachineEnergyHandler(T tile, long energy, long capacity, int voltageIn, int voltageOut, int amperageIn, int amperageOut) {
        super(energy, capacity, voltageIn, voltageOut, amperageIn, amperageOut);
        this.tile = tile;
        // if (tag != null) deserialize(tag);
        Tesseract.GT_ENERGY.registerNode(tile.getDimension(), tile.getPos().toLong(), this);
    }

    public MachineEnergyHandler(T tile) {
        this(tile, 0L, tile.getMachineTier().getVoltage() * (tile.getMachineType().has(GENERATOR) ? 40L : 66L),
            tile.getMachineType().has(GENERATOR) ? 0 : tile.getMachineTier().getVoltage(),
            tile.getMachineType().has(GENERATOR) ? tile.getMachineTier().getVoltage() : 0,
            tile.getMachineType().has(GENERATOR) ? 0 : 1,
            tile.getMachineType().has(GENERATOR) ? 1 : 0);
    }

    public void onUpdate() {
        if (controller != null) {
            controller.tick();
        }
    }

    public void onRemove() {
        Tesseract.GT_ENERGY.remove(tile.getDimension(), tile.getPos().toLong());
    }

    public void onReset() {
        Tesseract.GT_ENERGY.remove(tile.getDimension(), tile.getPos().toLong());
        Tesseract.GT_ENERGY.registerNode(tile.getDimension(), tile.getPos().toLong(), this);
    }

    @Override
    public long insert(long maxReceive, boolean simulate) {
        long inserted = super.insert(maxReceive, simulate);
        if (inserted == 0 && canChargeItem()) {
            inserted = insertIntoItems(maxReceive, simulate);
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

    public boolean canChargeItem() {
        return true;
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

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        if (event == ContentEvent.ENERGY_SLOT_CHANGED) {
            tile.itemHandler.ifPresent(h -> cachedItems = h.getChargeableItems());
        }
    }

    public void setEnergy(long energy) {
        this.energy = energy;
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

}