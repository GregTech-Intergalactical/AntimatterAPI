package muramasa.antimatter.capability.machine;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.*;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.event.MachineEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.Capability;
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

    public MachineEnergyHandler(T tile, CompoundNBT tag, long energy, long capacity, int voltage_in, int voltage_out, int amperage_in, int amperage_out) {
        super(energy, capacity, voltage_in, voltage_out, amperage_in, amperage_out);
        this.tile = tile;
        if (tag != null) deserialize(tag);
        if (tile.isServerSide()) Tesseract.GT_ENERGY.registerNode(tile.getDimension(), tile.getPos().toLong(), this);
    }

    public MachineEnergyHandler(T tile, CompoundNBT tag) {
        this(tile, tag, 0L, tile.getMachineTier().getVoltage() * (tile.getMachineType().has(GENERATOR) ? 40L : 66L),
            tile.getMachineType().has(GENERATOR) ? 0 : tile.getMachineTier().getVoltage(),
            tile.getMachineType().has(GENERATOR) ? tile.getMachineTier().getVoltage() : 0,
            tile.getMachineType().has(GENERATOR) ? 0 : 1,
            tile.getMachineType().has(GENERATOR) ? 1 : 0);
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
    @Override
    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        tag.putLong(Ref.TAG_MACHINE_ENERGY, energy);
        tag.putLong(Ref.TAG_MACHINE_CAPACITY, capacity);
        tag.putInt(Ref.TAG_MACHINE_VOLTAGE_IN, voltage_in);
        tag.putInt(Ref.TAG_MACHINE_VOLTAGE_OUT, voltage_out);
        tag.putInt(Ref.TAG_MACHINE_AMPERAGE_IN, amperage_in);
        tag.putInt(Ref.TAG_MACHINE_AMPERAGE_OUT, amperage_out);
        return tag;
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        energy = tag.getLong(Ref.TAG_MACHINE_ENERGY);
        capacity = tag.getLong(Ref.TAG_MACHINE_CAPACITY);
        voltage_in = tag.getInt(Ref.TAG_MACHINE_VOLTAGE_IN);
        voltage_out = tag.getInt(Ref.TAG_MACHINE_VOLTAGE_OUT);
        amperage_in = tag.getInt(Ref.TAG_MACHINE_AMPERAGE_IN);
        amperage_out = tag.getInt(Ref.TAG_MACHINE_AMPERAGE_OUT);
    }

    @Override
    public void onMachineEvent(IMachineEvent event, Object... data) {
        if (event == ContentEvent.ENERGY_SLOT_CHANGED) tile.itemHandler.ifPresent(h -> cachedItems = h.getChargeableItems());
    }

    public void setEnergy(long energy) {
        this.energy = energy;
    }

    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }

    public void setOutputAmperage(int amperage_out) {
        this.amperage_out = amperage_out;
    }

    public void setInputAmperage(int amperage_in) {
        this.amperage_in = amperage_in;
    }

    public void setOutputVoltage(int voltage_out) {
        this.voltage_out = voltage_out;
    }

    public void setInputVoltage(int voltage_in) {
        this.voltage_in = voltage_in;
    }

    @Override
    public Capability<?> getCapability() {
        return AntimatterCaps.ENERGY_HANDLER_CAPABILITY;
    }
}