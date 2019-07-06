package muramasa.gtu.api.structure;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBlockStatePredicate {

    boolean evaluate(World world, BlockPos pos, IBlockState state);
}
