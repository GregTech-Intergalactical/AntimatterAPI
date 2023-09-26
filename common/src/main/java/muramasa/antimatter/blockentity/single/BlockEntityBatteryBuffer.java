package muramasa.antimatter.blockentity.single;

import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.blockentity.BlockEntityStorage;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;


public class BlockEntityBatteryBuffer<T extends BlockEntityBatteryBuffer<T>> extends BlockEntityStorage<T> {

    public BlockEntityBatteryBuffer(Machine<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }
}