package muramasa.gtu.api.worldgen;

import com.google.gson.annotations.Expose;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.blocks.BlockOre;
import muramasa.gtu.api.blocks.BlockStone;
import muramasa.gtu.api.data.StoneType;
import muramasa.gtu.api.properties.GTProperties;
import muramasa.gtu.api.util.XSTR;
import muramasa.gtu.api.util.int2;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;

import java.util.ArrayList;
import java.util.Hashtable;

public class WorldGenStone extends WorldGenBase {

    private static final double SIZE_CONVERSION[] = {1, 1, 1.333333, 1.333333, 2, 2, 2, 2, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4}; // Bias the sizes towards skinnier boulders, ie more "shafts" than dikes or sills.
    //public static LongOpenHashSet SEEDS = new LongOpenHashSet();

    @Expose public String type;
    @Expose public int amount, size, probability, minY, maxY;

    public BlockStone block;
    public IBlockState stone;
    public Hashtable<Long, StoneSeeds> validStoneSeeds;

    public WorldGenStone(String id, StoneType type, int amount, int size, int probability, int minY, int maxY, boolean allowToGenInVoid, int... dimensions) {
        super(id, dimensions);
        this.type = type.getId();
        this.amount = amount;
        this.size = size;
        this.probability = probability;
        this.minY = minY;
        this.maxY = maxY;
    }

    @Override
    public WorldGenBase build() {
        this.block = GregTechAPI.get(BlockStone.class, type);
        this.stone = block.getDefaultState();
        this.validStoneSeeds = new Hashtable<>(1024);
        return this;
    }

