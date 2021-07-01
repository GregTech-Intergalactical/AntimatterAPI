package muramasa.antimatter.tile.single;

import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.tile.TileEntityStorage;


public class TileEntityBatteryBuffer<T extends TileEntityBatteryBuffer<T>> extends TileEntityStorage<T> {

    public TileEntityBatteryBuffer(Machine<?> type) {
        super(type);
        energyHandler.set(() -> new MachineEnergyHandler<T>((T)this, 0L, 0L, getMachineTier().getVoltage(), getMachineTier().getVoltage(), 0, 0));
    }

    @Override
    public ICover[] getValidCovers() {
        return new ICover[0];
    }

    @Override
    public boolean allowsFrontIO() {
        return true;
    }
}