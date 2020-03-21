package muramasa.antimatter.worldgen.feature;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.Configs;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.worldgen.WorldGenHelper;
import net.minecraft.block.BlockState;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Random;


public class FeatureSurfaceRock extends AntimatterFeature<NoFeatureConfig> {

    public static final Object2ObjectOpenHashMap<ChunkPos, List<Tuple<BlockPos, Material>>> ROCKS = new Object2ObjectOpenHashMap<>();

    public FeatureSurfaceRock() {
        super(NoFeatureConfig::deserialize, FeatureSurfaceRock.class);
    }

    @Override
    public boolean enabled() {
        return Configs.WORLD.ENABLE_SURFACE_ROCKS;
    }

    @Override
    public void init() {
        for (Biome biome : ForgeRegistries.BIOMES) {
            biome.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, new ConfiguredFeature<>(this, IFeatureConfig.NO_FEATURE_CONFIG));
        }
    }

    @Override
    public String getId() {
        return "feature_surface_rocks";
    }

    @Override
    public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        List<Tuple<BlockPos, Material>> rocks = ROCKS.remove(world.getChunk(pos).getPos());
        if (rocks == null) return false;
        StoneType stoneType;
        for (Tuple<BlockPos, Material> r : rocks) {
            stoneType = WorldGenHelper.STONE_MAP.get(world.getBlockState(r.getA().down()));
            if (stoneType == null) stoneType = StoneType.get("stone");
            BlockState rockState = MaterialType.ROCK.get().get(r.getB(), stoneType).asState();
            if (world.getBlockState(r.getA()) == WorldGenHelper.WATER_STATE) rockState = WorldGenHelper.waterLogState(rockState);
            WorldGenHelper.setState(world, r.getA(), rockState);
        }
        return true;
    }
}
