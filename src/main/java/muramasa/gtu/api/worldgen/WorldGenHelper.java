package muramasa.gtu.api.worldgen;

import com.google.common.base.Predicate;
import net.minecraft.block.BlockStone;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenHelper {

    public static Predicate<IBlockState> STONE_PREDICATE = state -> {
        if (state == null) return false;
        if (state.getBlock() == Blocks.STONE) {
            BlockStone.EnumType stoneType = state.getValue(BlockStone.VARIANT);
            return stoneType.isNatural();
        } else if (state.getBlock() == Blocks.END_STONE) {
            return true;
        } else {
            return false;
        }
    };

    public static void setState(World world, BlockPos pos, IBlockState state) {
        world.setBlockState(pos, state, 2 | 16);
    }

    public static boolean setStateOre(World world, int x, int y, int z, IBlockState ore) {
        BlockPos pos = new BlockPos(x, y, z);
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock().isReplaceableOreGen(state, world, pos, STONE_PREDICATE)) {
            //TODO small
            world.setBlockState(pos, ore, 2 | 16);
            return true;
        }
        return false;
    }
}
