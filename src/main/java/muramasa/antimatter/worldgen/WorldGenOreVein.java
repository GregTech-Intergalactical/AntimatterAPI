package muramasa.antimatter.worldgen;

import com.google.gson.annotations.Expose;
import com.google.gson.internal.LinkedTreeMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.Configs;
import muramasa.antimatter.Ref;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.materials.MaterialType;
import muramasa.antimatter.util.Utils;
import muramasa.antimatter.util.XSTR;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import java.util.List;

public class WorldGenOreVein extends WorldGenBase {

    private static final int WRONG_BIOME = 0;
    private static final int WRONG_DIMENSION = 1;
    private static final int NO_ORE_IN_BOTTOM_LAYER = 2;
    private static final int NO_OVERLAP = 3;
    private static final int ORE_PLACED = 4;
    private static final int NO_OVERLAP_AIR_BLOCK = 5;

    static int TOTAL_WEIGHT;

    static Long2ObjectOpenHashMap<WorldGenOreVein> VALID_VEINS = new Long2ObjectOpenHashMap<>();
    private static final WorldGenOreVein noOresInVein = new WorldGenOreVein("NoOresInVein", 0, 255, 0, 255, 16, null, null, null, null);
    private Material[] materials;
    @Expose private String primary, secondary, between, sporadic;
    @Expose private int minY, maxY, weight, density, size;

    private int primaryHash; //TODO remove

    public WorldGenOreVein(String id, int minY, int maxY, int weight, int density, int size, Material primary, Material secondary, Material between, Material sporadic, int... dimensions) {
        super(id, dimensions);
        this.minY = minY;
        this.maxY = maxY;
        this.weight = weight;
        this.density = density;
        this.size = size;
        this.materials = new Material[] {primary, secondary, between, sporadic};
        if (primary != null) {
            this.primary = primary.getId();
            this.secondary = secondary.getId();
            this.between = between.getId();
            this.sporadic = sporadic.getId();
        }
    }

    @Override
    public WorldGenBase onDataOverride(LinkedTreeMap dataMap) {
        super.onDataOverride(dataMap);
        if (dataMap.containsKey("primary")) primary = Utils.parseString(dataMap.get("primary"), primary);
        if (dataMap.containsKey("secondary")) secondary = Utils.parseString(dataMap.get("secondary"), secondary);
        if (dataMap.containsKey("between")) between = Utils.parseString(dataMap.get("between"), between);
        if (dataMap.containsKey("sporadic")) sporadic = Utils.parseString(dataMap.get("sporadic"), sporadic);
        if (dataMap.containsKey("minY")) minY = Utils.parseInt(dataMap.get("minY"), minY);
        if (dataMap.containsKey("maxY")) maxY = Utils.parseInt(dataMap.get("maxY"), maxY);
        if (dataMap.containsKey("weight")) weight = Utils.parseInt(dataMap.get("weight"), weight);
        if (dataMap.containsKey("density")) density = Utils.parseInt(dataMap.get("density"), density);
        if (dataMap.containsKey("size")) size = Utils.parseInt(dataMap.get("size"), size);
        return this;
    }

