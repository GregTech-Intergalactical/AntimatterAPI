package muramasa.itech.common.blocks;

import muramasa.itech.ITech;
import muramasa.itech.api.enums.CoilType;
import muramasa.itech.common.tileentities.multi.TileEntityCoil;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockCoils extends Block {

    public static final PropertyEnum<CoilType> COIL_TYPE = PropertyEnum.create("coiltype", CoilType.class);

    public BlockCoils() {
        super(net.minecraft.block.material.Material.IRON);
        setUnlocalizedName(ITech.MODID + "blockcoils");
        setRegistryName("blockcoils");
        setCreativeTab(ITech.TAB_MACHINES);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(COIL_TYPE).build();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(COIL_TYPE, CoilType.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(COIL_TYPE).ordinal();
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (CoilType type : CoilType.values()) {
            items.add(new ItemStack(this, 1, type.ordinal()));
        }
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(COIL_TYPE, CoilType.values()[placer.getHeldItem(hand).getMetadata()]);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityCoil();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

//    @Override
//    public void breakBlock(World world, BlockPos pos, IBlockState state) {
////        TileEntity tile = world.getTileEntity(pos);
////        super.breakBlock(world, pos, state);
////        if (tile != null && tile instanceof TileEntityComponent) {
////            ((TileEntityComponent) tile).notifyOfRemoval();
////        }
//        world.removeTileEntity(pos);
//    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        for (CoilType type : CoilType.values()) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.ordinal(), new ModelResourceLocation(getRegistryName(), "coiltype=" + type.getName()));
        }
    }
}
