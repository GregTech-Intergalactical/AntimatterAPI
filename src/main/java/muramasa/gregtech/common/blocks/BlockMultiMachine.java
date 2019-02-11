package muramasa.gregtech.common.blocks;

import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import static muramasa.gregtech.api.properties.ITechProperties.*;

public class BlockMultiMachine extends BlockMachine {

    public BlockMultiMachine(String type) {
        super(type);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(FACING).add(TYPE, TIER, OVERLAY, TINT, TEXTURE).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        return super.getExtendedState(state, world, pos);
    }

    //    @Override
//    protected BlockStateContainer createBlockState() {
//        return new BlockStateContainer.Builder(this).add(FACING).add(TYPE).build();
//    }
//
//    @Override
//    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
//        IExtendedBlockState exState = (IExtendedBlockState) state;
//        TileEntity tile = Utils.getTile(world, pos);
//        if (tile instanceof TileEntityMultiMachine) {
//            TileEntityMultiMachine machine = (TileEntityMultiMachine) tile;
//            exState = exState.withProperty(TYPE, machine.getType());
//        } else {
//            System.err.println("TILE INSTANCE NOT EQUAL: " + tile);
//        }
//        return exState;
//    }
//
//    @Override
//    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
//        for (MachineStack stack : MachineFlag.MULTI.getStacks()) {
//            items.add(stack.asItemStack());
//        }
//    }
//
//    @Override
//    public int getMetaFromState(IBlockState state) {
//        return 0;
//    }
//
//    @Nullable
//    @Override
//    public TileEntity createTileEntity(World world, IBlockState state) {
//        return new TileEntityMultiMachine();
//    }
//
//    @Override
//    public boolean hasTileEntity(IBlockState state) {
//        return true;
//    }
//
//    @Override
//    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
//        if (state instanceof IExtendedBlockState) {
//            player.openGui(ITech.INSTANCE, Ref.MULTI_MACHINE_ID, world, pos.getX(), pos.getY(), pos.getZ());
//        }
//        return true;
//    }
//
//    @Override
//    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
//        TileEntity tile = world.getTileEntity(pos);
//        if (tile != null && tile instanceof TileEntityMultiMachine && stack.getItem() instanceof ItemBlockMultiMachines) {
//            if (stack.hasTagCompound()) {
//                NBTTagCompound data = (NBTTagCompound) stack.getTagCompound().getTag(Ref.TAG_MACHINE_STACK_DATA);
//                TileEntityMultiMachine machine = (TileEntityMultiMachine) tile;
//                machine.init(data.getString(Ref.KEY_MACHINE_STACK_TYPE), Tier.MULTI.getName());
//            }
//        }
//        world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
//        super.onBlockPlacedBy(world, pos, state, placer, stack);
//    }
//
////    @Override
////    public void breakBlock(World world, BlockPos pos, IBlockState state) {
////        TileEntity tile = world.getTileEntity(pos);
//////        super.breakBlock(world, pos, state);
//////        if (tile instanceof TileEntityMultiMachine) {
//////            ((TileEntityMultiMachine) tile).clearComponents();
//////        }
//////        super.breakBlock(world, pos, state);
////        world.removeTileEntity(pos);
////    }
//
//    @Override
//    public BlockRenderLayer getBlockLayer() {
//        return BlockRenderLayer.CUTOUT_MIPPED;
//    }
//
//    @SideOnly(Side.CLIENT)
//    public void initModel() {
////        ModelLoader.setCustomStateMapper(this, new StateMapperRedirect(this));
//    }
}
