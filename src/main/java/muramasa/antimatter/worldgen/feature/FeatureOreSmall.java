package muramasa.antimatter.worldgen.feature;

import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Data;
import muramasa.antimatter.worldgen.AntimatterConfiguredFeatures;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import muramasa.antimatter.worldgen.WorldGenHelper;
import muramasa.antimatter.worldgen.object.WorldGenOreSmall;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;

import java.util.List;
import java.util.Random;

public class FeatureOreSmall extends AntimatterFeature<NoneFeatureConfiguration> {

    public FeatureOreSmall() {
        super(NoneFeatureConfiguration.CODEC, WorldGenOreSmall.class);
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
        event.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, AntimatterConfiguredFeatures.ORE_SMALL);
    }


    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctxt) {
        WorldGenLevel world = ctxt.level();
        BlockPos pos = ctxt.origin();
        Random rand = ctxt.random();
        List<WorldGenOreSmall> ores = AntimatterWorldGenerator.all(WorldGenOreSmall.class, world.getLevel().dimension());
        BlockPos.MutableBlockPos mut = new BlockPos.MutableBlockPos();
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
        return true;    }
}
