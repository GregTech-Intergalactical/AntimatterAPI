package muramasa.gtu.api.worldgen;

import com.google.common.base.Predicate;
import muramasa.gtu.api.blocks.BlockStone;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.materials.MaterialType;
import muramasa.gtu.api.ore.StoneType;
import muramasa.gtu.api.tileentities.TileEntityOre;
import muramasa.gtu.common.Data;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WorldGenHelper {

    public static IBlockState ORE_STATE;

    static {
        ORE_STATE = Data.ORE.getDefaultState();
    }

    public static Predicate<IBlockState> ORE_PREDICATE = state -> {
        if (state == null) return false;
        if (state.getBlock() == Blocks.STONE) {
            net.minecraft.block.BlockStone.EnumType stoneType = state.getValue(net.minecraft.block.BlockStone.VARIANT);
            return stoneType.isNatural();
        } else if (state.getBlock() == Blocks.NETHERRACK || state.getBlock() == Blocks.END_STONE || state.getBlock() instanceof BlockStone) {
            return true;
        } else {
            return false;
        }
    };

    public static Predicate<IBlockState> STONE_PREDICATE = state -> {
        if (state == null) return false;
        if (state.getBlock() == Blocks.STONE) {
            net.minecraft.block.BlockStone.EnumType stoneType = state.getValue(net.minecraft.block.BlockStone.VARIANT);
            return stoneType.isNatural();
        }
        return false;
    };

    public static void setState(World world, BlockPos pos, IBlockState state) {
        world.setBlockState(pos, state, 2 | 16);
    }

    public static boolean setStateOre(World world, BlockPos pos, Material material, MaterialType materialType) {
        IBlockState state = world.getBlockState(pos);

        if (state.getBlock() instanceof BlockStone) {
            world.setBlockState(pos, ORE_STATE, 2 | 16);
            world.setTileEntity(pos, new TileEntityOre(material, ((BlockStone) state.getBlock()).getType(), materialType));
            return true;
        } else if (state.getBlock().isReplaceableOreGen(state, world, pos, ORE_PREDICATE)) {
            if (state.getBlock() == Blocks.NETHERRACK) {
                world.setBlockState(pos, ORE_STATE, 2 | 16);
                world.setTileEntity(pos, new TileEntityOre(material, StoneType.NETHERRACK, materialType));
            } else if (state.getBlock() == Blocks.END_STONE) {
                world.setBlockState(pos, ORE_STATE, 2 | 16);
                world.setTileEntity(pos, new TileEntityOre(material, StoneType.ENDSTONE, materialType));
            } else {
                world.setBlockState(pos, ORE_STATE, 2 | 16);
                world.setTileEntity(pos, new TileEntityOre(material, StoneType.STONE, materialType));
            }
            return true;
        }
        return false;
    }
}
