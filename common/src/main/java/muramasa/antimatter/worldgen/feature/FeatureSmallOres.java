package muramasa.antimatter.worldgen.feature;

import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.worldgen.AntimatterConfiguredFeatures;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import muramasa.antimatter.worldgen.WorldGenHelper;
import muramasa.antimatter.worldgen.smallore.WorldGenSmallOre;
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
        super(NoneFeatureConfiguration.CODEC, WorldGenSmallOre.class);
    }

    @Override
    public String getId() {
        return "small_ores";
    }

    @Override
    public boolean enabled() {
        return AntimatterConfig.SMALL_ORES.get() && getRegistry().size() > 0;
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
        final int chunkCornerX = chunkX * 16;
        final int chunkCornerZ = chunkZ * 16;
        final int worldMinY = world.dimensionType().minY();
        final int worldMaxY = world.dimensionType().minY() + world.dimensionType().height();
        List<WorldGenSmallOre> smallOres = AntimatterWorldGenerator.all(WorldGenSmallOre.class, world.getLevel().dimension());
        int spawned = 0;
        for (WorldGenSmallOre smallOre : smallOres) {
            if (!smallOre.material.has(ORE_SMALL)) continue;
            int minY = Math.max(worldMinY, smallOre.minY);
            int maxY = Math.min(worldMaxY, smallOre.maxY);
            int i = 0;
            for (int j = Math.max(1, smallOre.amountPerChunk / 2 + random.nextInt(smallOre.amountPerChunk) / 2); i < j; i++) {
                BlockPos pos = new BlockPos(chunkCornerX + random.nextInt(16), minY + random.nextInt(Math.max(1, maxY - minY)), chunkCornerZ + random.nextInt(16));
                if (!smallOre.getValidBiomes().test(world.getBiome(pos))) continue;
                boolean spawn = setOreBlock(world, pos, smallOre);
                if (spawn) spawned++;
            }
        }


        return spawned > 0;
    }

    private boolean setOreBlock(WorldGenLevel level, BlockPos pos, WorldGenSmallOre smallOre){
        Holder<Biome> biome = level.getBiome(pos);
        ResourceLocation biomeKey = biome.unwrapKey().get().location();
        if (smallOre.biomes.contains(biomeKey) == smallOre.biomeBlacklist) return false;
        return WorldGenHelper.setOre(level, pos, smallOre.material, AntimatterMaterialTypes.ORE_SMALL);
    }
}
