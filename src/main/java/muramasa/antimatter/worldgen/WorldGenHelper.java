package muramasa.antimatter.worldgen;

import com.google.common.base.Predicate;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.materials.MaterialType;
import muramasa.antimatter.ore.StoneType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class WorldGenHelper {

    public static Object2ObjectOpenHashMap<BlockState, StoneType> STONE_MAP = new Object2ObjectOpenHashMap<>();
    public static Object2ObjectOpenHashMap<BlockState, BlockState> ROCK_MAP = new Object2ObjectOpenHashMap<>();
    public static ObjectOpenHashSet<BlockState> STONE_SET = new ObjectOpenHashSet<>();
    public static ObjectOpenHashSet<BlockState> TREE_SET = new ObjectOpenHashSet<>();
    public static ObjectOpenHashSet<String> TREE_BIOME_SET = new ObjectOpenHashSet<>();

    public static BlockState STONE_STATE = Blocks.STONE.getDefaultState();

    public static Predicate<BlockState> ORE_PREDICATE = state -> STONE_MAP.containsKey(state);
    public static Predicate<BlockState> ROCK_PREDICATE = state -> ROCK_MAP.containsKey(state);
    public static Predicate<BlockState> STONE_PREDICATE = state -> STONE_SET.contains(state);

    public static void init() {
        AntimatterAPI.all(StoneType.class).forEach(t -> STONE_MAP.put(t.getState(), t));

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

    public static void setState(IWorld world, BlockPos pos, BlockState state) {
        world.setBlockState(pos, state, 2 | 16);
    }

    private static void setRockState(IWorld world, BlockPos pos, BlockState state, Material material) {
//        world.setBlockState(pos, state, 2 | 16);
//        TileEntity tile = world.getTileEntity(pos);
//        if (tile instanceof TileEntityMaterial) ((TileEntityMaterial) tile).init(material);
    }

    public static boolean setOre(IWorld world, BlockPos pos, BlockState existing, Material material, MaterialType<?> type) {
        StoneType stone = STONE_MAP.get(existing);
        if (stone != null) {
            BlockState block = type == MaterialType.ORE ? MaterialType.ORE.get().get(material, stone).asState() : MaterialType.ORE_SMALL.get().get(material, stone).asState();
            return setOre(world, pos, existing, block);
        }
        return false;
    }

    public static boolean setOre(IWorld world, BlockPos pos, BlockState existing, BlockState replacement) {
        if (existing.isReplaceableOreGen(world, pos, ORE_PREDICATE)) {
            setState(world, pos, replacement);
            return true;
        }
        return false;
    }

    public static boolean setRock(IWorld world, BlockPos pos, Material material) {
//        pos = world.getHeight(Heightmap.Type.WORLD_SURFACE, pos).down();
//        BlockState existing = world.getBlockState(pos);
//        if (existing.getBlock().isReplaceableOreGen(existing, world, pos, ROCK_PREDICATE)) {
//            BlockState toSet = ROCK_MAP.get(existing);
//            if (toSet == null) toSet = ROCK_DEFAULT;
//            setRockState(world, pos.up(), toSet, material);
//            return true;
//        }
        return false;
    }

    public static boolean setStone(IWorld world, BlockPos pos, BlockState existing, StoneType stoneType) {
        return setStone(world, pos, existing, stoneType.getState());
    }

    public static boolean setStone(IWorld world, BlockPos pos, BlockState existing, BlockState replacement) {
        if (existing.isReplaceableOreGen(world, pos, STONE_PREDICATE)) {
            world.setBlockState(pos, replacement, 2 | 16);
            return true;
        }
        return false;
    }

    public static boolean canSetTree(IWorld world, BlockPos pos) {
        //Biome biome = world.getBiome(pos);
        //return biome.getRegistryName() != null && TREE_BIOME_SET.contains(biome.getRegistryName().toString()) && TREE_SET.contains(world.getBlockState(pos));
        //TODO
        return false;
    }
}
