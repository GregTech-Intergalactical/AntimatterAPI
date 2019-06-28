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
import muramasa.gtu.loaders.WorldGenLoader;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class WorldGenOreLayer extends WorldGenBase {

    public static final int WRONG_BIOME = 0;
    public static final int WRONG_DIMENSION = 1;
    public static final int NO_ORE_IN_BOTTOM_LAYER = 2;
    public static final int NO_OVERLAP = 3;
    public static final int ORE_PLACED = 4;
    public static final int NO_OVERLAP_AIR_BLOCK = 5;
    public static Int2ObjectArrayMap<List<WorldGenOreLayer>> ALL = new Int2ObjectArrayMap<>();
    public static int TOTAL_WEIGHT;

    private static Hashtable<Long, WorldGenOreLayer> VALID_VEINS = new Hashtable<>(1024);

    static {
        ALL.put(0, new ArrayList<>());
        ALL.put(-1, new ArrayList<>());
        ALL.put(1, new ArrayList<>());
    }

    public int minY, maxY, weight, density, size;
    public IBlockState primary, secondary, between, sporadic;
    public IBlockState primarySmall, secondarySmall, betweenSmall, sporadicSmall;
    public Material[] materials;

    public WorldGenOreLayer(String id, int minY, int maxY, int weight, int density, int size, Material primary, Material secondary, Material between, Material sporadic, MaterialTag... tags) {
        super(id, tags);
        this.minY = minY;
        this.maxY = maxY;
        this.weight = weight;
        this.density = density;
        this.size = size;

        BlockOre blockPrimary = GregTechAPI.get(BlockOre.class, primary.getId());
        BlockOre blockSecondary = GregTechAPI.get(BlockOre.class, secondary.getId());
        BlockOre blockBetween = GregTechAPI.get(BlockOre.class, between.getId());
        BlockOre blockSporadic = GregTechAPI.get(BlockOre.class, sporadic.getId());

        if (blockPrimary == null)
            throw new IllegalArgumentException(primary.getId() + " in WorldGenOreLayer: " + id + " does not have the ORE tag");
        if (blockSecondary == null)
            throw new IllegalArgumentException(secondary.getId() + " in WorldGenOreLayer: " + id + " does not have the ORE tag");
        if (blockBetween == null)
            throw new IllegalArgumentException(between.getId() + " in WorldGenOreLayer: " + id + " does not have the ORE tag");
        if (blockSporadic == null)
            throw new IllegalArgumentException(sporadic.getId() + " in WorldGenOreLayer: " + id + " does not have the ORE tag");

        this.primary = GregTechAPI.get(BlockOre.class, primary.getId()).get(StoneType.STONE);
        this.secondary = GregTechAPI.get(BlockOre.class, secondary.getId()).get(StoneType.STONE);
        this.between = GregTechAPI.get(BlockOre.class, between.getId()).get(StoneType.STONE);
        this.sporadic = GregTechAPI.get(BlockOre.class, sporadic.getId()).get(StoneType.STONE);

        this.primarySmall = GregTechAPI.get(BlockOreSmall.class, "small_" + primary.getId()).get(StoneType.STONE);
        this.secondarySmall = GregTechAPI.get(BlockOreSmall.class, "small_" + secondary.getId()).get(StoneType.STONE);
        this.betweenSmall = GregTechAPI.get(BlockOreSmall.class, "small_" + between.getId()).get(StoneType.STONE);
        this.sporadicSmall = GregTechAPI.get(BlockOreSmall.class, "small_" + sporadic.getId()).get(StoneType.STONE);

        this.materials = new Material[]{primary, secondary, between, secondary};
        TOTAL_WEIGHT += weight;
        if (dims.contains(MaterialTag.OVERWORLD)) ALL.get(0).add(this);
        if (dims.contains(MaterialTag.NETHER)) ALL.get(-1).add(this);
        if (dims.contains(MaterialTag.END)) ALL.get(1).add(this);
    }

    // How to evaluate oregen distribution
    // - Enable Ref.debugOreVeins
    // - Fly around for a while, or teleport jumping ~320 blocks at a time, with
    //   a 15-30s pause for worldgen to catch up
    // - Do this across a large area, at least 2000x2000 blocks for good numbers
    // - Open logs\gregtech.log
    // - Using notepad++, do a Search | Find  - enter "Added" for the search term
    // - Select Find All In Current Document
    // - In the Search window, right-click and Select All
    // - Copy and paste to a new file
    // - Delete extraneous stuff at top, and blank line at bottom.  Line count is
    //   # of total oreveins
    // - For simple spot checks, use Find All in Current Document for specific
    //   oremixes, ie ore.mix.diamond, to check how many appear in the list.
    // - For more complex work, import file into Excel, and sort based on oremix
    //   column.  Drag select the oremix names, in the bottom right will be how many
    //   entries to add in a seperate tab to calculate %ages.
    //
    // When using the ore weights, discount or remove the high altitude veins since
    // their high weight are offset by their rareness. I usually just use zero for them.
    // Actual spawn rates will vary based upon the average height of the stone layers
    // in the dimension. For example veins that range above and below the average height
    // will be less, and veins that are completely above the average height will be much less.

    public static void worldGenFindVein(World world, int chunkX, int chunkZ, int oreSeedX, int oreSeedZ, IChunkGenerator generator, IChunkProvider provider) {
        // Explanation of oreveinseed implementation.
        // (long)this.world.getSeed()<<16)    Deep Dark does two oregen passes, one with getSeed set to +1 the original world seed.  This pushes that +1 off the low bits of oreSeedZ, so that the hashes are far apart for the two passes.
        // ((this.world.provider.getDimension() & 0xffL)<<56)    Puts the dimension in the top bits of the hash, to make sure to get unique hashes per dimension
        // ((long)oreSeedX & 0x000000000fffffffL) << 28)    Puts the chunk X in the bits 29-55. Cuts off the top few bits of the chunk so we have bits for dimension.
        // ((long)oreSeedZ & 0x000000000fffffffL))    Puts the chunk Z in the bits 0-27. Cuts off the top few bits of the chunk so we have bits for dimension.
        long oreVeinSeed = (world.getSeed() << 16) ^ (((world.provider.getDimension() & 0xffL) << 56) | (((long) oreSeedX & 0x000000000fffffffL) << 28) | ((long) oreSeedZ & 0x000000000fffffffL)); // Use an RNG that is identical every time it is called for this oreseed.
        XSTR oreVeinRNG = new XSTR(oreVeinSeed);
        int oreVeinPercentageRoll = oreVeinRNG.nextInt(100); // Roll the dice, see if we get an orevein here at all
        //if (Ref.debugOreVein) GregTech.LOGGER.info("Finding oreveins for oreVeinSeed="+ oreVeinSeed + " chunkX="+ this.chunkX + " chunkZ="+ this.chunkZ + " oreSeedX="+ oreSeedX + " oreSeedZ="+ oreSeedZ + " worldSeed="+this.world.getSeed());

        // Search for a valid orevein for this dimension
        if (!VALID_VEINS.containsKey(oreVeinSeed)) {
            if (oreVeinPercentageRoll < Ref.ORE_VEIN_CHANCE && WorldGenOreLayer.TOTAL_WEIGHT > 0 && WorldGenOreLayer.ALL.get(world.provider.getDimension()).size() > 0) {
                int placementAttempts = 0;
                boolean oreVeinFound = false;
                int i;

                for (i = 0; (i < Ref.ORE_VEIN_FIND_ATTEMPTS) && (!oreVeinFound) && (placementAttempts < Ref.ORE_VEIN_PLACE_ATTEMPTS); i++) {
                    int tRandomWeight = oreVeinRNG.nextInt(WorldGenOreLayer.TOTAL_WEIGHT);
                    for (WorldGenOreLayer layer : WorldGenOreLayer.ALL.get(world.provider.getDimension())) {
                        tRandomWeight -= layer.weight;
                        if (tRandomWeight <= 0) {
                            // Adjust the seed so that this layer has a series of unique random numbers.  Otherwise multiple attempts at this same oreseed will get the same offset and X/Z values. If an orevein failed, any orevein with the
                            // same minimum heights would fail as well.  This prevents that, giving each orevein a unique height each pass through here.
                            int placementResult = layer.generateChunkified(world, new XSTR(oreVeinSeed ^ (layer.materials[0].getInternalId())), chunkX * 16, chunkZ * 16, oreSeedX * 16, oreSeedZ * 16, generator, provider);
                            switch (placementResult) {
                                case WorldGenOreLayer.ORE_PLACED:
                                    //if (Ref.debugOreVein) GregTech.LOGGER.info("Added near oreVeinSeed=" + oreVeinSeed + " " + layer.getId() + " tries at oremix=" + i + " placementAttempts=" + placementAttempts + " dimension=" + world.provider.getDimension());
                                    VALID_VEINS.put(oreVeinSeed, layer);
                                    oreVeinFound = true;
                                    break;
                                case WorldGenOreLayer.NO_ORE_IN_BOTTOM_LAYER:
                                    placementAttempts++;
                                    break; // Should do retry in this case until out of chances
                                case WorldGenOreLayer.NO_OVERLAP:
                                    //if (Ref.debugOreVein) GregTech.LOGGER.info("Added far oreVeinSeed=" + oreVeinSeed + " " + layer.getId() + " tries at oremix=" + i + " placementAttempts=" + placementAttempts + " dimension=" + world.provider.getDimension());
                                    VALID_VEINS.put(oreVeinSeed, layer);
                                    oreVeinFound = true;
                                    break;
                                case WorldGenOreLayer.NO_OVERLAP_AIR_BLOCK:
                                    //if (Ref.debugOreVein) GregTech.LOGGER.info("No overlap and air block in test spot=" + oreVeinSeed + " " + layer.getId() + " tries at oremix=" + i + " placementAttempts=" + placementAttempts + " dimension=" + world.provider.getDimension());
                                    placementAttempts++;
                                    break; // Should do retry in this case until out of chances
                            }
                            break; // Try the next orevein
                        }
                    }
                }
                // Only add an empty orevein if unable to place a vein at the oreseed chunk.
                if ((!oreVeinFound) && (chunkX == oreSeedX) && (chunkZ == oreSeedZ)) {
                    //if (Ref.debugOreVein) GregTech.LOGGER.info("Empty oreVeinSeed="+ oreVeinSeed + " chunkX="+ this.chunkX + " chunkZ="+ this.chunkZ + " oreSeedX="+ oreSeedX + " oreSeedZ="+ oreSeedZ + " tries at oremix=" + i + " placementAttempts=" + placementAttempts + " dimension=" + world.provider.getDimension());
                    VALID_VEINS.put(oreVeinSeed, WorldGenLoader.noOresInVein);
                }
            } else if (oreVeinPercentageRoll >= Ref.ORE_VEIN_CHANCE) {
                //if (Ref.debugOreVein) GregTech.LOGGER.info("Skipped oreVeinSeed="+ oreVeinSeed + " chunkX="+ this.chunkX + " chunkZ="+ this.chunkZ + " oreSeedX=" + oreSeedX + " oreSeedZ=" + oreSeedZ + " RNG=" + oreVeinPercentageRoll + " %=" + Ref.ORE_VEIN_CHANCE+ " dimension=" + world.provider.getDimension());
                VALID_VEINS.put(oreVeinSeed, WorldGenLoader.noOresInVein);
            }
        } else {
            // oreseed is located in the previously processed table
            //if (Ref.debugOreVein) GregTech.LOGGER.info("Valid oreVeinSeed="+ oreVeinSeed + " VALID_VEINS.size()=" + VALID_VEINS.size() + " ");
            WorldGenOreLayer layer = VALID_VEINS.get(oreVeinSeed);
            oreVeinRNG.setSeed(oreVeinSeed ^ (layer.materials[0].getInternalId()));  // Reset RNG to only be based on oreseed X/Z and type of vein
            int placementResult = layer.generateChunkified(world, oreVeinRNG, chunkX * 16, chunkZ * 16, oreSeedX * 16, oreSeedZ * 16, generator, provider);
            switch (placementResult) {
                case WorldGenOreLayer.NO_ORE_IN_BOTTOM_LAYER:
                    //if (Ref.debugOreVein) GregTech.LOGGER.info(" No ore in bottom layer");
                    break;
                case WorldGenOreLayer.NO_OVERLAP:
                    //if (Ref.debugOreVein) GregTech.LOGGER.info(" No overlap");
                    break;
            }
        }
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
            if (centerState.getBlock().isReplaceableOreGen(centerState, world, centerPos, WorldGenHelper.STONE_PREDICATE)) {
                return NO_OVERLAP; // Didn't reach, but could have placed. Save orevein for future use.
            } else {
                return NO_OVERLAP_AIR_BLOCK;// Didn't reach, but couldn't place in test spot anywys, try for another orevein
            }
        }
        // Determine North/Sound ends of orevein
        int nZVein = seedZ - rand.nextInt(size);
        int sZVein = seedZ + 16 + rand.nextInt(size);

        int nZ = Math.max(nZVein, chunkZ + 2);  // Bias placement by 2 blocks to prevent worldgen cascade.
        int sZ = Math.min(sZVein, chunkZ + 2 + 16);
        if (nZ >= sZ) { //No overlap between orevein and this chunk exists in Z
            if (centerState.getBlock().isReplaceableOreGen(centerState, world, centerPos, WorldGenHelper.STONE_PREDICATE)) {
                return NO_OVERLAP; // Didn't reach, but could have placed. Save orevein for future use.
            } else {
                return NO_OVERLAP_AIR_BLOCK; // Didn't reach, but couldn't place in test spot anywys, try for another orevein
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
        if (Ref.ORE_VEIN_SMALL_ORE_MARKERS) {
            int nSmallOres = (eX - wX) * (sZ - nZ) * this.density / 10 * Ref.ORE_VEIN_SMALL_ORE_MARKERS_MULTI;
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
