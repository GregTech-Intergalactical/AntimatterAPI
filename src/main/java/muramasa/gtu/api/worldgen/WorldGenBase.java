package muramasa.gtu.api.worldgen;

import muramasa.gtu.api.util.XSTR;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public abstract class WorldGenBase {

    protected String id;
    protected Set<DimensionType> dims;

    public WorldGenBase(String id, DimensionType... dims) {
        this.id = id;
        this.dims = new HashSet<>(Arrays.asList(dims));
    }

    public boolean generate(World world, XSTR rand, int passedX, int passedZ, IChunkGenerator generator, IChunkProvider provider){
        return true;
    }
}
