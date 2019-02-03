package muramasa.itech.common.blocks;

import muramasa.itech.ITech;
import muramasa.itech.api.capability.ICoverable;
import muramasa.itech.api.capability.ITechCapabilities;
import muramasa.itech.api.enums.CoverType;
import muramasa.itech.api.enums.MachineFlag;
import muramasa.itech.api.machines.MachineList;
import muramasa.itech.api.machines.MachineStack;
import muramasa.itech.api.properties.ITechProperties;
import muramasa.itech.api.util.Utils;
import muramasa.itech.client.render.bakedmodels.BakedModelBase;
import muramasa.itech.common.items.ItemBlockMachines;
import muramasa.itech.common.tileentities.base.TileEntityMachine;
import muramasa.itech.common.utils.Ref;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import static muramasa.itech.api.properties.ITechProperties.*;

public class BlockMachine extends Block {

    public BlockMachine(String name) {
        super(net.minecraft.block.material.Material.IRON);
        setUnlocalizedName(Ref.MODID + name);
        setRegistryName(name);
        setSoundType(SoundType.METAL);
        setCreativeTab(Ref.TAB_MACHINES);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(TYPE, TIER, FACING, STATE, TINT, COVERS).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityMachine) {
            TileEntityMachine machine = (TileEntityMachine) tile;
            exState = exState
                .withProperty(TYPE, machine.getTypeId())
                .withProperty(TIER, machine.getTierId())
                .withProperty(FACING, machine.getFacing())
                .withProperty(STATE, machine.getMachineState().getOverlayId())
                .withProperty(TINT, machine.getTint());
            ICoverable coverHandler = tile.getCapability(ITechCapabilities.COVERABLE, null);
            if (coverHandler != null) {
                exState = exState
                    .withProperty(COVERS, new CoverType[] {
                        coverHandler.getCover(EnumFacing.SOUTH),
                        coverHandler.getCover(EnumFacing.EAST),
                        coverHandler.getCover(EnumFacing.WEST),
                        coverHandler.getCover(EnumFacing.DOWN),
                        coverHandler.getCover(EnumFacing.UP),
                    });
            }
        }
        return exState;
    }

//    @Override
//    public IBlockState getStateFromMeta(int meta) {
//        return getDefaultState().withProperty(FACING, EnumFacing.HORIZONTALS[meta]);
//    }

    @Override
    public int getMetaFromState(IBlockState state) {
//        return state.getValue(FACING).getIndex() - 2;
        return 0;
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (MachineStack stack : MachineFlag.BASIC.getStacks()) {
            items.add(stack.asItemStack());
        }
        for (MachineStack stack : MachineFlag.MULTI.getStacks()) {
            items.add(stack.asItemStack());
        }
        for (MachineStack stack : MachineFlag.HATCH.getStacks()) {
            items.add(stack.asItemStack());
        }
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state) {
        return new TileEntityMachine();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (state instanceof IExtendedBlockState) {
            TileEntity tile = Utils.getTile(world, pos);
            if (tile instanceof TileEntityMachine) {
                int guiId = ((TileEntityMachine) tile).getMachineType().getGuiId();
                player.openGui(ITech.INSTANCE, guiId, world, pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (stack.getItem() instanceof ItemBlockMachines) {
            if (stack.hasTagCompound()) {
                NBTTagCompound data = (NBTTagCompound) stack.getTagCompound().getTag(Ref.TAG_MACHINE_STACK_DATA);
                String machineType = data.getString(Ref.KEY_MACHINE_STACK_TYPE);
                String machineTier = data.getString(Ref.KEY_MACHINE_STACK_TIER);
                try {
                    TileEntity tile = (TileEntity) MachineList.get(machineType).getTileClass().newInstance();
                    if (tile instanceof TileEntityMachine) {
                        world.setTileEntity(pos, tile);
                        tile = world.getTileEntity(pos);
                        if (tile instanceof TileEntityMachine) {
                            ((TileEntityMachine) tile).init(machineType, machineTier, placer.getHorizontalFacing().getOpposite().getIndex() - 2);
//                            ((TileEntityMachine) tile).setFacing();
                        }
                    }
                } catch (IllegalAccessException | InstantiationException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    //    @Override
//    public void breakBlock(World world, BlockPos pos, IBlockState state) {
//        ItemStack stack = new ItemStack(Item.getItemFromBlock(state.getBlock()), 1, (state.getValue(TYPE) * 10) + state.getValue(VOLTAGE));
//        world.spawnEntity(new EntityItem(world, pos.getX(), pos.getY(), pos.getZ(), stack));
//    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
//        if (state instanceof IExtendedBlockState) {
////            System.out.println("EX");
//            IExtendedBlockState exState = (IExtendedBlockState) state;
////            MachineStack machineStack = MachineList.get(exState.getValue(TYPE), exState.getValue(TIER));
//            MachineStack machineStack = new MachineStack(MachineList.INVALID, Tier.NONE);
//
//            TileEntity tile = Utils.getTile(world, pos);
//            if (tile instanceof TileEntityMachine) {
//                TileEntityMachine machine = (TileEntityMachine) tile;
////                System.out.println(machine.getType());
//            }
//
//
//            if (machineStack != null) {
//
////                System.out.println("MS VALID");
//                return machineStack.asItemStack();
//            }
//        }
        return new ItemStack(Blocks.DIAMOND_BLOCK);
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    public static class ColorHandler implements IBlockColor {
        @Override
        public int colorMultiplier(IBlockState state, @Nullable IBlockAccess worldIn, @Nullable BlockPos pos, int tintIndex) {
            if (tintIndex == 0 && state instanceof IExtendedBlockState) {
                IExtendedBlockState exState = (IExtendedBlockState) state;
                if (BakedModelBase.hasUnlistedProperty(exState, ITechProperties.TINT)) {
                    return exState.getValue(ITechProperties.TINT) != null ? exState.getValue(ITechProperties.TINT) : -1;
                }
            }
            return -1;
        }
    }
}
