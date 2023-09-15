package muramasa.antimatter.integration.fabric.megane.provider;

import lol.bai.megane.api.provider.EnergyProvider;
import muramasa.antimatter.tile.TileEntityMachine;
import tesseract.api.gt.IEnergyHandler;

public class MachineEnergyProvider extends EnergyProvider<TileEntityMachine> {
    @Override
    public long getStored() {
        return (long) getObject().energyHandler.side(null).map(i -> ((IEnergyHandler)i).getEnergy()).orElse(0L);
    }

    @Override
    public long getMax() {
        return (long) getObject().energyHandler.side(null).map(i -> ((IEnergyHandler)i).getCapacity()).orElse(0L);
    }
}
