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
    static class DebugAtSurface extends Placement<FrequencyConfig> {
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
    }
    public static void init() {
        for(Biome biome : ForgeRegistries.BIOMES) {
           biome.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION,
                    Feature.SIMPLE_BLOCK.withConfiguration(new BlockWithContextConfig(
                            Blocks.DIAMOND_BLOCK.getDefaultState(),
                            new BlockState[]{Blocks.GRASS_BLOCK.getDefaultState()},
                            new BlockState[]{Blocks.AIR.getDefaultState()},
                            new BlockState[]{Blocks.AIR.getDefaultState()})
                    ).func_227228_a_(new DebugAtSurface().func_227446_a_(
                            new FrequencyConfig(2))));
        }
    }
}
