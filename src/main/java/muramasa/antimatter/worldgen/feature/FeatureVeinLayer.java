package muramasa.antimatter.worldgen.feature;

import muramasa.antimatter.Configs;
import muramasa.antimatter.worldgen.WorldGenVeinLayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;

public class FeatureVeinLayer extends Feature<NoFeatureConfig> implements IAntimatterFeature {

    public FeatureVeinLayer() {
        super(null);
    }

    @Override
    public String getId() {
        return "feature_vein_layer";
    }

    @Override
    public void init() {
        for (Biome biome : ForgeRegistries.BIOMES) {
            biome.addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, new ConfiguredFeature<>(this, IFeatureConfig.NO_FEATURE_CONFIG));
        }
    }

    @Override
    public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        // Determine bounding box on how far out to check for ore veins affecting this chunk
        int westX = chunkX - (Configs.WORLD.ORE_VEIN_MAX_SIZE / 16);
        int eastX = chunkX + (Configs.WORLD.ORE_VEIN_MAX_SIZE / 16 + 1); // Need to add 1 since it is compared using a <
        int northZ = chunkZ - (Configs.WORLD.ORE_VEIN_MAX_SIZE / 16);
        int southZ = chunkZ + (Configs.WORLD.ORE_VEIN_MAX_SIZE / 16 + 1);

        // Search for oreVein seeds and add to the list;
        for (int x = westX; x < eastX; x++) {
            for (int z = northZ; z < southZ; z++) {
                if (((Math.abs(x) % 3) == 1) && ((Math.abs(z) % 3) == 1)) { //Determine if this X/Z is an oreVein seed
                    WorldGenVeinLayer.generate(world, chunkX, chunkZ, x, z);
                }
            }
        }
        return true;
    }
}
