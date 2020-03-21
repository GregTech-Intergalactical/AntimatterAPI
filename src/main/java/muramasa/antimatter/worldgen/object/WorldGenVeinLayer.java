package muramasa.antimatter.worldgen.object;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.Configs;
import muramasa.antimatter.Ref;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.util.XSTR;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import muramasa.antimatter.worldgen.VeinLayerResult;
import muramasa.antimatter.worldgen.WorldGenHelper;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;

import java.util.List;

import static muramasa.antimatter.worldgen.VeinLayerResult.*;

/**
 * Most of the WorldGenVeinLayer code is from the GTNewHorizons GT5 fork, refactored for 1.12 and somewhat optimised
 * Written in 1.7 by moronwmachinegun and mitchej123, adapted by Muramasa
 * **/
public class WorldGenVeinLayer extends WorldGenBase<WorldGenVeinLayer> {

    static int TOTAL_WEIGHT;
    public static Long2ObjectOpenHashMap<WorldGenVeinLayer> VALID_VEINS = new Long2ObjectOpenHashMap<>();

    private static final WorldGenVeinLayer NO_ORES_IN_VEIN = new WorldGenVeinLayer("NoOresInVein", 0, 255, 0, 255, 16, null, null, null, null) {
        @Override
        VeinLayerResult generateChunkified(IWorld world, XSTR rand, int posX, int posZ, int seedX, int seedZ) {
            return NO_ORES_VEIN;
        }
    };

    private Material[] materials;
    private String primary, secondary, between, sporadic;
    private int minY, maxY, weight, density, size;
    private int primaryHash;

