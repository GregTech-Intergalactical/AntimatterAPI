package muramasa.antimatter.worldgen.feature;

import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.worldgen.AntimatterConfiguredFeatures;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import muramasa.antimatter.worldgen.WorldGenHelper;
import muramasa.antimatter.worldgen.vein.WorldGenVein;
import muramasa.antimatter.worldgen.vein.WorldGenVeinVariant;
import muramasa.antimatter.worldgen.vein.WorldGenVeinVariantMaterial;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class FeatureVein extends AntimatterFeature<NoneFeatureConfiguration> {

  private static final HashMap<ChunkPos, Integer> heightCache = new HashMap<>();

  public FeatureVein() {
    super(NoneFeatureConfiguration.CODEC, WorldGenVein.class);
  }

  @Override
  public String getId() {
    return "feature_vein";
  }

  @Override
  public boolean enabled() {
    return AntimatterConfig.WORLD.ORE_VEINS && getRegistry().size() > 0;
  }

  @Override
  public void init() {
  }

  @Override
  public void build(ResourceLocation name, Biome.ClimateSettings climate, Biome.BiomeCategory category, BiomeSpecialEffects effects, BiomeGenerationSettings.Builder gen, MobSpawnSettings.Builder spawns) {
    gen.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, AntimatterConfiguredFeatures.VEIN);
  }

  @Override
  public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> placer) {
    //long start = new Date().getTime();

    final WorldGenLevel world = placer.level();
    List<WorldGenVein> veins = AntimatterWorldGenerator.all(WorldGenVein.class, world.getLevel().dimension());
    if (veins.size() == 0) {
      return false;
    }

    // calculate how man chunks we need to search for in each direction to find vane
    // chunks based on the largest
    // registered vane feature
    final int chunkX = placer.origin().getX() >> 4;
    final int chunkZ = placer.origin().getZ() >> 4;

    final int chunkCornerX = chunkX * 16;
    final int chunkCornerZ = chunkZ * 16;
    final int worldMinY = world.dimensionType().minY();
    final int worldMaxY = world.dimensionType().minY() + world.dimensionType().height();
    boolean placed = false;

    for (int layer : WorldGenVein.getAllLayers()) {
      List<WorldGenVein> layerVeins = veins.stream().filter(vein -> vein.layer == layer).collect(Collectors.toList());

      final int layerChunks = 1
          + Math.max(1, (int) Math.ceil(((double) WorldGenVein.getMaxLayerSize(layer) - 16) / 32 * 1.25));
      // Search for all vein chunks within max chunks radius
      for (int xOffset = -layerChunks; xOffset <= layerChunks; xOffset++) {
        for (int zOffset = -layerChunks; zOffset <= layerChunks; zOffset++) {
          final int veinChunkX = chunkX + xOffset;
          final int veinChunkZ = chunkZ + zOffset;
          final int veinCenterX = veinChunkX * 16 + 8;
          final int veinCenterZ = veinChunkZ * 16 + 8;

          // initialize a number generator with the worlds seed, layer and dimension
          final long seed = getOreVeinSeed(world, layer, veinChunkX, veinChunkZ);
          final Random random = new Random(seed);

          if (random.nextFloat() <= WorldGenVein.getLayerChance(layer)) {
            int chunkMaxY = Math.min(worldMaxY,
                getChunkMaxHeight(placer, veinChunkX, veinChunkZ, veinCenterX, veinCenterZ));
            // place vane in the current chunk
            if (this.placeVane(random, world, worldMinY, chunkMaxY, chunkCornerX, chunkCornerZ, veinCenterX,
                veinCenterZ, worldMinY, worldMaxY, layerVeins)) {
              placed = true;
            }

            //if (placed && layer == WorldGenVein.ORE_VEIN_LAYER) {
              // Antimatter.LOGGER.info("CHUNK" + veinCenterX + " " + veinCenterZ);
           // }
          }

        }
      }
    }

    // long end = new Date().getTime();
    // Antimatter.LOGGER.info("Feature Vein took " + (end - start) + "ms");
    return placed;
  }

  private boolean placeVane(Random random, WorldGenLevel world, int chunkMinY, int chunkMaxY, int chunkCornerX,
      int chunkCornerZ, int veinCenterX, int veinCenterZ, int worldMinY, int worldMaxY, List<WorldGenVein> veins) {
    final List<WorldGenVein> validVeins = veins.stream()
            .filter(vein -> (vein.minY >= chunkMinY && vein.minY <= chunkMaxY)
                    || (vein.maxY >= chunkMinY && vein.maxY <= chunkMaxY)).toList();
    if (validVeins.size() == 0) {
      return false;
    }
    final WorldGenVein vein = validVeins.get(random.nextInt(validVeins.size()));
    final @Nullable WorldGenVeinVariant variant = vein.variants.size() > 0
        ? vein.variants.get(random.nextInt(vein.variants.size()))
        : null;

    final int trueMinY = Math.max(chunkMinY, vein.minY);
    final int trueMaxY = Math.min(chunkMaxY, vein.maxY);
    if (trueMaxY <= trueMinY) {
      return false;
    }
    final int veinCenterY = random.nextInt(trueMaxY - trueMinY) + trueMinY;

    final int veinSizeX = random.nextInt(vein.maxSize - vein.minSize) + vein.minSize;
    final int veinSizeY = (int) (random.nextInt(vein.maxSize - vein.minSize) * vein.heightScale) + vein.minSize;
    final int veinSizeZ = random.nextInt(vein.maxSize - vein.minSize) + vein.minSize;

    final int minCheckY = (int) Math.max(veinCenterY - (veinSizeY * 1.25 / 2 + 1), chunkMinY);
    final int maxCheckY = (int) Math.min(veinCenterY + (veinSizeY * 1.25 / 2 + 1), chunkMaxY);

    boolean placed = false;

    // check all positions in the current chunk (with limited y) if they are part of
    // the vane
    for (int x = chunkCornerX; x < chunkCornerX + 16; x++) {
      for (int z = chunkCornerZ; z < chunkCornerZ + 16; z++) {
        for (int y = minCheckY; y < maxCheckY; y++) {
          if (isInEllipse(random, veinCenterX, veinCenterY, veinCenterZ, x, y, z, veinSizeX / 2, veinSizeY / 2,
              veinSizeZ / 2)) {
            final BlockPos pos = new BlockPos(x, y, z);

            // set stone type to the vanes fill type
            if (vein.fill != null) {
              final BlockState existing = world.getBlockState(pos);
              WorldGenHelper.setStone(world, pos, existing, vein.fill);
              placed = true;
            }

            // set ores and small ores based ond chance and sub materials
            if (variant != null && variant.materials.size() > 0) {
              final float currentOreChance = random.nextFloat();
              final boolean spawnOre = variant.oreChance != 0 && currentOreChance <= variant.oreChance * (1 + (vein.density / 10f));
              final boolean spawnSmallOre = !spawnOre && variant.smallOreChance != 0
                  && currentOreChance <= (variant.oreChance + variant.smallOreChance) * (1 + (vein.density / 10f));
              if (spawnOre || spawnSmallOre) {
                int currentY = y;
                final List<WorldGenVeinVariantMaterial> validMaterials = variant.materials.stream()
                        .filter(material -> currentY >= material.minY && currentY <= material.maxY).toList();
                if (validMaterials.size() > 0) {
                  final WorldGenVeinVariantMaterial material = validMaterials
                      .get(random.nextInt(validMaterials.size()));
                  if (WorldGenHelper.setOre(world, pos, material.material, spawnSmallOre ? AntimatterMaterialTypes.ORE_SMALL : AntimatterMaterialTypes.ORE)) {
                    placed = true;
                  }

                }
              }
            }
          }
        }
      }
    }
    if (placed && variant != null && variant.surfaceStoneChance > 0) {
      this.placeSurfaceStones(random, world, variant.surfaceStoneChance, chunkCornerX, chunkCornerZ, variant.materials,
          vein.fill);
    }
    if (placed && variant != null && variant.markerOreChance > 0) {
      this.placeOreMarkers(random, world, variant.markerOreChance, chunkCornerX, chunkCornerZ, variant.materials,
          worldMinY, worldMaxY);
    }

    return placed;
  }

  private void placeSurfaceStones(Random random, WorldGenLevel world, float chance, int chunkCornerX, int chunkCornerZ,
      List<WorldGenVeinVariantMaterial> materials, @Nullable() BlockState fill) {
    for (int x = chunkCornerX; x < chunkCornerX + 16; x += 4) {
      for (int z = chunkCornerZ; z < chunkCornerZ + 16; z += 4) {
        if (random.nextFloat() <= chance) {
          int y = Math.min(world.getHeight(Heightmap.Types.OCEAN_FLOOR, x, z),
              world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z));
          final BlockPos pos = new BlockPos(x, y, z);
          WorldGenVeinVariantMaterial material = materials.get(random.nextInt(materials.size()));
          WorldGenHelper.setRock(world, pos, material.material, fill);
        }
      }
    }
  }

  private void placeOreMarkers(Random random, WorldGenLevel world, float chance, int chunkCornerX, int chunkCornerZ,
      List<WorldGenVeinVariantMaterial> materials, int worldMinY, int worldMaxY) {
    for (int x = chunkCornerX; x < chunkCornerX + 16; x += 4) {
      for (int z = chunkCornerZ; z < chunkCornerZ + 16; z += 4) {
        for (int y = worldMinY; y < worldMaxY; y++) {
          if (random.nextFloat() <= chance) {
            final BlockPos pos = new BlockPos(x, y, z);
            WorldGenVeinVariantMaterial material = materials.get(random.nextInt(materials.size()));
            WorldGenHelper.setOre(world, pos, material.material, AntimatterMaterialTypes.ORE_SMALL);
          }
        }
      }
    }
  }

  private static boolean isInEllipse(Random random, int centerX, int centerY, int centerZ, int x, int y, int z,
      int sizeX, int sizeY, int sizeZ) {
    double diff = ((Math.pow((x - centerX), 2) / Math.pow(sizeX, 2)) +
        (Math.pow((y - centerY), 2) / Math.pow(sizeY, 2)) +
        (Math.pow((z - centerZ), 2) / Math.pow(sizeZ, 2)));
    if (diff <= 1) {
      return true;
    } else if (diff <= 1.25) {
      return (random.nextFloat() / 4) >= (diff - 1);
    } else {
      return false;
    }
  }

  private static long getOreVeinSeed(WorldGenLevel world, int layer, int x, int z) {
    long worldSeed = world.getSeed();
    int dimension = world.getLevel().dimension().location().hashCode();
    long seedPart = ((worldSeed) << 48 & 0xffff000000000000L);
    long dimensionPart = (((long) dimension << 40) & 0x0000ff0000000000L);
    long layerPart = (((long) layer << 32) & 0x000000ff00000000L);
    long xPart = (((long) x << 16) & 0x00000000ffff0000L);
    long zPart = ((z) & 0x000000000000ffffL);
    return seedPart | dimensionPart | layerPart | xPart | zPart;
  }

  private static int getChunkMaxHeight(FeaturePlaceContext<NoneFeatureConfiguration> placer, int veinChunkX,
      int veinChunkZ, int veinCenterX, int veinCenterZ) {
    ChunkPos pos = new ChunkPos(veinCenterX, veinCenterZ);
    Integer existing = heightCache.get(pos);
    if (existing != null) {
      return existing;
    } else {
      final ChunkAccess chunk = placer.level().getChunk(veinChunkX, veinChunkZ);
      final int height = placer.chunkGenerator().getBaseHeight(veinCenterX, veinCenterZ, Heightmap.Types.OCEAN_FLOOR_WG,
          chunk.getHeightAccessorForGeneration());
      heightCache.put(pos, height);
      return height;
    }
  }

}
