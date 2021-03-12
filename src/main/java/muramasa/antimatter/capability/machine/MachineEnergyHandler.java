package muramasa.antimatter.capability.machine;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.capability.EnergyHandler;
import muramasa.antimatter.capability.IEnergyHandler;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import tesseract.Tesseract;
import tesseract.util.Dir;

import java.util.List;

public class MachineEnergyHandler<T extends TileEntityMachine> extends EnergyHandler implements IMachineHandler{

    protected final T tile;

    protected List<IEnergyHandler> cachedItems = new ObjectArrayList<>();

    public MachineEnergyHandler(T tile, long energy, long capacity, int voltageIn, int voltageOut, int amperageIn, int amperageOut) {
        super(energy, capacity, voltageIn, voltageOut, amperageIn, amperageOut);
        this.tile = tile;
    }

    public MachineEnergyHandler(T tile, boolean isGenerator) {
        this(tile,1,isGenerator);
    }

    public MachineEnergyHandler(T tile, int amps, boolean isGenerator) {
        this(tile, 0L, tile.getMachineTier().getVoltage() * (isGenerator ? 40L : 66L), isGenerator ? 0 : tile.getMachineTier().getVoltage(), isGenerator ? tile.getMachineTier().getVoltage() : 0, isGenerator ? 0 : amps, isGenerator ? amps : 0);
    }

    @Override
    public void init() {
        cachedItems = tile.itemHandler.map(MachineItemHandler::getChargeableItems).orElse(cachedItems);
        registerNet();
    }

    public void onUpdate() {

    }

    public void onRemove() {
        if (tile.isServerSide()) {
            deregisterNet();
        }
    }

    @Override
    public boolean canInput(Dir direction) {
        return super.canInput(direction) && tile.getFacing().getIndex() != direction.getIndex();
    }

    @Override
    public long insert(long maxReceive, boolean simulate) {
        long inserted = super.insert(maxReceive, simulate);
        if (inserted == 0 && canChargeItem()) {
            inserted = insertIntoItems(maxReceive, simulate);
        }
        if (!simulate) {
            tile.onMachineEvent(MachineEvent.ENERGY_INPUTTED, inserted);
           // System.out.println("Insert " + maxReceive);
            // tile.markDirty();
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
            // tile.markDirty();
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
    public void onMachineEvent(IMachineEvent event, Object... data) {
        if (event == ContentEvent.ENERGY_SLOT_CHANGED) {
            tile.itemHandler.ifPresent(h -> cachedItems = h.getChargeableItems());
            refreshNet();
        }
    }

    @Override
    public void registerNet() {
        if (tile.getWorld() == null) return;
        Tesseract.GT_ENERGY.registerNode(tile.getDimension(), tile.getPos().toLong(), this);
    }

    @Override
    public void deregisterNet() {
        if (tile.getWorld() == null) return;
        Tesseract.GT_ENERGY.remove(tile.getDimension(), tile.getPos().toLong());
    }
}