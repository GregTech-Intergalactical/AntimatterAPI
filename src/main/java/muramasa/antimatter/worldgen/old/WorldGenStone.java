package muramasa.antimatter.worldgen.old;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.util.XSTR;
import muramasa.antimatter.worldgen.WorldGenHelper;
import muramasa.antimatter.worldgen.object.WorldGenBase;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.gen.ChunkGenerator;

public class WorldGenStone extends WorldGenBase<WorldGenStone> {

    private static final double SIZE_CONVERSION[] = {1, 1, 1.333333, 1.333333, 2, 2, 2, 2, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4}; // Bias the sizes towards skinnier boulders, ie more "shafts" than dikes or sills.

    public String type;
    public int minY, maxY, amount, size, probability;

    public BlockStone block;
    public BlockState stone;
    public LongOpenHashSet CHECKED_SEEDS;

    public WorldGenStone(String id, StoneType type, int amount, int size, int probability, int minY, int maxY, int... dimensions) {
        super(id, WorldGenStone.class, dimensions);
        this.type = type.getId();
        this.amount = amount;
        this.size = size;
        this.probability = probability;
        this.minY = minY;
        this.maxY = maxY;
    }

    @Override
    public WorldGenStone onDataOverride(JsonObject json) {
        super.onDataOverride(json);
        //if (json.has("type")) type = Utils.parseString(dataMap.get("type"), type);
        //if (json.has("minY")) minY = Utils.parseInt(dataMap.get("minY"), minY);
        //if (json.has("maxY")) maxY = Utils.parseInt(dataMap.get("maxY"), maxY);
        //if (json.has("amount")) amount = Utils.parseInt(dataMap.get("amount"), amount);
        //if (json.has("size")) size = Utils.parseInt(dataMap.get("size"), size);
        //if (json.has("probability")) probability = Utils.parseInt(dataMap.get("probability"), probability);
        return this;
    }

    @Override
    public WorldGenStone build() {
        super.build();
        this.block = AntimatterAPI.get(BlockStone.class, type);
        if (block == null) throw new IllegalArgumentException("WorldGenStone - " + getId() + ": was given a invalid stone type");
        this.stone = block.getDefaultState();
        this.CHECKED_SEEDS = new LongOpenHashSet();
        return this;
    }

    public boolean generate(World world, XSTR rand, int passedX, int passedZ, BlockPos.Mutable pos, BlockState state, ChunkGenerator generator, AbstractChunkProvider provider) {
        // I think the real size of the balls is mSize/8, but the original code was difficult to understand.
        // Overall there will be less GT stones since they aren't spheres any more. /16 since this code uses it as a radius.
        int realSize = size / 16;
        int windowWidth = realSize / 16 + 1; // Width of chunks to check for a potential stoneseed
        // Check stone seeds to see if they have been added
        for (int chunkX = passedX / 16 - windowWidth; chunkX < passedX / 16 + windowWidth + 1; chunkX++) {
            for (int chunkZ = passedZ / 16 - windowWidth; chunkZ < passedZ / 16 + windowWidth + 1; chunkZ++) {
                //compute hash for dimension and position
                long hash = (world.getDimension().getType().getId() & 0xffL) << 56 | ((long) chunkX & 0x000000000fffffffL) << 28 | (long) chunkZ & 0x000000000fffffffL;
                if (!CHECKED_SEEDS.contains(hash) && (probability <= 1 || rand.nextInt(probability) == 0)) CHECKED_SEEDS.add(hash);
                if (CHECKED_SEEDS.contains(hash)) {
                    int x = chunkX * 16;
                    int z = chunkZ * 16;
                    rand.setSeed(world.getSeed() ^ ((world.getDimension().getType().getId() & 0xffL) << 56 | ((long) x & 0x000000000fffffffL) << 28 | (long) z & 0x000000000fffffffL) + Math.abs(0/*mBlockMeta*/) + Math.abs(size) + (block.getType().getId().equals("granite_red") || block.getType().getId().equals("granite_black") ? 32768 : 0));  //Don't judge me
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

                        //pos = new BlockPos.MutableBlockPos(passedX + 8, tMinY, passedZ + 8);
                        //state = world.getBlockState(pos);
                        //if (state.getBlock().isAir(state, world, pos)) {
                        //if (Ref.debugStones) GregTech.LOGGER.info(id + " tX=" + tX + " tY=" + tY + " tZ=" + tZ + " realSize=" + realSize + " xSize=" + realSize/xSize + " ySize=" + realSize/ySize + " zSize=" + realSize/zSize + " tMinY=" + tMinY + " tMaxY=" + tMaxY + " - Skipped because first requesting chunk would not contain this stone");
                        //long hash = (world.provider.getDimension() & 0xffL) << 56 | ((long) x & 0x000000000fffffffL) << 28 | (long) z & 0x000000000fffffffL;
                        //CHECKED_SEEDS.put(hash, false);
                        //return;
                        //}

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
                                            WorldGenHelper.setOre(world, pos, state, ((BlockOre) state.getBlock()).getMaterial(), ((BlockOre) state.getBlock()).getOreType());
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