package muramasa.antimatter.tile;

import muramasa.antimatter.capability.IEnergyHandler;
import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.machine.event.IMachineEvent;
import muramasa.antimatter.machine.types.Machine;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import java.util.Optional;

import static muramasa.antimatter.machine.MachineFlag.ENERGY;
import static muramasa.antimatter.machine.MachineFlag.ITEM;

public class TileEntityStorage extends TileEntityMachine {

    public TileEntityStorage(Machine<?> type) {
        super(type);
    }

    //If, during next tick, amperage amount should be rechecked.
    private boolean checkAmps = false;

    @Override
    public void onLoad() {
        if (!energyHandler.isPresent() /*&& isServerSide()*/ && has(ENERGY)) energyHandler = Optional.of(new MachineEnergyHandler(this, 0, getMachineTier().getVoltage() * 64L, getMachineTier().getVoltage(), getMachineTier().getVoltage(), 1, 1){
            @Override
            public boolean  canOutput(@Nonnull Dir direction) {
                return tile.getOutputFacing().getIndex() == direction.getIndex();
            }
        });
        if (!itemHandler.isPresent() && has(ITEM)) itemHandler = Optional.of(new MachineItemHandler(this) {
            @Override
            public void onMachineEvent(IMachineEvent event, Object... data) {
                //TODO: onItemEvent
                if (event == ContentEvent.ENERGY_SLOT_CHANGED) scheduleAmperageCheck();
                super.onMachineEvent(event, data);
            }
        });
        super.onLoad();
    }
    //Schedules a check for amperage next tick.
    //TODO: This is not good but otherwise items are not available for checking. Check into onContentsChanged for container
    //so we can calculate amperage during the event ENERGY_SLOT_CHANGED!
    private void scheduleAmperageCheck() {
        checkAmps = true;
    }

    //calculateAmperage checks batteries and calculates the total available input/output amperage.
    private void calculateAmperage() {
        itemHandler.ifPresent(handler -> {
            energyHandler.ifPresent(ehandler -> {
                //Check all items that match the given voltage, and allow either input/output.
                int out = handler.getChargeableItems().stream().filter(item -> (item.getOutputVoltage() == 0 || item.getOutputVoltage() == ehandler.getOutputVoltage())).mapToInt(IEnergyHandler::getOutputAmperage).sum();
                int in = handler.getChargeableItems().stream().filter(item -> (item.getInputVoltage() == 0 || item.getInputVoltage() == ehandler.getInputVoltage())).mapToInt(IEnergyHandler::getInputAmperage).sum();
                //2 amps per battery input.
                ehandler.setInputAmperage(2*in);
                ehandler.setOutputAmperage(out);
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
}
