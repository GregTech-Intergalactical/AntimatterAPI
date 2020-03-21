package muramasa.antimatter.worldgen.feature;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.Configs;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.worldgen.WorldGenHelper;
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
import org.apache.commons.lang3.tuple.Triple;

import java.util.List;
import java.util.Random;

public class FeatureOre extends AntimatterFeature<NoFeatureConfig> {

    public static final Object2ObjectOpenHashMap<ChunkPos, List<Triple<BlockPos, Material, Boolean>>> ORES = new Object2ObjectOpenHashMap<>();

    public FeatureOre() {
        super(NoFeatureConfig::deserialize, FeatureOre.class);
    }

    @Override
    public boolean enabled() {
        return Configs.WORLD.ENABLE_STONE_LAYER_ORES;
    }

    @Override
    public void init() {
        for (Biome biome : ForgeRegistries.BIOMES) {
            biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, new ConfiguredFeature<>(this, IFeatureConfig.NO_FEATURE_CONFIG));
        }
    }

    @Override
    public String getId() {
        return "feature_ore";
    }

    @Override
    public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        List<Triple<BlockPos, Material, Boolean>> ores = ORES.remove(world.getChunk(pos).getPos());
        if (ores == null) return false;
        for (Triple<BlockPos, Material, Boolean> o : ores) {
            WorldGenHelper.setOre(world, o.getLeft(), world.getBlockState(o.getLeft()), o.getMiddle(), o.getRight() ? MaterialType.ORE : MaterialType.ORE_SMALL);
        }
        return true;
    }
}
