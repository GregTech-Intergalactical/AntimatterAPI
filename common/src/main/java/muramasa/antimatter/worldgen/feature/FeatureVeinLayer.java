package muramasa.antimatter.worldgen.feature;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.worldgen.AntimatterConfiguredFeatures;
import muramasa.antimatter.worldgen.vein.WorldGenVeinLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.List;

public class FeatureVeinLayer extends AntimatterFeature<NoneFeatureConfiguration> {

    public FeatureVeinLayer() {
        super(NoneFeatureConfiguration.CODEC, WorldGenVeinLayer.class);
    }

    @Override
    public String getId() {
        return "feature_vein_layer";
    }

    @Override
    public boolean enabled() {
        return AntimatterConfig.ORE_VEINS.get() && getRegistry().size() > 0;
    }

    @Override
    public void init() {

    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> placer) {
        int chunkX = placer.origin().getX() >> 4;
        int chunkZ = placer.origin().getZ() >> 4;
        for (Tuple<Integer, Integer> seed : getVeinSeeds(chunkX, chunkZ)) {
            WorldGenVeinLayer.generate(placer.level(), chunkX, chunkZ, seed.getA(), seed.getB());
        }
        return true;
    }

    public static List<Tuple<Integer, Integer>> getVeinSeeds(int chunkX, int chunkZ) {
        // Determine bounding box on how far out to check for ore veins affecting this chunk
        int westX = chunkX - (AntimatterConfig.ORE_VEIN_MAX_SIZE.get() / 16);
        int eastX = chunkX + (AntimatterConfig.ORE_VEIN_MAX_SIZE.get() / 16 + 1); // Need to add 1 since it is compared using a <
        int northZ = chunkZ - (AntimatterConfig.ORE_VEIN_MAX_SIZE.get() / 16);
        int southZ = chunkZ + (AntimatterConfig.ORE_VEIN_MAX_SIZE.get() / 16 + 1);
        List<Tuple<Integer, Integer>> res = new ObjectArrayList<>();
        // Search for oreVein seeds and add to the list;
        for (int x = westX; x < eastX; x++) {
            for (int z = northZ; z < southZ; z++) {
                if (((Math.abs(x) % 3) == 1) && ((Math.abs(z) % 3) == 1)) { //Determine if this X/Z is an oreVein seed
                    res.add(new Tuple<>(x, z));
                }
            }
        }
        return res;
    }

    @Override
    public void build(ResourceLocation name, Biome.ClimateSettings climate, BiomeSpecialEffects effects, BiomeGenerationSettings.Builder gen, MobSpawnSettings.Builder spawns) {
        gen.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, AntimatterConfiguredFeatures.VEIN_LAYER);
    }
}
