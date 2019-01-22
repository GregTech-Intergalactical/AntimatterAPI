package muramasa.itech.common.blocks;

import muramasa.itech.ITech;
import muramasa.itech.api.enums.AbilityFlag;
import muramasa.itech.api.enums.HatchTexture;
import muramasa.itech.api.machines.MachineStack;
import muramasa.itech.api.properties.UnlistedString;
import muramasa.itech.common.items.ItemBlockMachines;
import muramasa.itech.common.tileentities.multi.TileEntityHatch;
import muramasa.itech.common.utils.Ref;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockHatches extends Block {

    public static final UnlistedString TYPE = new UnlistedString();
    public static final UnlistedString TIER = new UnlistedString();
    public static final PropertyEnum<HatchTexture> TEXTURE = PropertyEnum.create("texture", HatchTexture.class);
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    public BlockHatches() {
        super(net.minecraft.block.material.Material.IRON);
        setUnlocalizedName(ITech.MODID + "blockhatches");
        setRegistryName("blockhatches");
        setCreativeTab(ITech.TAB_MACHINES);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(TEXTURE, FACING).add(TYPE, TIER).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState extendedState = (IExtendedBlockState) state;
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileEntityHatch) {
            TileEntityHatch hatch = (TileEntityHatch) tile;
            extendedState = extendedState.withProperty(TYPE, hatch.getType()).withProperty(TIER, hatch.getTier());
        }
        return extendedState;
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (MachineStack stack : AbilityFlag.HATCH.getStacks()) {
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
        return new TileEntityHatch();
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (state instanceof IExtendedBlockState) {
            player.openGui(ITech.INSTANCE, Ref.HATCH_ID, world, pos.getX(), pos.getY(), pos.getZ());
        }
        System.out.println(state.getValue(FACING));
        return true;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile != null && tile instanceof TileEntityHatch && stack.getItem() instanceof ItemBlockMachines) {
            if (stack.hasTagCompound()) {
                NBTTagCompound data = (NBTTagCompound) stack.getTagCompound().getTag(Ref.TAG_MACHINE_STACK_DATA);
                TileEntityHatch hatch = (TileEntityHatch) tile;
                hatch.init(data.getString(Ref.KEY_MACHINE_STACK_TYPE), data.getString(Ref.KEY_MACHINE_STACK_TIER));
            }
            world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
        }
    }

    @Override
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @SideOnly(Side.CLIENT)
    public void initItemModel() {
        Item itemBlock = Item.REGISTRY.getObject(new ResourceLocation(ITech.MODID, "blockhatches"));
        Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(itemBlock, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
}
