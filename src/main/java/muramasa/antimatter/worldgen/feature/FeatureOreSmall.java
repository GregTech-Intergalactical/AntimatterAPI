package muramasa.antimatter.worldgen.feature;

import muramasa.antimatter.Configs;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import muramasa.antimatter.worldgen.WorldGenHelper;
import muramasa.antimatter.worldgen.object.WorldGenOreSmall;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
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

public class FeatureOreSmall extends AntimatterFeature<NoFeatureConfig> {

    public FeatureOreSmall() {
        super(NoFeatureConfig::deserialize, WorldGenOreSmall.class);
    }

    @Override
    public String getId() {
        return "feature_ore_small";
    }

    @Override
    public boolean enabled() {
        return Configs.WORLD.ENABLE_SMALL_ORES && getRegistry().size() > 0;
    }

    @Override
    public void init() {
        for (Biome biome : ForgeRegistries.BIOMES) {
            biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, new ConfiguredFeature<>(this, IFeatureConfig.NO_FEATURE_CONFIG));
        }
    }

    @Override
    public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        List<WorldGenOreSmall> ores = AntimatterWorldGenerator.all(WorldGenOreSmall.class, world.getDimension().getType().getId());
        BlockPos.Mutable mut = new BlockPos.Mutable();
        int amount;
        BlockState existing;
        for (WorldGenOreSmall ore : ores) {
            amount = Math.max(1, ore.getAmount() / 2 + rand.nextInt(1 + ore.getAmount()) / 2);
            for (int i = 0; i < amount; i++) {
                mut.setPos(pos.getX() + rand.nextInt(16), ore.getMinY() + rand.nextInt(Math.max(1, ore.getMaxY() - ore.getMinY())), pos.getZ() + rand.nextInt(16));
                existing = world.getBlockState(mut);
                WorldGenHelper.setOre(world, mut, existing, ore.getMaterial(), MaterialType.ORE_SMALL);
            }
        }
        return true;
    }
}
