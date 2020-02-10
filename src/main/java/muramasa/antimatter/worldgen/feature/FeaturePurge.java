package muramasa.antimatter.worldgen.feature;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import muramasa.antimatter.worldgen.WorldGenHelper;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Random;

public class FeaturePurge extends Feature<NoFeatureConfig> implements IAntimatterFeature {

    //TODO is this needed? StoneLayers should eradicate all unwanted blocks, maybe unless StoneLayers are disabled?
    public FeaturePurge() {
        super(NoFeatureConfig::deserialize);
        AntimatterAPI.register(IAntimatterFeature.class, this);
    }

    @Override
    public String getId() {
        return "feature_purge";
    }

    @Override
    public void init() {
        for (Biome biome : ForgeRegistries.BIOMES) {
            biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, new ConfiguredFeature<>(this, IFeatureConfig.NO_FEATURE_CONFIG));
        }
    }

    @Override
    public boolean enabled() {
        return false;
    }

    @Override
    public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        BlockState mapEntry;
        BlockPos.Mutable mutPos = new BlockPos.Mutable();
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int maxHeight = world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, pos.getX() + i, pos.getZ() + j);
                for (int tY = 0; tY < maxHeight; tY++) {
                    mutPos.setPos(pos.getX() + i, tY, pos.getZ() + j);
                    mapEntry = AntimatterWorldGenerator.STATES_TO_PURGE.get(world.getBlockState(mutPos));
                    if (mapEntry == null) continue;
                    WorldGenHelper.setState(world, mutPos, mapEntry);
                }
            }
        }
        return true;
    }
}
