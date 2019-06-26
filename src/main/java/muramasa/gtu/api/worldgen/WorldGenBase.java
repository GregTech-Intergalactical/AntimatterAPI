package muramasa.gtu.api.worldgen;

import com.google.common.collect.Sets;
import muramasa.gtu.api.materials.IMaterialFlag;

import java.util.Set;

public abstract class WorldGenBase {

    protected String id;
    protected Set<IMaterialFlag> dims;

    public WorldGenBase(String id, IMaterialFlag... tags) {
        this.id = id;
        this.dims = Sets.newHashSet(tags);
    }

    //public abstract int generate(XSTR rand, int chunkX, int chunkZ, World world, IChunkGenerator generator, IChunkProvider provider);
}
