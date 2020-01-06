package muramasa.antimatter.blocks.pipe;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.blocks.BlockDynamic;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.registration.IItemBlock;
import muramasa.antimatter.texture.TextureData;
import muramasa.gtu.data.Textures;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public abstract class BlockPipe extends BlockDynamic implements IItemBlock, IColorHandler {

    protected String prefix, id;
    protected Material material;
    protected PipeSize size;

    //TODO merge functionality with BlockDynamic
    public BlockPipe(String prefix, Material material, PipeSize size, TextureData data) {
        super(Block.Properties.create(net.minecraft.block.material.Material.IRON));
        this.prefix = prefix;
        this.id = material.getId();
        this.material = material;
        this.size = size;

        setRegistryName(getId());
        AntimatterAPI.register(BlockPipe.class, this);
    }

    @Override
    public String getId() {
        return prefix + "_" + id + "_" + size.getId();
    }

    public int getRGB() {
        return material.getRGB();
    }

    public PipeSize getSize() {
        return size;
    }

//    @Override
//    public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos) {
//        IExtendedBlockState exState = (IExtendedBlockState) state;
//        TileEntity tile = Utils.getTile(world, pos);
//        if (tile instanceof TileEntityPipe) {
//            TileEntityPipe pipe = (TileEntityPipe) tile;
//            exState = exState.withProperty(PIPE_CONNECTIONS, pipe.getConnections());
//            exState = exState.withProperty(TEXTURE, getData());
//            if (pipe.coverHandler.isPresent()) {
//                exState = exState.withProperty(COVER, pipe.coverHandler.get().getAll());
//            }
//        }
//        return exState;
//    }

//    @Override
//    public AxisAlignedBB getBoundingBox(BlockState state, IBlockReader source, BlockPos pos) {
//        TileEntity tile = Utils.getTile(source, pos);
//        if (tile instanceof TileEntityPipe) {
//            PipeSize size = ((TileEntityPipe) tile).getSize();
////            if (size == null) return FULL_BLOCK_AABB;
////            switch (BakedPipe.CONFIG_MAP.get(((TileEntityPipe) tile).connections)[0]) {
//////                case 0: return new AxisAlignedBB(0.4375, 0.4375, 0.4375, 0.5625, 0.5625, 0.5625).grow(0.0625f * size.ordinal());
//////                case 1: new AxisAlignedBB(0.4375, 0.4375, 0.4375, 0.5625, 0.5625, 0.5625).grow(1, 0, 0);
////                default: return new AxisAlignedBB(0.4375, 0.4375, 0.4375, 0.5625, 0.5625, 0.5625).grow(0.0625f * size.ordinal());
////            }
//
//            //TODO temp disable
//            //return size != null ? size.getAABB() : PipeSize.TINY.getAABB();
//        }
//        return FULL_BLOCK_AABB;
//    }

//    @Override
//    public boolean hasTileEntity(BlockState state) {
//        return true;
//    }
//
//    @Nullable
//    @Override
//    public abstract TileEntity createTileEntity(World world, BlockState state);

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return AntimatterAPI.WRENCH_TOOL_TYPE;
    }

    //    @Override
//    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
//        TileEntity tile = Utils.getTile(world, pos);
//        return tile != null && GregTechAPI.interact(tile, player, hand, side, hitX, hitY, hitZ);
//    }

    //not needed probably
//    @Override
//    public void onBlockAdded(World world, BlockPos pos, BlockState state) {
//        TileEntity tile = Utils.getTile(world, pos);
//        if (tile instanceof TileEntityPipe) {
////            ((TileEntityPipe) tile).refreshConnections();
//        }
//    }

//    @Override
//    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
//        TileEntity tile = Utils.getTile(world, pos);
//        if (tile instanceof TileEntityPipe) {
//            ((TileEntityPipe) tile).refreshConnections();
//        }
//    }

//    @Override
//    public boolean isFullCube(BlockState state) {
//        return false;
//    }
//
//    @Override
//    public boolean isOpaqueCube(BlockState state) {
//        return false;
//    }


    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.create(0.1, 0.1, 0.1, 0.9, 0.9, 0.9);
    }

    @Override
    public int getBlockColor(BlockState state, @Nullable IBlockReader world, @Nullable BlockPos pos, int i) {
        if (!(state.getBlock() instanceof BlockPipe) && world == null || pos == null) return -1;
        return i == 0 || i == 1 || i == 2 ? getRGB() : -1;
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        if (!(block instanceof BlockPipe)) return -1;
        return i == 0 || i == 1 || i == 2 ? ((BlockPipe) block).getRGB() : -1;
    }

//    @Override
//    @SideOnly(Side.CLIENT)
//    public void onModelRegistration() {
//        for (int i = 0; i < sizes.length; i++) {
//            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), sizes[i].ordinal(), new ModelResourceLocation(Ref.MODID + ":" + getId(), "size=" + sizes[i].getName()));
//        }
//        //Redirect block model to custom baked model handling
//        ModelLoader.setCustomStateMapper(this, new StateMapperRedirect(new ResourceLocation(Ref.MODID, "block_pipe")));
//    }


    @Override
    public void onItemModelBuild(IItemProvider item, AntimatterItemModelProvider provider) {

    }

    @Override
    public void onBlockModelBuild(Block block, AntimatterBlockStateProvider provider) {
        provider.simpleBlock(block, provider.getBuilder(block).parent(provider.getExistingFile(provider.modLoc("block/pipe/" + getSize().getId() + "/line"))).texture("0", Textures.PIPE));
    }

    //    @Override
//    @OnlyIn(Dist.CLIENT)
//    public void onModelBuild(Map<ResourceLocation, IBakedModel> registry) {
//        //TODO keep copy of PipeModels and remove BakedTextureDataItem
//        ModelResourceLocation loc = new ModelResourceLocation(Ref.MODID + ":" + getId());
//        IBakedModel baked = new BakedTextureDataItem(BakedPipe.BAKED[size.ordinal()][2], new TextureData().base(Textures.PIPE_DATA[0].getBase()).overlay(Textures.PIPE_DATA[0].getOverlay(size.ordinal())));
//        registry.put(loc, baked);
//    }

    public abstract static class BlockPipeBuilder {

        protected Material material;
        protected PipeSize[] sizes;

        public BlockPipeBuilder(Material material, PipeSize[] sizes) {
            this.material = material;
            this.sizes = sizes;
        }

        public abstract void build();
    }
}
