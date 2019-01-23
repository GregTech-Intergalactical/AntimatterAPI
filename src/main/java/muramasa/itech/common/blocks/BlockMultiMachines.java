package muramasa.itech.common.blocks;

import muramasa.itech.ITech;
import muramasa.itech.api.enums.MachineFlag;
import muramasa.itech.api.machines.MachineStack;
import muramasa.itech.api.properties.UnlistedString;
import muramasa.itech.api.util.Utils;
import muramasa.itech.common.items.ItemBlockMultiMachines;
import muramasa.itech.common.tileentities.multi.TileEntityMultiMachine;
import muramasa.itech.common.utils.Ref;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockMultiMachines extends Block {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final UnlistedString TYPE = new UnlistedString();

    public BlockMultiMachines() {
        super(net.minecraft.block.material.Material.IRON);
        setUnlocalizedName(ITech.MODID + "blockmultimachines");
        setRegistryName("blockmultimachines");
        setCreativeTab(ITech.TAB_MACHINES);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(FACING).add(TYPE).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityMultiMachine) {
            TileEntityMultiMachine machine = (TileEntityMultiMachine) tile;
            exState = exState.withProperty(TYPE, machine.getType());
        } else {
            System.err.println("TILE INSTANCE NOT EQUAL: " + tile);
        }
        return exState;
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (MachineStack stack : MachineFlag.MULTI.getStacks()) {
            items.add(stack.asItemStack());
        }
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return null;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return false;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (state instanceof IExtendedBlockState) {
            player.openGui(ITech.INSTANCE, Ref.MULTI_MACHINE_ID, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileEntityMultiMachine && stack.getItem() instanceof ItemBlockMultiMachines) {
            if (stack.hasTagCompound()) {
                NBTTagCompound data = (NBTTagCompound) stack.getTagCompound().getTag(Ref.TAG_MACHINE_STACK_DATA);
                TileEntityMultiMachine machine = (TileEntityMultiMachine) tile;
                machine.init(data.getString(Ref.KEY_MACHINE_STACK_TYPE));
            }
        }
        world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
        super.onBlockPlacedBy(world, pos, state, placer, stack);
    }

//    @Override
//    public void breakBlock(World world, BlockPos pos, IBlockState state) {
//        TileEntity tile = world.getTileEntity(pos);
////        super.breakBlock(world, pos, state);
////        if (tile instanceof TileEntityMultiMachine) {
////            ((TileEntityMultiMachine) tile).clearComponents();
////        }
////        super.breakBlock(world, pos, state);
//        world.removeTileEntity(pos);
//    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
//        ModelLoader.setCustomStateMapper(this, new StateMapperRedirect(this));
    }
}
