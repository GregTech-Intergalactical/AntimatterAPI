//package muramasa.gtu.api.blocks.pipe;
//
//import muramasa.gtu.Ref;
//import muramasa.gtu.api.GregTechAPI;
//import muramasa.gtu.api.blocks.BlockDynamic;
//import muramasa.gtu.api.data.Textures;
//import muramasa.gtu.api.materials.Material;
//import muramasa.gtu.api.pipe.PipeSize;
//import muramasa.gtu.api.registration.*;
//import muramasa.gtu.api.texture.TextureData;
//import muramasa.gtu.api.util.Utils;
//import muramasa.gtu.client.render.bakedmodels.BakedPipe;
//import net.minecraft.block.Block;
//import net.minecraft.block.BlockState;
//import net.minecraft.client.renderer.model.IBakedModel;
//import net.minecraft.client.renderer.model.ModelResourceLocation;
//import net.minecraft.entity.player.PlayerEntity;
//import net.minecraft.item.ItemStack;
//import net.minecraft.tileentity.TileEntity;
//import net.minecraft.util.Direction;
//import net.minecraft.util.Hand;
//import net.minecraft.util.ResourceLocation;
//import net.minecraft.util.math.AxisAlignedBB;
//import net.minecraft.util.math.BlockPos;
//import net.minecraft.world.IBlockReader;
//import net.minecraft.world.World;
//import net.minecraftforge.api.distmarker.Dist;
//import net.minecraftforge.api.distmarker.OnlyIn;
//
//import javax.annotation.Nullable;
//import java.util.Map;
//
//import static muramasa.gtu.api.GregTechProperties.*;
//
//public abstract class BlockPipe extends BlockDynamic implements IGregTechObject, IItemBlock, IModelOverride, IStateOverride, IColorHandler {
//
//    protected String type, id;
//    protected Material material;
//    protected PipeSize size;
//
//    //TODO merge functionality with BlockDynamic
//    public BlockPipe(String type, Material material, PipeSize size, TextureData data) {
//        super(net.minecraft.block.material.Material.IRON, data);
//        this.type = type;
//        this.id = material.getId();
//        this.material = material;
//        this.size = size;
//
//        setRegistryName(getId());
//        GregTechAPI.register(BlockPipe.class, this);
//    }
//
//    @Override
//    public String getId() {
//        return type + "_" + id;
//    }
//
//    public int getRGB() {
//        return material.getRGB();
//    }
//
//    public PipeSize getSize() {
//        return size;
//    }
//
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
//
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
//
//    @Override
//    public boolean hasTileEntity(BlockState state) {
//        return true;
//    }
//
//    @Nullable
//    @Override
//    public abstract TileEntity createTileEntity(World world, BlockState state);
//
//    @Nullable
//    @Override
//    public String getHarvestTool(BlockState state) {
//        return "wrench";
//    }
//
//    @Override
//    public boolean onBlockActivated(World world, BlockPos pos, BlockState state, PlayerEntity player, Hand hand, Direction side, float hitX, float hitY, float hitZ) {
//        TileEntity tile = Utils.getTile(world, pos);
//        return tile != null && GregTechAPI.interact(tile, player, hand, side, hitX, hitY, hitZ);
//    }
//
//    @Override
//    public void onBlockAdded(World world, BlockPos pos, BlockState state) {
////        TileEntity tile = Utils.getTile(world, pos);
////        if (tile instanceof TileEntityPipe) {
//////            ((TileEntityPipe) tile).refreshConnections();
////        }
//    }
//
//    @Override
//    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos) {
//        TileEntity tile = Utils.getTile(world, pos);
//        if (tile instanceof TileEntityPipe) {
//            ((TileEntityPipe) tile).refreshConnections();
//        }
//    }
//
//    @Override
//    public boolean isFullCube(BlockState state) {
//        return false;
//    }
//
//    @Override
//    public boolean isOpaqueCube(BlockState state) {
//        return false;
//    }
//
//    @Override
//    public int getBlockColor(BlockState state, @Nullable IBlockReader world, @Nullable BlockPos pos, int i) {
//        if (!(state.getBlock() instanceof BlockPipe) && world == null || pos == null) return -1;
//        return i == 0 || i == 1 || i == 2 ? getRGB() : -1;
//    }
//
//    @Override
//    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
//        if (!(block instanceof BlockPipe)) return -1;
//        return i == 0 || i == 1 || i == 2 ? ((BlockPipe) block).getRGB() : -1;
//    }
//
////    @Override
////    @SideOnly(Side.CLIENT)
////    public void onModelRegistration() {
////        for (int i = 0; i < sizes.length; i++) {
////            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), sizes[i].ordinal(), new ModelResourceLocation(Ref.MODID + ":" + getId(), "size=" + sizes[i].getName()));
////        }
////        //Redirect block model to custom baked model handling
////        ModelLoader.setCustomStateMapper(this, new StateMapperRedirect(new ResourceLocation(Ref.MODID, "block_pipe")));
////    }
//
//
//    @Override
//    @OnlyIn(Dist.CLIENT)
//    public void onModelBuild(Map<ResourceLocation, IBakedModel> registry) {
//        //TODO keep copy of PipeModels and remove BakedTextureDataItem
//        ModelResourceLocation loc = new ModelResourceLocation(Ref.MODID + ":" + getId());
//        IBakedModel baked = new BakedTextureDataItem(BakedPipe.BAKED[size.ordinal()][2], new TextureData().base(Textures.PIPE_DATA[0].getBase()).overlay(Textures.PIPE_DATA[0].getOverlay(size.ordinal())));
//        registry.put(loc, baked);
//    }
//}
