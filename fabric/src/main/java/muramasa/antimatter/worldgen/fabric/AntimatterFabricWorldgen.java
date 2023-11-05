package muramasa.antimatter.worldgen.fabric;

import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.mixin.BiomeAccessor;
import muramasa.antimatter.util.Utils;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import net.fabricmc.fabric.api.biome.v1.BiomeModificationContext;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.carver.ConfiguredWorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

import static muramasa.antimatter.Ref.ID;
import static muramasa.antimatter.worldgen.AntimatterWorldGenerator.isDecoratedFeatureDisabled;

public class AntimatterFabricWorldgen {
    public static void init(){
        BiomeModifications.create(new ResourceLocation(ID, "worldgen_event")).add(ModificationPhase.ADDITIONS, b -> true, (s, m) -> AntimatterWorldGenerator.reloadEvent(s.getBiomeKey().location(), ((BiomeAccessor)(Object)s.getBiome()).getClimateSettings(), ((BiomeAccessor)(Object)s.getBiome()).getBiomeCategory(), s.getBiome().getSpecialEffects(), new CustomGenerationBuilder(m.getGenerationSettings()), new CustomSpawnSettings(m.getSpawnSettings())))
                .add(ModificationPhase.REMOVALS, b -> true, (s, m) -> handleFeatureRemoval(s.getBiome(), m.getGenerationSettings()));
    }

    private static void handleFeatureRemoval(Biome biome, BiomeModificationContext.GenerationSettingsContext context) {
        if (AntimatterConfig.VANILLA_ORE_GEN.get()) {
            removeOreFeatures(biome, context);
        }
        if (AntimatterConfig.VANILLA_STONE_GEN.get()) {
            removeStoneFeatures(biome, context);
        }
    }

    private static void removeStoneFeatures(Biome biome, BiomeModificationContext.GenerationSettingsContext context) {
        removeDecoratedFeatureFromAllBiomes(biome, context, GenerationStep.Decoration.UNDERGROUND_ORES, Feature.ORE, Blocks.ANDESITE.defaultBlockState(), Blocks.GRANITE.defaultBlockState(), Blocks.DIORITE.defaultBlockState(), Blocks.TUFF.defaultBlockState(), Blocks.DIRT.defaultBlockState(), Blocks.GRAVEL.defaultBlockState());
    }

    private static void removeOreFeatures(Biome biome, BiomeModificationContext.GenerationSettingsContext context) {
        removeDecoratedFeatureFromAllBiomes(biome, context, GenerationStep.Decoration.UNDERGROUND_ORES, Feature.ORE, Blocks.COAL_ORE.defaultBlockState(), Blocks.IRON_ORE.defaultBlockState(), Blocks.GOLD_ORE.defaultBlockState(), Blocks.COPPER_ORE.defaultBlockState(), Blocks.EMERALD_ORE.defaultBlockState(), Blocks.REDSTONE_ORE.defaultBlockState(), Blocks.LAPIS_ORE.defaultBlockState(), Blocks.DIAMOND_ORE.defaultBlockState());
        removeDecoratedFeatureFromAllBiomes(biome, context, GenerationStep.Decoration.UNDERGROUND_ORES, Feature.ORE, Blocks.DEEPSLATE_COAL_ORE.defaultBlockState(), Blocks.DEEPSLATE_IRON_ORE.defaultBlockState(), Blocks.DEEPSLATE_GOLD_ORE.defaultBlockState(), Blocks.DEEPSLATE_COPPER_ORE.defaultBlockState(), Blocks.DEEPSLATE_EMERALD_ORE.defaultBlockState(), Blocks.DEEPSLATE_REDSTONE_ORE.defaultBlockState(), Blocks.DEEPSLATE_LAPIS_ORE.defaultBlockState(), Blocks.DEEPSLATE_DIAMOND_ORE.defaultBlockState());
    }

    /**
     * Removes specific features, in specific generation stages, in all biomes registered
     *
     * @param stage           generation stage where the feature is added to
     * @param featureToRemove feature instance wishing to be removed
     * @param states          BlockStates wish to be removed
     */
    public static void removeDecoratedFeatureFromAllBiomes(Biome biome, BiomeModificationContext.GenerationSettingsContext context, @NotNull final GenerationStep.Decoration stage, @NotNull final Feature<?> featureToRemove, BlockState... states) {
        if (states.length == 0) Utils.onInvalidData("No BlockStates specified to be removed!");
        Set<BlockState> set = Set.of(states);
        // AntimatterAPI.runLaterCommon(() -> {
        //  for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
        biome.getGenerationSettings().features().get(stage.ordinal()).stream().toList().forEach(f -> {
            if (isDecoratedFeatureDisabled(f.value().feature().value(), featureToRemove, set)){
                context.removeFeature(stage, f.unwrapKey().orElseThrow());
            }
        });

    }

    public static class CustomGenerationBuilder extends BiomeGenerationSettings.Builder{
        final BiomeModificationContext.GenerationSettingsContext context;
        public CustomGenerationBuilder(BiomeModificationContext.GenerationSettingsContext context){
            this.context = context;
        }

        @Override
        public CustomGenerationBuilder addFeature(GenerationStep.Decoration step, Holder<PlacedFeature> feature) {
            context.addFeature(step, feature.unwrapKey().orElseThrow());
            return this;
        }

        @Override
        public CustomGenerationBuilder addFeature(int step, Holder<PlacedFeature> feature) {
            this.addFeature(GenerationStep.Decoration.values()[step], feature);
            return this;
        }

        @Override
        public BiomeGenerationSettings.Builder addCarver(GenerationStep.Carving step, Holder<? extends ConfiguredWorldCarver<?>> carver) {
            context.addCarver(step, (ResourceKey<ConfiguredWorldCarver<?>>) carver.unwrapKey().get());
            return this;
        }
    }

    public static class CustomSpawnSettings extends MobSpawnSettings.Builder {
        final BiomeModificationContext.SpawnSettingsContext context;
        public CustomSpawnSettings(BiomeModificationContext.SpawnSettingsContext context){
            this.context = context;
        }
        public MobSpawnSettings.Builder addSpawn(MobCategory classification, MobSpawnSettings.SpawnerData spawner) {
            context.addSpawn(classification, spawner);
            return this;
        }

        public MobSpawnSettings.Builder addMobCharge(EntityType<?> entityType, double spawnCostPerEntity, double maxSpawnCost) {
            context.setSpawnCost(entityType, spawnCostPerEntity, maxSpawnCost);
            return this;
        }

        public MobSpawnSettings.Builder creatureGenerationProbability(float probability) {
            context.setCreatureSpawnProbability(probability);
            return this;
        }
    }
}