    @Override
    public WorldGenBase build() {
        super.build();

        materials = new Material[] {Material.get(primary), Material.get(secondary), Material.get(between), Material.get(sporadic)};
        if (materials[0] == null || !materials[0].has(MaterialType.ORE)) throw new IllegalArgumentException("WorldGenOreVein - " + getId() + ": " + primary + " material either doesn't exist or doesn't have the ORE tag");
        if (materials[0] == null || !materials[0].has(MaterialType.ORE)) throw new IllegalArgumentException("WorldGenOreVein - " + getId() + ": " + secondary + " material either doesn't exist or doesn't have the ORE tag");
        if (materials[0] == null || !materials[0].has(MaterialType.ORE)) throw new IllegalArgumentException("WorldGenOreVein - " + getId() + ": " + between + " material either doesn't exist or doesn't have the ORE tag");
        if (materials[0] == null || !materials[0].has(MaterialType.ORE)) throw new IllegalArgumentException("WorldGenOreVein - " + getId() + ": " + sporadic + " material either doesn't exist or doesn't have the ORE tag");

        if (Configs.WORLD.ORE_VEIN_SMALL_ORE_MARKERS) {
            if (materials[0] == null || !materials[0].has(MaterialType.ORE_SMALL)) throw new IllegalArgumentException("WorldGenOreVein - " + getId() + ": " + primary + " material either doesn't exist or doesn't have the ORE_SMALL tag");
            if (materials[0] == null || !materials[0].has(MaterialType.ORE_SMALL)) throw new IllegalArgumentException("WorldGenOreVein - " + getId() + ": " + secondary + " material either doesn't exist or doesn't have the ORE_SMALL tag");
            if (materials[0] == null || !materials[0].has(MaterialType.ORE_SMALL)) throw new IllegalArgumentException("WorldGenOreVein - " + getId() + ": " + between + " material either doesn't exist or doesn't have the ORE_SMALL tag");
            if (materials[0] == null || !materials[0].has(MaterialType.ORE_SMALL)) throw new IllegalArgumentException("WorldGenOreVein - " + getId() + ": " + sporadic + " material either doesn't exist or doesn't have the ORE_SMALL tag");
        }

        TOTAL_WEIGHT += weight;
        primaryHash = materials[0].getHash(); //TODO remove

        return this;
    }

    public Material getMaterial(int i) {
        return materials[i];
    }

