package muramasa.gtu.api.worldgen;

import muramasa.gtu.api.util.XSTR;
import muramasa.gtu.loaders.WorldGenLoader;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GregTechWorldGenerator implements IWorldGenerator {

    private static final Object LIST_LOCK = new Object();
    private static List<WorldGenRunnable> RUNNABLES = new ArrayList<>();
    private static ArrayList<WorldGenRunnable.NearbySeeds> SEEDS = new ArrayList<>();
    private static boolean GENERATING = false;

    public GregTechWorldGenerator() {
        GameRegistry.registerWorldGenerator(this, 1073741823);
    }

    @Override
    public void generate(Random rand, int chunkX, int chunkZ, World world, IChunkGenerator generator, IChunkProvider provider) {
        synchronized (LIST_LOCK) {
            RUNNABLES.add(new WorldGenRunnable(new XSTR(Math.abs(rand.nextInt()) + 1), chunkX, chunkZ, world.provider.getDimension(), world, generator, provider));
            //if (debugWorldGen) GregTech.LOGGER.info("ADD WorldSeed:"+world.getSeed() + " DimId" + world.provider.getDimension() + " chunk chunkX:" + chunkX + " z:" + chunkZ + " PIPE_SIZE: " + RUNNABLES.size());
        }
        if (!GENERATING) {
            GENERATING = true;
            int count = RUNNABLES.size();
            count = Math.min(count, 5); // Run a maximum of 5 chunks at a time through worldgen. Extra chunks get done later.
            for (int i = 0; i < count; i++) {
                WorldGenRunnable toRun = RUNNABLES.get(0);
                //if (debugWorldGen) GregTech.LOGGER.info("RUN WorldSeed:"+world.getSeed()+ " DimId" + world.provider.getDimension() + " chunk chunkX:" + toRun.chunkX + " z:" + toRun.chunkZ + " PIPE_SIZE: " + this.RUNNABLES.size() + " i: " + i);
                synchronized (LIST_LOCK) {
                    RUNNABLES.remove(0);
                }
                toRun.run();
            }
            GENERATING = false;
        }
    }

    public class WorldGenRunnable implements Runnable {

        public final XSTR rand;
        public final int chunkX;
        public final int chunkZ;
        public final int dimension;
        public final World world;
        public final IChunkGenerator generator;
        public final IChunkProvider provider;

        // chunkX and chunkZ are now the by-chunk X and Z for the chunk of interest
        public WorldGenRunnable(XSTR rand, int chunkX, int chunkZ, int dimension, World world, IChunkGenerator generator, IChunkProvider provider) {
            this.rand = rand;
            this.chunkX = chunkX;
            this.chunkZ = chunkZ;
            this.dimension = dimension;
            this.world = world;
            this.generator = generator;
            this.provider = provider;
        }

        public void run() {
            //long startTime = System.nanoTime();
            int oreVeinMaxSize = 32;

            //Generate Stones and Small Ores
            for (WorldGenStone stone : WorldGenStone.ALL.get(world.provider.getDimension())) {
                stone.generate(world, rand, chunkX * 16, chunkZ * 16, generator, provider);
            }
            for (WorldGenOreSmall small : WorldGenOreSmall.ALL.get(world.provider.getDimension())) {
                small.generate(world, rand, chunkX * 16, chunkZ * 16, generator, provider);
            }

            //long leftOverTime = System.nanoTime();

            // Determine bounding box on how far out to check for oreveins affecting this chunk
            // For now, manually reducing oreVeinMaxSize when not in the Underdark for performance
            int wXbox = this.chunkX - (oreVeinMaxSize / 16);
            int eXbox = this.chunkX + (oreVeinMaxSize / 16 + 1); // Need to add 1 since it is compared using a <
            int nZbox = this.chunkZ - (oreVeinMaxSize / 16);
            int sZbox = this.chunkZ + (oreVeinMaxSize / 16 + 1);

            // Search for oreVein seeds and add to the list;
            for (int x = wXbox; x < eXbox; x++) {
                for (int z = nZbox; z < sZbox; z++) {
                    // Determine if this X/Z is an oreVein seed
                    if (((Math.abs(x) % 3) == 1) && ((Math.abs(z) % 3) == 1)) {
                        //if (debugWorldGen) GregTech.LOGGER.info("Adding seed chunkX="+chunkX+ " z="+z);
                        SEEDS.add(new NearbySeeds(x, z));
                    }
                }
            }

            // Now process each oreseed vs this requested chunk
            for (; SEEDS.size() != 0; SEEDS.remove(0)) {
                //if (debugWorldGen) GregTech.LOGGER.info("Processing seed chunkX=" + SEEDS.get(0).mX + " z=" + SEEDS.get(0).mZ);
                WorldGenOreLayer.worldGenFindVein(world, chunkX, chunkZ, SEEDS.get(0).mX, SEEDS.get(0).mZ, generator, provider);
            }

            //long oreGenTime = System.nanoTime();

            //Asteroid Worldgen
            WorldGenLoader.ASTEROID_GEN.generate(world, rand, chunkX, chunkZ, generator, provider);

            //long endTime = System.nanoTime();
            //long duration = (endTime - startTime);
            //if (Ref.debugWorldGen) GregTech.LOGGER.info("Oregen took " + (oreGenTime - leftOverTime) + " Leftover gen took " + (leftOverTime - startTime) + " Worldgen took " + duration + " ns");
        }

        // Local class to track which orevein seeds must be checked when doing chunkified worldgen
        public class NearbySeeds {

            public int mX;
            public int mZ;

            NearbySeeds(int x, int z) {
                this.mX = x;
                this.mZ = z;
            }

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof NearbySeeds)) return false;
                NearbySeeds other = (NearbySeeds) o;
                if (this.mX != other.mX) return false;
                return this.mZ == other.mZ;
            }

            @Override
            public int hashCode() {
                int result = this.mX;
                result = 31 * result + this.mZ;
                return result;
            }
        }
    }
}