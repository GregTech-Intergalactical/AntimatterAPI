package muramasa.gtu.api.worldgen;

import com.google.common.collect.Sets;
import muramasa.gtu.api.materials.IMaterialFlag;
import muramasa.gtu.api.util.XSTR;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;

import java.util.Set;

public abstract class WorldGenBase {

    protected String id;
    protected Set<IMaterialFlag> dims;

    public WorldGenBase(String id, IMaterialFlag... tags) {
        this.id = id;
        this.dims = Sets.newHashSet(tags);
    }

    public boolean generate(World world, XSTR rand, int passedX, int passedZ, IChunkGenerator generator, IChunkProvider provider){
        return true;
    }
}
