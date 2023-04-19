package muramasa.antimatter.worldgen.feature;

import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.worldgen.AntimatterConfiguredFeatures;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import muramasa.antimatter.worldgen.WorldGenHelper;
import muramasa.antimatter.worldgen.smallore.WorldGenSmallOreMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.List;
import java.util.Random;

import static muramasa.antimatter.data.AntimatterMaterialTypes.ORE_SMALL;

public class FeatureSmallOres extends AntimatterFeature<NoneFeatureConfiguration> {
    public FeatureSmallOres() {
        super(NoneFeatureConfiguration.CODEC, WorldGenSmallOreMaterial.class);
    }

    @Override
    public String getId() {
        return "small_ores";
    }

    @Override
    public boolean enabled() {
        return AntimatterConfig.WORLD.SMALL_ORES && getRegistry().size() > 0;
    }

    @Override
    public void init() {

    }


    @Override
    public void build(ResourceLocation name, Biome.ClimateSettings climate, Biome.BiomeCategory category, BiomeSpecialEffects effects, BiomeGenerationSettings.Builder gen, MobSpawnSettings.Builder spawns) {
        gen.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, AntimatterConfiguredFeatures.SMALL_ORES);
    }

    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> placer) {
        Random random = placer.random();
        BlockPos blockpos = placer.origin();
        WorldGenLevel world = placer.level();

        final int chunkX = placer.origin().getX() >> 4;
        final int chunkZ = placer.origin().getZ() >> 4;
        final int worldMinY = world.dimensionType().minY();
        final int worldMaxY = world.dimensionType().minY() + world.dimensionType().height();
        List<WorldGenSmallOreMaterial> smallOres = AntimatterWorldGenerator.all(WorldGenSmallOreMaterial.class, world.getLevel().dimension());
        int spawned = 0;
        for (WorldGenSmallOreMaterial smallOre : smallOres) {
            if (!smallOre.material.has(ORE_SMALL)) continue;
            if (smallOre.minY < worldMinY || smallOre.maxY > worldMaxY) continue;
            int i = 0;
            for (int j = Math.max(1, smallOre.weight / 2 + random.nextInt(smallOre.weight) / 2); i < j; i++) {
                boolean spawn = setOreBlock(world, chunkX + random.nextInt(16), smallOre.minY + random.nextInt(Math.max(1, smallOre.maxY - smallOre.minY)), chunkZ + random.nextInt(16), smallOre);
                if (spawn) spawned++;
            }
        }


        return spawned > 0;
    }

    private boolean setOreBlock(WorldGenLevel level, int x, int y, int z, WorldGenSmallOreMaterial smallOre){
        BlockPos pos = new BlockPos(x, y, z);
        Holder<Biome> biome = level.getBiome(pos);
        ResourceLocation biomeKey = biome.unwrapKey().get().location();
        if (smallOre.biomes.contains(biomeKey) == smallOre.biomeBlacklist) return false;
        return WorldGenHelper.setOre(level, pos, smallOre.material, AntimatterMaterialTypes.ORE_SMALL);
    }
}
