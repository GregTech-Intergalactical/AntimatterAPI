package muramasa.gtu.api.worldgen.objects;

import muramasa.gtu.GregTech;
import muramasa.gtu.Ref;
import muramasa.gtu.api.util.XSTR;
import muramasa.gtu.api.worldgen.AsteroidGenerator;
import muramasa.gtu.api.worldgen.WorldGenOreLayer;
import muramasa.gtu.api.worldgen.WorldGenStone;
import muramasa.gtu.loaders.WorldGenLoader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Random;

public class WorldGenRunnable implements Runnable {

    public final Random rand;
    public final int chunkX;
    public final int chunkZ;
    public final int dimension;
    public final World world;
    public final IChunkGenerator generator;
    public final IChunkProvider provider;

    public static Hashtable<Long, WorldGenOreLayer> VALID_VEINS = new Hashtable<>(1024);
    public static ArrayList<NearbySeeds> SEEDS = new ArrayList<>();

    // chunkX and chunkZ are now the by-chunk X and Z for the chunk of interest
    public WorldGenRunnable(Random aRandom, int chunkX, int chunkZ, int dimension, World world, IChunkGenerator generator, IChunkProvider provider) {
        this.rand = aRandom;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.dimension = dimension;
        this.world = world;
        this.generator = generator;
        this.provider = provider;
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

    public void worldGenFindVein(int oreSeedX, int oreSeedZ) {
        // Explanation of oreveinseed implementation.
        // (long)this.world.getSeed()<<16)    Deep Dark does two oregen passes, one with getSeed set to +1 the original world seed.  This pushes that +1 off the low bits of oreSeedZ, so that the hashes are far apart for the two passes.
        // ((this.world.provider.getDimension() & 0xffL)<<56)    Puts the dimension in the top bits of the hash, to make sure to get unique hashes per dimension
        // ((long)oreSeedX & 0x000000000fffffffL) << 28)    Puts the chunk X in the bits 29-55. Cuts off the top few bits of the chunk so we have bits for dimension.
        // ((long)oreSeedZ & 0x000000000fffffffL))    Puts the chunk Z in the bits 0-27. Cuts off the top few bits of the chunk so we have bits for dimension.
        long oreVeinSeed = (this.world.getSeed() <<16) ^ (((this.world.provider.getDimension() & 0xffL)<<56) | (((long)oreSeedX & 0x000000000fffffffL) << 28) | ((long)oreSeedZ & 0x000000000fffffffL)); // Use an RNG that is identical every time it is called for this oreseed.
        XSTR oreVeinRNG = new XSTR( oreVeinSeed );
        int oreVeinPercentageRoll = oreVeinRNG.nextInt(100); // Roll the dice, see if we get an orevein here at all
        //if (Ref.debugOreVein) GregTech.LOGGER.info("Finding oreveins for oreVeinSeed="+ oreVeinSeed + " chunkX="+ this.chunkX + " chunkZ="+ this.chunkZ + " oreSeedX="+ oreSeedX + " oreSeedZ="+ oreSeedZ + " worldSeed="+this.world.getSeed());

        // Search for a valid orevein for this dimension
        if(!VALID_VEINS.containsKey(oreVeinSeed)) {
            if (oreVeinPercentageRoll < Ref.oreveinPercentage && WorldGenOreLayer.TOTAL_WEIGHT > 0 && WorldGenOreLayer.LAYERS.get(world.provider.getDimension()).size() > 0) {
                int placementAttempts = 0;
                boolean oreVeinFound = false;
                int i;
                
                for ( i = 0; (i < Ref.oreveinAttempts) && (!oreVeinFound) && (placementAttempts<Ref.oreveinMaxPlacementAttempts); i++ ) {
                    int tRandomWeight = oreVeinRNG.nextInt(WorldGenOreLayer.TOTAL_WEIGHT);
                    for (WorldGenOreLayer layer : WorldGenOreLayer.LAYERS.get(world.provider.getDimension())) {
                        tRandomWeight -= layer.weight;
                        if (tRandomWeight <= 0) {
                            // Adjust the seed so that this layer has a series of unique random numbers.  Otherwise multiple attempts at this same oreseed will get the same offset and X/Z values. If an orevein failed, any orevein with the
                            // same minimum heights would fail as well.  This prevents that, giving each orevein a unique height each pass through here.
                            int placementResult = layer.generateChunkified(this.world, new XSTR( oreVeinSeed ^ (layer.materials[0].getInternalId())), this.chunkX *16, this.chunkZ *16, oreSeedX*16, oreSeedZ*16, this.generator, this.provider);
                            switch(placementResult) {
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
                if ((!oreVeinFound) && (this.chunkX == oreSeedX) && (this.chunkZ == oreSeedZ)){
                    //if (Ref.debugOreVein) GregTech.LOGGER.info("Empty oreVeinSeed="+ oreVeinSeed + " chunkX="+ this.chunkX + " chunkZ="+ this.chunkZ + " oreSeedX="+ oreSeedX + " oreSeedZ="+ oreSeedZ + " tries at oremix=" + i + " placementAttempts=" + placementAttempts + " dimension=" + world.provider.getDimension());
                    VALID_VEINS.put(oreVeinSeed, WorldGenLoader.noOresInVein);
                }
            } else if(oreVeinPercentageRoll >= Ref.oreveinPercentage) {
                //if (Ref.debugOreVein) GregTech.LOGGER.info("Skipped oreVeinSeed="+ oreVeinSeed + " chunkX="+ this.chunkX + " chunkZ="+ this.chunkZ + " oreSeedX=" + oreSeedX + " oreSeedZ=" + oreSeedZ + " RNG=" + oreVeinPercentageRoll + " %=" + Ref.oreveinPercentage+ " dimension=" + world.provider.getDimension());
                VALID_VEINS.put(oreVeinSeed, WorldGenLoader.noOresInVein);
            }
        } else {
            // oreseed is located in the previously processed table
            //if (Ref.debugOreVein) GregTech.LOGGER.info("Valid oreVeinSeed="+ oreVeinSeed + " VALID_VEINS.size()=" + VALID_VEINS.size() + " ");
            WorldGenOreLayer layer = VALID_VEINS.get(oreVeinSeed);
            oreVeinRNG.setSeed(oreVeinSeed ^ (layer.materials[0].getInternalId()));  // Reset RNG to only be based on oreseed X/Z and type of vein
            int placementResult = layer.generateChunkified(this.world, oreVeinRNG, this.chunkX *16, this.chunkZ *16, oreSeedX*16, oreSeedZ*16, this.generator, this.provider);
            switch(placementResult) {
                case WorldGenOreLayer.NO_ORE_IN_BOTTOM_LAYER:
                    //if (Ref.debugOreVein) GregTech.LOGGER.info(" No ore in bottom layer");
                    break;
                case WorldGenOreLayer.NO_OVERLAP:
                    //if (Ref.debugOreVein) GregTech.LOGGER.info(" No overlap");
                    break;
            }
        }
    }

    public void run() {
        long startTime = System.nanoTime();
        int oreVeinMaxSize = 32;

        for (WorldGenStone stone : WorldGenStone.STONES.get(world.provider.getDimension())) {
            stone.executeWorldgen(this.world, this.rand, this.chunkX *16, this.chunkZ *16, this.generator, this.provider);
        }

        // Do GT_Stones and GT_small_ores oregen for this chunk
        //try {
        //TODO
        //for (GT_Worldgen tWorldGen : GregTech_API.sWorldgenList) {
                    /*
                    if (debugWorldGen) GregTech.LOGGER.info(
                        "tWorldGen.mWorldGenName="+tWorldGen.mWorldGenName
                    );
                    */
        //tWorldGen.executeWorldgen(this.world, this.rand, this.mBiome, this.dimension, this.chunkX *16, this.chunkZ *16, this.generator, this.provider);
        //}
        //} catch (Throwable e) {
        //e.printStackTrace();
        //}
        long leftOverTime = System.nanoTime();

        // Determine bounding box on how far out to check for oreveins affecting this chunk
        // For now, manually reducing oreVeinMaxSize when not in the Underdark for performance
        int wXbox = this.chunkX - (oreVeinMaxSize/16);
        int eXbox = this.chunkX + (oreVeinMaxSize/16 + 1); // Need to add 1 since it is compared using a <
        int nZbox = this.chunkZ - (oreVeinMaxSize/16);
        int sZbox = this.chunkZ + (oreVeinMaxSize/16 + 1);

        // Search for oreVein seeds and add to the list;
        for (int x = wXbox; x < eXbox; x++) {
            for (int z = nZbox; z < sZbox; z++) {
                // Determine if this X/Z is an oreVein seed
                if (((Math.abs(x)%3) == 1) && ((Math.abs(z)%3) == 1)) {
                    //if (debugWorldGen) GregTech.LOGGER.info("Adding seed x="+x+ " z="+z);
                    SEEDS.add(new NearbySeeds(x,z));
                }
            }
        }

        // Now process each oreseed vs this requested chunk
        for(; SEEDS.size() != 0; SEEDS.remove(0)) {
            //if (debugWorldGen) GregTech.LOGGER.info("Processing seed x=" + SEEDS.get(0).mX + " z=" + SEEDS.get(0).mZ);
            worldGenFindVein(SEEDS.get(0).mX, SEEDS.get(0).mZ);
        }

        long oreGenTime = System.nanoTime();

        //Asteroid Worldgen
        AsteroidGenerator.generate(rand, chunkX, chunkZ, world, generator, provider);

        long endTime = System.nanoTime();
        long duration = (endTime - startTime);
        if (Ref.debugWorldGen) GregTech.LOGGER.info("Oregen took " + (oreGenTime-leftOverTime)+ " Leftover gen took " + (leftOverTime - startTime) + " Worldgen took " + duration + " ns");
    }
}
