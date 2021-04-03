package muramasa.antimatter.tile;

import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.util.LazyHolder;
import net.minecraftforge.common.util.LazyOptional;
import tesseract.api.gt.IEnergyHandler;
import tesseract.util.Dir;

import java.util.List;

public abstract class TileEntityStorage extends TileEntityMachine {

    public TileEntityStorage(Machine<?> type) {
        super(type);
        this.itemHandler = LazyOptional.of(() -> new MachineItemHandler<TileEntityStorage>(this) {
            @Override
            public void onMachineEvent(IMachineEvent event, Object... data) {
                if (event == ContentEvent.ENERGY_SLOT_CHANGED)
                    calculateAmperage();
            }
        });
        this.energyHandler = LazyOptional.of(() -> new MachineEnergyHandler<TileEntityStorage>(this, 0L, getMachineTier().getVoltage() * 64L, getMachineTier().getVoltage(), getMachineTier().getVoltage(), 1, 1) {
            @Override
            public boolean canOutput(Dir direction) {
                return tile.getOutputFacing().getIndex() == direction.getIndex();
            }

            @Override
            public boolean connects(Dir direction) {
                return true;
            }

            @Override
            public void onMachineEvent(IMachineEvent event, Object... data) {
                super.onMachineEvent(event, data);
                if (event == ContentEvent.ENERGY_SLOT_CHANGED) {
                    refreshNet();
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
                int out = h.getChargeableItems().stream().filter(item -> (item.getOutputVoltage() == 0 || item.getOutputVoltage() == e.getOutputVoltage())).mapToInt(IEnergyHandler::getOutputAmperage).sum();
                int in = h.getChargeableItems().stream().filter(item -> (item.getInputVoltage() == 0 || item.getInputVoltage() == e.getInputVoltage())).mapToInt(IEnergyHandler::getInputAmperage).sum();
                int oldOut = e.getOutputAmperage();
                int oldIn = e.getInputAmperage();
                // 2 amps per battery input.
                e.setInputAmperage(in);
                e.setOutputAmperage(out);
                if (oldOut != out || oldIn != in) {
                    e.refreshNet();
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
