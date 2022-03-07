package muramasa.antimatter.worldgen;

import com.google.gson.JsonObject;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Ref;
import muramasa.antimatter.integration.kubejs.AntimatterKubeJS;
import muramasa.antimatter.mixin.PlacedFeatureAccessor;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.util.Utils;
import muramasa.antimatter.worldgen.feature.*;
import muramasa.antimatter.worldgen.object.WorldGenBase;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AntimatterWorldGenerator {

    static final AntimatterFeature<NoneFeatureConfiguration> VEIN = new FeatureVein();

    protected record GenHandler(Consumer<BiomeLoadingEvent> consumer,
                                Predicate<Biome.BiomeCategory> validator) {
    }

    public static void clear() {
        AntimatterAPI.all(AntimatterFeature.class, t -> t.getRegistry().clear());
    }

    public static void preinit() {

    }

    public static void init() {
        AntimatterAPI.runLaterCommon(() -> {
            WorldGenHelper.init();
            try {
                AntimatterAPI.all(AntimatterFeature.class).stream().filter(AntimatterFeature::enabled).forEach(f -> {
                    f.onDataOverride(new JsonObject());
                    f.init();
                });
            } catch (Exception ex) {
                Antimatter.LOGGER.warn("Caught exception during World generator later init: " + ex.toString());
            }
        });
        MinecraftForge.EVENT_BUS.addListener(AntimatterWorldGenerator::reloadEvent);
        /*
        try {
            //Path config = FMLPaths.CONFIGDIR.get().resolve("GregTech/WorldGenDefault.json");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AntimatterWorldGenerator caught an exception while initializing");
        }
         */
    }

    public static void setup() {
        Antimatter.LOGGER.info("AntimatterAPI WorldGen Initialization Stage...");
        AntimatterAPI.onRegistration(RegistrationEvent.WORLDGEN_INIT);
        if (AntimatterAPI.isModLoaded(Ref.MOD_KJS)) {
            AntimatterKubeJS.loadWorldgenScripts();
        }
    }

    public static void register(Class<?> c, WorldGenBase<?> base) {
        AntimatterFeature<?> feature = AntimatterAPI.get(AntimatterFeature.class, c.getName());
        if (feature != null)
            base.getDims().forEach(d -> feature.getRegistry().computeIfAbsent(d, k -> new LinkedList<>()).add(base));
    }

    public static void register(Consumer<BiomeLoadingEvent> consumer, String id, String domain, Predicate<Biome.BiomeCategory> validator) {
        AntimatterAPI.register(GenHandler.class, id, domain, new GenHandler(consumer, validator));
    }

    public static <T> List<T> all(Class<T> c, ResourceKey<Level> dim) {
        AntimatterFeature<?> feat = AntimatterAPI.get(AntimatterFeature.class, c.getName());
        return feat != null ? feat.getRegistry().computeIfAbsent(dim.location(), k -> new LinkedList<>()).stream().map(c::cast).collect(Collectors.toList()) : Collections.emptyList();
    }

    private static void removeStoneFeatures(BiomeGenerationSettingsBuilder builder) {
        removeDecoratedFeatureFromAllBiomes(builder, GenerationStep.Decoration.UNDERGROUND_ORES, Feature.ORE, Blocks.ANDESITE.defaultBlockState(), Blocks.GRANITE.defaultBlockState(), Blocks.DIORITE.defaultBlockState(), Blocks.TUFF.defaultBlockState(), Blocks.DIRT.defaultBlockState(), Blocks.GRAVEL.defaultBlockState());
    }

    private static void removeOreFeatures(BiomeGenerationSettingsBuilder builder) {
        removeDecoratedFeatureFromAllBiomes(builder, GenerationStep.Decoration.UNDERGROUND_ORES, Feature.ORE, Blocks.COAL_ORE.defaultBlockState(), Blocks.IRON_ORE.defaultBlockState(), Blocks.GOLD_ORE.defaultBlockState(), Blocks.COPPER_ORE.defaultBlockState(), Blocks.EMERALD_ORE.defaultBlockState(), Blocks.REDSTONE_ORE.defaultBlockState(), Blocks.LAPIS_ORE.defaultBlockState(), Blocks.DIAMOND_ORE.defaultBlockState());
        removeDecoratedFeatureFromAllBiomes(builder, GenerationStep.Decoration.UNDERGROUND_ORES, Feature.ORE, Blocks.DEEPSLATE_COAL_ORE.defaultBlockState(), Blocks.DEEPSLATE_IRON_ORE.defaultBlockState(), Blocks.DEEPSLATE_GOLD_ORE.defaultBlockState(), Blocks.DEEPSLATE_COPPER_ORE.defaultBlockState(), Blocks.DEEPSLATE_EMERALD_ORE.defaultBlockState(), Blocks.DEEPSLATE_REDSTONE_ORE.defaultBlockState(), Blocks.DEEPSLATE_LAPIS_ORE.defaultBlockState(), Blocks.DEEPSLATE_DIAMOND_ORE.defaultBlockState());
    }

    /**
     * Removes specific features, in specific generation stages, in all biomes registered
     *
     * @param stage           generation stage where the feature is added to
     * @param featureToRemove feature instance wishing to be removed
     * @param states          BlockStates wish to be removed
     */
    public static void removeDecoratedFeatureFromAllBiomes(BiomeGenerationSettingsBuilder builder, @Nonnull final GenerationStep.Decoration stage, @Nonnull final Feature<?> featureToRemove, BlockState... states) {
        if (states.length == 0) Utils.onInvalidData("No BlockStates specified to be removed!");
        Set<BlockState> set = Set.of(states);
        // AntimatterAPI.runLaterCommon(() -> {
        //  for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
        builder.getFeatures(stage).removeIf(f -> isDecoratedFeatureDisabled(((PlacedFeatureAccessor) f.get()).getFeature().get(), featureToRemove, set));

    }

    /**
     * Removes specific features, in specific generation stages, in specific biomes
     *
     * @param biome           Biome wish to remove feature from
     * @param stage           generation stage where the feature is added to
     * @param featureToRemove feature instance wishing to be removed
     * @param states          BlockStates wish to be removed
     */
    public static void removeDecoratedFeaturesFromBiome(@Nonnull final Biome biome, final @Nonnull GenerationStep.Decoration stage, final @Nonnull Feature<?> featureToRemove, BlockState... states) {
        if (states.length == 0) Utils.onInvalidData("No BlockStates specified to be removed!");
        AntimatterAPI.runLaterCommon(() -> {
            for (BlockState state : states) {
                // biome.getFeatures(stage).removeIf(f -> isDecoratedFeatureDisabled(f, featureToRemove, state));
            }
        });
    }

    /**
     * Check with BlockState in a feature if it is disabled
     */
    public static boolean isDecoratedFeatureDisabled(@Nonnull ConfiguredFeature<?, ?> configuredFeature, @Nonnull Feature<?> featureToRemove, @Nonnull Set<BlockState> state) {
        if (configuredFeature.config instanceof OreConfiguration config) {
            return config.targetStates.stream().anyMatch(t -> state.contains(t.state));
        }
        /*if (configuredFeature.config instanceof Feat) {
            FeatureConfiguration config = configuredFeature.config;
            Feature<?> feature = null;
            while (config instanceof DecoratedFeatureConfiguration) {
                feature = ((DecoratedFeatureConfiguration) config).feature.get().feature;
                config = ((DecoratedFeatureConfiguration) config).feature.get().config;
                if (!(feature instanceof DecoratedFeature)) {
                    break;
                }
            }
            if (feature == null) return false;
            if (feature instanceof OreFeature && featureToRemove == Feature.ORE) {
                OreConfiguration conf = (OreConfiguration) config;
                BlockState configState = conf.state;
                //TODO: state or not?
                return state.getBlock() == configState.getBlock();

            }
            //TODO: should this also be ore or not?
            if (config instanceof BlockStateConfiguration && featureToRemove == Feature.ORE) {
                BlockState configState = ((BlockStateConfiguration) config).state; // Constructor BlockState var
                return state == configState;
            }
        }*/
        return false;
    }


    public static void reloadEvent(BiomeLoadingEvent event) {
        AntimatterAPI.all(AntimatterFeature.class, t -> {
            t.build(event.getGeneration());
        });
        AntimatterAPI.all(GenHandler.class, t -> {
            if (event.getName() == null) return;
            if (t.validator.test(event.getCategory())) {
                t.consumer.accept(event);
            }
        });
        handleFeatureRemoval(event);
    }

    private static void handleFeatureRemoval(BiomeLoadingEvent event) {
        BiomeGenerationSettingsBuilder builder = event.getGeneration();
        if (AntimatterConfig.WORLD.VANILLA_ORE_GEN) {
            removeOreFeatures(builder);
        }
        if (AntimatterConfig.WORLD.VANILLA_STONE_GEN) {
            removeStoneFeatures(builder);
        }
    }
}