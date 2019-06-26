package muramasa.gtu.api.worldgen;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.blocks.BlockOre;
import muramasa.gtu.api.blocks.BlockOreSmall;
import muramasa.gtu.api.data.StoneType;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialTag;
import muramasa.gtu.api.util.XSTR;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;

import java.util.ArrayList;
import java.util.List;

public class WorldGenOreLayer extends WorldGenBase {

    public static final int WRONG_BIOME = 0;
    public static final int WRONG_DIMENSION = 1;
    public static final int NO_ORE_IN_BOTTOM_LAYER = 2;
    public static final int NO_OVERLAP = 3;
    public static final int ORE_PLACED = 4;
    public static final int NO_OVERLAP_AIR_BLOCK = 5;

    public static Int2ObjectArrayMap<List<WorldGenOreLayer>> LAYERS = new Int2ObjectArrayMap<>();

    public static int TOTAL_WEIGHT;

    static {
        LAYERS.put(0, new ArrayList<>());
        LAYERS.put(-1, new ArrayList<>());
        LAYERS.put(1, new ArrayList<>());
    }

    public int minY, maxY, weight, density, size;
    public IBlockState primary, secondary, between, sporadic;
    public IBlockState primarySmall, secondarySmall, betweenSmall, sporadicSmall;
    public IBlockState[] states;
    public Material[] materials;

    public WorldGenOreLayer(String id, int minY, int maxY, int weight, int density, int size, Material primary, Material secondary, Material between, Material sporadic, MaterialTag... tags) {
        super(id, tags);
        this.minY = minY;
        this.maxY = maxY;
        this.weight = weight;
        this.density = density;
        this.size = size;

        this.primary = GregTechAPI.get(BlockOre.class, primary.getId()).get(StoneType.STONE);
        this.secondary = GregTechAPI.get(BlockOre.class, secondary.getId()).get(StoneType.STONE);
        this.between = GregTechAPI.get(BlockOre.class, between.getId()).get(StoneType.STONE);
        this.sporadic = GregTechAPI.get(BlockOre.class, sporadic.getId()).get(StoneType.STONE);

        this.primarySmall = GregTechAPI.get(BlockOreSmall.class, "small_" + primary.getId()).get(StoneType.STONE);
        this.secondarySmall = GregTechAPI.get(BlockOreSmall.class, "small_" + secondary.getId()).get(StoneType.STONE);
        this.betweenSmall = GregTechAPI.get(BlockOreSmall.class, "small_" + between.getId()).get(StoneType.STONE);
        this.sporadicSmall = GregTechAPI.get(BlockOreSmall.class, "small_" + sporadic.getId()).get(StoneType.STONE);

        this.states = new IBlockState[]{this.primary, this.secondary, this.between, this.sporadic};
        this.materials = new Material[]{primary, secondary, between, secondary};
        TOTAL_WEIGHT += weight;
        if (dims.contains(MaterialTag.OVERWORLD)) LAYERS.get(0).add(this);
        if (dims.contains(MaterialTag.NETHER)) LAYERS.get(-1).add(this);
        if (dims.contains(MaterialTag.END)) LAYERS.get(1).add(this);
    }

