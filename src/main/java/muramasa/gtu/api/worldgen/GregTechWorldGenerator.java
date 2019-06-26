package muramasa.gtu.api.worldgen;

import muramasa.gtu.api.util.XSTR;
import muramasa.gtu.api.worldgen.objects.WorldGenRunnable;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.*;

public class GregTechWorldGenerator implements IWorldGenerator {

    public static List<WorldGenRunnable> RUNNABLES = new ArrayList<>();
    public boolean mIsGenerating = false;
    public static final Object LIST_LOCK = new Object();

    public GregTechWorldGenerator() {
        GameRegistry.registerWorldGenerator(this, 1073741823);
    }

    @Override
    public void generate(Random aRandom, int chunkX, int chunkZ, World world, IChunkGenerator generator, IChunkProvider provider) {
        synchronized (LIST_LOCK) {
            RUNNABLES.add(new WorldGenRunnable(new XSTR(Math.abs(aRandom.nextInt()) +1), chunkX, chunkZ, world.provider.getDimension(), world, generator, provider));
            //if (debugWorldGen) GregTech.LOGGER.info("ADD WorldSeed:"+world.getSeed() + " DimId" + world.provider.getDimension() + " chunk x:" + chunkX + " z:" + chunkZ + " PIPE_SIZE: " + RUNNABLES.size());
        }
        if (!this.mIsGenerating) {
            this.mIsGenerating = true;
            int count = RUNNABLES.size();
            count = Math.min(count, 5); // Run a maximum of 5 chunks at a time through worldgen. Extra chunks get done later.
            for (int i = 0; i < count; i++) {
                WorldGenRunnable toRun = RUNNABLES.get(0);
                //if (debugWorldGen) GregTech.LOGGER.info("RUN WorldSeed:"+world.getSeed()+ " DimId" + world.provider.getDimension() + " chunk x:" + toRun.chunkX + " z:" + toRun.chunkZ + " PIPE_SIZE: " + this.RUNNABLES.size() + " i: " + i);
                synchronized (LIST_LOCK) {
                    RUNNABLES.remove(0);
                }
                toRun.run();
            }
            this.mIsGenerating = false;
        }
    }
}