package muramasa.antimatter.worldgen.feature;

import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.data.AntimatterStoneTypes;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.worldgen.AntimatterConfiguredFeatures;
import muramasa.antimatter.worldgen.WorldGenHelper;
import muramasa.antimatter.worldgen.smallore.WorldGenSmallOreMaterial;
import muramasa.antimatter.worldgen.vein.WorldGenVein;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.BulkSectionAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.BitSet;
import java.util.Random;
import java.util.function.Function;

import static muramasa.antimatter.data.AntimatterMaterialTypes.ORE_STONE;
import static muramasa.antimatter.data.AntimatterMaterials.Coal;
import static muramasa.antimatter.worldgen.WorldGenHelper.ORE_PREDICATE;
import static trinsdar.gt4r.worldgen.GT4RPlacedFeatures.FEATURE_MAP;

public class FeatureSmallOres extends AntimatterFeature<NoneFeatureConfiguration> {
    public FeatureSmallOres() {
        super(NoneFeatureConfiguration.CODEC, WorldGenSmallOreMaterial.class);
    }

    @Override
    public String getId() {
        return "small_ores";
    }

    @Override
    public boolean enabled() {
        return AntimatterConfig.WORLD.SMALL_ORES && getRegistry().size() > 0;
    }

    @Override
    public void init() {

    }


