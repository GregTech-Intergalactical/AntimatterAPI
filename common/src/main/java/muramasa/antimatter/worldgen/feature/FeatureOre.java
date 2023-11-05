package muramasa.antimatter.worldgen.feature;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.worldgen.AntimatterConfiguredFeatures;
import muramasa.antimatter.worldgen.WorldGenHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;

public class FeatureOre extends AntimatterFeature<NoneFeatureConfiguration> {

    public static final Object2ObjectOpenHashMap<ChunkPos, List<Triple<BlockPos, Material, Boolean>>> ORES = new Object2ObjectOpenHashMap<>();

    public FeatureOre() {
        super(NoneFeatureConfiguration.CODEC, FeatureOre.class);
    }

    @Override
    public boolean enabled() {
        return AntimatterConfig.STONE_LAYER_ORES.get();
    }

    @Override
    public void init() {
    }

    @Override
    public String getId() {
        return "feature_ore";
    }


    @Override
    public void build(ResourceLocation name, Biome.ClimateSettings climate, Biome.BiomeCategory category, BiomeSpecialEffects effects, BiomeGenerationSettings.Builder gen, MobSpawnSettings.Builder spawns) {
        gen.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, AntimatterConfiguredFeatures.ORE);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel world = context.level();
        BlockPos pos = context.origin();
        List<Triple<BlockPos, Material, Boolean>> ores = ORES.remove(world.getChunk(pos).getPos());
        if (ores == null) return false;
        for (Triple<BlockPos, Material, Boolean> o : ores) {
            WorldGenHelper.setOre(world, o.getLeft(), world.getBlockState(o.getLeft()), o.getMiddle(), o.getRight() ? AntimatterMaterialTypes.ORE : AntimatterMaterialTypes.ORE_SMALL);
        }
        return true;
    }
}