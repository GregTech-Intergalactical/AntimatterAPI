package muramasa.antimatter.structure;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public interface IBlockStatePredicate {

    boolean evaluate(IWorldReader reader, BlockPos pos, BlockState state);
}
