package muramasa.antimatter.tile;

import muramasa.antimatter.machine.types.Machine;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityRFMachine<T extends TileEntityRFMachine<T>>  extends TileEntityMachine<T>{
    public TileEntityRFMachine(Machine<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}