    int getWeight() {
        return weight;
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
    public static void generate(IWorld world, int chunkX, int chunkZ, int oreSeedX, int oreSeedZ) {
        List<WorldGenOreVein> veins = AntimatterWorldGenerator.getVeins(world.getDimension().getType().getId());
        if (veins == null || veins.size() == 0)
            return;
        BlockPos.Mutable pos = new BlockPos.Mutable();

        // Explanation of oreveinseed implementation.
        // (long)this.world.getSeed()<<16)    Deep Dark does two oregen passes, one with getSeed set to +1 the original world seed.  This pushes that +1 off the low bits of oreSeedZ, so that the hashes are far apart for the two passes.
        // ((this.world.provider.getDimension() & 0xffL)<<56)    Puts the dimension in the top bits of the hash, to make sure to get unique hashes per dimension
        // ((long)oreSeedX & 0x000000000fffffffL) << 28)    Puts the chunk X in the bits 29-55. Cuts off the top few bits of the chunk so we have bits for dimension.
        // ((long)oreSeedZ & 0x000000000fffffffL))    Puts the chunk Z in the bits 0-27. Cuts off the top few bits of the chunk so we have bits for dimension.
        long oreVeinSeed = world.getSeed() << 16 ^ ((world.getDimension().getType().getId() & 0xffL) << 56 | ((long) oreSeedX & 0x000000000fffffffL) << 28 | (long) oreSeedZ & 0x000000000fffffffL); // Use an RNG that is identical every time it is called for this oreseed.
        XSTR oreVeinRNG = new XSTR(oreVeinSeed);
        int oreVeinPercentageRoll = oreVeinRNG.nextInt(100); // Roll the dice, see if we get an orevein here at all
        if (Ref.debugOreVein)
            Antimatter.LOGGER.info("Finding oreveins for oreVeinSeed="+ oreVeinSeed + " chunkX="+ chunkX + " chunkZ="+ chunkZ + " oreSeedX=" + oreSeedX + " oreSeedZ=" + oreSeedZ + " worldSeed=" + world.getSeed());

        // Search for a valid orevein for this dimension
        if (!VALID_VEINS.containsKey(oreVeinSeed)) {
            int veinCount = veins.size();
            if (oreVeinPercentageRoll < Configs.WORLD.ORE_VEIN_CHANCE && WorldGenOreVein.TOTAL_WEIGHT > 0 && veinCount > 0) {
                int placementAttempts = 0;
                boolean oreVeinFound = false;
                int i;

                for (i = 0; i < Configs.WORLD.ORE_VEIN_FIND_ATTEMPTS && !oreVeinFound && placementAttempts < Configs.WORLD.ORE_VEIN_PLACE_ATTEMPTS; i++) {
                    int tRandomWeight = oreVeinRNG.nextInt(WorldGenOreVein.TOTAL_WEIGHT);
                    for (WorldGenOreVein vein : veins) {
                        tRandomWeight -= vein.weight;
                        if (tRandomWeight <= 0) {
                            // Adjust the seed so that this vein has a series of unique random numbers.  Otherwise multiple attempts at this same oreseed will get the same offset and X/Z values. If an orevein failed, any orevein with the
                            // same minimum heights would fail as well.  This prevents that, giving each orevein a unique height each pass through here.
                            int placementResult = vein.generateChunkified(world, new XSTR(oreVeinSeed ^ vein.primaryHash/*vein.material[0].getInternalId()*/), chunkX * 16, chunkZ * 16, oreSeedX * 16, oreSeedZ * 16, pos);
                            switch (placementResult) {
                                case WorldGenOreVein.ORE_PLACED:
                                    if (Ref.debugOreVein)
                                        Antimatter.LOGGER.info("Added near oreVeinSeed=" + oreVeinSeed + " " + vein.getId() + " tries at oremix=" + i + " placementAttempts=" + placementAttempts + " dimension=" + world.getDimension());
                                    VALID_VEINS.put(oreVeinSeed, vein);
                                    oreVeinFound = true;
                                    break;
                                case WorldGenOreVein.NO_ORE_IN_BOTTOM_LAYER:
                                    placementAttempts++;
                                    break; // Should do retry in this case until out of chances
                                case WorldGenOreVein.NO_OVERLAP:
                                    if (Ref.debugOreVein)
                                        Antimatter.LOGGER.info("Added far oreVeinSeed=" + oreVeinSeed + " " + vein.getId() + " tries at oremix=" + i + " placementAttempts=" + placementAttempts + " dimension=" + world.getDimension());
                                    VALID_VEINS.put(oreVeinSeed, vein);
                                    oreVeinFound = true;
                                    break;
                                case WorldGenOreVein.NO_OVERLAP_AIR_BLOCK:
                                    if (Ref.debugOreVein)
                                        Antimatter.LOGGER.info("No overlap and air block in test spot=" + oreVeinSeed + " " + vein.getId() + " tries at oremix=" + i + " placementAttempts=" + placementAttempts + " dimension=" + world.getDimension());
                                    placementAttempts++;
                                    break; // Should do retry in this case until out of chances
                            }
                            break; // Try the next orevein
                        }
                    }
                }
                // Only add an empty orevein if unable to place a vein at the oreseed chunk.
                if (!oreVeinFound && chunkX == oreSeedX && chunkZ == oreSeedZ) {
                    if (Ref.debugOreVein)
                        Antimatter.LOGGER.info("Empty oreVeinSeed="+ oreVeinSeed + " chunkX="+ chunkX + " chunkZ="+ chunkZ + " oreSeedX="+ oreSeedX + " oreSeedZ="+ oreSeedZ + " tries at oremix=" + i + " placementAttempts=" + placementAttempts + " dimension=" + world.getDimension());
                    VALID_VEINS.put(oreVeinSeed, noOresInVein);
                }
            } else if (oreVeinPercentageRoll >= Configs.WORLD.ORE_VEIN_CHANCE) {
                if (Ref.debugOreVein)
                    Antimatter.LOGGER.info("Skipped oreVeinSeed="+ oreVeinSeed + " chunkX="+ chunkX + " chunkZ="+ chunkZ + " oreSeedX=" + oreSeedX + " oreSeedZ=" + oreSeedZ + " RNG=" + oreVeinPercentageRoll + " %=" + Configs.WORLD.ORE_VEIN_CHANCE+ " dimension=" + world.getDimension());
                VALID_VEINS.put(oreVeinSeed, noOresInVein);
            }
        } else {
            // oreseed is located in the previously processed table
            if (Ref.debugOreVein)
                Antimatter.LOGGER.info("Valid oreVeinSeed="+ oreVeinSeed + " VALID_VEINS.size()=" + VALID_VEINS.size() + " ");
            WorldGenOreVein vein = VALID_VEINS.get(oreVeinSeed);
            oreVeinRNG.setSeed(oreVeinSeed ^ vein.primaryHash/*vein.material[0].getInternalId()*/);  // Reset RNG to only be based on oreseed X/Z and type of vein
            int placementResult = vein.generateChunkified(world, oreVeinRNG, chunkX * 16, chunkZ * 16, oreSeedX * 16, oreSeedZ * 16, pos);
            switch (placementResult) {
                case WorldGenOreVein.NO_ORE_IN_BOTTOM_LAYER:
                    if (Ref.debugOreVein)
                        Antimatter.LOGGER.info(" No ore in bottom layer");
                    break;
                case WorldGenOreVein.NO_OVERLAP:
                    if (Ref.debugOreVein)
                        Antimatter.LOGGER.info(" No overlap");
                    break;
            }
        }
    }

    private int generateChunkified(IWorld world, XSTR rand, int chunkX, int chunkZ, int seedX, int seedZ, BlockPos.Mutable pos) {
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
        BlockState centerState = world.getBlockState(centerPos);
        //Block tBlock = world.getBlock(chunkX + 7, tMinY, chunkZ + 9);

        if (wX >= eX) {  //No overlap between orevein and this chunk exists in X
            if (centerState.getBlock().isReplaceableOreGen(centerState, world, centerPos, WorldGenHelper.ORE_PREDICATE)) {
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
            if (centerState.getBlock().isReplaceableOreGen(centerState, world, centerPos, WorldGenHelper.ORE_PREDICATE)) {
                return NO_OVERLAP; // Didn't reach, but could have placed. Save orevein for future use.
            } else {
                return NO_OVERLAP_AIR_BLOCK; // Didn't reach, but couldn't place in test spot anywys, try for another orevein
            }
        }

        if (Ref.debugOreVein)
            Antimatter.LOGGER.info("Trying Orevein:" + getId() + " Dimension=" + world.getDimension() + " chunkX="+chunkX/16+ " chunkZ="+chunkZ/16+ " oreseedX="+ seedX/16 + " oreseedZ="+ seedZ/16 + " cY="+tMinY);
        // Adjust the density down the more chunks we are away from the oreseed.  The 5 chunks surrounding the seed should always be max density due to truncation of Math.sqrt().
        int localDensity = Math.max(1, this.density / (int) Math.sqrt(2 + Math.pow(chunkX / 16 - seedX / 16, 2) + Math.pow(chunkZ / 16 - seedZ / 16, 2)));

        // To allow for early exit due to no ore placed in the bottom layer (probably because we are in the sky), unroll 1 pass through the loop
        // Now we do bottom-level-first oregen, and work our way upwards.
        // Layer -1 Secondary and Sporadic
        int level = tMinY - 1; //Dunno why, but the first layer is actually played one below tMinY.  Go figure.
        for (int tX = wX; tX < eX; tX++) {
            int placeX = Math.max(1, Math.max(MathHelper.abs(wXVein - tX), MathHelper.abs(eXVein - tX)) / localDensity);
            for (int tZ = nZ; tZ < sZ; tZ++) {
                int placeZ = Math.max(1, Math.max(MathHelper.abs(sZVein - tZ), MathHelper.abs(nZVein - tZ)) / localDensity);
                if (rand.nextInt(placeZ) == 0 || rand.nextInt(placeX) == 0) {
                    pos.setPos(tX, level, tZ);
                    if (WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[1], MaterialType.ORE))
                        placeCount[1]++;
                } else if (rand.nextInt(7) == 0 && (rand.nextInt(placeZ) == 0 || rand.nextInt(placeX) == 0)) {  // Sporadics are reduce by 1/7 to compensate
                    pos.setPos(tX, level, tZ);
                    if (WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[3], MaterialType.ORE))
                        placeCount[3]++;
                }
            }
        }
        if (placeCount[1] + placeCount[3] == 0) {
            if (Ref.debugOreVein)
                Antimatter.LOGGER.info(" No ore in bottom layer");
            return NO_ORE_IN_BOTTOM_LAYER;  // Exit early, didn't place anything in the bottom layer
        }
        // Layers 0 & 1 Secondary and Sporadic
        for (level = tMinY; level < tMinY + 2; level++) {
            for (int tX = wX; tX < eX; tX++) {
                int placeX = Math.max(1, Math.max(MathHelper.abs(wXVein - tX), MathHelper.abs(eXVein - tX)) / localDensity);
                for (int tZ = nZ; tZ < sZ; tZ++) {
                    int placeZ = Math.max(1, Math.max(MathHelper.abs(sZVein - tZ), MathHelper.abs(nZVein - tZ)) / localDensity);
                    if (rand.nextInt(placeZ) == 0 || rand.nextInt(placeX) == 0) {
                        pos.setPos(tX, level, tZ);
                        if (WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[1], MaterialType.ORE))
                            placeCount[1]++;
                    } else if (rand.nextInt(7) == 0 && (rand.nextInt(placeZ) == 0 || rand.nextInt(placeX) == 0)) {  // Sporadics are reduce by 1/7 to compensate
                        pos.setPos(tX, level, tZ);
                        if (WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[3], MaterialType.ORE))
                            placeCount[3]++;
                    }
                }
            }
        }
        // Layer 2 is Secondary, in-between, and sporadic
        for (int tX = wX; tX < eX; tX++) {
            int placeX = Math.max(1, Math.max(MathHelper.abs(wXVein - tX), MathHelper.abs(eXVein - tX)) / localDensity);
            for (int tZ = nZ; tZ < sZ; tZ++) {
                int placeZ = Math.max(1, Math.max(MathHelper.abs(sZVein - tZ), MathHelper.abs(nZVein - tZ)) / localDensity);
                if (rand.nextInt(2) == 0 && (rand.nextInt(placeZ) == 0 || rand.nextInt(placeX) == 0)) {  // Between are reduce by 1/2 to compensate
                    pos.setPos(tX, level, tZ);
                    if (WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[2], MaterialType.ORE))
                        placeCount[2]++;
                } else if (rand.nextInt(placeZ) == 0 || rand.nextInt(placeX) == 0) {
                    pos.setPos(tX, level, tZ);
                    if (WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[1], MaterialType.ORE))
                        placeCount[1]++;
                } else if (rand.nextInt(7) == 0 && (rand.nextInt(placeZ) == 0 || rand.nextInt(placeX) == 0)) {  // Sporadics are reduce by 1/7 to compensate
                    pos.setPos(tX, level, tZ);
                    if (WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[3], MaterialType.ORE))
                        placeCount[3]++;
                }
            }
        }
        level++; // Increment level to next layer
        // Layer 3 is In-between, and sporadic
        for (int tX = wX; tX < eX; tX++) {
            int placeX = Math.max(1, Math.max(MathHelper.abs(wXVein - tX), MathHelper.abs(eXVein - tX)) / localDensity);
            for (int tZ = nZ; tZ < sZ; tZ++) {
                int placeZ = Math.max(1, Math.max(MathHelper.abs(sZVein - tZ), MathHelper.abs(nZVein - tZ)) / localDensity);
                if (rand.nextInt(2) == 0 && (rand.nextInt(placeZ) == 0 || rand.nextInt(placeX) == 0)) {  // Between are reduce by 1/2 to compensate
                    pos.setPos(tX, level, tZ);
                    if (WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[2], MaterialType.ORE))
                        placeCount[2]++;
                } else if (rand.nextInt(7) == 0 && (rand.nextInt(placeZ) == 0 || rand.nextInt(placeX) == 0)) {  // Sporadics are reduce by 1/7 to compensate
                    pos.setPos(tX, level, tZ);
                    if (WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[3], MaterialType.ORE))
                        placeCount[3]++;
                }
            }
        }
        level++; // Increment level to next layer
        // Layer 4 is In-between, Primary and sporadic
        for (int tX = wX; tX < eX; tX++) {
            int placeX = Math.max(1, Math.max(MathHelper.abs(wXVein - tX), MathHelper.abs(eXVein - tX)) / localDensity);
            for (int tZ = nZ; tZ < sZ; tZ++) {
                int placeZ = Math.max(1, Math.max(MathHelper.abs(sZVein - tZ), MathHelper.abs(nZVein - tZ)) / localDensity);
                if (rand.nextInt(2) == 0 && (rand.nextInt(placeZ) == 0 || rand.nextInt(placeX) == 0)) {  // Between are reduce by 1/2 to compensate
                    pos.setPos(tX, level, tZ);
                    if (WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[2], MaterialType.ORE))
                        placeCount[2]++;
                } else if (rand.nextInt(placeZ) == 0 || rand.nextInt(placeX) == 0) {
                    pos.setPos(tX, level, tZ);
                    if (WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[0], MaterialType.ORE))
                        placeCount[1]++;
                } else if (rand.nextInt(7) == 0 && (rand.nextInt(placeZ) == 0 || rand.nextInt(placeX) == 0)) {  // Sporadics are reduce by 1/7 to compensate
                    pos.setPos(tX, level, tZ);
                    if (WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[3], MaterialType.ORE))
                        placeCount[3]++;
                }
            }
        }
        level++; // Increment level to next layer
        // Layer 5 is In-between, Primary and sporadic
        for (int tX = wX; tX < eX; tX++) {
            int placeX = Math.max(1, Math.max(MathHelper.abs(wXVein - tX), MathHelper.abs(eXVein - tX)) / localDensity);
            for (int tZ = nZ; tZ < sZ; tZ++) {
                int placeZ = Math.max(1, Math.max(MathHelper.abs(sZVein - tZ), MathHelper.abs(nZVein - tZ)) / localDensity);
                if (rand.nextInt(2) == 0 && (rand.nextInt(placeZ) == 0 || rand.nextInt(placeX) == 0)) {  // Between are reduce by 1/2 to compensate
                    pos.setPos(tX, level, tZ);
                    if (WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[2], MaterialType.ORE))
                        placeCount[2]++;
                } else if (rand.nextInt(placeZ) == 0 || rand.nextInt(placeX) == 0) {
                    pos.setPos(tX, level, tZ);
                    if (WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[0], MaterialType.ORE))
                        placeCount[1]++;
                } else if (rand.nextInt(7) == 0 && (rand.nextInt(placeZ) == 0 || rand.nextInt(placeX) == 0)) {  // Sporadics are reduce by 1/7 to compensate
                    pos.setPos(tX, level, tZ);
                    if (WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[3], MaterialType.ORE))
                        placeCount[3]++;
                }
            }
        }
        level++; // Increment level to next layer
        // Layer 6 is Primary and sporadic
        for (int tX = wX; tX < eX; tX++) {
            int placeX = Math.max(1, Math.max(MathHelper.abs(wXVein - tX), MathHelper.abs(eXVein - tX)) / localDensity);
            for (int tZ = nZ; tZ < sZ; tZ++) {
                int placeZ = Math.max(1, Math.max(MathHelper.abs(sZVein - tZ), MathHelper.abs(nZVein - tZ)) / localDensity);
                if (rand.nextInt(placeZ) == 0 || rand.nextInt(placeX) == 0) {
                    pos.setPos(tX, level, tZ);
                    if (WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[0], MaterialType.ORE))
                        placeCount[1]++;
                } else if (rand.nextInt(7) == 0 && (rand.nextInt(placeZ) == 0 || rand.nextInt(placeX) == 0)) {  // Sporadics are reduce by 1/7 to compensate
                    pos.setPos(tX, level, tZ);
                    if (WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[3], MaterialType.ORE)) placeCount[3]++;
                }
            }
        }
        level++; // Increment level to next layer
        // Layer 7 is Primary and sporadic
        for (int tX = wX; tX < eX; tX++) {
            int placeX = Math.max(1, Math.max(MathHelper.abs(wXVein - tX), MathHelper.abs(eXVein - tX)) / localDensity);
            for (int tZ = nZ; tZ < sZ; tZ++) {
                int placeZ = Math.max(1, Math.max(MathHelper.abs(sZVein - tZ), MathHelper.abs(nZVein - tZ)) / localDensity);
                if (rand.nextInt(placeZ) == 0 || rand.nextInt(placeX) == 0) {
                    pos.setPos(tX, level, tZ);
                    if (WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[0], MaterialType.ORE))
                        placeCount[1]++;
                } else if (rand.nextInt(7) == 0 && (rand.nextInt(placeZ) == 0 || rand.nextInt(placeX) == 0)) {  // Sporadics are reduce by 1/7 to compensate
                    pos.setPos(tX, level, tZ);
                    if (WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[3], MaterialType.ORE))
                        placeCount[3]++;
                }
            }
        }
        //Place small ores for the vein
        if (Configs.WORLD.ORE_VEIN_SMALL_ORE_MARKERS) {
            int nSmallOres = (eX - wX) * (sZ - nZ) * this.density / 10 * Configs.WORLD.ORE_VEIN_SMALL_ORE_MARKERS_MULTI;
            //Small ores are placed in the whole chunk in which the vein appears.
            for (int nSmallOresCount = 0; nSmallOresCount < nSmallOres; nSmallOresCount++) {
                int tX = rand.nextInt(16) + chunkX + 2;
                int tZ = rand.nextInt(16) + chunkZ + 2;
                int tY = rand.nextInt(160) + 10; // Y height can vary from 10 to 170 for small ores.
                pos.setPos(tX, tY, tZ);
                WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[0], MaterialType.ORE_SMALL);

                tX = rand.nextInt(16) + chunkX + 2;
                tZ = rand.nextInt(16) + chunkZ + 2;
                tY = rand.nextInt(160) + 10; // Y height can vary from 10 to 170 for small ores.
                pos.setPos(tX, tY, tZ);
                WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[1], MaterialType.ORE_SMALL);

                tX = rand.nextInt(16) + chunkX + 2;
                tZ = rand.nextInt(16) + chunkZ + 2;
                tY = rand.nextInt(160) + 10; // Y height can vary from 10 to 170 for small ores.
                pos.setPos(tX, tY, tZ);
                WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[2], MaterialType.ORE_SMALL);

                tX = rand.nextInt(16) + chunkX + 2;
                tZ = rand.nextInt(16) + chunkZ + 2;
                tY = rand.nextInt(190) + 10; // Y height can vary from 10 to 200 for small ores.
                pos.setPos(tX, tY, tZ);
                WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[3], MaterialType.ORE_SMALL);
            }
        }
        if (Ref.debugOreVein)
            Antimatter.LOGGER.info(" wXVein" + wXVein + " eXVein" + eXVein + " nZVein" + nZVein + " sZVein" + sZVein + " locDen=" + localDensity + " Den=" + this.density + " Sec="+placeCount[1]+ " Spo="+placeCount[3]+ " Bet="+placeCount[2]+ " Pri="+placeCount[0]);
        // Something (at least the bottom layer must have 1 block) must have been placed, return true
        return ORE_PLACED;
    }
}
