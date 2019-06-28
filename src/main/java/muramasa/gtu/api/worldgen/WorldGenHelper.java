package muramasa.gtu.api.worldgen;

import com.google.common.base.Predicate;
import muramasa.gtu.api.blocks.BlockStone;
import muramasa.gtu.api.data.StoneType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static muramasa.gtu.api.properties.GTProperties.ORE_STONE;

public class WorldGenHelper {

    public static Predicate<IBlockState> ORE_PREDICATE = state -> {
        if (state == null) return false;
        if (state.getBlock() == Blocks.STONE) {
            net.minecraft.block.BlockStone.EnumType stoneType = state.getValue(net.minecraft.block.BlockStone.VARIANT);
            return stoneType.isNatural();
        } else if (state.getBlock() == Blocks.NETHERRACK || state.getBlock() == Blocks.END_STONE || state.getBlock() instanceof BlockStone) {
            return true;
        } else {
            return false;
        }
    };

    public static Predicate<IBlockState> STONE_PREDICATE = state -> {
        if (state == null) return false;
        if (state.getBlock() == Blocks.STONE) {
            net.minecraft.block.BlockStone.EnumType stoneType = state.getValue(net.minecraft.block.BlockStone.VARIANT);
            return stoneType.isNatural();
        }
        return false;
    };

    public static void setState(World world, BlockPos pos, IBlockState state) {
        world.setBlockState(pos, state, 2 | 16);
    }

    public static boolean setStateOre(World world, int x, int y, int z, IBlockState ore) {
        BlockPos pos = new BlockPos(x, y, z);
        IBlockState state = world.getBlockState(pos);

        if (state.getBlock() instanceof BlockStone) {
            world.setBlockState(pos, ore.withProperty(ORE_STONE, ((BlockStone) state.getBlock()).getType().getInternalId()), 2 | 16);
            return true;
        } else if (state.getBlock().isReplaceableOreGen(state, world, pos, ORE_PREDICATE)) {
            if (state.getBlock() == Blocks.NETHERRACK) {
                world.setBlockState(pos, ore.withProperty(ORE_STONE, StoneType.NETHERRACK.getInternalId()), 2 | 16);
            } else if (state.getBlock() == Blocks.END_STONE) {
                world.setBlockState(pos, ore.withProperty(ORE_STONE, StoneType.ENDSTONE.getInternalId()));
            } else {
                world.setBlockState(pos, ore, 2 | 16);
            }
            return true;
        }
        return false;
    }
}
