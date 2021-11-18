package muramasa.antimatter.worldgen;

import com.google.gson.JsonObject;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Ref;
import muramasa.antimatter.integration.kubejs.AntimatterKubeJS;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.util.Utils;
import muramasa.antimatter.worldgen.feature.*;
import muramasa.antimatter.worldgen.object.WorldGenBase;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.RegistryKey;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class AntimatterWorldGenerator {

    static final AntimatterFeature<NoFeatureConfig> VEIN_LAYER = new FeatureVeinLayer();
    static final AntimatterFeature<NoFeatureConfig> ORE = new FeatureOre();
    static final AntimatterFeature<NoFeatureConfig> ORE_SMALL = new FeatureOreSmall();
    static final AntimatterFeature<NoFeatureConfig> STONE_LAYER = new FeatureStoneLayer();
    static final AntimatterFeature<NoFeatureConfig> SURFACE_ROCK = new FeatureSurfaceRock();

    protected static class GenHandler {
        public final Consumer<BiomeLoadingEvent> consumer;
        public final Predicate<Biome.Category> validator;

        public GenHandler(Consumer<BiomeLoadingEvent> consumer, Predicate<Biome.Category> validator) {
            this.consumer = consumer;
            this.validator = validator;
        }
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

    public static void register(Consumer<BiomeLoadingEvent> consumer, String id, String domain, Predicate<Biome.Category> validator) {
        AntimatterAPI.register(GenHandler.class, id, domain, new GenHandler(consumer, validator));
    }

    public static <T> List<T> all(Class<T> c, RegistryKey<World> dim) {
        AntimatterFeature<?> feat = AntimatterAPI.get(AntimatterFeature.class, c.getName());
        return feat != null ? feat.getRegistry().computeIfAbsent(dim.location(), k -> new LinkedList<>()).stream().map(c::cast).collect(Collectors.toList()) : Collections.emptyList();
    }

    private static void removeStoneFeatures(BiomeGenerationSettingsBuilder builder) {
        removeDecoratedFeatureFromAllBiomes(builder, GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE, Blocks.ANDESITE.defaultBlockState(), Blocks.GRANITE.defaultBlockState(), Blocks.DIORITE.defaultBlockState());
    }

    private static void removeOreFeatures(BiomeGenerationSettingsBuilder builder) {
        removeDecoratedFeatureFromAllBiomes(builder, GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE, Blocks.COAL_ORE.defaultBlockState(), Blocks.IRON_ORE.defaultBlockState(), Blocks.GOLD_ORE.defaultBlockState(), Blocks.REDSTONE_ORE.defaultBlockState(), Blocks.LAPIS_ORE.defaultBlockState(), Blocks.DIAMOND_ORE.defaultBlockState());
    }

    /**
     * Removes specific features, in specific generation stages, in all biomes registered
     *
     * @param stage           generation stage where the feature is added to
     * @param featureToRemove feature instance wishing to be removed
     * @param states          BlockStates wish to be removed
     */
    public static void removeDecoratedFeatureFromAllBiomes(BiomeGenerationSettingsBuilder builder, @Nonnull final GenerationStage.Decoration stage, @Nonnull final Feature<?> featureToRemove, BlockState... states) {
        if (states.length == 0) Utils.onInvalidData("No BlockStates specified to be removed!");
        // AntimatterAPI.runLaterCommon(() -> {
        //  for (Biome biome : ForgeRegistries.BIOMES.getValues()) {
        for (BlockState state : states) {
            builder.getFeatures(stage).removeIf(f -> isDecoratedFeatureDisabled(f.get(), featureToRemove, state));
        }
        //  }
        // });
    }

    /**
     * Removes specific features, in specific generation stages, in specific biomes
     *
     * @param biome           Biome wish to remove feature from
     * @param stage           generation stage where the feature is added to
     * @param featureToRemove feature instance wishing to be removed
     * @param states          BlockStates wish to be removed
     */
    public static void removeDecoratedFeaturesFromBiome(@Nonnull final Biome biome, final @Nonnull GenerationStage.Decoration stage, final @Nonnull Feature<?> featureToRemove, BlockState... states) {
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
    public static boolean isDecoratedFeatureDisabled(@Nonnull ConfiguredFeature<?, ?> configuredFeature, @Nonnull Feature<?> featureToRemove, @Nonnull BlockState state) {
        if (configuredFeature.config instanceof DecoratedFeatureConfig) {
            IFeatureConfig config = configuredFeature.config;
            Feature<?> feature = null;
            while (config instanceof DecoratedFeatureConfig) {
                feature = ((DecoratedFeatureConfig) config).feature.get().feature;
                config = ((DecoratedFeatureConfig) config).feature.get().config;
                if (!(feature instanceof DecoratedFeature)) {
                    break;
                }
            }
            if (feature == null) return false;
            if (feature instanceof OreFeature && featureToRemove == Feature.ORE) {
                OreFeatureConfig conf = (OreFeatureConfig) config;
                BlockState configState = conf.state;
                //TODO: state or not?
                return state.getBlock() == configState.getBlock();

            }
            //TODO: should this also be ore or not?
            if (config instanceof BlockStateFeatureConfig && featureToRemove == Feature.ORE) {
                BlockState configState = ((BlockStateFeatureConfig) config).state; // Constructor BlockState var
                return state == configState;
            }
        }
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