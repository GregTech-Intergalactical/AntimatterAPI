package muramasa.antimatter.worldgen;

import com.google.common.base.Predicate;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.materials.MaterialType;
import muramasa.antimatter.ore.BlockOre;
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

//    public static boolean setStoneState(World world, BlockPos pos, IBlockState existing, IBlockState stone) {
//        if (existing.getBlock().isReplaceableOreGen(existing, world, pos, WorldGenHelper.STONE_PREDICATE)) {
//            world.setBlockState(pos, stone);
//        } else if (existing.getBlock() instanceof BlockOre) {
//            world.setBlockState(pos, WorldGenHelper.ORE_STATE, 2 | 16);
//            TileEntity tile = Utils.getTile(world, pos);
//            if (tile instanceof TileEntityOre) {
//                ((TileEntityOre) tile).init(((TileEntityOre) tile).getMaterial(), block.getMaterialType(), ((TileEntityOre) tile).getMaterialType());
//            }
//        }
//    }

    private static void setOreState(IWorld world, BlockPos pos, StoneType stone, Material material, MaterialType type) {
        world.setBlockState(pos, BlockOre.get(material, type, stone), 2 | 16);
    }

    private static void setRockState(IWorld world, BlockPos pos, BlockState state, Material material) {
//        world.setBlockState(pos, state, 2 | 16);
//        TileEntity tile = world.getTileEntity(pos);
//        if (tile instanceof TileEntityMaterial) ((TileEntityMaterial) tile).init(material);
    }

    public static boolean setOre(IWorld world, BlockPos pos, BlockState existing, Material material, MaterialType type) {
        if (existing.getBlock().isReplaceableOreGen(existing, world, pos, ORE_PREDICATE)) {
            StoneType stoneType = STONE_MAP.get(world.getBlockState(pos));
            if (stoneType == null) return false;
            setOreState(world, pos, stoneType, material, type);
            if (type == MaterialType.ORE && Ref.RNG.nextInt(64) == 0) setRock(world, pos, material);
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

    public static boolean setStone(IWorld world, BlockPos pos, BlockState existing, StoneType type) {
        if (existing.isReplaceableOreGen(world, pos, STONE_PREDICATE)) {
            world.setBlockState(pos, type.getState(), 2 | 16);
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
