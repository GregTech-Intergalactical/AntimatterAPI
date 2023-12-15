package muramasa.antimatter.blockentity.single;

import muramasa.antimatter.capability.machine.MachineEnergyHandler;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import static muramasa.antimatter.machine.MachineFlag.GENERATOR;

public class BlockEntityGenerator<T extends BlockEntityGenerator<T>> extends BlockEntityMachine<T> {

    public BlockEntityGenerator(Machine<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        energyHandler.set(() -> new MachineEnergyHandler<T>((T) this, type.amps(), type.has(GENERATOR)) {

            @Override
            public boolean canOutput(Direction direction) {
                return super.canOutput(direction) && direction == tile.getFacing();

            }
        });
    }

    @Override
    public Tier getPowerLevel() {
        return Tier.getMax();
    }

    @Override
    public boolean toggleMachine() {
        return false;
    }
}
