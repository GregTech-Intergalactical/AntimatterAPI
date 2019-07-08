package muramasa.gtu.api.worldgen;

import com.google.common.base.Predicate;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.gtu.api.blocks.BlockStone;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.ore.BlockOre;
import muramasa.gtu.api.ore.OreType;
import muramasa.gtu.api.ore.StoneType;
import muramasa.gtu.api.properties.GTProperties;
import muramasa.gtu.api.tileentities.TileEntityOre;
import net.minecraft.block.BlockSand;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenHelper {

    public static Object2ObjectOpenHashMap<IBlockState, IBlockState> ORE_MAP = new Object2ObjectOpenHashMap<>();
    public static ObjectOpenHashSet<IBlockState> STONE_SET = new ObjectOpenHashSet<>();
    public static IBlockState ORE_DEFAULT;

    public static Predicate<IBlockState> ORE_PREDICATE = state -> ORE_MAP.containsKey(state);
    public static Predicate<IBlockState> STONE_PREDICATE = state -> STONE_SET.contains(state);

    public static void init() {
        ORE_DEFAULT = BlockOre.get(StoneType.STONE).getDefaultState();

        ORE_MAP.put(Blocks.STONE.getDefaultState().withProperty(net.minecraft.block.BlockStone.VARIANT, net.minecraft.block.BlockStone.EnumType.STONE), BlockOre.get(StoneType.STONE).getDefaultState());
        ORE_MAP.put(Blocks.STONE.getDefaultState().withProperty(net.minecraft.block.BlockStone.VARIANT, net.minecraft.block.BlockStone.EnumType.GRANITE), BlockOre.get(StoneType.GRANITE).getDefaultState());
        ORE_MAP.put(Blocks.STONE.getDefaultState().withProperty(net.minecraft.block.BlockStone.VARIANT, net.minecraft.block.BlockStone.EnumType.DIORITE), BlockOre.get(StoneType.DIORITE).getDefaultState());
        ORE_MAP.put(Blocks.STONE.getDefaultState().withProperty(net.minecraft.block.BlockStone.VARIANT, net.minecraft.block.BlockStone.EnumType.ANDESITE), BlockOre.get(StoneType.ANDESITE).getDefaultState());
        ORE_MAP.put(Blocks.SAND.getDefaultState().withProperty(BlockSand.VARIANT, BlockSand.EnumType.SAND), BlockOre.get(StoneType.SAND).getDefaultState());
        ORE_MAP.put(Blocks.SAND.getDefaultState().withProperty(BlockSand.VARIANT, BlockSand.EnumType.RED_SAND), BlockOre.get(StoneType.SAND_RED).getDefaultState());
        ORE_MAP.put(Blocks.SANDSTONE.getDefaultState(), BlockOre.get(StoneType.SANDSTONE).getDefaultState());
        ORE_MAP.put(Blocks.NETHERRACK.getDefaultState(), BlockOre.get(StoneType.NETHERRACK).getDefaultState());
        ORE_MAP.put(Blocks.END_STONE.getDefaultState(), BlockOre.get(StoneType.ENDSTONE).getDefaultState());
        ORE_MAP.put(BlockStone.get(StoneType.GRANITE_RED).getDefaultState(), BlockOre.get(StoneType.GRANITE_RED).getDefaultState());
        ORE_MAP.put(BlockStone.get(StoneType.GRANITE_RED).getDefaultState(), BlockOre.get(StoneType.GRANITE_RED).getDefaultState());
        ORE_MAP.put(BlockStone.get(StoneType.MARBLE).getDefaultState(), BlockOre.get(StoneType.MARBLE).getDefaultState());
        ORE_MAP.put(BlockStone.get(StoneType.BASALT).getDefaultState(), BlockOre.get(StoneType.BASALT).getDefaultState());

        STONE_SET.add(Blocks.STONE.getDefaultState().withProperty(net.minecraft.block.BlockStone.VARIANT, net.minecraft.block.BlockStone.EnumType.STONE));
        STONE_SET.add(Blocks.STONE.getDefaultState().withProperty(net.minecraft.block.BlockStone.VARIANT, net.minecraft.block.BlockStone.EnumType.GRANITE));
        STONE_SET.add(Blocks.STONE.getDefaultState().withProperty(net.minecraft.block.BlockStone.VARIANT, net.minecraft.block.BlockStone.EnumType.DIORITE));
        STONE_SET.add(Blocks.STONE.getDefaultState().withProperty(net.minecraft.block.BlockStone.VARIANT, net.minecraft.block.BlockStone.EnumType.ANDESITE));
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

    public static boolean setOreState(World world, BlockPos pos, IBlockState existing, Material material, OreType type) {
        if (existing.getBlock().isReplaceableOreGen(existing, world, pos, ORE_PREDICATE)) {
            IBlockState toSet = ORE_MAP.get(existing);
            if (toSet == null) toSet = ORE_DEFAULT;
            world.setBlockState(pos, toSet.withProperty(GTProperties.ORE_TYPE, type), 2 | 16);
            TileEntity tile = world.getTileEntity(pos);
            if (tile instanceof TileEntityOre) ((TileEntityOre) tile).init(material);
            return true;
        }
        return false;
    }
}
