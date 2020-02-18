package muramasa.antimatter.worldgen;

import com.google.common.base.Predicate;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.materials.MaterialType;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.worldgen.feature.FeatureSurfaceRocks;
import muramasa.antimatter.worldgen.object.WorldGenStoneLayer;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.Heightmap;

import java.util.ArrayList;
import java.util.List;

public class WorldGenHelper {

    public static Object2ObjectOpenHashMap<BlockState, StoneType> STONE_MAP = new Object2ObjectOpenHashMap<>();
    public static ObjectOpenHashSet<BlockState> ROCK_SET = new ObjectOpenHashSet<>();
    public static ObjectOpenHashSet<BlockState> STONE_SET = new ObjectOpenHashSet<>();
    public static ObjectOpenHashSet<BlockState> TREE_SET = new ObjectOpenHashSet<>();
    public static ObjectOpenHashSet<String> TREE_BIOME_SET = new ObjectOpenHashSet<>();

    public static BlockState STONE_STATE = Blocks.STONE.getDefaultState();
    public static BlockState WATER_STATE = Blocks.WATER.getDefaultState();

    public static Predicate<BlockState> ORE_PREDICATE = state -> STONE_MAP.containsKey(state);
    public static Predicate<BlockState> ROCK_PREDICATE = state -> ROCK_SET.contains(state);
    public static Predicate<BlockState> STONE_PREDICATE = state -> STONE_SET.contains(state);

    public static void init() {
        AntimatterAPI.all(StoneType.class).forEach(t -> STONE_MAP.put(t.getState(), t));

        ROCK_SET.add(Blocks.WATER.getDefaultState());

        STONE_SET.add(Blocks.STONE.getDefaultState());
        STONE_SET.add(Blocks.GRANITE.getDefaultState());
        STONE_SET.add(Blocks.ANDESITE.getDefaultState());
        STONE_SET.add(Blocks.DIORITE.getDefaultState());

        STONE_SET.add(Blocks.COAL_ORE.getDefaultState());
        STONE_SET.add(Blocks.IRON_ORE.getDefaultState());
        STONE_SET.add(Blocks.GOLD_ORE.getDefaultState());
        STONE_SET.add(Blocks.DIAMOND_ORE.getDefaultState());
        STONE_SET.add(Blocks.EMERALD_ORE.getDefaultState());
        STONE_SET.add(Blocks.LAPIS_ORE.getDefaultState());
        STONE_SET.add(Blocks.REDSTONE_ORE.getDefaultState());

        TREE_SET.add(Blocks.GRASS.getDefaultState());
        TREE_BIOME_SET.add("Forest");
        TREE_BIOME_SET.add("ForestHills");
    }

    /** Efficiently sets a BlockState, without causing block updates or notifying the client **/
    public static boolean setState(IWorld world, BlockPos pos, BlockState state) {
        return world.setBlockState(pos, state, 2 | 16);
    }

    public static boolean setOre(IWorld world, BlockPos pos, BlockState existing, Material material, MaterialType<?> type) {
        StoneType stone = STONE_MAP.get(existing);
        if (stone == null) return false;
        BlockState oreState = type == MaterialType.ORE ? MaterialType.ORE.get().get(material, stone).asState() : MaterialType.ORE_SMALL.get().get(material, stone).asState();
        return setOre(world, pos, existing, oreState);
    }

    /** More efficient version of setOre, used by FeatureStoneLayer with pre computed BlockStates **/
    public static boolean setOre(IWorld world, BlockPos pos, BlockState existing, StoneLayerOre ore, boolean normalOre) {
        StoneType stone = STONE_MAP.get(existing);
        if (stone == null) return false;
        return setOre(world, pos, existing, normalOre ? ore.getOreState() : ore.getOreSmallState());
    }

    /** Raw version of setOre, will only place the passed state if the existing state is a registered stone **/
    public static boolean setOre(IWorld world, BlockPos pos, BlockState existing, BlockState replacement) {
        if (!existing.isReplaceableOreGen(world, pos, ORE_PREDICATE)) return false;
        return setState(world, pos, replacement);
    }

    /** Adds a rock to the global map for placing in a later generation stage **/
    public static boolean addRock(IWorld world, BlockPos pos, Material material, int chance) {
        int y = Math.min(world.getHeight(Heightmap.Type.OCEAN_FLOOR, pos.getX(), pos.getZ()), world.getHeight(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, pos.getX(), pos.getZ()));
        return addRockRaw(world, new BlockPos(pos.getX(), y, pos.getZ()), material, chance);
    }

    public static boolean addRockRaw(IWorld world, BlockPos pos, Material material, int chance) {
        if (world.getRandom().nextInt(chance) != 0) return false;
        List<Tuple<BlockPos, Material>> entry = FeatureSurfaceRocks.ROCKS_TO_PLACE.computeIfAbsent(world.getChunk(pos).getPos(), k -> new ArrayList<>());
        entry.add(new Tuple<>(pos, material));
        return true;
    }

    public static boolean setStone(IWorld world, BlockPos pos, BlockState existing, WorldGenStoneLayer stoneLayer) {
        //if (rockChance > 0 && stoneLayer.getStoneType() != null) addRock(world, pos, existing, stoneLayer.getStoneType().getMaterial(), rockChance);
        return setStone(world, pos, existing, stoneLayer.getStoneState());
    }

    public static boolean setStone(IWorld world, BlockPos pos, BlockState existing, BlockState replacement) {
        if (!existing.isReplaceableOreGen(world, pos, STONE_PREDICATE)) return false;
        return setState(world, pos, replacement);
    }

    public static BlockState waterLogState(BlockState state) {
        return state.has(BlockStateProperties.WATERLOGGED) ? state.with(BlockStateProperties.WATERLOGGED, true) : state;
    }

    public static boolean canSetTree(IWorld world, BlockPos pos) {
        //Biome biome = world.getBiome(pos);
        //return biome.getRegistryName() != null && TREE_BIOME_SET.contains(biome.getRegistryName().toString()) && TREE_SET.contains(world.getBlockState(pos));
        //TODO
        return false;
    }
}
