package muramasa.antimatter.worldgen.feature;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterProperties;
import muramasa.antimatter.Configs;
import muramasa.antimatter.blocks.BlockStone;
import muramasa.antimatter.blocks.BlockSurfaceRock;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.worldgen.object.WorldGenVeinLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
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

import java.util.Objects;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static net.minecraft.state.properties.BlockStateProperties.WATERLOGGED;


public class FeatureIndicatorRocks extends AntimatterFeature<NoFeatureConfig>{
    static final int SURFACE_ROCK_MODEL_COUNT = 7; // randomly select one of the models
    static final int SURFACE_ROCKS_PER_CHUNK = 10; // maybe move to config?
    public FeatureIndicatorRocks() {
        super(NoFeatureConfig::deserialize, FeatureIndicatorRocks.class);
    }

    @Override
    public boolean enabled() {
        return Configs.WORLD.ENABLE_ORE_VEINS;
    }

    @Override
    public void init() {
        for (Biome biome : ForgeRegistries.BIOMES) {
            biome.addFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION,
                    new ConfiguredFeature<>(this, IFeatureConfig.NO_FEATURE_CONFIG));
        }
    }

    @Override
    public String getId() {
        return "feature_indicator_rocks";
    }

    static Stream<BlockPos> getPositions(IWorld worldIn, Random random, BlockPos pos) {
        return IntStream.range(0, FeatureIndicatorRocks.SURFACE_ROCKS_PER_CHUNK).mapToObj(n -> {
            int x = random.nextInt(16) + pos.getX();
            int z = random.nextInt(16) + pos.getZ();

            int y = Math.min(worldIn.getHeight(Heightmap.Type.OCEAN_FLOOR, x, z),
                    worldIn.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z));
            return new BlockPos(x, y, z);
        });
    }

    @Override
    public boolean place(IWorld worldIn, ChunkGenerator<? extends GenerationSettings> generator, Random rand, BlockPos pos, NoFeatureConfig config) {
        Object[] vv = FeatureVeinLayer.veinCenters(pos.getX() >> 4, pos.getZ() >> 4).stream().map(
                p -> WorldGenVeinLayer.VALID_VEINS.get(WorldGenVeinLayer.getOreVeinSeed(worldIn, p.getA(), p.getB()))).filter(Objects::nonNull).toArray();
        if (vv.length == 0)
            return false;
        getPositions(worldIn, rand, pos).forEach(p -> place(worldIn, rand, vv, p));
        return true;
    }

    private void place(IWorld worldIn, Random rand, Object[] vv, BlockPos p) {
        if (worldIn.getBlockState(p) != Blocks.AIR.getDefaultState() && worldIn.getBlockState(p) != Blocks.WATER.getDefaultState())
            return;
        boolean inWater = worldIn.getBlockState(p) == Blocks.WATER.getDefaultState();
        // No idea what else may be filtered out here
        if (worldIn.getBlockState(p.down()).hasTileEntity())
            return;
        Material m = AntimatterAPI.getMaterial("flint");
        // 10% chances to get filnt
        if (rand.nextInt(10) > 1)
            m = ((WorldGenVeinLayer) vv[rand.nextInt(vv.length)]).getMaterial(0);
        // in half or more of the cases should place container rock
        if (rand.nextInt(10) > 4) {
            BlockState bs = worldIn.getBlockState(new BlockPos(p.getX(), rand.nextInt(p.getY()), p.getZ()));
            if (bs.getBlock() instanceof BlockOre)
                m = ((BlockOre) bs.getBlock()).getStoneType().getMaterial();
            else if (bs.getBlock() instanceof BlockStone)
                m = ((BlockStone) bs.getBlock()).getType().getMaterial();
        }
        // TODO: use material specific block
        worldIn.setBlockState(p, BlockSurfaceRock.get(m, StoneType.get("stone")).with(
                AntimatterProperties.ROCK_MODEL, rand.nextInt(SURFACE_ROCK_MODEL_COUNT)).with(
                WATERLOGGED, Boolean.valueOf(inWater)), 2);
    }
}
