package muramasa.antimatter.tile;

import muramasa.antimatter.capability.IEnergyHandler;
import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
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

    @Override
    public void onLoad() {
        if (!energyHandler.isPresent() /*&& isServerSide()*/ && has(ENERGY)) energyHandler = Optional.of(new MachineEnergyHandler(this, getMachineTier().getVoltage() * 64L, getMachineTier().getVoltage() * 64L, getMachineTier().getVoltage(), getMachineTier().getVoltage(), 1, 1){
            @Override
            public boolean canOutput(@Nonnull Dir direction) {
                return tile.getOutputFacing().getIndex() == direction.getIndex();
            }
        });
        if (!itemHandler.isPresent() && isServerSide() && has(ITEM)) itemHandler = Optional.of(new MachineItemHandler(this) {
            @Override
            public void onMachineEvent(IMachineEvent event, Object... data) {
                //TODO: onItemEvent
                calculateAmperage();
                super.onMachineEvent(event, data);
            }
        });
        super.onLoad();
    }
    //calculateAmperage checks batteries and calculates the total available input/output amperage.
    private void calculateAmperage() {
        itemHandler.ifPresent(handler -> {
            energyHandler.ifPresent(ehandler -> {
                int out = handler.getChargeableItems().stream().mapToInt(IEnergyHandler::getOutputAmperage).sum();
                int in = handler.getChargeableItems().stream().mapToInt(IEnergyHandler::getInputAmperage).sum();
                ehandler.setInputAmperage(in);
                ehandler.setOutputAmperage(out);
            });
        });
    }

    @Override
    public void onServerUpdate() {
        super.onServerUpdate();
    }
}
