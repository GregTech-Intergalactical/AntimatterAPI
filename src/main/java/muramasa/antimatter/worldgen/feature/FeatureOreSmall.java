package muramasa.antimatter.worldgen.feature;

import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Data;
import muramasa.antimatter.worldgen.AntimatterConfiguredFeatures;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import muramasa.antimatter.worldgen.WorldGenHelper;
import muramasa.antimatter.worldgen.object.WorldGenOreSmall;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;

import java.util.List;
import java.util.Random;

public class FeatureOreSmall extends AntimatterFeature<NoFeatureConfig> {

    public FeatureOreSmall() {
        super(NoFeatureConfig.CODEC, WorldGenOreSmall.class);
    }

    @Override
    public String getId() {
        return "feature_ore_small";
    }

    @Override
    public boolean enabled() {
        return AntimatterConfig.WORLD.SMALL_ORES && getRegistry().size() > 0;
    }

    @Override
    public void init() {

    }

    @Override
    public void build(BiomeGenerationSettingsBuilder event) {
        event.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, AntimatterConfiguredFeatures.ORE_SMALL);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        List<WorldGenOreSmall> ores = AntimatterWorldGenerator.all(WorldGenOreSmall.class, world.getLevel().dimension());
        BlockPos.Mutable mut = new BlockPos.Mutable();
        int amount;
        BlockState existing;
        for (WorldGenOreSmall ore : ores) {
            amount = Math.max(1, ore.getAmount() / 2 + rand.nextInt(1 + ore.getAmount()) / 2);
            for (int i = 0; i < amount; i++) {
                mut.set(pos.getX() + rand.nextInt(16), ore.getMinY() + rand.nextInt(Math.max(1, ore.getMaxY() - ore.getMinY())), pos.getZ() + rand.nextInt(16));
                existing = world.getBlockState(mut);
                WorldGenHelper.setOre(world, mut, existing, ore.getMaterial(), Data.ORE_SMALL);
            }
        }
        return true;
    }
}
