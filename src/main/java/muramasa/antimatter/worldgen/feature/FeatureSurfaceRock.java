package muramasa.antimatter.worldgen.feature;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Data;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.worldgen.AntimatterConfiguredFeatures;
import muramasa.antimatter.worldgen.WorldGenHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;

import java.util.List;


public class FeatureSurfaceRock extends AntimatterFeature<NoneFeatureConfiguration> {

    public static final Object2ObjectOpenHashMap<ChunkPos, List<Tuple<BlockPos, Material>>> ROCKS = new Object2ObjectOpenHashMap<>();

    public FeatureSurfaceRock() {
        super(NoneFeatureConfiguration.CODEC, FeatureSurfaceRock.class);
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
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> placer) {
        WorldGenLevel world = placer.level();
        BlockPos pos = placer.origin();
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
        event.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, AntimatterConfiguredFeatures.SURFACE_ROCK);
    }
}
