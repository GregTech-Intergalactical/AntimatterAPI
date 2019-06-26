package muramasa.gtu.api.worldgen;

import com.google.common.base.Predicate;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import muramasa.gtu.api.blocks.BlockOre;
import muramasa.gtu.api.blocks.BlockStone;
import muramasa.gtu.api.data.StoneType;
import muramasa.gtu.api.materials.MaterialTag;
import muramasa.gtu.api.properties.GTProperties;
import muramasa.gtu.api.util.XSTR;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;

public class WorldGenStone extends WorldGenBase {

    public static Int2ObjectArrayMap<List<WorldGenStone>> STONES = new Int2ObjectArrayMap<>();

    static final double sizeConversion[] = {1, 1, 1.333333, 1.333333, 2, 2, 2, 2, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4}; // Bias the sizes towards skinnier boulders, ie more "shafts" than dikes or sills.

    public Hashtable<Long, StoneSeeds> validStoneSeeds = new Hashtable<>(1024);
    public BlockStone block;
    public IBlockState stone;
    public int amount, size, probability, minY, maxY;
    public boolean allowToGenInVoid;

    public static Predicate<IBlockState> PREDICATE = state -> {
        if (state == null) return false;
        if ((state.getBlock() == Blocks.STONE && state.getValue(net.minecraft.block.BlockStone.VARIANT).isNatural()) ||
            (state.getBlock() instanceof BlockStone)
        ) return true;
        return false;
    };

    static {
        STONES.put(0, new ArrayList<>());
        STONES.put(-1, new ArrayList<>());
        STONES.put(1, new ArrayList<>());
    }

    public WorldGenStone(String id, BlockStone block, int amount, int size, int probability, int minY, int maxY, boolean allowToGenInVoid, MaterialTag... tags) {
        super(id, tags);
        this.block = block;
        this.stone = block.getDefaultState();
        this.amount = amount;
        this.size = size;
        this.probability = probability;
        this.minY = minY;
        this.maxY = maxY;
        this.allowToGenInVoid = allowToGenInVoid;
        if (dims.contains(MaterialTag.OVERWORLD)) STONES.get(0).add(this);
        if (dims.contains(MaterialTag.NETHER)) STONES.get(-1).add(this);
        if (dims.contains(MaterialTag.END)) STONES.get(1).add(this);
    }

