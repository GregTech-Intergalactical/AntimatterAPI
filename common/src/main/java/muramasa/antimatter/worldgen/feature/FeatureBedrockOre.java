package muramasa.antimatter.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class FeatureBedrockOre extends AntimatterFeature<NoneFeatureConfiguration>{

    public FeatureBedrockOre(Codec<NoneFeatureConfiguration> codec, Class<?> c) {
        super(codec, c);
    }

    @Override
    public String getId() {
        return "bedrock_ores";
    }

    @Override
    public boolean enabled() {
        return !getRegistry().isEmpty();
    }

    @Override
    public void init() {

    }

    @Override
    public void build(ResourceLocation name, Biome.ClimateSettings climate, Biome.BiomeCategory category, BiomeSpecialEffects effects, BiomeGenerationSettings.Builder gen, MobSpawnSettings.Builder spawns) {

    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        return false;
    }
}
