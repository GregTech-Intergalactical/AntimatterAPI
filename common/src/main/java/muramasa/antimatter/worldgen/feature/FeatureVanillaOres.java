package muramasa.antimatter.worldgen.feature;

import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.data.AntimatterStoneTypes;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.material.MaterialTypeBlock;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.worldgen.AntimatterConfiguredFeatures;
import muramasa.antimatter.worldgen.AntimatterWorldGenerator;
import muramasa.antimatter.worldgen.WorldGenHelper;
import muramasa.antimatter.worldgen.vanillaore.WorldGenVanillaOre;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
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
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import static muramasa.antimatter.Antimatter.LOGGER;
import static muramasa.antimatter.worldgen.WorldGenHelper.ORE_PREDICATE;

public class FeatureVanillaOres extends AntimatterFeature<NoneFeatureConfiguration> {
    public FeatureVanillaOres() {
        super(NoneFeatureConfiguration.CODEC, WorldGenVanillaOre.class);
    }

    @Override
    public String getId() {
        return "vanilla_ores";
    }

    @Override
    public boolean enabled() {
        return getRegistry().size() > 0;
    }

    @Override
    public void init() {

    }


    @Override
    public void build(ResourceLocation name, Biome.ClimateSettings climate, Biome.BiomeCategory category, BiomeSpecialEffects effects, BiomeGenerationSettings.Builder gen, MobSpawnSettings.Builder spawns) {
        gen.addFeature(GenerationStep.Decoration.UNDERGROUND_ORES, AntimatterConfiguredFeatures.VANILLA_ORES);
    }

    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> placer) {
        Random random = placer.random();
        BlockPos blockpos = placer.origin();
        WorldGenLevel world = placer.level();

        final int chunkX = placer.origin().getX() >> 4;
        final int chunkZ = placer.origin().getZ() >> 4;
        final int chunkCornerX = chunkX * 16;
        final int chunkCornerZ = chunkZ * 16;
        final int worldMinY = world.dimensionType().minY();
        final int worldMaxY = world.dimensionType().minY() + world.dimensionType().height();
        List<WorldGenVanillaOre> vanillaOres = AntimatterWorldGenerator.all(WorldGenVanillaOre.class, world.getLevel().dimension());
        int spawned = 0;
        for (WorldGenVanillaOre vanillaOre : vanillaOres) {
            if (!vanillaOre.primary.has(vanillaOre.materialType) || (vanillaOre.secondary != Material.NULL && !vanillaOre.secondary.has(vanillaOre.secondaryType))) continue;
            if (vanillaOre.rare && !(random.nextFloat() < 1.0F / (float)vanillaOre.weight)) continue;
            int minY = Math.max(worldMinY, vanillaOre.minY);
            int maxY = Math.min(worldMaxY, vanillaOre.maxY);
            int i = 0;
            int amountPerChunk = vanillaOre.rare ? 1 : vanillaOre.weight;
            for (int j = amountPerChunk; i < j; i++) {
                int y = vanillaOre.triangle ? sample(random, minY, maxY) : minY + random.nextInt(Math.max(1, maxY - minY));
                BlockPos spawnPos = new BlockPos(chunkCornerX + random.nextInt(16), y, chunkCornerZ + random.nextInt(16));
                if (!vanillaOre.getValidBiomes().test(world.getBiome(spawnPos))) continue;
                boolean spawn = vanillaOre.size > 1 ? place(world, random, spawnPos, vanillaOre) : setOreBlock(world, spawnPos, vanillaOre);
                if (spawn) spawned++;
            }
        }


        return spawned > 0;
    }

    public boolean place(WorldGenLevel worldgenlevel, Random random, BlockPos blockpos, WorldGenVanillaOre config) {

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
                if (config.spawnOnOceanFloor){
                    int y2 = worldgenlevel.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, ix, iz);
                    double minY0 = y + random.nextInt(3) - 2;
                    double maxY0 = y + random.nextInt(3) - 2;
                    return this.doPlace(worldgenlevel, random, config, minX, maxX, minZ, maxZ, minY0, maxY0, x, y2, z, width, height);
                }
                if (y <= worldgenlevel.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, ix, iz)) {
                    return this.doPlace(worldgenlevel, random, config, minX, maxX, minZ, maxZ, minY, maxY, x, y, z, width, height);
                }
            }
        }

        return false;
    }

    protected boolean doPlace(WorldGenLevel pLevel, Random pRandom, WorldGenVanillaOre config, double pMinX, double pMaxX, double pMinZ, double pMaxZ, double pMinY, double pMaxY, int pX, int pY, int pZ, int pWidth, int pHeight) {
        int i = 0;
        BitSet bitset = new BitSet(pWidth * pHeight * pWidth);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        int size = config.size;
        double[] adouble = new double[size * 4];

        for(int k = 0; k < size; ++k) {
            float f = (float)k / (float)size;
            double lerpX = Mth.lerp(f, pMinX, pMaxX);
            double lerpY = Mth.lerp(f, pMinY, pMaxY);
            double lerpZ = Mth.lerp(f, pMinZ, pMaxZ);
            double d3 = pRandom.nextDouble() * (double)size / 16.0D;
            double d4 = ((double)(Mth.sin((float)Math.PI * f) + 1.0F) * d3 + 1.0D) / 2.0D;
            adouble[k * 4 + 0] = lerpX;
            adouble[k * 4 + 1] = lerpY;
            adouble[k * 4 + 2] = lerpZ;
            adouble[k * 4 + 3] = d4;
        }

        for(int currentIndex = 0; currentIndex < size - 1; ++currentIndex) {
            if (!(adouble[currentIndex * 4 + 3] <= 0.0D)) {
                for(int offset = currentIndex + 1; offset < size; ++offset) {
                    if (!(adouble[offset * 4 + 3] <= 0.0D)) {
                        double d8 = adouble[currentIndex * 4 + 0] - adouble[offset * 4 + 0];
                        double d10 = adouble[currentIndex * 4 + 1] - adouble[offset * 4 + 1];
                        double d12 = adouble[currentIndex * 4 + 2] - adouble[offset * 4 + 2];
                        double d14 = adouble[currentIndex * 4 + 3] - adouble[offset * 4 + 3];
                        if (d14 * d14 > d8 * d8 + d10 * d10 + d12 * d12) {
                            if (d14 > 0.0D) {
                                adouble[offset * 4 + 3] = -1.0D;
                            } else {
                                adouble[currentIndex * 4 + 3] = -1.0D;
                            }
                        }
                    }
                }
            }
        }

        BulkSectionAccess bulksectionaccess = new BulkSectionAccess(pLevel);

        try {
            for(int j4 = 0; j4 < size; ++j4) {
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

                                                        Material mat = config.primary;
                                                        MaterialType<?> type = config.materialType;
                                                        if (config.secondary != Material.NULL && config.secondaryChance > 0 && config.secondaryChance < 1.0F && pRandom.nextFloat() < config.secondaryChance){
                                                            mat =  config.secondary;
                                                            if (config.secondaryType != config.materialType) type = config.secondaryType;
                                                        }
                                                        if (placeOre(lx, ly, lz, levelchunksection, bulksectionaccess::getBlockState, pRandom, config, mat, type, blockpos$mutableblockpos)) {
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

    private int sample(Random random, int minY, int maxY) {
        if (minY > maxY) {
            LOGGER.warn("Empty height range: {}", this);
            return minY;
        } else {
            int difference = maxY - minY;
            if (0 >= difference) {
                return Mth.randomBetweenInclusive(random, minY, maxY);
            } else {
                int l = difference / 2;
                int m = difference - l;
                return minY + Mth.randomBetweenInclusive(random, 0, m) + Mth.randomBetweenInclusive(random, 0, l);
            }
        }
    }

    private boolean setOreBlock(WorldGenLevel level, BlockPos pos, WorldGenVanillaOre vanillaOre){
        Holder<Biome> biome = level.getBiome(pos);
        ResourceLocation biomeKey = biome.unwrapKey().get().location();
        if (vanillaOre.biomes.contains(biomeKey) == vanillaOre.biomeBlacklist) return false;
        Material material = vanillaOre.primary;
        MaterialType<?> type = vanillaOre.materialType;
        if (vanillaOre.secondaryChance > 0.0f && vanillaOre.secondary != Material.NULL && level.getRandom().nextFloat() < vanillaOre.secondaryChance){
            material = vanillaOre.secondary;
            if (vanillaOre.secondaryType != vanillaOre.materialType) type = vanillaOre.secondaryType;
        }
        return WorldGenHelper.setOre(level, pos, material, type);
    }

    public static BlockState getOre(BlockState existing, Material material,
                                    MaterialType<?> type) {

        StoneType stone = WorldGenHelper.STONE_MAP.get(existing);
        if (stone == null || !stone.doesGenerateOre() || stone == AntimatterStoneTypes.BEDROCK)
            return null;
        BlockState oreState = type.get() instanceof MaterialTypeBlock.IOreGetter getter ? getter.get(material, stone).asState()
                : type.get() instanceof MaterialTypeBlock.IBlockGetter getter ? getter.get(material).asState() : null;
        if (existing == null || (type.get() instanceof MaterialTypeBlock.IOreGetter && !ORE_PREDICATE.test(existing)))
            return null;
        return oreState;
    }

    public boolean placeOre(int x, int y, int z, LevelChunkSection chunkSection, Function<BlockPos, BlockState> adjacentStateAccessor, Random random, WorldGenVanillaOre config, Material material, MaterialType<?> type, BlockPos.MutableBlockPos mutable){
        BlockState blockState = chunkSection.getBlockState(x, y, z);
        BlockState oreToPlace = getOre(blockState, material, type);
        if (oreToPlace != null && canPlaceOre(blockState, adjacentStateAccessor, random, config, material, type, mutable)) {
            chunkSection.setBlockState(x, y, z, oreToPlace, false);
            return true;
        }
        return false;
    }

    public static boolean canPlaceOre(BlockState state, Function<BlockPos, BlockState> adjacentStateAccessor, Random random, WorldGenVanillaOre config, Material material, MaterialType<?> type, BlockPos.MutableBlockPos mutable) {
        if (getOre(state, material, type) == null) {
            return false;
        } else if (shouldSkipAirCheck(random, config.discardOnExposureChance)) {
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
