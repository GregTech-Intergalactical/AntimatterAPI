package muramasa.gtu.api.blocks.pipe;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.blocks.BlockBaked;
import muramasa.gtu.api.materials.Material;
import muramasa.gtu.api.pipe.PipeSize;
import muramasa.gtu.api.registration.IColorHandler;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.registration.IItemBlock;
import muramasa.gtu.api.registration.IModelOverride;
import muramasa.gtu.api.texture.TextureData;
import muramasa.gtu.api.tileentities.pipe.TileEntityPipe;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.client.render.StateMapperRedirect;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

import static muramasa.gtu.api.properties.GTProperties.*;

public abstract class BlockPipe extends BlockBaked implements IGregTechObject, IItemBlock, IModelOverride, IColorHandler {

    protected String type, id;
    protected Material material;
    protected PipeSize[] sizes;
    protected PropertyEnum<PipeSize> PIPE_SIZE;

    public BlockPipe(String type, Material material, TextureData data, PipeSize... sizes) {
        super(data);
        this.type = type;
        this.id = material.getId();
        this.material = material;
        this.sizes = sizes.length > 0 ? sizes : PipeSize.VALUES;
        PIPE_SIZE = PropertyEnum.create("size", PipeSize.class, this.sizes);

        //Hack to dynamically create a BlockState with a correctly sized size property based on the passed sizes array
        BlockStateContainer blockStateContainer = createBlockState();
        ObfuscationReflectionHelper.setPrivateValue(Block.class, this, blockStateContainer, 21);
        setDefaultState(blockStateContainer.getBaseState());

        setUnlocalizedName(getId());
        setRegistryName(getId());
        setCreativeTab(Ref.TAB_MACHINES);
        GregTechAPI.register(BlockPipe.class, this);
    }

    @Override
    public String getId() {
        return type + "_" + id;
    }

    public int getRGB() {
        return material.getRGB();
    }

    public PipeSize[] getSizes() {
        return sizes;
    }

    public PropertyEnum<PipeSize> getSizeProp() {
        return PIPE_SIZE;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return PIPE_SIZE != null ? new BlockStateContainer.Builder(this).add(PIPE_SIZE).add(PIPE_CONNECTIONS, TEXTURE, COVER).build() : new BlockStateContainer(this);
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        IExtendedBlockState exState = (IExtendedBlockState) state;
        TileEntity tile = Utils.getTile(world, pos);
        if (tile instanceof TileEntityPipe) {
            TileEntityPipe pipe = (TileEntityPipe) tile;
            exState = exState.withProperty(PIPE_CONNECTIONS, pipe.getConnections());
            exState = exState.withProperty(TEXTURE, getDefaultData());
            if (pipe.coverHandler.isPresent()) {
                exState = exState.withProperty(COVER, pipe.coverHandler.get().getAll());
            }
        }
        return exState;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(PIPE_SIZE, PipeSize.VALUES[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(PIPE_SIZE).ordinal();
    }

    @Override
    public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
        for (int i = 0; i < sizes.length; i++) {
            items.add(new ItemStack(this, 1, sizes[i].ordinal()));
        }
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

            //TODO temp disable
            //return size != null ? size.getAABB() : PipeSize.TINY.getAABB();
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
        return "wrench";
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tile = Utils.getTile(world, pos);
        return tile != null && GregTechAPI.interact(tile, player, hand, side, hitX, hitY, hitZ);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
        int stackMeta = placer.getHeldItem(hand).getMetadata();
        int size = stackMeta > 7 ? stackMeta - 8 : stackMeta;
        return getDefaultState().withProperty(PIPE_SIZE, PipeSize.VALUES[size]);
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return new ItemStack(this, 1, state.getValue(PIPE_SIZE).ordinal());
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
    public int getBlockColor(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int i) {
        return i == 0 || i == 1 || i == 2 ? getRGB() : -1;
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return i == 0 || i == 1 || i == 2 ? ((BlockPipe) block).getRGB() : -1;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onModelRegistration() {
        for (int i = 0; i < sizes.length; i++) {
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), sizes[i].ordinal(), new ModelResourceLocation(Ref.MODID + ":" + getId(), "size=" + sizes[i].getName()));
        }
        //Redirect block model to custom baked model handling
        ModelLoader.setCustomStateMapper(this, new StateMapperRedirect(new ResourceLocation(Ref.MODID, "block_pipe")));
    }
}
