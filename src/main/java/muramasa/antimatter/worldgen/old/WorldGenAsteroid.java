package muramasa.antimatter.worldgen.old;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Configs;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.util.XSTR;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import muramasa.antimatter.worldgen.object.WorldGenBase;
import muramasa.antimatter.worldgen.WorldGenHelper;
import muramasa.antimatter.worldgen.object.WorldGenVeinLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.gen.ChunkGenerator;

import java.util.List;

public class WorldGenAsteroid extends WorldGenBase {

    private static int mSize = 100;

    private static boolean endAsteroids = true;
    private static int endMinSize = 50;
    private static int endMaxSize = 200;
    private static int mEndAsteroidProbability = 300;

    private static boolean gcAsteroids = true;
    private static int gcMinSize = 100;
    private static int gcMaxSize = 400;
    private static int mGCAsteroidProbability = 50;

    private static BlockState END_STONE_STATE = null;
    private static BlockState GRANITE_RED_STATE = null;

    public WorldGenAsteroid(String id, int... dimensions) {
        super(id, WorldGenAsteroid.class, dimensions);
    }

    @Override
    public WorldGenBase build() {
        END_STONE_STATE = Blocks.END_STONE.getDefaultState();
        GRANITE_RED_STATE = AntimatterAPI.get(BlockStone.class, "granite_red").getDefaultState();
        return this;
    }

    public boolean generate(World world, XSTR rand, int passedX, int passedZ, BlockPos.Mutable pos, BlockState state, ChunkGenerator generator, AbstractChunkProvider provider) {
        if (mEndAsteroidProbability <= 1 || rand.nextInt(mEndAsteroidProbability) == 0) {
            List<WorldGenVeinLayer> layers = AntimatterWorldGenerator.all(WorldGenVeinLayer.class, world.getDimension().getType().getId());
            int layerCount = layers.size();
            WorldGenVeinLayer layerToGen = null;
            if (WorldGenVeinLayer.getTotalWeight() > 0 && layerCount > 0) {
                int randomWeight;
                WorldGenVeinLayer layer;
                for (int i = 0; i < Configs.WORLD.ORE_VEIN_FIND_ATTEMPTS; i++) {
                    randomWeight = rand.nextInt(WorldGenVeinLayer.getTotalWeight());
                    for (int j = 0; j < layerCount; j++) {
                        layer = layers.get(j);
                        randomWeight -= layer.getWeight();
                        if (randomWeight <= 0) {
                            layerToGen = layer;
                            break;
                        }
                    }
                }
            }

            //TODO Mura code
            if (layerToGen == null) return false;

            //mSize = rand.nextInt(world.provider.getDimension() == 1 ? (endMaxSize - endMinSize) : (gcMaxSize - gcMinSize));
            mSize = endMaxSize - endMinSize;

            //if(GT_Values.D1)GT_FML_LOGGER.info("do asteroid gen: "+this.passedX+" "+this.passedZ);
            int tX = passedX * 16 + rand.nextInt(16);
            int tY = 50 + rand.nextInt(200 - 50);
            int tZ = passedZ * 16 + rand.nextInt(16);

            pos.setPos(tX, tY, tZ);
            state = world.getBlockState(pos);
            if (state.getBlock().isAir(state, world, pos)) {
                float var6 = rand.nextFloat() * 3.141593F;
                double var7 = tX + 8 + MathHelper.sin(var6) * mSize / 8.0F;
                double var9 = tX + 8 - MathHelper.sin(var6) * mSize / 8.0F;
                double var11 = tZ + 8 + MathHelper.cos(var6) * mSize / 8.0F;
                double var13 = tZ + 8 - MathHelper.cos(var6) * mSize / 8.0F;
                double var15 = tY + rand.nextInt(3) - 2;
                double var17 = tY + rand.nextInt(3) - 2;
                for (int var19 = 0; var19 <= mSize; var19++) {
                    double var20 = var7 + (var9 - var7) * var19 / mSize;
                    double var22 = var15 + (var17 - var15) * var19 / mSize;
                    double var24 = var11 + (var13 - var11) * var19 / mSize;
                    double var26 = rand.nextDouble() * mSize / 16.0D;
                    double var28 = (MathHelper.sin(var19 * 3.141593F / mSize) + 1.0F) * var26 + 1.0D;
                    double var30 = (MathHelper.sin(var19 * 3.141593F / mSize) + 1.0F) * var26 + 1.0D;
                    int tMinX = MathHelper.floor(var20 - var28 / 2.0D);
                    int tMinY = MathHelper.floor(var22 - var30 / 2.0D);
                    int tMinZ = MathHelper.floor(var24 - var28 / 2.0D);
                    int tMaxX = MathHelper.floor(var20 + var28 / 2.0D);
                    int tMaxY = MathHelper.floor(var22 + var30 / 2.0D);
                    int tMaxZ = MathHelper.floor(var24 + var28 / 2.0D);
                    for (int eX = tMinX; eX <= tMaxX; eX++) {
                        double var39 = (eX + 0.5D - var20) / (var28 / 2.0D);
                        if (var39 * var39 < 1.0D) {
                            for (int eY = tMinY; eY <= tMaxY; eY++) {
                                double var42 = (eY + 0.5D - var22) / (var30 / 2.0D);
                                if (var39 * var39 + var42 * var42 < 1.0D) {
                                    for (int eZ = tMinZ; eZ <= tMaxZ; eZ++) {
                                        double var45 = (eZ + 0.5D - var24) / (var28 / 2.0D);
                                        pos.setPos(tX, tY, tZ);
                                        BlockState airState = world.getBlockState(pos);
                                        if (var39 * var39 + var42 * var42 + var45 * var45 < 1.0D && airState.getBlock().isAir(airState, world, pos)) {
                                            pos.setPos(eX, eY, eZ);
                                            state = world.getBlockState(pos);
                                            int ranOre = rand.nextInt(50);
                                            if (ranOre < 3) {
                                                WorldGenHelper.setOre(world, pos, state, layerToGen.getMaterial(0), MaterialType.ORE);
                                            } else if (ranOre < 6) {
                                                WorldGenHelper.setOre(world, pos, state, layerToGen.getMaterial(1), MaterialType.ORE);
                                            } else if (ranOre < 8) {
                                                WorldGenHelper.setOre(world, pos, state, layerToGen.getMaterial(2), MaterialType.ORE);
                                            } else if (ranOre < 10) {
                                                WorldGenHelper.setOre(world, pos, state, layerToGen.getMaterial(3), MaterialType.ORE);
                                            } else {
                                                if (world.getDimension().getType().getId() == Ref.ASTEROIDS) {
                                                    WorldGenHelper.setState(world, pos, GRANITE_RED_STATE);
                                                } else {
                                                    WorldGenHelper.setState(world, pos, END_STONE_STATE);
                                                }
                                            }
                                        }
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
}
