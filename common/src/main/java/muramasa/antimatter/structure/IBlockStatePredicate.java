package muramasa.antimatter.structure;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface IBlockStatePredicate {

    boolean evaluate(LevelReader reader, BlockPos pos, BlockState state);
}
