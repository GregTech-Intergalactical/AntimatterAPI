package muramasa.antimatter.tile.single;

import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.tile.TileEntityStorage;

import tesseract.util.Dir;

import java.util.Optional;

import static muramasa.antimatter.machine.MachineFlag.ENERGY;

public class TileBatteryBufferCreative extends TileEntityStorage {

    public TileBatteryBufferCreative(Machine<?> type) {
        super(type);
    }

    @Override
    public void onLoad() {
        //Anonymous inherited classes are annoying since you have to rewrite code. probably move the energy handlers to an actual class.
        if (/*isServerSide() &&*/ has(ENERGY)) energyHandler = Optional.of(new MachineEnergyHandler(this, getMachineTier().getVoltage() * 64L, getMachineTier().getVoltage() * 64L, 0, getMachineTier().getVoltage(), 0, 1) {
            @Override
            public long extract(long maxExtract, boolean simulate) {
                return maxExtract;
            }

            @Override
            public boolean canOutput(Dir direction) {
                return tile.getFacing().getIndex() == direction.getIndex();
            }

            @Override
            public boolean connects(Dir direction) {
                return true;
            }
        });
        super.onLoad();
    }
}