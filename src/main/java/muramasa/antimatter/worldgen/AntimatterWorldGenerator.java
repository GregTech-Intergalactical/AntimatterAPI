package muramasa.antimatter.worldgen;

import com.google.gson.JsonObject;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.util.Utils;
import muramasa.antimatter.worldgen.feature.AntimatterFeature;
import muramasa.antimatter.worldgen.object.WorldGenBase;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.stream.Collectors;

public class AntimatterWorldGenerator {

    public static void init() {
        Antimatter.LOGGER.info("AntimatterAPI WorldGen Initialization Stage...");
        AntimatterAPI.onRegistration(RegistrationEvent.WORLDGEN_INIT);
        if (!AntimatterConfig.WORLD.VANILLA_STONE_GEN) removeStoneFeatures();
        if (!AntimatterConfig.WORLD.VANILLA_ORE_GEN) removeOreFeatures();
        AntimatterAPI.runLaterCommon(() -> {
            AntimatterAPI.all(AntimatterFeature.class).stream().filter(AntimatterFeature::enabled).forEach(f -> {
                f.onDataOverride(new JsonObject());
                f.init();
            });
            WorldGenHelper.init();
        });
        /*
        try {
            //Path config = FMLPaths.CONFIGDIR.get().resolve("GregTech/WorldGenDefault.json");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AntimatterWorldGenerator caught an exception while initializing");
        }
         */
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
     * Removes specific features, in specific generation stages, in all biomes registered
     * @param stage generation stage where the feature is added to
     * @param featureToRemove feature instance wishing to be removed
     * @param states BlockStates wish to be removed
     */
    public static void removeDecoratedFeatureFromAllBiomes(@Nonnull final GenerationStage.Decoration stage, @Nonnull final Feature<?> featureToRemove, BlockState... states) {
        if (states.length == 0) Utils.onInvalidData("No BlockStates specified to be removed!");
        AntimatterAPI.runLaterCommon(() -> {
            for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
                for (BlockState state : states) {
                    biome.getFeatures(stage).removeIf(f -> isDecoratedFeatureDisabled(f, featureToRemove, state));
                }
            }
        });
    }

    /**
     * Removes specific features, in specific generation stages, in specific biomes
     * @param biome Biome wish to remove feature from
     * @param stage generation stage where the feature is added to
     * @param featureToRemove feature instance wishing to be removed
     * @param states BlockStates wish to be removed
     */
    public static void removeDecoratedFeaturesFromBiome(@Nonnull final Biome biome, final @Nonnull GenerationStage.Decoration stage, final @Nonnull Feature<?> featureToRemove, BlockState... states) {
        if (states.length == 0) Utils.onInvalidData("No BlockStates specified to be removed!");
        AntimatterAPI.runLaterCommon(() -> {
            for (BlockState state : states) {
                biome.getFeatures(stage).removeIf(f -> isDecoratedFeatureDisabled(f, featureToRemove, state));
            }
        });
    }

    /**
     * Check with BlockState in a feature if it is disabled
     */
    public static boolean isDecoratedFeatureDisabled(@Nonnull ConfiguredFeature<?, ?> configuredFeature, @Nonnull Feature<?> featureToRemove, @Nonnull BlockState state) {
        if (configuredFeature.config instanceof DecoratedFeatureConfig) {
            DecoratedFeatureConfig config = (DecoratedFeatureConfig) configuredFeature.config;
            Feature<?> feature = config.feature.feature;
            if (feature == featureToRemove) {
                IFeatureConfig featureConfig = config.feature.config;
                if (featureConfig instanceof OreFeatureConfig) {
                    BlockState configState = ((OreFeatureConfig) featureConfig).state;
                    return state == configState;
                }
                if (featureConfig instanceof BlockStateFeatureConfig) {
                    BlockState configState = ((BlockStateFeatureConfig) featureConfig).field_227270_a_; // Constructor BlockState var
                    return state == configState;
                }
            }
        }
        return false;
    }
}