package muramasa.antimatter.worldgen.feature;

import muramasa.antimatter.Configs;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import muramasa.antimatter.worldgen.NoiseGenerator;
import muramasa.antimatter.worldgen.StoneLayerOre;
import muramasa.antimatter.worldgen.WorldGenHelper;
import muramasa.antimatter.worldgen.object.WorldGenStoneLayer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.List;
import java.util.Random;

public class FeatureStoneLayer extends AntimatterFeature<NoFeatureConfig> {

    public FeatureStoneLayer() {
        super(NoFeatureConfig::deserialize, WorldGenStoneLayer.class);
    }

    @Override
    public String getId() {
        return "feature_stone_layer";
    }

    @Override
    public boolean enabled() {
        return Configs.WORLD.ENABLE_STONE_LAYERS && getRegistry().size() > 0;
    }

    @Override
    public void init() {
        for (Biome biome : ForgeRegistries.BIOMES) {
            biome.addFeature(GenerationStage.Decoration.UNDERGROUND_STRUCTURES, new ConfiguredFeature<>(this, IFeatureConfig.NO_FEATURE_CONFIG));
        }
    }

    @Override
    public boolean place(IWorld world, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        List<WorldGenStoneLayer> stones = AntimatterWorldGenerator.all(WorldGenStoneLayer.class, world.getDimension().getType().getId());
        WorldGenStoneLayer[] layers = new WorldGenStoneLayer[7];
        NoiseGenerator noise = new NoiseGenerator(world);
        int stonesSize = stones.size(), stonesMax = stonesSize - 1;
        BlockState existing;
        int maxHeight;
        boolean isAir;
        StoneType lastStone;
        Material lastOre;
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int tX = pos.getX() + i, tZ = pos.getZ() + j;
                lastOre = null;
                lastStone = null;

                layers[0] = stones.get(Math.min(stonesMax, (int) (((noise.get(tX, -2, tZ) + 1) / 2) * stonesSize)));
                layers[1] = stones.get(Math.min(stonesMax, (int) (((noise.get(tX, -1, tZ) + 1) / 2) * stonesSize)));
                layers[2] = stones.get(Math.min(stonesMax, (int) (((noise.get(tX, 0, tZ) + 1) / 2) * stonesSize)));
                layers[3] = stones.get(Math.min(stonesMax, (int) (((noise.get(tX, 1, tZ) + 1) / 2) * stonesSize)));
                layers[4] = stones.get(Math.min(stonesMax, (int) (((noise.get(tX, 2, tZ) + 1) / 2) * stonesSize)));
                layers[5] = stones.get(Math.min(stonesMax, (int) (((noise.get(tX, 3, tZ) + 1) / 2) * stonesSize)));
                layers[6] = stones.get(Math.min(stonesMax, (int) (((noise.get(tX, 4, tZ) + 1) / 2) * stonesSize)));

                //if (layers[3].getType().getState() == Blocks.STONE.getDefaultState()) continue;

                maxHeight = world.getHeight(Heightmap.Type.WORLD_SURFACE_WG, pos.add(i, 0, j)).getY() + 1; //+1 for placing rocks on top of the max height
                for (int tY = 1; tY < maxHeight; tY++) {
                    existing = world.getBlockState(pos.add(i, tY, j));
                    isAir = existing.isAir(world, pos.add(i, tY, j));
                    if (!isAir && Configs.WORLD.ENABLE_STONE_LAYER_ORES) {
                        if (layers[1] == layers[5]) {
                            for (StoneLayerOre ore : layers[3].getOres()) {
                                if (ore.canPlace(pos.add(i, tY, j), rand) && WorldGenHelper.setOre(world, pos.add(i, tY, j), existing, ore, layers[0] == layers[6])) {
                                    lastOre = ore.getMaterial();
                                    break;
                                }
                            }
                        } else {
                            for (StoneLayerOre ore : WorldGenStoneLayer.getCollision(layers[3].getStoneType(), layers[5].getStoneState(), layers[1].getStoneState())) {
                                if (ore.canPlace(pos.add(i, tY, j), rand) && WorldGenHelper.setOre(world, pos.add(i, tY, j), existing, ore, true)) {
                                    lastOre = ore.getMaterial();
                                    break;
                                }
                            }
                        }

                        //If we haven't placed an ore, and not trying to set the same state as existing
                        if (lastOre == null && existing != layers[3].getStoneState()) {
                            if (WorldGenHelper.setStone(world, pos.add(i, tY, j), existing, layers[3])) {
                                lastStone = layers[3].getStoneType();
                            }
                        }
                    }
                    if ((isAir || WorldGenHelper.ROCK_SET.contains(existing)) && (lastOre != null || lastStone != null)) {
                        BlockState below = world.getBlockState(pos.add(i, tY - 1, j));
                        if (!below.isAir(world, pos.add(i, tY - 1, j)) && below != WorldGenHelper.WATER_STATE) {
                            WorldGenHelper.addRockRaw(world, pos.add(i, tY, j), lastOre != null ? lastOre : lastStone.getMaterial(), Configs.WORLD.STONE_LAYER_ROCK_CHANCE);
                        }
                    }

                    // And scan for next Block on the Stone Layer Type.
                    System.arraycopy(layers, 1, layers, 0, layers.length - 1);
                    layers[6] = stones.get(Math.min(stonesMax, (int)(((noise.get(tX, tY + 4, tZ) + 1) / 2) * stonesSize)));
                }
            }
        }
        return true;
    }
}
