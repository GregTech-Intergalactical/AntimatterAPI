package muramasa.antimatter.worldgen;

import com.google.gson.JsonObject;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Configs;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.worldgen.feature.AntimatterFeature;
import muramasa.antimatter.worldgen.object.WorldGenBase;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Collectors;

public class AntimatterWorldGenerator {

    public static void init() {
        try {
            //Path config = FMLPaths.CONFIGDIR.get().resolve("GregTech/WorldGenDefault.json");
            AntimatterAPI.onRegistration(RegistrationEvent.WORLDGEN_INIT);
            if (Configs.WORLD.DISABLE_VANILLA_STONE_GEN) removeStoneFeatures();
            if (Configs.WORLD.DISABLE_VANILLA_ORE_GEN) removeOreFeatures();
            AntimatterAPI.all(AntimatterFeature.class).stream().filter(AntimatterFeature::enabled).forEach(feat -> {
                feat.onDataOverride(new JsonObject());
                feat.init();
            });
            WorldGenHelper.init();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AntimatterWorldGenerator caught an exception while initializing");
        }
    }

    public static void register(Class<?> c, WorldGenBase<?> base) {
        AntimatterFeature<?> feature = AntimatterAPI.get(AntimatterFeature.class, c.getName());
        if (feature != null) base.getDims().forEach(d -> feature.getRegistry().computeIfAbsent((int) d, k -> new LinkedList<>()).add(base));
    }

    public static <T> List<T> all(Class<T> c, int dim) {
        AntimatterFeature<?> feat = AntimatterAPI.get(AntimatterFeature.class, c.getName());
        return feat != null ? feat.getRegistry().computeIfAbsent(dim, k -> new LinkedList<>()).stream().map(c::cast).collect(Collectors.toList()) : Collections.emptyList();
    }

    private static void removeStoneFeatures() {
        removeDecoratedFeatureFromAllBiomes(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE, Blocks.ANDESITE.getDefaultState(), Blocks.GRANITE.getDefaultState(), Blocks.DIORITE.getDefaultState());
    }

    private static void removeOreFeatures() {
        removeDecoratedFeatureFromAllBiomes(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE, Blocks.COAL_ORE.getDefaultState(), Blocks.IRON_ORE.getDefaultState(), Blocks.GOLD_ORE.getDefaultState(), Blocks.REDSTONE_ORE.getDefaultState(), Blocks.LAPIS_ORE.getDefaultState(), Blocks.DIAMOND_ORE.getDefaultState());
    }

    /**
     * Removes specific features, in specific generation stages, in specific biomes
     * @param biomes set containing biomes wish to remove features from
     * @param stage generation stage where the feature is added to
     * @param featureToRemove feature instance wishing to be removed
     * @param states BlockStates wish to be removed
     */
    public static void removeDecoratedFeaturesFromBiomes(Set<Biome> biomes, GenerationStage.Decoration stage, Feature<?> featureToRemove, BlockState... states) {
        for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
            if (!biomes.contains(biome)) continue;
            for (BlockState state : states) {
                biome.getFeatures(stage).removeIf(f -> isDecoratedFeatureDisabled(f, featureToRemove, state));
            }
        }
    }

    /**
     * Removes specific features, in specific generation stages, in all biomes registered
     * @param stage generation stage where the feature is added to
     * @param featureToRemove feature instance wishing to be removed
     * @param states BlockStates wish to be removed
     */
    public static void removeDecoratedFeatureFromAllBiomes(GenerationStage.Decoration stage, Feature<?> featureToRemove, BlockState... states) {
        for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
            for (BlockState state : states) {
                biome.getFeatures(stage).removeIf(f -> isDecoratedFeatureDisabled(f, featureToRemove, state));
            }
        }
    }

    /**
     * Check with BlockState in a feature if it is disabled
     */
    public static boolean isDecoratedFeatureDisabled(ConfiguredFeature<?, ?> configuredFeature, Feature<?> featureToRemove, BlockState state) {
        if (configuredFeature.config instanceof DecoratedFeatureConfig) {
            DecoratedFeatureConfig config = (DecoratedFeatureConfig) configuredFeature.config;
            Feature<?> feature = config.feature.feature;
            if (feature == featureToRemove) {
                IFeatureConfig featureConfig = config.feature.config;
                if (featureConfig instanceof OreFeatureConfig) {
                    BlockState configState = ((OreFeatureConfig) featureConfig).state;
                    if (configState != null && state == configState) return true;
                }
                if (featureConfig instanceof BlockStateFeatureConfig) {
                    BlockState configState = ((BlockStateFeatureConfig) featureConfig).field_227270_a_;
                    if (configState != null && state == configState) return true;
                }
            }
        }
        return false;
    }
}