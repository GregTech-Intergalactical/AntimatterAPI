package muramasa.gtu.api.worldgen;

import com.google.common.base.Predicate;
import muramasa.gtu.api.util.XSTR;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;

import java.util.List;
import java.util.Random;

public class OreLayerGenerator implements IWorldGenerator {

    //public static Int2ObjectArrayMap<List<OreLayer>> LAYERS = new Int2ObjectArrayMap<>();
    //

    public OreLayerGenerator() {
        //GameRegistry.registerWorldGenerator(this, 1073641823);
    }

    public static void addLayer(WorldGenOreLayer layer) {

    }

    //public static void addStone(WorldGenStone stone) {

    //}

    @Override
    public void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator generator, IChunkProvider provider) {
        XSTR rand = new XSTR(world.getSeed());
        //List<WorldGenStone> stones = STONES.get(world.provider.getDimension());
        //WorldGenStone stone = stones.get(rand.nextInt(stones.size()));

        //stone.generate(rand, chunkX, chunkZ, world, generator, provider);

        List<WorldGenOreLayer> layers = WorldGenOreLayer.LAYERS.get(world.provider.getDimension());
        WorldGenOreLayer layer = layers.get(rand.nextInt(layers.size()));

        int deltaY = layer.maxY - layer.minY;
        BlockPos pos = new BlockPos((chunkX * 16) + 8, layer.minY + rand.nextInt(deltaY), (chunkZ * 16) + 8);
        genPad(world, pos);
        //generate(layer.states, layer.size, world, rand, pos, null);
    }

    public void genPad(World world, BlockPos pos) {
        for (int x = -8; x < 24; x++) {
            for (int z = -8; z < 24; z++) {
                BlockPos currPos = new BlockPos(x, 0, z).add(pos);
                IBlockState state = world.getBlockState(currPos);
                if (state.getBlock().isReplaceableOreGen(state, world, currPos, WorldGenHelper.STONE_PREDICATE)) {
                    if (currPos.getX() == pos.getX() && currPos.getY() == pos.getY() && currPos.getZ() == pos.getZ()) {
                        world.setBlockState(pos, Blocks.DIAMOND_BLOCK.getDefaultState(), 2 | 16);
                    } else {
                        //world.setBlockState(currPos, states[rand.nextInt(4)], 2 | 16);
                        world.setBlockState(currPos, Blocks.END_STONE.getDefaultState(), 2 | 16);
                    }
                }
            }
        }
    }

    public boolean generate(IBlockState[] states, int blockCount, World world, Random rand, BlockPos pos, Predicate<IBlockState> predicate) {
        float f = rand.nextFloat() * (float) Math.PI;
        double d0 = (double) ((float) (pos.getX() + 8) + MathHelper.cos(f) * (float) blockCount / 8.0F);
        double d1 = (double) ((float) (pos.getX() + 8) - MathHelper.cos(f) * (float) blockCount / 8.0F);
        double d2 = (double) ((float) (pos.getZ() + 8) + MathHelper.cos(f) * (float) blockCount / 8.0F);
        double d3 = (double) ((float) (pos.getZ() + 8) - MathHelper.cos(f) * (float) blockCount / 8.0F);
        double d4 = (double) (pos.getY() + rand.nextInt(3) - 2);
        double d5 = (double) (pos.getY() + rand.nextInt(3) - 2);

        for (int i = 0; i < blockCount; ++i) {
            float f1 = (float) i / (float) blockCount;
            double d6 = d0 + (d1 - d0) * (double) f1;
            double d7 = d4 + (d5 - d4) * (double) f1;
            double d8 = d2 + (d3 - d2) * (double) f1;
            double d9 = rand.nextDouble() * (double) blockCount / 16.0D;
            double d10 = (double) (MathHelper.sin((float) Math.PI * f1) + 1.0F) * d9 + 1.0D;
            double d11 = (double) (MathHelper.sin((float) Math.PI * f1) + 1.0F) * d9 + 1.0D;
            int j = MathHelper.floor(d6 - d10 / 2.0D);
            int k = MathHelper.floor(d7 - d11 / 2.0D);
            int l = MathHelper.floor(d8 - d10 / 2.0D);
            int i1 = MathHelper.floor(d6 + d10 / 2.0D);
            int j1 = MathHelper.floor(d7 + d11 / 2.0D);
            int k1 = MathHelper.floor(d8 + d10 / 2.0D);

            for (int l1 = j; l1 <= i1; ++l1) {
                double d12 = ((double) l1 + 0.5D - d6) / (d10 / 2.0D);

                if (d12 * d12 < 1.0D) {
                    for (int i2 = k; i2 <= j1; ++i2) {
                        double d13 = ((double) i2 + 0.5D - d7) / (d11 / 2.0D);

                        if (d12 * d12 + d13 * d13 < 1.0D) {
                            for (int j2 = l; j2 <= k1; ++j2) {
                                double d14 = ((double) j2 + 0.5D - d8) / (d10 / 2.0D);

                                if (d12 * d12 + d13 * d13 + d14 * d14 < 1.0D) {
                                    BlockPos blockpos = new BlockPos(l1, i2, j2);

                                    IBlockState state = world.getBlockState(blockpos);
                                    if (state.getBlock().isReplaceableOreGen(state, world, blockpos, WorldGenHelper.STONE_PREDICATE)) {
                                        world.setBlockState(blockpos, states[rand.nextInt(4)], 2 | 16);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        return true;
    }

    public void setState(World world, BlockPos pos, IBlockState ore) {
        IBlockState state = world.getBlockState(pos);
        if (state.getBlock().isReplaceableOreGen(state, world, pos, WorldGenHelper.STONE_PREDICATE)) {
            world.setBlockState(pos, ore, 2 | 16);
        }
    }
}
