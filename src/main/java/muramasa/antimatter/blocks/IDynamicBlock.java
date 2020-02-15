package muramasa.antimatter.blocks;

import muramasa.antimatter.Ref;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public interface IDynamicBlock extends IInfoProvider {

    int[] DEFAULT_CONFIG = new int[]{-1};

    /** Connection Logic **/
    default int[] getConfig(BlockState state, IBlockReader world, BlockPos.Mutable mut, BlockPos pos) {
        int[] ct = new int[1];
        BlockState adjState;
        for (int s = 0; s < 6; s++) {
            adjState = world.getBlockState(mut.setPos(pos.offset(Ref.DIRECTIONS[s])));
            if (canConnect(world, adjState, mut)) ct[0] += 1 << s;
        }
        return ct[0] == 0 ? DEFAULT_CONFIG : ct;
    }

    default boolean canConnect(IBlockReader world, BlockState state, BlockPos pos) {
        return state.getBlock() == this;
    }

    @Override
    default List<String> getInfo(List<String> info, World world, BlockState state, BlockPos pos) {
        info.add("Dynamic Config: " + Arrays.toString(getConfig(state, world, new BlockPos.Mutable(pos), pos)));
        return info;
    }
}
