package muramasa.antimatter.tile.single;

import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.cover.CoverDynamo;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.tile.TileEntityStorage;
import net.minecraft.util.Direction;


public class TileEntityBatteryBuffer<T extends TileEntityBatteryBuffer<T>> extends TileEntityStorage<T> {

    public TileEntityBatteryBuffer(Machine<?> type) {
        super(type);
        energyHandler.set(() -> new MachineEnergyHandler<T>((T)this, 0L, 0L, getMachineTier().getVoltage(), getMachineTier().getVoltage(), 0, 0) {
            @Override
            public boolean canOutput(Direction direction) {
                Direction dir = tile.coverHandler.map(ch -> ch.lookupSingle(CoverDynamo.class)).orElse(null);
                return super.canOutput(direction) && (dir != null && dir.getIndex() == direction.getIndex());
            }

            @Override
            public boolean canInput(Direction direction) {
                Direction dir = tile.coverHandler.map(ch -> ch.lookupSingle(CoverDynamo.class)).orElse(null);
                return super.canInput(direction) && (dir != null && dir != direction);
            }
        });


    }
}