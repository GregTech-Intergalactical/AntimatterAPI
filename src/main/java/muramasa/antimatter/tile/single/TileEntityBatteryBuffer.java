package muramasa.antimatter.tile.single;

import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.cover.CoverDynamo;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.tile.TileEntityStorage;
import muramasa.antimatter.util.LazyHolder;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import tesseract.util.Dir;

public class TileEntityBatteryBuffer extends TileEntityStorage {

    public TileEntityBatteryBuffer(Machine<?> type) {
        super(type);
        this.energyHandler = LazyOptional.of(() -> new MachineEnergyHandler<TileEntityMachine>(this, 0L, 0L, getMachineTier().getVoltage(), getMachineTier().getVoltage(), 0, 0) {
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

        });


    }
}