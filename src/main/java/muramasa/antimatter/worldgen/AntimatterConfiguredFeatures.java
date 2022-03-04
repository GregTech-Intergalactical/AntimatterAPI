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

    public static final PlacedFeature VEIN = register("vein", AntimatterWorldGenerator.VEIN.configured(NoneFeatureConfiguration.NONE));

    public static PlacedFeature register(String id, ConfiguredFeature<?, ?> feature) {
        return Registry.register(BuiltinRegistries.PLACED_FEATURE, new ResourceLocation(Ref.ID, id), new PlacedFeature(() -> feature, Collections.emptyList()));
    }

    public static void init() {
    }
}
