package muramasa.antimatter.worldgen.object;

import com.google.common.collect.Sets;
import com.google.gson.annotations.Expose;
import com.google.gson.internal.LinkedTreeMap;
import muramasa.antimatter.util.XSTR;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.AbstractChunkProvider;
import net.minecraft.world.gen.ChunkGenerator;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class WorldGenBase<T extends WorldGenBase<?>> {

    private String id;
    @Expose private boolean enabled = true;
    @Expose private Set<Integer> dimensions;
    private boolean custom;

    public WorldGenBase() {

    }

    public WorldGenBase(String id, Class<? extends WorldGenBase<?>> c, int... dimensions) {
        this.id = id;
        this.dimensions = Arrays.stream(dimensions).boxed().collect(Collectors.toCollection(Sets::newLinkedHashSet));
        AntimatterWorldGenerator.register(c, this);
    }

    public String getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Set<Integer> getDims() {
        return dimensions;
    }

    public boolean isCustom() {
        return custom;
    }

    public WorldGenBase<T> asCustom() {
        this.custom = true;
        return this;
    }

    public WorldGenBase<T> onDataOverride(LinkedTreeMap dataMap) {
        if (dataMap.containsKey("enabled")) enabled = (Boolean) dataMap.get("enabled");
        return this;
    }

    public WorldGenBase<T> build() {
        if (dimensions == null) throw new IllegalStateException("WorldGenBase - " + id + ": dimensions cannot be null");
        return this;
    }

    public boolean generate(World world, XSTR rand, int posX, int posZ, BlockPos.Mutable pos, BlockState state, ChunkGenerator generator, AbstractChunkProvider provider) {
        return true;
    }

    public Predicate<Biome> getValidBiomes() {
        return b -> true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof WorldGenBase)) return false;
        WorldGenBase<?> other = (WorldGenBase<?>) o;
        return other.id.equals(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
