package muramasa.gtu.api.worldgen;

import muramasa.gtu.Ref;
import muramasa.gtu.api.util.XSTR;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;

import java.util.Random;

public class AsteroidGenerator /*implements IWorldGenerator*/ {

    private static boolean endAsteroids = true;
    private static int mEndAsteroidProbability = 300;
    private static int mSize = 100;
    private static int endMinSize = 50;
    private static int endMaxSize = 200;

    private static boolean gcAsteroids = true;
    private static int mGCAsteroidProbability = 50;
    private static int gcMinSize = 100;
    private static int gcMaxSize = 400;

    private static IBlockState END_STONE_STATE = Blocks.END_STONE.getDefaultState();

    public AsteroidGenerator() {
//        endAsteroids = GregTech_API.sWorldgenFile.get("endasteroids", "GenerateAsteroids", true);
//        endMinSize = GregTech_API.sWorldgenFile.get("endasteroids", "AsteroidMinSize", 50);
//        endMaxSize = GregTech_API.sWorldgenFile.get("endasteroids", "AsteroidMaxSize", 200);
//        mEndAsteroidProbability = GregTech_API.sWorldgenFile.get("endasteroids", "AsteroidProbability", 300);
//        gcAsteroids = GregTech_API.sWorldgenFile.get("gcasteroids", "GenerateGCAsteroids", true);
//        gcMinSize = GregTech_API.sWorldgenFile.get("gcasteroids", "GCAsteroidMinSize", 100);
//        gcMaxSize = GregTech_API.sWorldgenFile.get("gcasteroids", "GCAsteroidMaxSize", 400);
//        mGCAsteroidProbability = GregTech_API.sWorldgenFile.get("gcasteroids", "GCAsteroidProbability", 300);
        if (endAsteroids) {
            //GameRegistry.registerWorldGenerator(this, 5);
        }
    }

    //@Override
    public static void generate(Random random, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        if (world.provider.getDimension() != 1) return; //TODO add GC dims
        XSTR rand = new XSTR();

        if (mEndAsteroidProbability <= 1 || rand.nextInt(mEndAsteroidProbability) == 0) {
            WorldGenOreLayer layerToGen = null;
            if (WorldGenOreLayer.TOTAL_WEIGHT > 0 && WorldGenOreLayer.LAYERS.get(world.provider.getDimension()).size() > 0) {
                boolean temp = true;
                int tRandomWeight;
                for (int i = 0; (i < Ref.oreveinAttempts) && (temp); i++) {
                    tRandomWeight = rand.nextInt(WorldGenOreLayer.TOTAL_WEIGHT);
                    for (WorldGenOreLayer layer : WorldGenOreLayer.LAYERS.get(world.provider.getDimension())) {
                        tRandomWeight -= layer.weight;
                        if (tRandomWeight <= 0) {
                            //try {
                                //if ((tWorldGen.mEndAsteroid && tDimensionType == 1) || (tWorldGen.mAsteroid && tDimensionType == -30)) {
                                //if (tWorldGen.mEndAsteroid && tDimensionType == 1) {
                                    //primaryMeta = tWorldGen.mPrimaryMeta;
                                    //secondaryMeta = tWorldGen.mSecondaryMeta;
                                    //betweenMeta = tWorldGen.mBetweenMeta;
                                    //sporadicMeta = tWorldGen.mSporadicMeta;
                                    //temp = false;
                                    layerToGen = layer;
                                    break;
                                //}
                            //} catch (Throwable e) {
                                //e.printStackTrace();
                            //}
                        }
                    }
                }
            }

            //TODO mura code
            if (layerToGen == null) return;

            //if(GT_Values.D1)GT_FML_LOGGER.info("do asteroid gen: "+this.chunkX+" "+this.chunkZ);
            int tX = chunkX * 16 + rand.nextInt(16);
            int tY = 50 + rand.nextInt(200 - 50);
            int tZ = chunkZ * 16 + rand.nextInt(16);

            //TODO remove
            if (world.provider.getDimension() == 1) {
                mSize = rand.nextInt((int) (endMaxSize - endMinSize));
                //} else if (tDimensionName.equals("Asteroids")) {
                //    mSize = rand.nextInt((int) (gcMaxSize - gcMinSize));
            }

            BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(tX, tY, tZ);
            IBlockState state = world.getBlockState(pos);
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

                                        BlockPos airPos = new BlockPos(tX, tY, tZ);
                                        IBlockState airState = world.getBlockState(airPos);
                                        if ((var39 * var39 + var42 * var42 + var45 * var45 < 1.0D) && (airState.getBlock().isAir(airState, world, airPos))) {
                                            pos.setPos(eX, eY, eZ);
                                            int ranOre = rand.nextInt(50);
                                            if (ranOre < 3) {
                                                WorldGenHelper.setState(world, new BlockPos(eX, eY, eZ), layerToGen.primary);
                                                //GT_TileEntity_Ores.setOreBlock(world, eX, eY, eZ, primaryMeta, false);
                                            } else if (ranOre < 6) {
                                                WorldGenHelper.setState(world, new BlockPos(eX, eY, eZ), layerToGen.secondary);
                                                //GT_TileEntity_Ores.setOreBlock(world, eX, eY, eZ, secondaryMeta, false);
                                            } else if (ranOre < 8) {
                                                WorldGenHelper.setState(world, new BlockPos(eX, eY, eZ), layerToGen.between);
                                                //GT_TileEntity_Ores.setOreBlock(world, eX, eY, eZ, betweenMeta, false);
                                            } else if (ranOre < 10) {
                                                WorldGenHelper.setState(world, new BlockPos(eX, eY, eZ), layerToGen.sporadic);
                                                //GT_TileEntity_Ores.setOreBlock(world, eX, eY, eZ, sporadicMeta, false);
                                            } else {
                                                //if (tDimensionType == 1) {//TODO CHECK
                                                WorldGenHelper.setState(world, pos, END_STONE_STATE);
                                                //} else if (tDimensionName.equals("Asteroids")) {
                                                ////int asteroidType = rand.nextInt(20);
                                                ////if (asteroidType == 19) { //Rare Asteroid?
                                                ////world.setBlock(eX, eY, eZ, GregTech_API.sBlockGranites, 8, 3);
                                                ////} else {
                                                //world.setBlock(eX, eY, eZ, GregTech_API.sBlockGranites, 8, 3);
                                                ////}
                                                //}
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
}
