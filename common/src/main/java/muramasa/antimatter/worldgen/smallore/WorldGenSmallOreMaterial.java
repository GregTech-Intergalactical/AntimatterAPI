package muramasa.antimatter.worldgen.smallore;

import muramasa.antimatter.material.Material;
import muramasa.antimatter.worldgen.object.WorldGenBase;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class WorldGenSmallOreMaterial extends WorldGenBase<WorldGenSmallOreMaterial> {
    public final Material material;
    public final int minY, maxY, weight;
    public final List<ResourceLocation> dimensions, biomes;

    public final boolean biomeBlacklist;

    WorldGenSmallOreMaterial(Material material, int minY, int maxY, int weight, List<ResourceLocation> dimensions, List<ResourceLocation> biomes, boolean biomeBlacklist){
        super(material.getId(), WorldGenSmallOreMaterial.class, dimensions.stream().map(r -> ResourceKey.create(Registry.DIMENSION_REGISTRY, r)).toList());

        this.material = material;
        this.minY = minY;
        this.maxY = maxY;
        this.weight = weight;
        this.dimensions = dimensions;
        this.biomes = biomes;
        this.biomeBlacklist = biomeBlacklist;
    }
}