    public WorldGenVeinLayer(String id, int minY, int maxY, int weight, int density, int size, Material primary, Material secondary, Material between, Material sporadic, int... dimensions) {
        super(id, WorldGenVeinLayer.class, dimensions);
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
    public WorldGenVeinLayer onDataOverride(JsonObject json) {
        super.onDataOverride(json);
        //if (json.has("primary")) primary = Utils.parseString(dataMap.get("primary"), primary);
        //if (json.has("secondary")) secondary = Utils.parseString(dataMap.get("secondary"), secondary);
        //if (json.has("between")) between = Utils.parseString(dataMap.get("between"), between);
        //if (json.has("sporadic")) sporadic = Utils.parseString(dataMap.get("sporadic"), sporadic);
        //if (json.has("minY")) minY = Utils.parseInt(dataMap.get("minY"), minY);
        //if (json.has("maxY")) maxY = Utils.parseInt(dataMap.get("maxY"), maxY);
        //if (json.has("weight")) weight = Utils.parseInt(dataMap.get("weight"), weight);
        //if (json.has("density")) density = Utils.parseInt(dataMap.get("density"), density);
        //if (json.has("size")) size = Utils.parseInt(dataMap.get("size"), size);
        build();
        return this;
    }

    @Override
    public WorldGenVeinLayer build() {
        super.build();

        materials = new Material[] {Material.get(primary), Material.get(secondary), Material.get(between), Material.get(sporadic)};
        if (materials[0] == null || !materials[0].has(MaterialType.ORE)) throw new IllegalArgumentException("WorldGenOreVein - " + getId() + ": " + primary + " material either doesn't exist or doesn't have the ORE tag");
        if (materials[1] == null || !materials[1].has(MaterialType.ORE)) throw new IllegalArgumentException("WorldGenOreVein - " + getId() + ": " + secondary + " material either doesn't exist or doesn't have the ORE tag");
        if (materials[2] == null || !materials[2].has(MaterialType.ORE)) throw new IllegalArgumentException("WorldGenOreVein - " + getId() + ": " + between + " material either doesn't exist or doesn't have the ORE tag");
        if (materials[3] == null || !materials[3].has(MaterialType.ORE)) throw new IllegalArgumentException("WorldGenOreVein - " + getId() + ": " + sporadic + " material either doesn't exist or doesn't have the ORE tag");

        if (Configs.WORLD.ORE_VEIN_SMALL_ORE_MARKERS) {
            if (materials[0] == null || !materials[0].has(MaterialType.ORE_SMALL)) throw new IllegalArgumentException("WorldGenOreVein - " + getId() + ": " + primary + " material either doesn't exist or doesn't have the ORE_SMALL tag");
            if (materials[1] == null || !materials[1].has(MaterialType.ORE_SMALL)) throw new IllegalArgumentException("WorldGenOreVein - " + getId() + ": " + secondary + " material either doesn't exist or doesn't have the ORE_SMALL tag");
            if (materials[2] == null || !materials[2].has(MaterialType.ORE_SMALL)) throw new IllegalArgumentException("WorldGenOreVein - " + getId() + ": " + between + " material either doesn't exist or doesn't have the ORE_SMALL tag");
            if (materials[3] == null || !materials[3].has(MaterialType.ORE_SMALL)) throw new IllegalArgumentException("WorldGenOreVein - " + getId() + ": " + sporadic + " material either doesn't exist or doesn't have the ORE_SMALL tag");
        }

        TOTAL_WEIGHT += weight;
        primaryHash = materials[0].getHash(); //TODO remove

        return this;
    }

    public Material getMaterial(int i) {
        return materials[i];
    }

    public int getWeight() {
        return weight;
    }

    public static int getTotalWeight() {
        return TOTAL_WEIGHT;
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
        List<WorldGenVeinLayer> veins = AntimatterWorldGenerator.all(WorldGenVeinLayer.class, world.getDimension().getType().getId());
        if (veins == null || veins.size() == 0)
            return;

        // Explanation of oreveinseed implementation.
        // (long)this.world.getSeed()<<16)    Deep Dark does two oregen passes, one with getSeed set to +1 the original world seed.  This pushes that +1 off the low bits of oreSeedZ, so that the hashes are far apart for the two passes.
        // ((this.world.provider.getDimension() & 0xffL)<<56)    Puts the dimension in the top bits of the hash, to make sure to get unique hashes per dimension
        // ((long)oreSeedX & 0x000000000fffffffL) << 28)    Puts the chunk X in the bits 29-55. Cuts off the top few bits of the chunk so we have bits for dimension.
        // ((long)oreSeedZ & 0x000000000fffffffL))    Puts the chunk Z in the bits 0-27. Cuts off the top few bits of the chunk so we have bits for dimension.
        long oreVeinSeed = getOreVeinSeed(world, oreSeedX, oreSeedZ);
        XSTR oreVeinRNG = new XSTR(oreVeinSeed);
        int oreVeinPercentageRoll = oreVeinRNG.nextInt(100); // Roll the dice, see if we get an orevein here at all
        if (Ref.debugOreVein)
            Antimatter.LOGGER.info("Finding oreveins for oreVeinSeed="+ oreVeinSeed + " chunkX="+ chunkX + " chunkZ="+ chunkZ + " oreSeedX=" + oreSeedX + " oreSeedZ=" + oreSeedZ + " worldSeed=" + world.getSeed());

        // Search for a valid orevein for this dimension
        if (!VALID_VEINS.containsKey(oreVeinSeed)) {
            int veinCount = veins.size();
            if (oreVeinPercentageRoll < Configs.WORLD.ORE_VEIN_CHANCE && WorldGenVeinLayer.TOTAL_WEIGHT > 0 && veinCount > 0) {
                int placementAttempts = 0;
                boolean oreVeinFound = false;
                int i;

                for (i = 0; i < Configs.WORLD.ORE_VEIN_FIND_ATTEMPTS && !oreVeinFound && placementAttempts < Configs.WORLD.ORE_VEIN_PLACE_ATTEMPTS; i++) {
                    int tRandomWeight = oreVeinRNG.nextInt(WorldGenVeinLayer.TOTAL_WEIGHT);
                    for (WorldGenVeinLayer vein : veins) {
                        tRandomWeight -= vein.weight;
                        if (tRandomWeight <= 0) {
                            // Adjust the seed so that this vein has a series of unique random numbers.  Otherwise multiple attempts at this same oreseed will get the same offset and X/Z values. If an orevein failed, any orevein with the
                            // same minimum heights would fail as well.  This prevents that, giving each orevein a unique height each pass through here.
                            VeinLayerResult placementResult = vein.generateChunkified(world, new XSTR(oreVeinSeed ^ vein.primaryHash/*vein.material[0].getInternalId()*/), chunkX * 16, chunkZ * 16, oreSeedX * 16, oreSeedZ * 16);
                            switch (placementResult) {
                                case ORE_PLACED:
                                    if (Ref.debugOreVein)
                                        Antimatter.LOGGER.info("Added near oreVeinSeed=" + oreVeinSeed + " " + vein.getId() + " tries at oremix=" + i + " placementAttempts=" + placementAttempts + " dimension=" + world.getDimension());
                                    VALID_VEINS.put(oreVeinSeed, vein);
                                    oreVeinFound = true;
                                    break;
                                case NO_ORE_IN_BOTTOM_LAYER:
                                    placementAttempts++;
                                    break; // Should do retry in this case until out of chances
                                case NO_OVERLAP:
                                    if (Ref.debugOreVein)
                                        Antimatter.LOGGER.info("Added far oreVeinSeed=" + oreVeinSeed + " " + vein.getId() + " tries at oremix=" + i + " placementAttempts=" + placementAttempts + " dimension=" + world.getDimension());
                                    VALID_VEINS.put(oreVeinSeed, vein);
                                    oreVeinFound = true;
                                    break;
                                case NO_OVERLAP_AIR_BLOCK:
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
                    VALID_VEINS.put(oreVeinSeed, NO_ORES_IN_VEIN);
                }
            } else if (oreVeinPercentageRoll >= Configs.WORLD.ORE_VEIN_CHANCE) {
                if (Ref.debugOreVein)
                    Antimatter.LOGGER.info("Skipped oreVeinSeed="+ oreVeinSeed + " chunkX="+ chunkX + " chunkZ="+ chunkZ + " oreSeedX=" + oreSeedX + " oreSeedZ=" + oreSeedZ + " RNG=" + oreVeinPercentageRoll + " %=" + Configs.WORLD.ORE_VEIN_CHANCE+ " dimension=" + world.getDimension());
                VALID_VEINS.put(oreVeinSeed, NO_ORES_IN_VEIN);
            }
        } else {
            // oreseed is located in the previously processed table
            if (Ref.debugOreVein)
                Antimatter.LOGGER.info("Valid oreVeinSeed="+ oreVeinSeed + " VALID_VEINS.size()=" + VALID_VEINS.size() + " ");
            WorldGenVeinLayer vein = VALID_VEINS.get(oreVeinSeed);
            oreVeinRNG.setSeed(oreVeinSeed ^ vein.primaryHash/*vein.material[0].getInternalId()*/);  // Reset RNG to only be based on oreseed X/Z and type of vein
            VeinLayerResult placementResult = vein.generateChunkified(world, oreVeinRNG, chunkX * 16, chunkZ * 16, oreSeedX * 16, oreSeedZ * 16);
            switch (placementResult) {
                case NO_ORE_IN_BOTTOM_LAYER:
                    if (Ref.debugOreVein)
                        Antimatter.LOGGER.info(" No ore in bottom layer");
                    break;
                case NO_OVERLAP:
                    if (Ref.debugOreVein)
                        Antimatter.LOGGER.info(" No overlap");
                    break;
            }
        }
    }

    public static long getOreVeinSeed(IWorld world, long oreSeedX, long oreSeedZ) {
        return world.getSeed() << 16 ^ ((world.getDimension().getType().getId() & 0xffL) << 56 | (oreSeedX & 0x000000000fffffffL) << 28 | oreSeedZ & 0x000000000fffffffL);
    }

    VeinLayerResult generateChunkified(IWorld world, XSTR rand, int posX, int posZ, int seedX, int seedZ) {
        int tMinY = minY + rand.nextInt(maxY - minY - 5);
        
        //If the selected tMinY is more than the max height if the current position, escape
//        if (tMinY > world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, posX, posZ)) {
//            return CHUNK_HEIGHT_TOO_LOW;
//        }

        // Determine West/East ends of orevein
        int wXVein = seedX - rand.nextInt(size);        // West side
        int eXVein = seedX + 16 + rand.nextInt(size);
        // Limit Orevein to only blocks present in current chunk
        int wX = Math.max(wXVein, posX);
        int eX = Math.min(eXVein, posX + 16);

        // Get a block at the center of the chunk and the bottom of the orevein.

        BlockPos centerPos = new BlockPos(posX + 7, tMinY, posZ + 9);
        BlockState centerState = world.getBlockState(centerPos);
        //Block tBlock = world.getBlock(posX + 7, tMinY, posZ + 9);

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

        int nZ = Math.max(nZVein, posZ);
        int sZ = Math.min(sZVein, posZ+ 16);
        if (nZ >= sZ) { //No overlap between orevein and this chunk exists in Z
            if (centerState.getBlock().isReplaceableOreGen(centerState, world, centerPos, WorldGenHelper.ORE_PREDICATE)) {
                return NO_OVERLAP; // Didn't reach, but could have placed. Save orevein for future use.
            } else {
                return NO_OVERLAP_AIR_BLOCK; // Didn't reach, but couldn't place in test spot anywys, try for another orevein
            }
        }

        if (Ref.debugOreVein)
            Antimatter.LOGGER.info("Trying Orevein:" + getId() + " Dimension=" + world.getDimension() + " posX="+posX/16+ " posZ="+posZ/16+ " oreseedX="+ seedX/16 + " oreseedZ="+ seedZ/16 + " cY="+tMinY);
        //if (!generateSquare(world, rand, posX, posZ, seedX, seedZ, tMinY, wXVein, eXVein, nZVein, sZVein, wX, eX, nZ, sZ))
        if (!generateByFunction(world, rand, tMinY, wXVein, eXVein, nZVein, sZVein, wX, eX, nZ, sZ))
            return NO_ORE_IN_BOTTOM_LAYER;  // Exit early, didn't place anything in the bottom layer

        //Place small ores for the vein
        if (Configs.WORLD.ORE_VEIN_SMALL_ORE_MARKERS) {
            int nSmallOres = (eX - wX) * (sZ - nZ) * this.density / 10 * Configs.WORLD.ORE_VEIN_SMALL_ORE_MARKERS_MULTI;
            generateSmallOres(world, rand, posX, posZ, nSmallOres);
        }
        // Something (at least the bottom layer must have 1 block) must have been placed, return true
        return ORE_PLACED;
    }

    //Small ores are placed in the whole chunk in which the vein appears.
    private void generateSmallOres(IWorld world, XSTR rand, int posX, int posZ, int nSmallOres) {
        BlockPos.Mutable pos = new BlockPos.Mutable();
        for (int nSmallOresCount = 0; nSmallOresCount < nSmallOres; nSmallOresCount++) {
            int tX = rand.nextInt(16) + posX;
            int tZ = rand.nextInt(16) + posZ;
            int tY = rand.nextInt(160) + 10; // Y height can vary from 10 to 170 for small ores.
            pos.setPos(tX, tY, tZ);
            WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[0], MaterialType.ORE_SMALL);

            tX = rand.nextInt(16) + posX;
            tZ = rand.nextInt(16) + posZ;
            tY = rand.nextInt(160) + 10; // Y height can vary from 10 to 170 for small ores.
            pos.setPos(tX, tY, tZ);
            WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[1], MaterialType.ORE_SMALL);

            tX = rand.nextInt(16) + posX;
            tZ = rand.nextInt(16) + posZ;
            tY = rand.nextInt(160) + 10; // Y height can vary from 10 to 170 for small ores.
            pos.setPos(tX, tY, tZ);
            WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[2], MaterialType.ORE_SMALL);

            tX = rand.nextInt(16) + posX;
            tZ = rand.nextInt(16) + posZ;
            tY = rand.nextInt(190) + 10; // Y height can vary from 10 to 200 for small ores.
            pos.setPos(tX, tY, tZ);
            WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[3], MaterialType.ORE_SMALL);
        }
    }

