package muramasa.antimatter.tile.single;

import muramasa.antimatter.capability.machine.MachineCoverHandler;
import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.cover.CoverDynamo;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.util.Direction;

import static muramasa.antimatter.machine.MachineFlag.GENERATOR;

public class TileEntityGenerator<T extends TileEntityGenerator<T>> extends TileEntityMachine<T> {

    public TileEntityGenerator(Machine<?> type) {
        super(type);
        energyHandler.set(() -> new MachineEnergyHandler<T>((T)this, type.amps(),type.has(GENERATOR)){
            @Override
            public boolean canInput(Direction direction) {
                return false;
            }
            @Override
            public boolean canInput() {
                return false;
            }

            @Override
            public boolean canOutput(Direction direction) {
                Direction out = tile.coverHandler.map(MachineCoverHandler::getOutputFacing).orElse(null);
                if (out == null) return false;
                ICover o = tile.getMachineType().getOutputCover();
                return canOutput() && o instanceof CoverDynamo && direction == out;

            }
        });
    };
    @Override
    public Tier getPowerLevel() {
        return Tier.getMax();
    }
}
