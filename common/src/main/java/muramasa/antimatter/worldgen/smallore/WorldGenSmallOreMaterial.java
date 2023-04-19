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
    final Material material;
    final int minY, maxY, weight;
    final List<ResourceLocation> dimensions, biomes;

    final boolean diimensionBlacklist, biomeBlacklist;

    WorldGenSmallOreMaterial(String id, Material material, int minY, int maxY, int weight, List<ResourceLocation> dimensions, boolean dimensionBlacklist, List<ResourceLocation> biomes, boolean biomeBlacklist){
        super(id, WorldGenSmallOreMaterial.class, dimensions.stream().map(r -> ResourceKey.create(Registry.DIMENSION_REGISTRY, r)).toList());

        this.material = material;
        this.minY = minY;
        this.maxY = maxY;
        this.weight = weight;
        this.dimensions = dimensions;
        this.diimensionBlacklist = dimensionBlacklist;
        this.biomes = biomes;
        this.biomeBlacklist = biomeBlacklist;
    }
}
