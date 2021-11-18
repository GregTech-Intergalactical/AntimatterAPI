package muramasa.antimatter.worldgen.feature;

import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.worldgen.*;
import muramasa.antimatter.worldgen.object.WorldGenStoneLayer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Random;

public class FeatureStoneLayer extends AntimatterFeature<NoFeatureConfig> {

    public FeatureStoneLayer() {
        super(NoFeatureConfig.CODEC, WorldGenStoneLayer.class);
    }

    @Override
    public String getId() {
        return "feature_stone_layer";
    }

    @Override
    public boolean enabled() {
        return AntimatterConfig.WORLD.STONE_LAYERS && getRegistry().size() > 0;
    }

    @Override
    public void init() {
        for (Biome biome : ForgeRegistries.BIOMES) {
            // biome.addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, new ConfiguredFeature<>(this, IFeatureConfig.NO_FEATURE_CONFIG));
        }
    }

    @Override
    public void build(BiomeGenerationSettingsBuilder event) {
        event.addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, AntimatterConfiguredFeatures.STONE_LAYER);
    }

    @Override
    public boolean place(ISeedReader world, ChunkGenerator generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        List<WorldGenStoneLayer> stones = AntimatterWorldGenerator.all(WorldGenStoneLayer.class, world.getLevel().dimension());
        if (stones.size() == 0) return false;
        WorldGenStoneLayer[] layers = new WorldGenStoneLayer[7];
        NoiseGenerator noise = new NoiseGenerator(world);
        int stonesSize = stones.size(), stonesMax = stonesSize - 1;
        BlockState existing;
        int maxHeight;
        boolean isAir;
        Material lastMaterial;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int tX = pos.getX() + i, tZ = pos.getZ() + j;

                layers[0] = stones.get(Math.min(stonesMax, (int) (((noise.get(tX, -2, tZ) + 1) / 2) * stonesSize)));
                layers[1] = stones.get(Math.min(stonesMax, (int) (((noise.get(tX, -1, tZ) + 1) / 2) * stonesSize)));
                layers[2] = stones.get(Math.min(stonesMax, (int) (((noise.get(tX, 0, tZ) + 1) / 2) * stonesSize)));
                layers[3] = stones.get(Math.min(stonesMax, (int) (((noise.get(tX, 1, tZ) + 1) / 2) * stonesSize)));
                layers[4] = stones.get(Math.min(stonesMax, (int) (((noise.get(tX, 2, tZ) + 1) / 2) * stonesSize)));
                layers[5] = stones.get(Math.min(stonesMax, (int) (((noise.get(tX, 3, tZ) + 1) / 2) * stonesSize)));
                layers[6] = stones.get(Math.min(stonesMax, (int) (((noise.get(tX, 4, tZ) + 1) / 2) * stonesSize)));

                maxHeight = world.getHeightmapPos(Heightmap.Type.OCEAN_FLOOR_WG, pos.offset(i, 0, j)).getY() + 1; //+1 for placing rocks on top of the max height
                for (int tY = 1; tY < maxHeight; tY++) {
                    lastMaterial = null;
                    existing = world.getBlockState(pos.offset(i, tY, j));
                    isAir = existing.isAir(world, pos.offset(i, tY, j));

                    //If we haven't placed an ore, and not trying to set the same state as existing
                    if (!isAir && /*lastMaterial == null &&*/ existing != layers[3].getStoneState()) {
                        if (WorldGenHelper.setStone(world, pos.offset(i, tY, j), existing, layers[3])) {
                            lastMaterial = layers[3].getStoneType() != null ? layers[3].getStoneType().getMaterial() : null;
                        }
                    }

                    if (!isAir && AntimatterConfig.WORLD.STONE_LAYER_ORES) {
                        if (layers[1] == layers[5]) {
                            for (StoneLayerOre ore : layers[3].getOres()) {
                                if (ore.canPlace(pos.offset(i, tY, j), rand) && WorldGenHelper.addOre(world, pos.offset(i, tY, j), ore.getMaterial(), layers[0] == layers[6])) {
                                    lastMaterial = ore.getMaterial();
                                    break;
                                }
                            }
                        } else {
                            for (StoneLayerOre ore : WorldGenStoneLayer.getCollision(layers[3].getStoneType(), layers[5].getStoneState(), layers[1].getStoneState())) {
                                if (ore.canPlace(pos.offset(i, tY, j), rand) && WorldGenHelper.addOre(world, pos.offset(i, tY, j), ore.getMaterial(), true)) {
                                    lastMaterial = ore.getMaterial();
                                    break;
                                }
                            }
                        }
                    }

                    if ((isAir || WorldGenHelper.ROCK_SET.contains(existing)) && lastMaterial != null) {
                        BlockState below = world.getBlockState(pos.offset(i, tY - 1, j));
                        if (!below.isAir(world, pos.offset(i, tY - 1, j)) && below != WorldGenHelper.WATER_STATE) {
                            WorldGenHelper.addRockRaw(world, pos.offset(i, tY, j), lastMaterial, AntimatterConfig.WORLD.STONE_LAYER_ROCK_CHANCE);
                        }
                    }

                    // And scan for next Block on the Stone Layer Type.
                    System.arraycopy(layers, 1, layers, 0, layers.length - 1);
                    layers[6] = stones.get(Math.min(stonesMax, (int) (((noise.get(tX, tY + 4, tZ) + 1) / 2) * stonesSize)));
                }
            }
        }
        return true;
    }
}
