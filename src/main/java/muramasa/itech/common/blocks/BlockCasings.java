package muramasa.itech.common.blocks;

import muramasa.itech.ITech;
import muramasa.itech.api.enums.CasingType;
import muramasa.itech.common.tileentities.base.multi.TileEntityCasing;
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

public class BlockCasings extends Block {

    public static final PropertyEnum<CasingType> CASING_TYPE = PropertyEnum.create("casingtype", CasingType.class);

    public BlockCasings() {
        super(net.minecraft.block.material.Material.IRON);
        setUnlocalizedName(ITech.MODID + "blockcasings");
        setRegistryName("blockcasings");
        setCreativeTab(ITech.TAB_MACHINES);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(CASING_TYPE).build();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(CASING_TYPE, CasingType.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(CASING_TYPE).ordinal();
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (CasingType type : CasingType.values()) {
            items.add(new ItemStack(this, 1, type.ordinal()));
        }
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        return getDefaultState().withProperty(CASING_TYPE, CasingType.values()[placer.getHeldItem(hand).getMetadata()]);
    }

//    @Override
//    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
//        return getDefaultState().withProperty(CASING_TYPE, target.);
//    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityCasing();
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
////        super.breakBlock(world, pos, state);
//        world.removeTileEntity(pos);
//    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        for (CasingType type : CasingType.values()) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), type.ordinal(), new ModelResourceLocation(getRegistryName(), "casingtype=" + type.getName()));
        }
    }
}