    public int generateChunkified(World world, XSTR rand, int chunkX, int chunkZ, int seedX, int seedZ, IChunkGenerator generator, IChunkProvider provider) {
        int[] placeCount = new int[4];

        int tMinY = minY + rand.nextInt(maxY - minY - 5);
        // Determine West/East ends of orevein
        int wXVein = seedX - rand.nextInt(size);        // West side
        int eXVein = seedX + 16 + rand.nextInt(size);
        // Limit Orevein to only blocks present in current chunk
        int wX = Math.max(wXVein, chunkX + 2);  // Bias placement by 2 blocks to prevent worldgen cascade.
        int eX = Math.min(eXVein, chunkX + 2 + 16);

        // Get a block at the center of the chunk and the bottom of the orevein.

        BlockPos centerPos = new BlockPos(chunkX + 7, tMinY, chunkZ + 9);
        IBlockState centerState = world.getBlockState(centerPos);
        //Block tBlock = world.getBlock(chunkX + 7, tMinY, chunkZ + 9);

        if (wX >= eX) {  //No overlap between orevein and this chunk exists in X
            if (centerState.getBlock().isReplaceableOreGen(centerState, world, centerPos, WorldGenHelper.STONE_PREDICATE) /*||
                centerState.getBlock().isReplaceableOreGen(world, chunkX+7, tMinY, chunkZ + 9, Blocks.netherrack) ||
                centerState.getBlock().isReplaceableOreGen(world, chunkX+7, tMinY, chunkZ + 9, Blocks.end_stone) ||
                centerState.getBlock().isReplaceableOreGen(world, chunkX+7, tMinY, chunkZ + 9, GregTech_API.sBlockGranites) ||
                centerState.getBlock().isReplaceableOreGen(world, chunkX+7, tMinY, chunkZ + 9, GregTech_API.sBlockStones)*/) {
                // Didn't reach, but could have placed. Save orevein for future use.
                return NO_OVERLAP;
            } else {
                // Didn't reach, but couldn't place in test spot anywys, try for another orevein
                return NO_OVERLAP_AIR_BLOCK;
            }
        }
        // Determine North/Sound ends of orevein
        int nZVein = seedZ - rand.nextInt(size);
        int sZVein = seedZ + 16 + rand.nextInt(size);

        int nZ = Math.max(nZVein, chunkZ + 2);  // Bias placement by 2 blocks to prevent worldgen cascade.
        int sZ = Math.min(sZVein, chunkZ + 2 + 16);
        if (nZ >= sZ) { //No overlap between orevein and this chunk exists in Z
            if (centerState.getBlock().isReplaceableOreGen(centerState, world, centerPos, WorldGenHelper.STONE_PREDICATE) /*||
                centerState.getBlock().isReplaceableOreGen(world, chunkX+7, tMinY, chunkZ + 9, Blocks.netherrack) ||
                centerState.getBlock().isReplaceableOreGen(world, chunkX+7, tMinY, chunkZ + 9, Blocks.end_stone) ||
                centerState.getBlock().isReplaceableOreGen(world, chunkX+7, tMinY, chunkZ + 9, GregTech_API.sBlockGranites) ||
                centerState.getBlock().isReplaceableOreGen(world, chunkX+7, tMinY, chunkZ + 9, GregTech_API.sBlockStones)*/) {
                // Didn't reach, but could have placed. Save orevein for future use.
                return NO_OVERLAP;
            } else {
                // Didn't reach, but couldn't place in test spot anywys, try for another orevein
                return NO_OVERLAP_AIR_BLOCK;
            }
        }

        //if (Ref.debugOreVein) GregTech.LOGGER.info("Trying Orevein:" + getId() + " Dimension=" + world.provider.getDimension() + " chunkX="+chunkX/16+ " chunkZ="+chunkZ/16+ " oreseedX="+ seedX/16 + " oreseedZ="+ seedZ/16 + " cY="+tMinY);
        // Adjust the density down the more chunks we are away from the oreseed.  The 5 chunks surrounding the seed should always be max density due to truncation of Math.sqrt().
        int localDensity = Math.max(1, this.density / ((int) Math.sqrt(2 + Math.pow(chunkX / 16 - seedX / 16, 2) + Math.pow(chunkZ / 16 - seedZ / 16, 2))));

        // To allow for early exit due to no ore placed in the bottom layer (probably because we are in the sky), unroll 1 pass through the loop
        // Now we do bottom-level-first oregen, and work our way upwards.
        // Layer -1 Secondary and Sporadic
        int level = tMinY - 1; //Dunno why, but the first layer is actually played one below tMinY.  Go figure.
        for (int tX = wX; tX < eX; tX++) {
            int placeX = Math.max(1, Math.max(MathHelper.abs(wXVein - tX), MathHelper.abs(eXVein - tX)) / localDensity);
            for (int tZ = nZ; tZ < sZ; tZ++) {
                int placeZ = Math.max(1, Math.max(MathHelper.abs(sZVein - tZ), MathHelper.abs(nZVein - tZ)) / localDensity);
                if (((rand.nextInt(placeZ) == 0) || (rand.nextInt(placeX) == 0))) {
                    if (WorldGenHelper.setStateOre(world, tX, level, tZ, secondary)) placeCount[1]++;
                } else if ((rand.nextInt(7) == 0) && ((rand.nextInt(placeZ) == 0) || (rand.nextInt(placeX) == 0))) {  // Sporadics are reduce by 1/7 to compensate
                    if (WorldGenHelper.setStateOre(world, tX, level, tZ, sporadic)) placeCount[3]++;
                }
            }
        }
        if ((placeCount[1] + placeCount[3]) == 0) {
            //if (Ref.debugOreVein) GregTech.LOGGER.info(" No ore in bottom layer");
            return NO_ORE_IN_BOTTOM_LAYER;  // Exit early, didn't place anything in the bottom layer
        }
        // Layers 0 & 1 Secondary and Sporadic
        for (level = tMinY; level < (tMinY + 2); level++) {
            for (int tX = wX; tX < eX; tX++) {
                int placeX = Math.max(1, Math.max(MathHelper.abs(wXVein - tX), MathHelper.abs(eXVein - tX)) / localDensity);
                for (int tZ = nZ; tZ < sZ; tZ++) {
                    int placeZ = Math.max(1, Math.max(MathHelper.abs(sZVein - tZ), MathHelper.abs(nZVein - tZ)) / localDensity);
                    if (((rand.nextInt(placeZ) == 0) || (rand.nextInt(placeX) == 0))) {
                        if (WorldGenHelper.setStateOre(world, tX, level, tZ, secondary)) placeCount[1]++;
                    } else if ((rand.nextInt(7) == 0) && ((rand.nextInt(placeZ) == 0) || (rand.nextInt(placeX) == 0))) {  // Sporadics are reduce by 1/7 to compensate
                        if (WorldGenHelper.setStateOre(world, tX, level, tZ, sporadic)) placeCount[3]++;
                    }
                }
            }
        }
        // Layer 2 is Secondary, in-between, and sporadic
        for (int tX = wX; tX < eX; tX++) {
            int placeX = Math.max(1, Math.max(MathHelper.abs(wXVein - tX), MathHelper.abs(eXVein - tX)) / localDensity);
            for (int tZ = nZ; tZ < sZ; tZ++) {
                int placeZ = Math.max(1, Math.max(MathHelper.abs(sZVein - tZ), MathHelper.abs(nZVein - tZ)) / localDensity);
                if ((rand.nextInt(2) == 0) && ((rand.nextInt(placeZ) == 0) || (rand.nextInt(placeX) == 0))) {  // Between are reduce by 1/2 to compensate
                    if (WorldGenHelper.setStateOre(world, tX, level, tZ, between)) placeCount[2]++;
                } else if (((rand.nextInt(placeZ) == 0) || (rand.nextInt(placeX) == 0))) {
                    if (WorldGenHelper.setStateOre(world, tX, level, tZ, secondary)) placeCount[1]++;
                } else if ((rand.nextInt(7) == 0) && ((rand.nextInt(placeZ) == 0) || (rand.nextInt(placeX) == 0))) {  // Sporadics are reduce by 1/7 to compensate
                    if (WorldGenHelper.setStateOre(world, tX, level, tZ, sporadic)) placeCount[3]++;
                }
            }
        }
        level++; // Increment level to next layer
        // Layer 3 is In-between, and sporadic
        for (int tX = wX; tX < eX; tX++) {
            int placeX = Math.max(1, Math.max(MathHelper.abs(wXVein - tX), MathHelper.abs(eXVein - tX)) / localDensity);
            for (int tZ = nZ; tZ < sZ; tZ++) {
                int placeZ = Math.max(1, Math.max(MathHelper.abs(sZVein - tZ), MathHelper.abs(nZVein - tZ)) / localDensity);
                if ((rand.nextInt(2) == 0) && ((rand.nextInt(placeZ) == 0) || (rand.nextInt(placeX) == 0))) {  // Between are reduce by 1/2 to compensate
                    if (WorldGenHelper.setStateOre(world, tX, level, tZ, between)) placeCount[2]++;
                } else if ((rand.nextInt(7) == 0) && ((rand.nextInt(placeZ) == 0) || (rand.nextInt(placeX) == 0))) {  // Sporadics are reduce by 1/7 to compensate
                    if (WorldGenHelper.setStateOre(world, tX, level, tZ, sporadic)) placeCount[3]++;
                }
            }
        }
        level++; // Increment level to next layer
        // Layer 4 is In-between, Primary and sporadic
        for (int tX = wX; tX < eX; tX++) {
            int placeX = Math.max(1, Math.max(MathHelper.abs(wXVein - tX), MathHelper.abs(eXVein - tX)) / localDensity);
            for (int tZ = nZ; tZ < sZ; tZ++) {
                int placeZ = Math.max(1, Math.max(MathHelper.abs(sZVein - tZ), MathHelper.abs(nZVein - tZ)) / localDensity);
                if ((rand.nextInt(2) == 0) && ((rand.nextInt(placeZ) == 0) || (rand.nextInt(placeX) == 0))) {  // Between are reduce by 1/2 to compensate
                    if (WorldGenHelper.setStateOre(world, tX, level, tZ, between)) placeCount[2]++;
                } else if (((rand.nextInt(placeZ) == 0) || (rand.nextInt(placeX) == 0))) {
                    if (WorldGenHelper.setStateOre(world, tX, level, tZ, primary)) placeCount[1]++;
                } else if ((rand.nextInt(7) == 0) && ((rand.nextInt(placeZ) == 0) || (rand.nextInt(placeX) == 0))) {  // Sporadics are reduce by 1/7 to compensate
                    if (WorldGenHelper.setStateOre(world, tX, level, tZ, sporadic)) placeCount[3]++;
                }
            }
        }
        level++; // Increment level to next layer
        // Layer 5 is In-between, Primary and sporadic
        for (int tX = wX; tX < eX; tX++) {
            int placeX = Math.max(1, Math.max(MathHelper.abs(wXVein - tX), MathHelper.abs(eXVein - tX)) / localDensity);
            for (int tZ = nZ; tZ < sZ; tZ++) {
                int placeZ = Math.max(1, Math.max(MathHelper.abs(sZVein - tZ), MathHelper.abs(nZVein - tZ)) / localDensity);
                if ((rand.nextInt(2) == 0) && ((rand.nextInt(placeZ) == 0) || (rand.nextInt(placeX) == 0))) {  // Between are reduce by 1/2 to compensate
                    if (WorldGenHelper.setStateOre(world, tX, level, tZ, between)) placeCount[2]++;
                } else if (((rand.nextInt(placeZ) == 0) || (rand.nextInt(placeX) == 0))) {
                    if (WorldGenHelper.setStateOre(world, tX, level, tZ, primary)) placeCount[1]++;
                } else if ((rand.nextInt(7) == 0) && ((rand.nextInt(placeZ) == 0) || (rand.nextInt(placeX) == 0))) {  // Sporadics are reduce by 1/7 to compensate
                    if (WorldGenHelper.setStateOre(world, tX, level, tZ, sporadic)) placeCount[3]++;
                }
            }
        }
        level++; // Increment level to next layer
        // Layer 6 is Primary and sporadic
        for (int tX = wX; tX < eX; tX++) {
            int placeX = Math.max(1, Math.max(MathHelper.abs(wXVein - tX), MathHelper.abs(eXVein - tX)) / localDensity);
            for (int tZ = nZ; tZ < sZ; tZ++) {
                int placeZ = Math.max(1, Math.max(MathHelper.abs(sZVein - tZ), MathHelper.abs(nZVein - tZ)) / localDensity);
                if (((rand.nextInt(placeZ) == 0) || (rand.nextInt(placeX) == 0))) {
                    if (WorldGenHelper.setStateOre(world, tX, level, tZ, primary)) placeCount[1]++;
                } else if ((rand.nextInt(7) == 0) && ((rand.nextInt(placeZ) == 0) || (rand.nextInt(placeX) == 0))) {  // Sporadics are reduce by 1/7 to compensate
                    if (WorldGenHelper.setStateOre(world, tX, level, tZ, sporadic)) placeCount[3]++;
                }
            }
        }
        level++; // Increment level to next layer
        // Layer 7 is Primary and sporadic
        for (int tX = wX; tX < eX; tX++) {
            int placeX = Math.max(1, Math.max(MathHelper.abs(wXVein - tX), MathHelper.abs(eXVein - tX)) / localDensity);
            for (int tZ = nZ; tZ < sZ; tZ++) {
                int placeZ = Math.max(1, Math.max(MathHelper.abs(sZVein - tZ), MathHelper.abs(nZVein - tZ)) / localDensity);
                if (((rand.nextInt(placeZ) == 0) || (rand.nextInt(placeX) == 0))) {
                    if (WorldGenHelper.setStateOre(world, tX, level, tZ, primary)) placeCount[1]++;
                } else if ((rand.nextInt(7) == 0) && ((rand.nextInt(placeZ) == 0) || (rand.nextInt(placeX) == 0))) {  // Sporadics are reduce by 1/7 to compensate
                    if (WorldGenHelper.setStateOre(world, tX, level, tZ, sporadic)) placeCount[3]++;
                }
            }
        }
        //Place small ores for the vein
        if (Ref.oreveinPlacerOres) {
            int nSmallOres = (eX - wX) * (sZ - nZ) * this.density / 10 * Ref.oreveinPlacerOresMultiplier;
            //Small ores are placed in the whole chunk in which the vein appears.
            for (int nSmallOresCount = 0; nSmallOresCount < nSmallOres; nSmallOresCount++) {
                int tX = rand.nextInt(16) + chunkX + 2;
                int tZ = rand.nextInt(16) + chunkZ + 2;
                int tY = rand.nextInt(160) + 10; // Y height can vary from 10 to 170 for small ores.
                WorldGenHelper.setStateOre(world, tX, tY, tZ, primarySmall);
                tX = rand.nextInt(16) + chunkX + 2;
                tZ = rand.nextInt(16) + chunkZ + 2;
                tY = rand.nextInt(160) + 10; // Y height can vary from 10 to 170 for small ores.
                WorldGenHelper.setStateOre(world, tX, tY, tZ, secondarySmall);
                tX = rand.nextInt(16) + chunkX + 2;
                tZ = rand.nextInt(16) + chunkZ + 2;
                tY = rand.nextInt(160) + 10; // Y height can vary from 10 to 170 for small ores.
                WorldGenHelper.setStateOre(world, tX, tY, tZ, betweenSmall);
                tX = rand.nextInt(16) + chunkX + 2;
                tZ = rand.nextInt(16) + chunkZ + 2;
                tY = rand.nextInt(190) + 10; // Y height can vary from 10 to 200 for small ores.
                WorldGenHelper.setStateOre(world, tX, tY, tZ, sporadicSmall);
            }
        }
        //if (Ref.debugOreVein) GregTech.LOGGER.info(" wXVein" + wXVein + " eXVein" + eXVein + " nZVein" + nZVein + " sZVein" + sZVein + " locDen=" + localDensity + " Den=" + this.density + " Sec="+placeCount[1]+ " Spo="+placeCount[3]+ " Bet="+placeCount[2]+ " Pri="+placeCount[0]);
        // Something (at least the bottom layer must have 1 block) must have been placed, return true
        return ORE_PLACED;
    }
}
