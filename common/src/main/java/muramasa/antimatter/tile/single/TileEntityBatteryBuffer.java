package muramasa.antimatter.tile.single;

import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.tile.TileEntityStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;


public class TileEntityBatteryBuffer<T extends TileEntityBatteryBuffer<T>> extends TileEntityStorage<T> {

    public TileEntityBatteryBuffer(Machine<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public CoverFactory[] getValidCovers() {
        return new CoverFactory[0];
    }
}