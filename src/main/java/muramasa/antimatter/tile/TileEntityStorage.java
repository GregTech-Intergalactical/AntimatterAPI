package muramasa.antimatter.tile;

import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.types.Machine;
import net.minecraft.util.Direction;
import tesseract.api.capability.TesseractGTCapability;
import tesseract.api.gt.IEnergyHandler;

import java.util.List;

public abstract class TileEntityStorage<T extends TileEntityStorage<T>> extends TileEntityMachine<T> {

    public TileEntityStorage(Machine<?> type) {
        super(type);
        itemHandler.set(() -> new MachineItemHandler<T>((T) this) {
            @Override
            public void onMachineEvent(IMachineEvent event, Object... data) {
                if (event == ContentEvent.ENERGY_SLOT_CHANGED)
                    calculateAmperage();
            }
        });
        energyHandler.set(() -> new MachineEnergyHandler<T>((T) this, 0L, 0/*getMachineTier().getVoltage() * 64L*/, getMachineTier().getVoltage(), getMachineTier().getVoltage(), 1, 1) {
            @Override
            public boolean canOutput(Direction direction) {
                Direction dir = tile.getFacing();
                return dir != null && dir.get3DDataValue() == direction.get3DDataValue();
            }

            @Override
            public void onMachineEvent(IMachineEvent event, Object... data) {
                super.onMachineEvent(event, data);
                if (event == ContentEvent.ENERGY_SLOT_CHANGED) {
                    this.tile.refreshCap(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY);
                }
            }
        });
    }

    @Override
    public void onLoad() {
        super.onLoad();
        calculateAmperage();
    }

    // calculateAmperage checks batteries and calculates the total available input/output amperage.
    private void calculateAmperage() {
        itemHandler.ifPresent(h -> {
            energyHandler.ifPresent(e -> {
                // Check all items that match the given voltage, and allow either input/output.
                long out = h.getChargeableItems().stream().filter(item -> (item.getOutputVoltage() == 0 || item.getOutputVoltage() == e.getOutputVoltage())).mapToLong(IEnergyHandler::getOutputAmperage).sum();
                long in = h.getChargeableItems().stream().filter(item -> (item.getInputVoltage() == 0 || item.getInputVoltage() == e.getInputVoltage())).mapToLong(IEnergyHandler::getInputAmperage).sum();
                long oldOut = e.getOutputAmperage();
                long oldIn = e.getInputAmperage();
                // 2 amps per battery input.
                e.setInputAmperage(in);
                e.setOutputAmperage(out);
                if (oldOut != out || oldIn != in) {
                    this.refreshCap(TesseractGTCapability.ENERGY_HANDLER_CAPABILITY);
                }
            });
        });
    }

    @Override
    public void onServerUpdate() {
        //  if (checkAmps) {
        //      calculateAmperage();
        //      checkAmps = false;
        //  }
        super.onServerUpdate();
    }

    @Override
    public List<String> getInfo() {
        List<String> info = super.getInfo();
        energyHandler.ifPresent(h -> {
            info.add("Amperage In: " + h.getInputAmperage());
            info.add("Amperage Out: " + h.getOutputAmperage());
        });
        return info;
    }
}
