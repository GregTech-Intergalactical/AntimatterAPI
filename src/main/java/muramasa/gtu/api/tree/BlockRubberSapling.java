package muramasa.gtu.api.tree;

import muramasa.gtu.Ref;
import muramasa.gtu.common.Data;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.terraingen.TerrainGen;

import java.util.Random;

public class BlockRubberSapling extends BlockSaplingBase {

    public BlockRubberSapling() {
        super(new ResourceLocation(Ref.MODID, "rubber_sapling"));
    }

    @Override
    public void generateTree(World world, BlockPos pos, Random rand) {
        if (!TerrainGen.saplingGrowTree(world, rand, pos)) return;
        int maxHeight = getMaxHeight(world, pos, 9);
        if (maxHeight < 7) return;
        maxHeight = pos.getY()+7+rand.nextInt(maxHeight-6);

        int aX = pos.getX(), aZ = pos.getZ();

        for (int i = -1; i <= 1; i++) for (int j = -1; j <= 1; j++) if (i != 0 || j != 0) if (!canPlaceTree(world, aX+i, maxHeight-5, aZ+j)) return;

        boolean tCanPlaceResinHole = true;

        for (int tY = pos.getY(); tY < maxHeight; tY++) {
            if (tCanPlaceResinHole && maxHeight - tY > 5 && tY - pos.getY() > 0 && rand.nextInt(2) == 0) {
                tCanPlaceResinHole = false;
                placeTree(world, aX, tY, aZ, Data.RUBBER_LOG.getDefaultState().withProperty(BlockRubberLog.RESIN_STATE, BlockRubberLog.ResinState.EMPTY).withProperty(BlockRubberLog.RESIN_FACING, EnumFacing.HORIZONTALS[rand.nextInt(4)]));
            }
            placeTree(world, aX, tY, aZ, Data.RUBBER_LOG.getDefaultState());
        }

        placeTree(world, aX, maxHeight  , aZ, Data.RUBBER_LEAVES.getDefaultState());
        placeTree(world, aX, maxHeight+1, aZ, Data.RUBBER_LEAVES.getDefaultState());
        for (int i = -1; i <= 1; i++) for (int j = -1; j <= 1; j++) if (i != 0 || j != 0) {
            placeTree(world, aX+i, maxHeight-1, aZ+j, Data.RUBBER_LEAVES.getDefaultState());
        }
        for (int i = -2; i <= 2; i++) for (int j = -2; j <= 2; j++) if (i != 0 || j != 0) {
            if (Math.abs(i*j) < 2) {
                placeTree(world, aX+i, maxHeight-2, aZ+j, Data.RUBBER_LEAVES.getDefaultState());
            }
            if (Math.abs(i*j) < 4) {
                placeTree(world, aX+i, maxHeight-3, aZ+j, Data.RUBBER_LEAVES.getDefaultState());
                placeTree(world, aX+i, maxHeight-4, aZ+j, Data.RUBBER_LEAVES.getDefaultState());
            }
            placeTree(world, aX+i, maxHeight-5, aZ+j, Data.RUBBER_LEAVES.getDefaultState());
        }
    }

    public static int getMaxHeight(World world, BlockPos pos, int aMaxTreeHeight) {
        aMaxTreeHeight--;
        int rMaxHeight = 0;
        while (rMaxHeight++ < aMaxTreeHeight) {
            if (pos.getY()+rMaxHeight >= world.getHeight() || !canPlaceTree(world, pos.getX(), pos.getY() + rMaxHeight, pos.getZ())) {
                return rMaxHeight-1;
            }
        }
        return rMaxHeight;
    }

    public static boolean placeTree(World world, int x, int y, int z, IBlockState state) {
        if (canPlaceTree(world, x, y, z)) {
            world.setBlockState(new BlockPos(x, y, z), state, 3);
            //TODO remove
            world.notifyBlockUpdate(new BlockPos(x, y, z), state, state, 3);
            return true;
        }
        return false;
    }

    public static boolean canPlaceTree(World world, int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof BlockTallGrass || state.getBlock().canBeReplacedByLeaves(state, world, pos);
    }
}