    public boolean executeWorldgen(World world, Random rand, int passedX, int passedZ, IChunkGenerator generator, IChunkProvider provider) {
        XSTR stoneRNG = new XSTR();
        ArrayList<ValidSeeds> stones = new ArrayList<>();

        // I think the real size of the balls is mSize/8, but the original code was difficult to understand.
        // Overall there will be less GT stones since they aren't spheres any more. /16 since this code uses it as a radius.
        double realSize = size / 16;
        int windowWidth = ((int) realSize) / 16 + 1; // Width of chunks to check for a potential stoneseed
        // Check stone seeds to see if they have been added
        for (int x = passedX / 16 - windowWidth; x < (passedX / 16 + windowWidth + 1); x++) {
            for (int z = passedZ / 16 - windowWidth; z < (passedZ / 16 + windowWidth + 1); z++) {
                long hash = (((world.provider.getDimension() & 0xffL) << 56) | (((long) x & 0x000000000fffffffL) << 28) | ((long) z & 0x000000000fffffffL));
                if (!validStoneSeeds.containsKey(hash)) {
                    // Determine if RNG says to add stone at this chunk
                    stoneRNG.setSeed(world.getSeed() ^ hash + Math.abs(0/*mBlockMeta*/) + Math.abs(size) + ((block.getType() == StoneType.GRANITE_RED || block.getType() == StoneType.GRANITE_BLACK) ? (32768) : (0)));  //Don't judge me. Want different values for different block types
                    if ((probability <= 1) || (stoneRNG.nextInt(probability) == 0)) {
                        // Add stone at this chunk
                        validStoneSeeds.put(hash, new StoneSeeds(true));
                        // Add to generation list
                        stones.add(new ValidSeeds(x, z));
                        //if (Ref.debugStones) GregTech.LOGGER.info("New stoneseed="+id+ " x="+x+ " z="+z+ " realSize="+realSize);
                    } else {
                        validStoneSeeds.put(hash, new StoneSeeds(false));
                    }
                } else {
                    // This chunk has already been checked, check to see if a boulder exists here
                    if (validStoneSeeds.get(hash).mExists) {
                        // Add to generation list
                        stones.add(new ValidSeeds(x, z));
                    }
                }
            }
        }

        boolean result = true;
        if (stones.size() == 0) {
            result = false;
        }
        // Now process each oreseed vs this requested chunk
        for (; stones.size() != 0; stones.remove(0)) {
            int x = stones.get(0).mX * 16;
            int z = stones.get(0).mZ * 16;

            stoneRNG.setSeed(world.getSeed() ^ (((world.provider.getDimension() & 0xffL) << 56) | (((long) x & 0x000000000fffffffL) << 28) | ((long) z & 0x000000000fffffffL)) + Math.abs(0/*mBlockMeta*/) + Math.abs(size) + ((block.getType() == StoneType.GRANITE_RED || block.getType() == StoneType.GRANITE_BLACK) ? (32768) : (0)));  //Don't judge me
            for (int i = 0; i < amount; i++) { // Not sure why you would want more than one in a chunk! Left alone though.
                // Locate the stoneseed XYZ. Original code would request an isAir at the seed location, causing a chunk generation request.
                // To reduce potential worldgen cascade, we just always try to place a ball and use the check inside the for loop to prevent
                // placement instead.
                int tX = x + stoneRNG.nextInt(16);
                int tY = minY + stoneRNG.nextInt(maxY - minY);
                int tZ = z + stoneRNG.nextInt(16);

                //Determine the XYZ sizes of the stoneseed
                double xSize = sizeConversion[stoneRNG.nextInt(sizeConversion.length)];
                double ySize = sizeConversion[stoneRNG.nextInt(sizeConversion.length) / 2];  // Skew the ySize towards the larger sizes, more long skinny pipes
                double zSize = sizeConversion[stoneRNG.nextInt(sizeConversion.length)];

                //Equation for an ellipsoid centered around 0,0,0
                // Sx, Sy, and Sz are size controls (size = 1/S_)
                // 1 = full size, 1.333 = 75%, 2 = 50%, 4 = 25%
                // (x * Sx)^2 + (y * Sy)^2 + (z * sZ)^2 <= (mSize)^2

                //So, we setup the intial boundaries to be the size of the boulder plus a block in each direction
                int tMinX = tX - (int) (realSize / xSize - 1.0);
                int tMaxX = tX + (int) (realSize / xSize + 2.0);
                int tMinY = tY - (int) (realSize / ySize - 1.0);
                int tMaxY = tY + (int) (realSize / ySize + 2.0);
                int tMinZ = tZ - (int) (realSize / zSize - 1.0);
                int tMaxZ = tZ + (int) (realSize / zSize + 2.0);

                // If the (tY-ySize) of the stoneseed is air in the current chunk, mark the seed empty and move on.

                BlockPos airPos = new BlockPos(passedX + 8, tMinY, passedZ + 8);
                IBlockState airState = world.getBlockState(airPos);
                if (airState.getBlock().isAir(airState, world, airPos)) {
                    //if (Ref.debugStones) GregTech.LOGGER.info(id + " tX=" + tX + " tY=" + tY + " tZ=" + tZ + " realSize=" + realSize + " xSize=" + realSize/xSize + " ySize=" + realSize/ySize + " zSize=" + realSize/zSize + " tMinY=" + tMinY + " tMaxY=" + tMaxY + " - Skipped because first requesting chunk would not contain this stone");
                    long hash = (((world.provider.getDimension() & 0xffL) << 56) | (((long) x & 0x000000000fffffffL) << 28) | ((long) z & 0x000000000fffffffL));
                    validStoneSeeds.remove(hash);
                    validStoneSeeds.put(hash, new StoneSeeds(false));
                }

                //Chop the boundaries by the parts that intersect with the current chunk
                int wX = Math.max(tMinX, passedX + 8);
                int eX = Math.min(tMaxX, passedX + 8 + 16);

                int sZ = Math.max(tMinZ, passedZ + 8);
                int nZ = Math.min(tMaxZ, passedZ + 8 + 16);

                //if (Ref.debugStones) GregTech.LOGGER.info(id + " tX=" + tX + " tY=" + tY + " tZ=" + tZ + " realSize=" + realSize + " xSize=" + realSize/xSize + " ySize=" + realSize/ySize + " zSize=" + realSize/zSize + " wX=" + wX + " eX=" + eX + " tMinY=" + tMinY + " tMaxY=" + tMaxY + " sZ=" + sZ + " nZ=" + nZ);

                double rightHandSide = realSize * realSize + 1;  //Precalc the right hand side
                for (int iY = tMinY; iY < tMaxY; iY++) {  // Do placement from the bottom up layer up.  Maybe better on cache usage?
                    double yCalc = ((double) (iY - tY) * ySize);
                    yCalc = yCalc * yCalc; // (y*Sy)^2
                    double leftHandSize = yCalc;
                    if (leftHandSize > rightHandSide) {
                        continue; // If Y alone is larger than the RHS, skip the rest of the loops
                    }
                    for (int iX = wX; iX < eX; iX++) {
                        double xCalc = ((double) (iX - tX) * xSize);
                        xCalc = xCalc * xCalc;
                        leftHandSize = yCalc + xCalc;
                        if (leftHandSize > rightHandSide) { // Again, if X and Y is larger than the RHS, skip to the next value
                            continue;
                        }
                        for (int iZ = sZ; iZ < nZ; iZ++) {
                            double zCalc = ((double) (iZ - tZ) * zSize);
                            zCalc = zCalc * zCalc;
                            leftHandSize = zCalc + xCalc + yCalc;
                            if (leftHandSize > rightHandSide) {
                                continue;
                            } else {
                                // Yay! We can actually place a block now. (this part copied from original code)
                                BlockPos targetPos = new BlockPos(iX, iY, iZ);
                                IBlockState targetState = world.getBlockState(targetPos);
                                if (targetState.getBlock() instanceof BlockOre) {
                                   world.setBlockState(targetPos, targetState.withProperty(GTProperties.ORE_STONE, block.getType().getInternalId()));
                                } else if (targetState.getBlock().isReplaceableOreGen(targetState, world, targetPos, PREDICATE)) {
                                    world.setBlockState(targetPos, stone);
                                }

//                                Block tTargetedBlock = world.getBlock(iX, iY, iZ);
//                                if (tTargetedBlock instanceof GT_Block_Ores_Abstract) {
//                                    TileEntity tTileEntity = world.getTileEntity(iX, iY, iZ);
//                                    if ((tTileEntity instanceof GT_TileEntity_Ores)) {
//                                        if (tTargetedBlock != GregTech_API.sBlockOres1) {
//                                            ((GT_TileEntity_Ores) tTileEntity).convertOreBlock(world, iX, iY, iZ);
//                                        }
//                                        ((GT_TileEntity_Ores) tTileEntity).overrideOreBlockMaterial(this.mBlock, (byte) this.mBlockMeta);
//                                    }
//                                } else if (((this.mAllowToGenerateinVoid) && (world.getBlock(iX, iY, iZ).isAir(world, iX, iY, iZ))) || ((tTargetedBlock != null) && ((tTargetedBlock.isReplaceableOreGen(world, iX, iY, iZ, Blocks.stone)) || (tTargetedBlock.isReplaceableOreGen(world, iX, iY, iZ, Blocks.stained_hardened_clay)) || (tTargetedBlock.isReplaceableOreGen(world, iX, iY, iZ, Blocks.cobblestone)) || (tTargetedBlock.isReplaceableOreGen(world, iX, iY, iZ, Blocks.end_stone)) || (tTargetedBlock.isReplaceableOreGen(world, iX, iY, iZ, Blocks.netherrack)) || (tTargetedBlock.isReplaceableOreGen(world, iX, iY, iZ, GregTech_API.sBlockGranites)) || (tTargetedBlock.isReplaceableOreGen(world, iX, iY, iZ, GregTech_API.sBlockStones))))) {
//                                    world.setBlock(iX, iY, iZ, this.mBlock, this.mBlockMeta, 0);
//                                }
                            }
                        }
                    }
                }
            }
        }
        return result;
    }

    class StoneSeeds {
        public boolean mExists;

        StoneSeeds(boolean exists) {
            mExists = exists;
        }
    }

    class ValidSeeds {
        public int mX;
        public int mZ;

        ValidSeeds(int x, int z) {
            this.mX = x;
            this.mZ = z;
        }
    }
}