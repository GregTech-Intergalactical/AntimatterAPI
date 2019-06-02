package muramasa.gtu.common.blocks.pipe;

import muramasa.gtu.Ref;
import muramasa.gtu.api.pipe.PipeSize;
import muramasa.gtu.api.pipe.PipeStack;
import muramasa.gtu.api.pipe.types.Pipe;
import muramasa.gtu.api.registration.IHasItemBlock;
import muramasa.gtu.api.registration.IHasModelOverride;
import muramasa.gtu.api.texture.TextureData;
import muramasa.gtu.api.tileentities.pipe.TileEntityPipe;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.client.render.StateMapperRedirect;
import muramasa.gtu.common.blocks.BlockBaked;
import net.minecraft.block.Block;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

import static muramasa.gtu.api.properties.GTProperties.*;

public abstract class BlockPipe extends BlockBaked implements IHasItemBlock, IHasModelOverride {

    private static StateMapperRedirect stateMapRedirect = new StateMapperRedirect(new ResourceLocation(Ref.MODID, "block_pipe"));

    public BlockPipe(String name, TextureData data) {
        super(data);
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(Ref.TAB_MACHINES);
        setDefaultState(getDefaultState().withProperty(SIZE, 0));
    }

    public abstract Pipe getType();

    public int getRGB() {
        return getType().getRGB();
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer.Builder(this).add(SIZE).add(CONNECTIONS, TEXTURE).build();
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityPipe) {
            TileEntityPipe pipe = (TileEntityPipe) tile;
            exState = exState.withProperty(CONNECTIONS, pipe.getConnections());
            exState = exState.withProperty(TEXTURE, getBlockData());
        }
        return exState;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(SIZE, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(SIZE);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        TileEntity tile = Utils.getTile(source, pos);
        if (tile instanceof TileEntityPipe) {
            PipeSize size = ((TileEntityPipe) tile).getSize();
//            if (size == null) return FULL_BLOCK_AABB;
//            switch (BakedPipe.CONFIG_MAP.get(((TileEntityPipe) tile).connections)[0]) {
////                case 0: return new AxisAlignedBB(0.4375, 0.4375, 0.4375, 0.5625, 0.5625, 0.5625).grow(0.0625f * size.ordinal());
////                case 1: new AxisAlignedBB(0.4375, 0.4375, 0.4375, 0.5625, 0.5625, 0.5625).grow(1, 0, 0);
//                default: return new AxisAlignedBB(0.4375, 0.4375, 0.4375, 0.5625, 0.5625, 0.5625).grow(0.0625f * size.ordinal());
//            }

            return size != null ? size.getAABB() : PipeSize.TINY.getAABB();
        }
        return FULL_BLOCK_AABB;
    }

    @Override
    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    @Nullable
    @Override
    public abstract TileEntity createTileEntity(World world, IBlockState state);

    @Nullable
    @Override
    public String getHarvestTool(IBlockState state) {
        return "wire_cutter";
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        return false;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        ItemStack stack = placer.getHeldItem(hand);
        if (!stack.isEmpty() && stack.hasTagCompound()) {
            return getDefaultState().withProperty(SIZE, stack.getTagCompound().getInteger(Ref.KEY_PIPE_STACK_SIZE));
        }
        return getDefaultState();
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityPipe) {
            TileEntityPipe pipe = (TileEntityPipe) tile;
            return new PipeStack(pipe.getBlockType(), pipe.getType(), pipe.getSize()).asItemStack();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
//        TileEntity tile = Utils.getTile(world, pos);
//        if (tile instanceof TileEntityPipe) {
////            ((TileEntityPipe) tile).refreshConnections();
//        }
    }

    @Override
    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityPipe) {
            ((TileEntityPipe) tile).refreshConnections();
        }
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public String getItemStackDisplayName(Block block, ItemStack stack) {
        return getType().getDisplayName(stack);
    }

    @Override
    public List<String> addInformation(ItemStack stack) {
        return getType().getTooltip(stack);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(Ref.MODID + ":block_pipe", "inventory"));
        ModelLoader.setCustomStateMapper(this, stateMapRedirect);
    }
}
