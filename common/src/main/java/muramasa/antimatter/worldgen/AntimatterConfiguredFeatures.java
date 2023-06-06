package muramasa.antimatter.worldgen;

import muramasa.antimatter.Ref;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Collections;

public class AntimatterConfiguredFeatures {
    
    public static final Holder<PlacedFeature> VEIN = register("vein", FeatureUtils.register("vein", AntimatterWorldGenerator.VEIN, NoneFeatureConfiguration.NONE));
    public static final Holder<PlacedFeature> SMALL_ORES = register("small_ores", FeatureUtils.register("small_ores", AntimatterWorldGenerator.SMALL_ORE, NoneFeatureConfiguration.NONE));

    public static final Holder<PlacedFeature> VANILLA_ORES = register("vanilla_ores", FeatureUtils.register("vanilla_ores", AntimatterWorldGenerator.VANILLA_ORE, NoneFeatureConfiguration.NONE));
    public static final Holder<PlacedFeature> VEIN_LAYER = register("vein_layer", FeatureUtils.register("vein_layer", AntimatterWorldGenerator.VEIN_LAYER, NoneFeatureConfiguration.NONE));
    public static final Holder<PlacedFeature> ORE = register("ore", FeatureUtils.register("ore", AntimatterWorldGenerator.ORE, NoneFeatureConfiguration.NONE));
    public static final Holder<PlacedFeature> STONE_LAYER = register("stone_layer", FeatureUtils.register("stone_layer", AntimatterWorldGenerator.STONE_LAYER, NoneFeatureConfiguration.NONE));

    @SuppressWarnings("unchecked")
    public static <T extends FeatureConfiguration> Holder<PlacedFeature> register(String id, Holder<ConfiguredFeature<T, ?>> feature) {
        return PlacementUtils.register("antimatter:"+id, feature, Collections.emptyList());
    }

    public static void init() {
    }
}
