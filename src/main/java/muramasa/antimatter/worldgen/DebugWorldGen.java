package muramasa.antimatter.worldgen;

import com.mojang.datafixers.Dynamic;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.Configs;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.placement.CountConfig;
import net.minecraft.world.gen.placement.FrequencyConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.block.*;

import java.util.Random;
import java.util.function.Function;
import java.util.logging.Logger;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DebugWorldGen {
    /*static class DebugAtSurface extends Placement<FrequencyConfig> {
        public DebugAtSurface() {
            super(FrequencyConfig::deserialize);
        }

        public Stream<BlockPos> getPositions(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generatorIn, Random random, FrequencyConfig configIn, BlockPos pos) {
            return IntStream.range(0, configIn.count).mapToObj((p_227442_3_) -> {
                int i = random.nextInt(16) + pos.getX();
                int j = random.nextInt(16) + pos.getZ();
                int k = worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING, i, j);
                return new BlockPos(i, k, j);
            });
        }
    }*/
    static class DefaultOreGen extends Feature<NoFeatureConfig>{

        public DefaultOreGen() {
            super(null);
        }

        @Override
        public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
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
                        WorldGenOreVein.generate(worldIn, chunkX, chunkZ, x, z);
                    }
                }
            }
            return true;
        }
    }
    public static void init() {
        WorldGenHelper.init();
        AntimatterWorldGenerator.reload();
        for(Biome biome : ForgeRegistries.BIOMES) {
/*           biome.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION,
                    Feature.SIMPLE_BLOCK.withConfiguration(new BlockWithContextConfig(
                            Blocks.DIAMOND_BLOCK.getDefaultState(),
                            new BlockState[]{Blocks.GRASS_BLOCK.getDefaultState()},
                            new BlockState[]{Blocks.AIR.getDefaultState()},
                            new BlockState[]{Blocks.AIR.getDefaultState()})
                    ).func_227228_a_(new DebugAtSurface().func_227446_a_(
                            new FrequencyConfig(2))));
*/
            biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, new ConfiguredFeature(new DefaultOreGen(), IFeatureConfig.NO_FEATURE_CONFIG));
        }
    }
}
