package muramasa.antimatter.worldgen.smallore;

import muramasa.antimatter.material.Material;
import muramasa.antimatter.util.TagUtils;
import muramasa.antimatter.worldgen.object.WorldGenBase;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import java.util.List;
import java.util.function.Predicate;

public class WorldGenSmallOre extends WorldGenBase<WorldGenSmallOre> {
    public final Material material;
    public final int minY, maxY, amountPerChunk;
    public final List<ResourceLocation> dimensions;
    public final List<String> biomes;

    public final boolean biomeBlacklist;

    WorldGenSmallOre(String id, Material material, int minY, int maxY, int amountPerChunk, List<ResourceLocation> dimensions, List<String> biomes, boolean biomeBlacklist){
        super(id, WorldGenSmallOre.class, dimensions.stream().map(r -> ResourceKey.create(Registry.DIMENSION_REGISTRY, r)).toList());

        this.material = material;
        this.minY = minY;
        this.maxY = maxY;
        this.amountPerChunk = amountPerChunk;
        this.dimensions = dimensions;
        this.biomes = biomes;
        this.biomeBlacklist = biomeBlacklist;
    }

    @Override
    public Predicate<Holder<Biome>> getValidBiomes() {
        return b -> {
            if (biomes.isEmpty()) return biomeBlacklist;
            Predicate<String> predicate = s -> {
                if (s.contains("#")) return b.is(TagUtils.getBiomeTag(new ResourceLocation(s.replace("#", ""))));
                return b.is(ResourceKey.create(Registry.BIOME_REGISTRY, new ResourceLocation(s)));
            };
            return biomeBlacklist ? biomes.stream().anyMatch(predicate) : biomes.stream().noneMatch(predicate);

        };
    }
}