    public boolean generate(World world, XSTR rand, int passedX, int passedZ, IChunkGenerator generator, IChunkProvider provider) {
        ArrayList<int2> stones = new ArrayList<>();

        // I think the real size of the balls is mSize/8, but the original code was difficult to understand.
        // Overall there will be less GT stones since they aren't spheres any more. /16 since this code uses it as a radius.
        int realSize = size / 16;
        int windowWidth = realSize / 16 + 1; // Width of chunks to check for a potential stoneseed
        // Check stone seeds to see if they have been added
        for (int x = passedX / 16 - windowWidth; x < passedX / 16 + windowWidth + 1; x++) {
            for (int z = passedZ / 16 - windowWidth; z < passedZ / 16 + windowWidth + 1; z++) {
                //compute hash for dimension and position
                long hash = (world.provider.getDimension() & 0xffL) << 56 | ((long) x & 0x000000000fffffffL) << 28 | (long) z & 0x000000000fffffffL;
                if (!validStoneSeeds.containsKey(hash)) { //if this hash does not exist in our lookup, determine if RGB says to add stones in this chunk
                    rand.setSeed(world.getSeed() ^ hash + block.getType().getInternalId());
                    if (probability <= 1 || rand.nextInt(probability) == 0) {
                        //We got a valid RNG roll, allow stone gen at this chunk
                        validStoneSeeds.put(hash, new StoneSeeds(true));
                        // Add to generation list
                        stones.add(new int2(x, z));
                        //if (Ref.debugStones) GregTech.LOGGER.info("New stoneseed="+id+ " chunkX="+chunkX+ " z="+z+ " realSize="+realSize);
                    } else {
                        validStoneSeeds.put(hash, new StoneSeeds(false));
                    }
                } else {
                    // This chunk has already been checked, check to see if a boulder exists here
                    if (validStoneSeeds.get(hash).mExists) {
                        // Add to generation list
                        stones.add(new int2(x, z));
                    }
                }
            }
        }

        // Now process each stoneSeed vs this requested chunk
        int count = stones.size();
        if (count == 0) return false;

        int2 seed;
        for (int s = 0; s < count; s++) {
            seed = stones.get(s);

            int x = seed.x * 16;
            int z = seed.y * 16;

            rand.setSeed(world.getSeed() ^ ((world.provider.getDimension() & 0xffL) << 56 | ((long) x & 0x000000000fffffffL) << 28 | (long) z & 0x000000000fffffffL) + Math.abs(0/*mBlockMeta*/) + Math.abs(size) + (block.getType() == StoneType.GRANITE_RED || block.getType() == StoneType.GRANITE_BLACK ? 32768 : 0));  //Don't judge me
            for (int i = 0; i < amount; i++) { // Not sure why you would want more than one in a chunk! Left alone though.
                // Locate the stoneseed XYZ. Original code would request an isAir at the seed location, causing a chunk generation request.
                // To reduce potential worldgen cascade, we just always try to place a ball and use the check inside the for loop to prevent
                // placement instead.
                int tX = x + rand.nextInt(16);
                int tY = minY + rand.nextInt(maxY - minY);
                int tZ = z + rand.nextInt(16);

                //Determine the XYZ sizes of the stoneseed
                double xSize = SIZE_CONVERSION[rand.nextInt(SIZE_CONVERSION.length)];
                double ySize = SIZE_CONVERSION[rand.nextInt(SIZE_CONVERSION.length) / 2];  // Skew the ySize towards the larger sizes, more long skinny pipes
                double zSize = SIZE_CONVERSION[rand.nextInt(SIZE_CONVERSION.length)];

                //Equation for an ellipsoid centered around 0,0,0
                // Sx, Sy, and Sz are size controls (size = 1/S_)
                // 1 = full size, 1.333 = 75%, 2 = 50%, 4 = 25%
                // (chunkX * Sx)^2 + (y * Sy)^2 + (z * sZ)^2 <= (mSize)^2

                //So, we setup the intial boundaries to be the size of the boulder plus a block in each direction
                int tMinX = tX - (int) (realSize / xSize - 1.0);
                int tMaxX = tX + (int) (realSize / xSize + 2.0);
                int tMinY = tY - (int) (realSize / ySize - 1.0);
                int tMaxY = tY + (int) (realSize / ySize + 2.0);
                int tMinZ = tZ - (int) (realSize / zSize - 1.0);
                int tMaxZ = tZ + (int) (realSize / zSize + 2.0);

                // If the (tY-ySize) of the stoneseed is air in the current chunk, mark the seed empty and move on.

                BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(passedX + 8, tMinY, passedZ + 8);
                IBlockState state = world.getBlockState(pos);
                if (state.getBlock().isAir(state, world, pos)) {
                    //if (Ref.debugStones) GregTech.LOGGER.info(id + " tX=" + tX + " tY=" + tY + " tZ=" + tZ + " realSize=" + realSize + " xSize=" + realSize/xSize + " ySize=" + realSize/ySize + " zSize=" + realSize/zSize + " tMinY=" + tMinY + " tMaxY=" + tMaxY + " - Skipped because first requesting chunk would not contain this stone");
                    long hash = (world.provider.getDimension() & 0xffL) << 56 | ((long) x & 0x000000000fffffffL) << 28 | (long) z & 0x000000000fffffffL;
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
                    double yCalc = (double) (iY - tY) * ySize;
                    yCalc = yCalc * yCalc; // (y*Sy)^2
                    double leftHandSize = yCalc;
                    if (leftHandSize > rightHandSide) {
                        continue; // If Y alone is larger than the RHS, skip the rest of the loops
                    }
                    for (int iX = wX; iX < eX; iX++) {
                        double xCalc = (double) (iX - tX) * xSize;
                        xCalc = xCalc * xCalc;
                        leftHandSize = yCalc + xCalc;
                        if (leftHandSize > rightHandSide) { // Again, if X and Y is larger than the RHS, skip to the next value
                            continue;
                        }
                        for (int iZ = sZ; iZ < nZ; iZ++) {
                            double zCalc = (double) (iZ - tZ) * zSize;
                            zCalc = zCalc * zCalc;
                            leftHandSize = zCalc + xCalc + yCalc;
                            if (leftHandSize <= rightHandSide) {
                                // Yay! We can actually place a block now. (this part copied from original code)
                                pos.setPos(iX, iY, iZ);
                                state = world.getBlockState(pos);
                                if (state.getBlock().isReplaceableOreGen(state, world, pos, WorldGenHelper.STONE_PREDICATE)) {
                                    world.setBlockState(pos, stone);
                                } else if (state.getBlock() instanceof BlockOre) {
                                    world.setBlockState(pos, state.withProperty(GTProperties.ORE_STONE, block.getType().getInternalId()));
                                } /*else if (allowToGenInVoid && state.getBlock().isAir(state, world, pos)) {
                                    world.setBlockState(pos, state);
                                }*/
                            }
                        }
                    }
                }
            }
        }
        stones.clear();
        return true;
    }

    class StoneSeeds {
        public boolean mExists;

        StoneSeeds(boolean exists) {
            mExists = exists;
        }
    }
}