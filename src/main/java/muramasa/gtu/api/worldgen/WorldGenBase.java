package muramasa.gtu.api.worldgen;

import com.google.common.collect.Sets;
import com.google.gson.annotations.Expose;
import muramasa.gtu.api.util.XSTR;
import net.minecraft.world.World;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.IChunkGenerator;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class WorldGenBase {

    @Expose private String id;
    @Expose private boolean enabled = true;
    @Expose private Set<Integer> dimensions;

    public WorldGenBase() {

    }

    public WorldGenBase(String id, int... dimensions) {
        this.id = id;
        this.dimensions = Sets.newLinkedHashSet(Arrays.stream(dimensions).boxed().collect(Collectors.toList()));
        GregTechWorldGenerator.register(this);
    }

    public String getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Set<Integer> getDimensions() {
        return dimensions;
    }

    public WorldGenBase build() {
        return this;
    }

    public boolean generate(World world, XSTR rand, int passedX, int passedZ, IChunkGenerator generator, IChunkProvider provider) {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorldGenBase)) return false;
        WorldGenBase other = (WorldGenBase) o;
        return other.id.equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
