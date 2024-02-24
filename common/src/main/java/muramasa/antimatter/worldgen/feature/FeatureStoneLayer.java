package muramasa.antimatter.worldgen.feature;

import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.ore.BlockOreStone;
import muramasa.antimatter.worldgen.*;
import muramasa.antimatter.worldgen.object.WorldGenStoneLayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.List;
import java.util.Random;

import static muramasa.antimatter.data.AntimatterMaterialTypes.ROCK;

public class FeatureStoneLayer extends AntimatterFeature<NoneFeatureConfiguration> {

    public FeatureStoneLayer() {
        super(NoneFeatureConfiguration.CODEC, WorldGenStoneLayer.class);
    }

    @Override
    public String getId() {
        return "feature_stone_layer";
    }

    @Override
    public boolean enabled() {
        return AntimatterConfig.STONE_LAYERS.get() && getRegistry().size() > 0;
    }

    @Override
    public void init() {
    }

    @Override
    public void build(ResourceLocation name, Biome.ClimateSettings climate, Biome.BiomeCategory category, BiomeSpecialEffects effects, BiomeGenerationSettings.Builder gen, MobSpawnSettings.Builder spawns) {
        gen.addFeature(GenerationStep.Decoration.UNDERGROUND_STRUCTURES, AntimatterConfiguredFeatures.STONE_LAYER);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> ctxt) {
        WorldGenLevel world = ctxt.level();
        BlockPos pos = ctxt.origin();
        Random rand = ctxt.random();
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

                maxHeight = world.getHeightmapPos(Heightmap.Types.OCEAN_FLOOR_WG, pos.offset(i, 0, j)).getY() + 1; //+1 for placing rocks on top of the max height
                for (int tY = -63; tY < maxHeight; tY++) {
                    int offsetY = tY + 64;
                    lastMaterial = null;
                    BlockPos offset = pos.offset(i, offsetY, j);
                    existing = world.getBlockState(offset);
                    isAir = existing.isAir();

                    //If we haven't placed an ore, and not trying to set the same state as existing, also doesn't work if the veins is either stone or deepslate, lets it fall back to vanilla for those
                    if (!isAir && /*lastMaterial == null &&*/ existing != layers[3].getStoneState() && layers[3].getStoneState().getBlock() != Blocks.STONE && layers[3].getStoneState().getBlock() != Blocks.DEEPSLATE) {
                        if (WorldGenHelper.setStone(world, offset, existing, layers[3].getStoneState())) {
                            lastMaterial = layers[3].getStoneType() != null ? layers[3].getStoneType().getMaterial() : layers[3].getStoneState().getBlock() instanceof BlockOreStone oreStone ? oreStone.getMaterial() : null;
                        }
                    }

                    if (!isAir && AntimatterConfig.STONE_LAYER_ORES.get()) {
                        if (layers[1] == layers[5]) {
                            for (StoneLayerOre ore : layers[3].getOres()) {
                                if (ore.canPlace(offset, rand, world) && WorldGenHelper.addOre(world, offset, ore.getMaterial(), layers[0] == layers[6])) {
                                    lastMaterial = ore.getMaterial();
                                    break;
                                }
                            }
                        } else {
                            for (StoneLayerOre ore : WorldGenStoneLayer.getCollision(layers[3].getStoneType(), layers[5].getStoneState(), layers[1].getStoneState())) {
                                if (ore.canPlace(offset, rand, world) && WorldGenHelper.addOre(world, offset, ore.getMaterial(), true)) {
                                    lastMaterial = ore.getMaterial();
                                    break;
                                }
                            }
                        }
                    }

                    if (lastMaterial != null && (lastMaterial.has(AntimatterMaterialTypes.ORE) || lastMaterial.has(AntimatterMaterialTypes.ORE_STONE)) && lastMaterial.has(ROCK)) {
                        BlockState below = world.getBlockState(offset.offset(0, -1, 0));
                        int y = Math.min(world.getHeight(Heightmap.Types.OCEAN_FLOOR, offset.getX(), offset.getZ()), world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, offset.getX(), offset.getZ()));
                        if (!below.isAir() && below != WorldGenHelper.WATER_STATE) {
                            WorldGenHelper.setRock(world, offset.mutable().setY(y).immutable(), lastMaterial, below, AntimatterConfig.STONE_LAYER_ROCK_CHANCE.get() * (lastMaterial.has(AntimatterMaterialTypes.ORE_STONE) ? 100 : 1));
                        }
                    }

                    // And scan for next Block on the Stone Layer Type.
                    System.arraycopy(layers, 1, layers, 0, layers.length - 1);
                    layers[6] = stones.get(Math.min(stonesMax, (int) (((noise.get(tX, offsetY + 4, tZ) + 1) / 2) * stonesSize)));
                }
            }
        }
        return true;
    }
}
