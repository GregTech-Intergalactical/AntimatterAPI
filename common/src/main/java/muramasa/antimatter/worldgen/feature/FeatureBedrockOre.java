package muramasa.antimatter.worldgen.feature;

import com.mojang.serialization.Codec;
import muramasa.antimatter.worldgen.AntimatterConfiguredFeatures;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import muramasa.antimatter.worldgen.bedrockore.WorldGenBedrockVein;
import muramasa.antimatter.worldgen.object.WorldGenStoneLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.List;
import java.util.Random;

public class FeatureBedrockOre extends AntimatterFeature<NoneFeatureConfiguration>{

    public FeatureBedrockOre() {
        super(NoneFeatureConfiguration.CODEC, WorldGenBedrockVein.class);
    }

    @Override
    public String getId() {
        return "bedrock_veins";
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
        gen.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, AntimatterConfiguredFeatures.BEDROCK_VEINS);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctxt) {
        WorldGenLevel world = ctxt.level();
        BlockPos pos = ctxt.origin();
        Random rand = ctxt.random();

        List<WorldGenBedrockVein> veins = AntimatterWorldGenerator.all(WorldGenBedrockVein.class, world.getLevel().dimension());
        if (veins.isEmpty()) return false;
        for (WorldGenBedrockVein vein : veins) {
            if (rand.nextInt(vein.probability) != 0) continue;
            return WorldGenBedrockVein.generateVein(vein.material, world, 0, pos.getX(), pos.getZ(), rand);
        }
        return false;
    }
}
