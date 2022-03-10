package muramasa.antimatter.worldgen;

import muramasa.antimatter.Ref;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.FeatureUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

import java.util.Collections;

public class AntimatterConfiguredFeatures {
    
    public static final PlacedFeature VEIN = register("vein", FeatureUtils.register("vein", AntimatterWorldGenerator.VEIN, NoneFeatureConfiguration.NONE));

    @SuppressWarnings("unchecked")
    public static <T extends FeatureConfiguration> PlacedFeature register(String id, Holder<ConfiguredFeature<T, ?>> feature) {
        Holder<ConfiguredFeature<?,?>> v = (Holder<ConfiguredFeature<?,?>>) (Holder) feature;
        return Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(Ref.ID, id), new PlacedFeature(v, Collections.emptyList()));
    }

    public static void init() {
    }
}
