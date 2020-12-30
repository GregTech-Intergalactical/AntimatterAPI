package muramasa.antimatter.tile.single;

import muramasa.antimatter.capability.IEnergyHandler;
import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.cover.CoverDynamo;
import muramasa.antimatter.cover.CoverEnergy;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.TileEntityStorage;
import muramasa.antimatter.util.LazyHolder;
import net.minecraft.util.Direction;
import tesseract.util.Dir;

import java.util.function.Function;

public class TileEntityBatteryBuffer extends TileEntityStorage {

    public TileEntityBatteryBuffer(Machine<?> type) {
        super(type);
        this.energyHandler = LazyHolder.of(() -> new MachineEnergyHandler<TileEntityMachine>(this, 0L, 0L, getMachineTier().getVoltage(), getMachineTier().getVoltage(), 0, 0) {
            @Override
            public boolean canOutput(Dir direction) {
                Direction dir = tile.coverHandler.map(ch -> ch.lookupSingle(CoverDynamo.class)).orElse(null);
                return super.canOutput(direction) && (dir != null && dir.getIndex() == direction.getIndex());
            }

            @Override
            public boolean canInput(Dir direction) {
                Direction dir = tile.coverHandler.map(ch -> ch.lookupSingle(CoverDynamo.class)).orElse(null);
                return super.canInput(direction) && (dir != null && dir.getIndex() != direction.getIndex());
            }

            @Override
            public boolean connects(Dir direction) {
                return true;
            }

            @Override
            public long getCapacity() {
                return super.getCapacity() + (cachedItems != null ? cachedItems.stream().mapToLong(IEnergyHandler::getCapacity).sum() : 0);
            }

            @Override
            public long getEnergy() {
                return super.getEnergy() + (cachedItems != null ? cachedItems.stream().mapToLong(IEnergyHandler::getEnergy).sum() : 0);
            }
        });


    }
}