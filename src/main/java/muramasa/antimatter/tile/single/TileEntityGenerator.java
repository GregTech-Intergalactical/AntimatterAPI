package muramasa.antimatter.tile.single;

import muramasa.antimatter.capability.machine.MachineCoverHandler;
import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.cover.CoverDynamo;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;

import static muramasa.antimatter.machine.MachineFlag.GENERATOR;

public class TileEntityGenerator extends TileEntityMachine {

    public TileEntityGenerator(Machine<?> type) {
        super(type);

        this.energyHandler = LazyOptional.of(() -> new MachineEnergyHandler<TileEntityGenerator>(this, type.amps(),type.has(GENERATOR)){
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
