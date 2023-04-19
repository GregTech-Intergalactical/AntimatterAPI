package muramasa.antimatter.worldgen.smallore;

import muramasa.antimatter.material.Material;
import muramasa.antimatter.worldgen.object.WorldGenBase;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class WorldGenSmallOre extends WorldGenBase<WorldGenSmallOre> {
    public final Material material;
    public final int minY, maxY, amountPerChunk;
    public final List<ResourceLocation> dimensions, biomes;

    public final boolean biomeBlacklist;

    WorldGenSmallOre(String id, Material material, int minY, int maxY, int amountPerChunk, List<ResourceLocation> dimensions, List<ResourceLocation> biomes, boolean biomeBlacklist){
        super(id, WorldGenSmallOre.class, dimensions.stream().map(r -> ResourceKey.create(Registry.DIMENSION_REGISTRY, r)).toList());

        this.material = material;
        this.minY = minY;
        this.maxY = maxY;
        this.amountPerChunk = amountPerChunk;
        this.dimensions = dimensions;
        this.biomes = biomes;
        this.biomeBlacklist = biomeBlacklist;
    }
}