    private boolean generateSquare(IWorld world, XSTR rand, int posX, int posZ, int seedX, int seedZ, int tMinY, int wXVein, int eXVein, int nZVein, int sZVein, int wX, int eX, int nZ, int sZ) {
        BlockPos.Mutable pos = new BlockPos.Mutable();
        int[] placeCount = new int[4];

        // Adjust the density down the more chunks we are away from the oreseed.  The 5 chunks surrounding the seed should always be max density due to truncation of Math.sqrt().
        int localDensity = Math.max(1, this.density / (int) Math.sqrt(2 + Math.pow(posX / 16 - seedX / 16, 2) + Math.pow(posZ / 16 - seedZ / 16, 2)));

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
            return false;
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
        if (Ref.debugOreVein)
            Antimatter.LOGGER.info(" wXVein" + wXVein + " eXVein" + eXVein + " nZVein" + nZVein + " sZVein" + sZVein + " locDen=" + localDensity
                    + " Den=" + this.density + " Sec="+placeCount[1]+ " Spo="+placeCount[3]+ " Bet="+placeCount[2]+ " Pri="+placeCount[0]);
        return true;
    }
    private boolean generateByFunction(IWorld world, XSTR rand,
                                       int tMinY, int wXVein, int eXVein, int nZVein, int sZVein, // vein
                                       int wX, int eX, int nZ, int sZ) { // vein & current chunk intersection
        BlockPos.Mutable pos = new BlockPos.Mutable();
        int[] placeCount = new int[4];
        final int centerX = (wXVein +  eXVein)/2;
        final int centerY = tMinY + 4;
        final int centerZ = (nZVein + sZVein)/2;
        final double a = 4.0 / ((wXVein - eXVein) *(wXVein - eXVein)); // Elliptic shape defined as
        final double b = 0.04; // 1 / (5*5)
        final double c = 4.0 / ((nZVein - sZVein) *(nZVein - sZVein)); // aX^2 + bY^2 +cZ^2 = 1

        for (int y = tMinY - 1; y < (tMinY + 8); ++y){
            for (int x = wX; x < eX; ++x) {
                for (int z = nZ; z < sZ; ++z){
                    double p = 1.0 - a * (centerX - x)*(centerX - x) - b*(centerY - y)*(centerY - y) - c*(centerZ-z)*(centerZ-z);
                    if (p <= 0)
                        continue;
                    if (rand.nextInt(100) > 100*p)
                        continue; // rolled outside the probability function
                    if (rand.nextInt(12) > density) // should be tested, but seems to be fine
                        continue;
                    pos.setPos(x, y, z);
                    if (rand.nextInt(100) < 10) { // let each 10th be sproradic
                        if (WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[3], MaterialType.ORE))
                            placeCount[3]++;
                    } else {
                        int oreIndex = (p > 0.5) ? 0 : (p > 0.2 ? 1 : 2);
                        if (WorldGenHelper.setOre(world, pos, world.getBlockState(pos), materials[oreIndex], MaterialType.ORE))
                            placeCount[oreIndex]++;
                    }
                }
            }
            if (y == tMinY + 1) { // early bail out test
                if ((placeCount[0] + placeCount[1] + placeCount[2] + placeCount[3]) == 0){
                    if (Ref.debugOreVein)
                        Antimatter.LOGGER.info(" No ore in bottom layer");
                    return false;
                }
            }
        }
        if (Ref.debugOreVein)
            Antimatter.LOGGER.info(" wXVein" + wXVein + " eXVein" + eXVein + " nZVein" + nZVein + " sZVein" + sZVein
                    + " Den=" + this.density + " Sec="+placeCount[1]+ " Spo="+placeCount[3]+ " Bet="+placeCount[2]+ " Pri="+placeCount[0]);
        return true;
    }
}
