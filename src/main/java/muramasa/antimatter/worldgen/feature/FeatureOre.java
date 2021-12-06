package muramasa.antimatter.worldgen.feature;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Data;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.worldgen.AntimatterConfiguredFeatures;
import muramasa.antimatter.worldgen.WorldGenHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Random;

public class FeatureOre extends AntimatterFeature<NoneFeatureConfiguration> {

    public static final Object2ObjectOpenHashMap<ChunkPos, List<Triple<BlockPos, Material, Boolean>>> ORES = new Object2ObjectOpenHashMap<>();

    public FeatureOre() {
        super(NoneFeatureConfiguration.CODEC, FeatureOre.class);
    }

    @Override
    public boolean enabled() {
        return AntimatterConfig.WORLD.STONE_LAYER_ORES;
    }

    @Override
    public void init() {
        for (Biome biome : ForgeRegistries.BIOMES) {
            // biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, new ConfiguredFeature<>(this, IFeatureConfig.NO_FEATURE_CONFIG));
        }
    }

    @Override
    public String getId() {
        return "feature_ore";
    }


    @Override
    public void build(BiomeGenerationSettingsBuilder event) {
        event.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, AntimatterConfiguredFeatures.ORE);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel world = context.level();
        BlockPos pos = context.origin();
        List<Triple<BlockPos, Material, Boolean>> ores = ORES.remove(world.getChunk(pos).getPos());
        if (ores == null) return false;
        for (Triple<BlockPos, Material, Boolean> o : ores) {
            WorldGenHelper.setOre(world, o.getLeft(), world.getBlockState(o.getLeft()), o.getMiddle(), o.getRight() ? Data.ORE : Data.ORE_SMALL);
        }
        return true;
    }
}
