package muramasa.antimatter.worldgen.feature;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Data;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.worldgen.AntimatterConfiguredFeatures;
import muramasa.antimatter.worldgen.WorldGenHelper;
import net.minecraft.block.BlockState;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;

import java.util.List;
import java.util.Random;


public class FeatureSurfaceRock extends AntimatterFeature<NoFeatureConfig> {

    public static final Object2ObjectOpenHashMap<ChunkPos, List<Tuple<BlockPos, Material>>> ROCKS = new Object2ObjectOpenHashMap<>();

    public FeatureSurfaceRock() {
        super(NoFeatureConfig.CODEC, FeatureSurfaceRock.class);
    }

    @Override
    public boolean enabled() {
        return AntimatterConfig.WORLD.SURFACE_ROCKS;
    }

    @Override
    public void init() {

    }

    @Override
    public String getId() {
        return "feature_surface_rocks";
    }


    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        List<Tuple<BlockPos, Material>> rocks = ROCKS.remove(world.getChunk(pos).getPos());
        if (rocks == null) return false;
        StoneType stoneType;
        for (Tuple<BlockPos, Material> r : rocks) {
            stoneType = WorldGenHelper.STONE_MAP.get(world.getBlockState(r.getA().below()));
            if (stoneType == null)
                stoneType = StoneType.get("stone"); //TODO change to direct ref when vanilla types are in AM
            BlockState rockState = Data.ROCK.get().get(r.getB(), stoneType).asState();
            if (world.getBlockState(r.getA()) == WorldGenHelper.WATER_STATE)
                rockState = WorldGenHelper.waterLogState(rockState);
            WorldGenHelper.setState(world, r.getA(), rockState);
        }
        return true;
    }

    @Override
    public void build(BiomeGenerationSettingsBuilder event) {
        event.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, AntimatterConfiguredFeatures.SURFACE_ROCK);
    }
}
