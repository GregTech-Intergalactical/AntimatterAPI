package muramasa.antimatter.tile;

import muramasa.antimatter.capability.IEnergyHandler;
import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.types.Machine;
import tesseract.util.Dir;

import java.util.List;
import java.util.Optional;

import static muramasa.antimatter.machine.MachineFlag.ENERGY;
import static muramasa.antimatter.machine.MachineFlag.ITEM;

public class TileEntityStorage extends TileEntityMachine {

    public TileEntityStorage(Machine<?> type) {
        super(type);
        itemHandler.init((tile) -> new MachineItemHandler<TileEntityMachine>(tile) {
            @Override
            public void onMachineEvent(IMachineEvent event, Object... data) {
                if (event == ContentEvent.ENERGY_SLOT_CHANGED) scheduleAmperageCheck();
            }
        });
        energyHandler.init((tile) -> new MachineEnergyHandler<TileEntityMachine>(tile, 0, tile.getMachineTier().getVoltage() * 64L, tile.getMachineTier().getVoltage(), tile.getMachineTier().getVoltage(), 1, 1) {
            @Override
            public boolean  canOutput(Dir direction) {
                return tile.getOutputFacing().getIndex() == direction.getIndex();
            }

            @Override
            public boolean connects(Dir direction) {
                return true;
            }
        });
    }

    // If, during next tick, amperage amount should be rechecked.
    private boolean checkAmps = false;

    @Override
    public void onFirstTick() {
        super.onFirstTick();
        calculateAmperage();
    }

    // Schedules a check for amperage next tick.
    //TODO: This is not good but otherwise items are not available for checking. Check into onContentsChanged for container
    // So we can calculate amperage during the event ENERGY_SLOT_CHANGED!
    private void scheduleAmperageCheck() {
        checkAmps = true;
    }

    // calculateAmperage checks batteries and calculates the total available input/output amperage.
    private void calculateAmperage() {
        itemHandler.ifPresent(h -> {
            energyHandler.ifPresent(e -> {
                // Check all items that match the given voltage, and allow either input/output.
                int out = h.getChargeableItems().stream().filter(item -> (item.getOutputVoltage() == 0 || item.getOutputVoltage() == e.getOutputVoltage())).mapToInt(IEnergyHandler::getOutputAmperage).sum();
                int in = h.getChargeableItems().stream().filter(item -> (item.getInputVoltage() == 0 || item.getInputVoltage() == e.getInputVoltage())).mapToInt(IEnergyHandler::getInputAmperage).sum();

                // 2 amps per battery input.
                e.setInputAmperage(2 * in);
                e.setOutputAmperage(out);
            });
        });
    }

    @Override
    public void onServerUpdate() {
        if (checkAmps) {
            calculateAmperage();
            checkAmps = false;
        }
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
