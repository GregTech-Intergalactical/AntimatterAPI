package muramasa.antimatter.worldgen;

import muramasa.antimatter.Ref;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Collections;

public class AntimatterConfiguredFeatures {

    public static final PlacedFeature VEIN_LAYER = register("stone_layer", AntimatterWorldGenerator.VEIN_LAYER.configured(NoneFeatureConfiguration.NONE));
    public static final PlacedFeature ORE = register("ore", AntimatterWorldGenerator.ORE.configured(NoneFeatureConfiguration.NONE));
    public static final PlacedFeature ORE_SMALL = register("ore_small", AntimatterWorldGenerator.ORE_SMALL.configured(NoneFeatureConfiguration.NONE));
    public static final PlacedFeature STONE_LAYER = register("stone_layer", AntimatterWorldGenerator.STONE_LAYER.configured(NoneFeatureConfiguration.NONE));
    public static final PlacedFeature SURFACE_ROCK = register("surface_rock", AntimatterWorldGenerator.SURFACE_ROCK.configured(NoneFeatureConfiguration.NONE));

    public static PlacedFeature register(String id, ConfiguredFeature<?, ?> feature) {
        return Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(Ref.ID, id), new PlacedFeature(() -> feature, Collections.emptyList()));
    }

    public static void init() {
    }
}
