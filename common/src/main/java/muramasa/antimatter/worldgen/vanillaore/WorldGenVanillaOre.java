package muramasa.antimatter.worldgen.vanillaore;

import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.util.TagUtils;
import muramasa.antimatter.worldgen.object.WorldGenBase;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import java.util.List;
import java.util.function.Predicate;

public class WorldGenVanillaOre extends WorldGenBase<WorldGenVanillaOre> {
    public final Material primary, secondary;
    public final MaterialType<?> materialType;
    public final int minY, maxY, weight, size, plateau;
    public final float secondaryChance, discardOnExposureChance;
    public final List<ResourceLocation> dimensions;
    public final List<String> biomes;

    public final boolean biomeBlacklist, rare, triangle, spawnOnOceanFloor;

    WorldGenVanillaOre(String id, Material primary, Material secondary, MaterialType<?> type, float secondaryChance, float discardOnExposureChance, int minY, int maxY, int weight, int size, boolean rare, boolean triangle, int plateau, boolean spawnOnOceanFlor, List<ResourceLocation> dimensions, List<String> biomes, boolean biomeBlacklist){
        super(id, WorldGenVanillaOre.class, dimensions.stream().map(r -> ResourceKey.create(Registry.DIMENSION_REGISTRY, r)).toList());

        this.primary = primary;
        this.secondary = secondary;
        this.materialType = type;
        this.secondaryChance = secondaryChance;
        this.discardOnExposureChance = discardOnExposureChance;
        this.minY = minY;
        this.maxY = maxY;
        this.weight = weight;
        this.size = size;
        this.rare = rare;
        this.triangle = triangle;
        this.spawnOnOceanFloor = spawnOnOceanFlor;
        this.plateau = plateau;
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