    @Override
    public void build(ResourceLocation name, Biome.ClimateSettings climate, Biome.BiomeCategory category, BiomeSpecialEffects effects, BiomeGenerationSettings.Builder gen, MobSpawnSettings.Builder spawns) {
        gen.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, AntimatterConfiguredFeatures.SMALL_ORES);
    }

    public boolean place(WorldGenLevel worldgenlevel, Random random, BlockPos blockpos, NoneFeatureConfiguration config) {

        float f = random.nextFloat() * (float)Math.PI;
        float f1 = (float)config.size / 8.0F;
        int i = Mth.ceil(((float)config.size / 16.0F * 2.0F + 1.0F) / 2.0F);
        double minX = (double)blockpos.getX() + Math.sin(f) * (double)f1;
        double maxX = (double)blockpos.getX() - Math.sin(f) * (double)f1;
        double minZ = (double)blockpos.getZ() + Math.cos(f) * (double)f1;
        double maxZ = (double)blockpos.getZ() - Math.cos(f) * (double)f1;
        double minY = blockpos.getY() + random.nextInt(3) - 2;
        double maxY = blockpos.getY() + random.nextInt(3) - 2;
        int x = blockpos.getX() - Mth.ceil(f1) - i;
        int y = blockpos.getY() - 2 - i;
        int z = blockpos.getZ() - Mth.ceil(f1) - i;
        int width = 2 * (Mth.ceil(f1) + i);
        int height = 2 * (2 + i);

        for(int ix = x; ix <= x + width; ++ix) {
            for(int iz = z; iz <= z + width; ++iz) {
                if (y <= worldgenlevel.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, ix, iz)) {
                    return this.doPlace(worldgenlevel, random, config, minX, maxX, minZ, maxZ, minY, maxY, x, y, z, width, height);
                }
            }
        }

        return false;
    }

    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> pContext) {
        Random random = pContext.random();
        BlockPos blockpos = pContext.origin();
        WorldGenLevel worldgenlevel = pContext.level();


        return false;
        /*
        GT4ROreFeatureConfig config = pContext.config();
        if (!FEATURE_MAP.get(config.getId()).dimensions().contains(worldgenlevel.getLevel().dimension())) return false;
        if (!FEATURE_MAP.get(config.getId()).filterContext().test(worldgenlevel.getBiome(blockpos))) return false;
        return place(worldgenlevel, random, blockpos, config);*/
    }

    protected boolean doPlace(WorldGenLevel pLevel, Random pRandom, GT4ROreFeatureConfig config, double pMinX, double pMaxX, double pMinZ, double pMaxZ, double pMinY, double pMaxY, int pX, int pY, int pZ, int pWidth, int pHeight) {
        int i = 0;
        BitSet bitset = new BitSet(pWidth * pHeight * pWidth);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        int j = config.size;
        double[] adouble = new double[j * 4];

        for(int k = 0; k < j; ++k) {
            float f = (float)k / (float)j;
            double d0 = Mth.lerp(f, pMinX, pMaxX);
            double d1 = Mth.lerp(f, pMinY, pMaxY);
            double d2 = Mth.lerp(f, pMinZ, pMaxZ);
            double d3 = pRandom.nextDouble() * (double)j / 16.0D;
            double d4 = ((double)(Mth.sin((float)Math.PI * f) + 1.0F) * d3 + 1.0D) / 2.0D;
            adouble[k * 4 + 0] = d0;
            adouble[k * 4 + 1] = d1;
            adouble[k * 4 + 2] = d2;
            adouble[k * 4 + 3] = d4;
        }

        for(int l3 = 0; l3 < j - 1; ++l3) {
            if (!(adouble[l3 * 4 + 3] <= 0.0D)) {
                for(int i4 = l3 + 1; i4 < j; ++i4) {
                    if (!(adouble[i4 * 4 + 3] <= 0.0D)) {
                        double d8 = adouble[l3 * 4 + 0] - adouble[i4 * 4 + 0];
                        double d10 = adouble[l3 * 4 + 1] - adouble[i4 * 4 + 1];
                        double d12 = adouble[l3 * 4 + 2] - adouble[i4 * 4 + 2];
                        double d14 = adouble[l3 * 4 + 3] - adouble[i4 * 4 + 3];
                        if (d14 * d14 > d8 * d8 + d10 * d10 + d12 * d12) {
                            if (d14 > 0.0D) {
                                adouble[i4 * 4 + 3] = -1.0D;
                            } else {
                                adouble[l3 * 4 + 3] = -1.0D;
                            }
                        }
                    }
                }
            }
        }

        BulkSectionAccess bulksectionaccess = new BulkSectionAccess(pLevel);

        try {
            for(int j4 = 0; j4 < j; ++j4) {
                double d9 = adouble[j4 * 4 + 3];
                if (!(d9 < 0.0D)) {
                    double d11 = adouble[j4 * 4 + 0];
                    double d13 = adouble[j4 * 4 + 1];
                    double d15 = adouble[j4 * 4 + 2];
                    int k4 = Math.max(Mth.floor(d11 - d9), pX);
                    int l = Math.max(Mth.floor(d13 - d9), pY);
                    int i1 = Math.max(Mth.floor(d15 - d9), pZ);
                    int j1 = Math.max(Mth.floor(d11 + d9), k4);
                    int k1 = Math.max(Mth.floor(d13 + d9), l);
                    int l1 = Math.max(Mth.floor(d15 + d9), i1);

                    for(int rx = k4; rx <= j1; ++rx) {
                        double d5 = ((double)rx + 0.5D - d11) / d9;
                        if (d5 * d5 < 1.0D) {
                            for(int ry = l; ry <= k1; ++ry) {
                                double d6 = ((double)ry + 0.5D - d13) / d9;
                                if (d5 * d5 + d6 * d6 < 1.0D) {
                                    for(int rz = i1; rz <= l1; ++rz) {
                                        double d7 = ((double)rz + 0.5D - d15) / d9;
                                        if (d5 * d5 + d6 * d6 + d7 * d7 < 1.0D && !pLevel.isOutsideBuildHeight(ry)) {
                                            int l2 = rx - pX + (ry - pY) * pWidth + (rz - pZ) * pWidth * pHeight;
                                            if (!bitset.get(l2)) {
                                                bitset.set(l2);
                                                blockpos$mutableblockpos.set(rx, ry, rz);
                                                if (pLevel.ensureCanWrite(blockpos$mutableblockpos)) {
                                                    LevelChunkSection levelchunksection = bulksectionaccess.getSection(blockpos$mutableblockpos);
                                                    if (levelchunksection != null) {
                                                        int lx = SectionPos.sectionRelative(rx);
                                                        int ly = SectionPos.sectionRelative(ry);
                                                        int lz = SectionPos.sectionRelative(rz);
                                                        BlockState blockstate = levelchunksection.getBlockState(lx, ly, lz);

                                                        Material mat = Material.get(config.getPrimary());
                                                        if (mat.has(ORE_STONE) && mat != Coal){
                                                            StoneType stone = WorldGenHelper.STONE_MAP.get(blockstate);
                                                            if (stone == null || stone == AntimatterStoneTypes.DIRT) continue;
                                                            levelchunksection.setBlockState(lx, ly, lz, ORE_STONE.get().get(mat).asState(), false);
                                                            ++i;
                                                            continue;
                                                        }
                                                        if (!config.getSecondary().isEmpty() && !config.getSecondary().equals("null") && config.getSecondaryChance() > 0 && config.getSecondaryChance() < 1.0F){
                                                            mat = pRandom.nextFloat() < config.getSecondaryChance() ? Material.get(config.getSecondary()) : Material.get(config.getPrimary());
                                                        }
                                                        if (placeOre(lx, ly, lz, levelchunksection, bulksectionaccess::getBlockState, pRandom, config, mat, AntimatterMaterialTypes.ORE, blockpos$mutableblockpos)) {
                                                            ++i;
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Throwable throwable1) {
            try {
                bulksectionaccess.close();
            } catch (Throwable throwable) {
                throwable1.addSuppressed(throwable);
            }

            throw throwable1;
        }

        bulksectionaccess.close();
        return i > 0;
    }

    public static BlockState getOre(BlockState existing, Material material,
                                 MaterialType<?> type) {

        StoneType stone = WorldGenHelper.STONE_MAP.get(existing);
        if (stone == null || stone == AntimatterStoneTypes.DIRT)
            return null;
        BlockState oreState = type == AntimatterMaterialTypes.ORE ? AntimatterMaterialTypes.ORE.get().get(material, stone).asState()
                : AntimatterMaterialTypes.ORE_SMALL.get().get(material, stone).asState();
        if (!ORE_PREDICATE.test(existing))
            return null;
        return oreState;
    }

    public boolean placeOre(int x, int y, int z, LevelChunkSection chunkSection, Function<BlockPos, BlockState> adjacentStateAccessor, Random random, GT4ROreFeatureConfig config, Material material, MaterialType<?> type, BlockPos.MutableBlockPos mutable){
        BlockState blockState = chunkSection.getBlockState(x, y, z);
        BlockState oreToPlace = getOre(blockState, material, type);
        if (oreToPlace != null && canPlaceOre(blockState, adjacentStateAccessor, random, config, material, type, mutable)) {
            chunkSection.setBlockState(x, y, z, oreToPlace, false);
            return true;
        }
        return false;
    }

    public static boolean canPlaceOre(BlockState state, Function<BlockPos, BlockState> adjacentStateAccessor, Random random, GT4ROreFeatureConfig config, Material material, MaterialType<?> type, BlockPos.MutableBlockPos mutable) {
        if (getOre(state, material, type) == null) {
            return false;
        } else if (shouldSkipAirCheck(random, config.getDiscardOnExposureChance())) {
            return true;
        } else {
            return !isAdjacentToAir(adjacentStateAccessor, mutable);
        }
    }

    protected static boolean shouldSkipAirCheck(Random pRandom, float pChance) {
        if (pChance <= 0.0F) {
            return true;
        } else if (pChance >= 1.0F) {
            return false;
        } else {
            return pRandom.nextFloat() >= pChance;
        }
    }
}
