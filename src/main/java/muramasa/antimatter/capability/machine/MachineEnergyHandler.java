package muramasa.antimatter.capability.machine;

import muramasa.antimatter.capability.IEnergyHandler;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.capability.EnergyHandler;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.nbt.CompoundNBT;
import tesseract.Tesseract;
import tesseract.api.ITickingController;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class MachineEnergyHandler extends EnergyHandler implements IMachineHandler {

    protected TileEntityMachine tile;
    protected ITickingController controller;
    //Cached chargeable items from the energy handler. Updated on machine event as to not always extract caps.
    protected List<IEnergyHandler> cachedItems;

    public MachineEnergyHandler(TileEntityMachine tile, long energy, long capacity, int voltage_in, int voltage_out, int amperage_in, int amperage_out) {
        super(energy, capacity, voltage_in, voltage_out, amperage_in, amperage_out);
        this.tile = tile;
        if (this.tile.isServerSide()) {
            Tesseract.ELECTRIC.registerNode(tile.getDimension(), tile.getPos().toLong(), this);
        }
        tile.itemHandler.ifPresent(handler -> cachedItems = handler.getChargeableItems());
        if (cachedItems == null) {
            cachedItems = new ArrayList<>();
        }
    }

    @Override
    public long insert(long maxReceive, boolean simulate) {
        long inserted = super.insert(maxReceive, simulate);

        if (inserted == 0 && canChargeItem()) {
            inserted = insertIntoItems(maxReceive,simulate);
        }
        if (!simulate) {
            this.tile.onMachineEvent(MachineEvent.ENERGY_INPUTTED, inserted);
        }
        return inserted;
    }

    protected long insertIntoItems(long maxReceive, boolean simulate) {
        for (IEnergyHandler ihandler : cachedItems) {
            long itemInsert = ihandler.insert(maxReceive, true);
            if (itemInsert == maxReceive) {
                if (!simulate) ihandler.insert(maxReceive, false);
                return itemInsert;
            }
        }
        return 0;
    }

    public MachineEnergyHandler(TileEntityMachine tile) {
        this(tile, 0, tile.getMachineTier().getVoltage() * 64L, tile.getMachineTier().getVoltage(), 0, 1, 0);
    }

    public void onRemove() {
        if (tile.isServerSide()) Tesseract.ELECTRIC.remove(tile.getDimension(), tile.getPos().toLong());
    }
    //Transfers energy from internal buffer between items.
    public void onUpdate() {
        if (controller != null && tile.isServerSide()) controller.tick();
    }

    public void setOutputAmperage(int amp) {
        amperage_out = amp;
    }

    public void setInputAmperage(int amp) {
        amperage_in = amp;
    }

    /**
     *
     * @return whether or not this handler can charge items in charge slots.
     */
    public boolean canChargeItem() {
        return true;
    }

    /**
     *
     * @return whether or not this handler change charge from items in charge slots.
     */
    public boolean canChargeFromItem() {
        return false;
    }

    @Override
    public long extract(long maxExtract, boolean simulate) {
        //Uncomment for debug energy
        //return maxExtract;

        long extracted = super.extract(maxExtract, simulate);

        if (extracted == 0) {
            extracted = extractFromItems(maxExtract,simulate);
        }
        if (!simulate) {
            this.tile.onMachineEvent(MachineEvent.ENERGY_DRAINED, extracted);
        }
        return extracted;
    }

    protected long extractFromItems(long maxExtract, boolean simulate) {
        for (IEnergyHandler handler : cachedItems) {
            long iExtract = handler.extract(maxExtract, true);
            if (iExtract == maxExtract) {
                if (!simulate) {
                    return handler.extract(maxExtract,false);
                } else {
                    return iExtract;
                }
            }
        }
        return 0;
    }

    @Override
    public boolean connects(@Nonnull Dir direction) {
        return tile.getFacing().getIndex() != direction.getIndex()/* && tile.getCover(Ref.DIRECTIONS[direction.getIndex()]).isEqual(Data.COVER_EMPTY)*/;
    }

    @Override
    public void reset(ITickingController oldController, ITickingController newController) {
        if (oldController == null || (controller == oldController && newController == null) || controller != oldController)
            controller = newController;
    }

    @Override
    public boolean canOutput(@Nonnull Dir direction) {
        return false;
    }

    /** NBT **/
    // TODO: Finish
    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        tag.putLong("Energy", energy);
        return tag;
    }

    public void deserialize(CompoundNBT tag) {
        energy = tag.getLong("Energy");
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        if (event == ContentEvent.ENERGY_SLOT_CHANGED) tile.itemHandler.ifPresent(handler -> cachedItems = handler.getChargeableItems());
    }
}