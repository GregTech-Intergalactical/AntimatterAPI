package muramasa.gtu.api.worldgen;

import com.google.common.base.Predicate;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.gtu.Ref;
import muramasa.gtu.api.blocks.BlockStone;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.ore.BlockOre;
import muramasa.gtu.api.ore.BlockRock;
import muramasa.gtu.api.ore.OreType;
import muramasa.gtu.api.ore.StoneType;
import muramasa.gtu.api.tileentities.TileEntityMaterial;
import net.minecraft.block.BlockSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenHelper {

    public static Object2ObjectOpenHashMap<IBlockState, StoneType> STONE_MAP = new Object2ObjectOpenHashMap<>();
    public static Object2ObjectOpenHashMap<IBlockState, IBlockState> ROCK_MAP = new Object2ObjectOpenHashMap<>();
    public static ObjectOpenHashSet<IBlockState> STONE_SET = new ObjectOpenHashSet<>();
    public static ObjectOpenHashSet<IBlockState> TREE_SET = new ObjectOpenHashSet<>();
    public static ObjectOpenHashSet<String> TREE_BIOME_SET = new ObjectOpenHashSet<>();

    public static IBlockState ROCK_DEFAULT;

    public static Predicate<IBlockState> ORE_PREDICATE = state -> STONE_MAP.containsKey(state);
    public static Predicate<IBlockState> ROCK_PREDICATE = state -> ROCK_MAP.containsKey(state);
    public static Predicate<IBlockState> STONE_PREDICATE = state -> STONE_SET.contains(state);

    public static void init() {
        ROCK_DEFAULT = BlockRock.get(StoneType.STONE);

        for (StoneType stoneType : StoneType.getAllActive()) {
            IBlockState state = stoneType.getBaseState();
            if (state == StoneType.AIR) continue;
            STONE_MAP.put(state, stoneType);
            if (state.getBlock() == Blocks.STONE) {
                ROCK_MAP.put(state, ROCK_DEFAULT);
                STONE_SET.add(state);
            }
        }
        //STONE_MAP.put(Blocks.STONE.getDefaultState().withProperty(net.minecraft.block.BlockStone.VARIANT, net.minecraft.block.BlockStone.EnumType.STONE), StoneType.STONE);
        //STONE_MAP.put(Blocks.STONE.getDefaultState().withProperty(net.minecraft.block.BlockStone.VARIANT, net.minecraft.block.BlockStone.EnumType.GRANITE), StoneType.GRANITE);
        //STONE_MAP.put(Blocks.STONE.getDefaultState().withProperty(net.minecraft.block.BlockStone.VARIANT, net.minecraft.block.BlockStone.EnumType.DIORITE), StoneType.DIORITE);
        //STONE_MAP.put(Blocks.STONE.getDefaultState().withProperty(net.minecraft.block.BlockStone.VARIANT, net.minecraft.block.BlockStone.EnumType.ANDESITE), StoneType.ANDESITE);
        //STONE_MAP.put(Blocks.SAND.getDefaultState().withProperty(BlockSand.VARIANT, BlockSand.EnumType.SAND), StoneType.SAND);
        //STONE_MAP.put(Blocks.SAND.getDefaultState().withProperty(BlockSand.VARIANT, BlockSand.EnumType.RED_SAND), StoneType.SAND_RED);
        //STONE_MAP.put(Blocks.SANDSTONE.getDefaultState(), StoneType.SANDSTONE);
        //STONE_MAP.put(Blocks.NETHERRACK.getDefaultState(), StoneType.NETHERRACK);
        //STONE_MAP.put(Blocks.END_STONE.getDefaultState(), StoneType.ENDSTONE);

        //STONE_MAP.put(BlockStone.get(StoneType.GRANITE_RED).getDefaultState(), StoneType.GRANITE_RED);
        //STONE_MAP.put(BlockStone.get(StoneType.GRANITE_BLACK).getDefaultState(), StoneType.GRANITE_BLACK);
        //STONE_MAP.put(BlockStone.get(StoneType.MARBLE).getDefaultState(), StoneType.MARBLE);
        //STONE_MAP.put(BlockStone.get(StoneType.BASALT).getDefaultState(), StoneType.BASALT);

        ROCK_MAP.put(Blocks.GRASS.getDefaultState(), ROCK_DEFAULT);
        ROCK_MAP.put(Blocks.DIRT.getDefaultState(), ROCK_DEFAULT);
        //ROCK_MAP.put(Blocks.STONE.getDefaultState().withProperty(net.minecraft.block.BlockStone.VARIANT, net.minecraft.block.BlockStone.EnumType.STONE), BlockRock.get(StoneType.STONE));
        //ROCK_MAP.put(Blocks.STONE.getDefaultState().withProperty(net.minecraft.block.BlockStone.VARIANT, net.minecraft.block.BlockStone.EnumType.GRANITE), BlockRock.get(StoneType.STONE));
        //ROCK_MAP.put(Blocks.STONE.getDefaultState().withProperty(net.minecraft.block.BlockStone.VARIANT, net.minecraft.block.BlockStone.EnumType.DIORITE), BlockRock.get(StoneType.STONE));
        //ROCK_MAP.put(Blocks.STONE.getDefaultState().withProperty(net.minecraft.block.BlockStone.VARIANT, net.minecraft.block.BlockStone.EnumType.ANDESITE), BlockRock.get(StoneType.STONE));

        //STONE_SET.add(Blocks.STONE.getDefaultState().withProperty(net.minecraft.block.BlockStone.VARIANT, net.minecraft.block.BlockStone.EnumType.STONE));
        //STONE_SET.add(Blocks.STONE.getDefaultState().withProperty(net.minecraft.block.BlockStone.VARIANT, net.minecraft.block.BlockStone.EnumType.GRANITE));
        //STONE_SET.add(Blocks.STONE.getDefaultState().withProperty(net.minecraft.block.BlockStone.VARIANT, net.minecraft.block.BlockStone.EnumType.DIORITE));
        //STONE_SET.add(Blocks.STONE.getDefaultState().withProperty(net.minecraft.block.BlockStone.VARIANT, net.minecraft.block.BlockStone.EnumType.ANDESITE));

        TREE_SET.add(Blocks.GRASS.getDefaultState());
        TREE_BIOME_SET.add("Forest");
        TREE_BIOME_SET.add("ForestHills");
    }

    public static void setState(World world, BlockPos pos, IBlockState state) {
        world.setBlockState(pos, state, 2 | 16);
    }

//    public static boolean setStoneState(World world, BlockPos pos, IBlockState existing, IBlockState stone) {
//        if (existing.getBlock().isReplaceableOreGen(existing, world, pos, WorldGenHelper.STONE_PREDICATE)) {
//            world.setBlockState(pos, stone);
//        } else if (existing.getBlock() instanceof BlockOre) {
//            world.setBlockState(pos, WorldGenHelper.ORE_STATE, 2 | 16);
//            TileEntity tile = Utils.getTile(world, pos);
//            if (tile instanceof TileEntityOre) {
//                ((TileEntityOre) tile).init(((TileEntityOre) tile).getMaterial(), block.getType(), ((TileEntityOre) tile).getType());
//            }
//        }
//    }

    private static void setOreState(World world, BlockPos pos, StoneType stone, Material material, OreType type) {
        world.setBlockState(pos, BlockOre.get(type, material, stone), 2 | 16);
    }

    private static void setRockState(World world, BlockPos pos, IBlockState state, Material material) {
        world.setBlockState(pos, state, 2 | 16);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityMaterial) ((TileEntityMaterial) tile).init(material);
    }

    public static boolean setOre(World world, BlockPos pos, IBlockState existing, Material material, OreType type) {
        if (existing.getBlock().isReplaceableOreGen(existing, world, pos, ORE_PREDICATE)) {
            StoneType stone = STONE_MAP.get(world.getBlockState(pos));
            if (stone == null) stone = StoneType.STONE;
            setOreState(world, pos, stone, material, type);
            if (type == OreType.NORMAL && Ref.RNG.nextInt(64) == 0) setRock(world, pos, material);
            return true;
        }
        return false;
    }

    public static boolean setRock(World world, BlockPos pos, Material material) {
        pos = new BlockPos(pos.getX(), world.getHeight(pos.getX(), pos.getZ()) - 1, pos.getZ());
        IBlockState existing = world.getBlockState(pos);
        if (existing.getBlock().isReplaceableOreGen(existing, world, pos, ROCK_PREDICATE)) {
            IBlockState toSet = ROCK_MAP.get(existing);
            if (toSet == null) toSet = ROCK_DEFAULT;
            setRockState(world, pos.up(), toSet, material);
            return true;
        }
        return false;
    }

    public static boolean canSetTree(World world, BlockPos pos) {
        return TREE_BIOME_SET.contains(world.getBiome(pos).getBiomeName()) && TREE_SET.contains(world.getBlockState(pos));
    }
}
