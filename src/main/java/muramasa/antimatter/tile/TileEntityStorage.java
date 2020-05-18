package muramasa.antimatter.tile;

import muramasa.antimatter.capability.impl.MachineEnergyHandler;
import muramasa.antimatter.machine.types.Machine;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import java.util.Optional;

import static muramasa.antimatter.machine.MachineFlag.ENERGY;

public class TileEntityStorage extends TileEntityMachine {

    public TileEntityStorage(Machine<?> type) {
        super(type);
    }

    @Override
    public void onLoad() {
        if (!energyHandler.isPresent() && isServerSide() && has(ENERGY)) energyHandler = Optional.of(new MachineEnergyHandler(this) {
            @Override
            public boolean canOutput(@Nonnull Dir direction) {
                return tile.getOutputFacing().getIndex() == direction.getIndex();
            }
        });
        super.onLoad();
    }

    @Override
    public void onServerUpdate() {
        energyHandler.ifPresent(MachineEnergyHandler::onUpdate);
        super.onServerUpdate();
    }
}
