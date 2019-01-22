package muramasa.itech.common.blocks;

import muramasa.itech.ITech;
import muramasa.itech.api.capability.ICoverable;
import muramasa.itech.api.capability.ITechCapabilities;
import muramasa.itech.api.enums.AbilityFlag;
import muramasa.itech.api.enums.CoverType;
import muramasa.itech.api.machines.MachineStack;
import muramasa.itech.api.properties.UnlistedBoolean;
import muramasa.itech.api.properties.UnlistedCoverType;
import muramasa.itech.api.properties.UnlistedString;
import muramasa.itech.api.util.Utils;
import muramasa.itech.common.items.ItemBlockMachines;
import muramasa.itech.common.tileentities.TileEntityMachine;
import muramasa.itech.common.utils.Ref;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockMachines extends Block {

    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final UnlistedString TYPE = new UnlistedString();
    public static final UnlistedString TIER = new UnlistedString();
    public static final UnlistedBoolean ACTIVE = new UnlistedBoolean();
    public static final UnlistedCoverType COVERS = new UnlistedCoverType();

    public BlockMachines() {
        super(net.minecraft.block.material.Material.IRON);
        setUnlocalizedName(ITech.MODID + "blockmachines");
        setRegistryName("blockmachines");
        setCreativeTab(ITech.TAB_MACHINES);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(FACING).add(TYPE, TIER, ACTIVE, COVERS).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityMachine) {
            TileEntityMachine machine = (TileEntityMachine) tile;
            ICoverable coverHandler = tile.getCapability(ITechCapabilities.COVERABLE, null);
            exState = exState
                .withProperty(TYPE, machine.getType())
                .withProperty(TIER, machine.getTier())
                .withProperty(ACTIVE, machine.getCurProgress() > 0)
                .withProperty(COVERS, new CoverType[] {
                    coverHandler.getCover(EnumFacing.SOUTH),
                    coverHandler.getCover(EnumFacing.EAST),
                    coverHandler.getCover(EnumFacing.WEST),
                    coverHandler.getCover(EnumFacing.DOWN),
                    coverHandler.getCover(EnumFacing.UP),
                });
        } else {
            System.err.println("TILE INSTANCE NOT EQUAL: " + tile);
        }
        return exState;
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (MachineStack stack : AbilityFlag.BASIC.getStacks()) {
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
        return new TileEntityMachine();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (state instanceof IExtendedBlockState) {
            player.openGui(ITech.INSTANCE, Ref.MACHINE_ID, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityMachine && stack.getItem() instanceof ItemBlockMachines) {
            if (stack.hasTagCompound()) {
                NBTTagCompound data = (NBTTagCompound) stack.getTagCompound().getTag(Ref.TAG_MACHINE_STACK_DATA);
                TileEntityMachine machine = (TileEntityMachine) tile;
                machine.init(data.getString(Ref.KEY_MACHINE_STACK_TYPE), data.getString(Ref.KEY_MACHINE_STACK_TIER));
            }
            world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
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
    public void initItemModel() {
        Item itemBlock = Item.REGISTRY.getObject(new ResourceLocation(ITech.MODID, "blockmachines"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(itemBlock, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
