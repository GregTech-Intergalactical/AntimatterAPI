package muramasa.antimatter.pipe;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockDynamic;
import muramasa.antimatter.block.IInfoProvider;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.client.ModelConfig;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.registration.IItemBlockProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;

public abstract class BlockPipe extends BlockDynamic implements IItemBlockProvider, IColorHandler, IInfoProvider {

    protected PipeType type;
    protected Material material;
    protected PipeSize size;

    public BlockPipe(String domain, PipeType type, Material material, PipeSize size) {
        super(domain, type.getId() + "_" + material.getId() + "_" + size.getId(), Block.Properties.create(net.minecraft.block.material.Material.IRON));
        this.type = type;
        this.material = material;
        this.size = size;
    }

    public void register(Class<?> c) {
        AntimatterAPI.register(BlockPipe.class, this);
        AntimatterAPI.register(c, this);
    }

    public PipeType getType() {
        return type;
    }

    public Material getMaterial() {
        return material;
    }

    public PipeSize getSize() {
        return size;
    }

    public int getRGB() {
        return getMaterial().getRGB();
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

    public int getPipeID(int config, int cull) {
        return ((size.ordinal() + 1) * 100) + ((type.getModelId() + 1) * 1000) + (cull == 0 ? 0 : 10000) + config;
    }

    @Override
    public ModelConfig getConfig(BlockState state, IBlockReader world, BlockPos.Mutable mut, BlockPos pos) {
        int ct = 0;
        int cull = 0;
        BlockState adjState;
        for (int s = 0; s < 6; s++) {
            adjState = world.getBlockState(mut.setPos(pos.offset(Ref.DIRECTIONS[s])));
            if (canConnect(world, adjState, mut)) {
                ct += 1 << s;
                //if (((BlockPipe) adjState.getBlock()).getSize().ordinal() < getSize().ordinal()) cull += 1;
            }
        }
        return config.set(new int[]{getPipeID(ct, /*cull > 0 ? 0 : 1*/0)});
    }

    @Override
    public boolean canConnect(IBlockReader world, BlockState state, BlockPos pos) {
        return state.getBlock() instanceof BlockPipe;
    }

    @Override
    public int getBlockColor(BlockState state, @Nullable IBlockReader world, @Nullable BlockPos pos, int i) {
        return state.getBlock() instanceof BlockPipe ? getRGB() : -1;
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return block instanceof BlockPipe ? ((BlockPipe) block).getRGB() : -1;
    }

    @Override
    public void onItemModelBuild(IItemProvider item, AntimatterItemModelProvider prov) {
        prov.getBuilder(item).parent(prov.existing("antimatter", "block/pipe/" + getSize().getId() + "/line_inv")).texture("all", getType().getSide()).texture("overlay", getType().getFace(getSize()));
    }

    @Override
    public void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        prov.getVariantBuilder(block).forAllStates(s -> ConfiguredModel.builder()
            .modelFile(getPipeConfig(prov.getBuilder(block)))
            .uvLock(true)
            .build()
        );
    }

    public String getModelLoc(String shape, int cull) {
        return "antimatter:block/pipe/" + getSize().getId() + "/" + shape + (cull == 0 ? "" : (shape.equals("base") ? "" : "_culled"));
    }

    public AntimatterBlockModelBuilder getPipeConfig(AntimatterBlockModelBuilder builder) {
        builder.model(getModelLoc("base", 0), of("all", getType().getSide(), "overlay", getType().getFace(getSize())));
        builder.staticConfigId("pipe");

        //Default Shape (0 Connections)
        builder.config(getPipeID(0, 0), getModelLoc("base", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))));

        //Single Shapes (1 Connections)
        builder.config(getPipeID(1, 0), getModelLoc("single", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(-90, 0, 0));
        builder.config(getPipeID(2, 0), getModelLoc("single", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(90, 0, 0));
        builder.config(getPipeID(4, 0), getModelLoc("single", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))));
        builder.config(getPipeID(8, 0), getModelLoc("single", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, 180, 0));
        builder.config(getPipeID(16, 0), getModelLoc("single", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, 90, 0));
        builder.config(getPipeID(32, 0), getModelLoc("single", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, -90, 0));

        //Line Shapes (2 Connections)
        builder.config(getPipeID(3, 0), getModelLoc("line", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(90, 0, 0));
        builder.config(getPipeID(12, 0), getModelLoc("line", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))));
        builder.config(getPipeID(48, 0), getModelLoc("line", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, 90, 0));

        //Elbow Shapes (2 Connections)
        builder.config(getPipeID(5, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, 0, -90));
        builder.config(getPipeID(6, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, 0, 90));
        builder.config(getPipeID(9, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, 180, -90));
        builder.config(getPipeID(10, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, 180, 90));
        builder.config(getPipeID(17, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(90, 180, 0));
        builder.config(getPipeID(18, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(-90, 180, 0));
        builder.config(getPipeID(20, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, 90, 0));
        builder.config(getPipeID(24, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, -180, 0));
        builder.config(getPipeID(33, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(-90, 0, 0));
        builder.config(getPipeID(34, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(90, 0, 0));
        builder.config(getPipeID(36, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))));
        builder.config(getPipeID(40, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, -90, 0));

        //Side Shapes (3 Connections)
        builder.config(getPipeID(7, 0), getModelLoc("side", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(-90, 0, 0));
        builder.config(getPipeID(11, 0), getModelLoc("side", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(90, 0, 0));
        builder.config(getPipeID(13, 0), getModelLoc("side", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, 0, 180));
        builder.config(getPipeID(14, 0), getModelLoc("side", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))));
        builder.config(getPipeID(19, 0), getModelLoc("side", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(90, 0, 90));
        builder.config(getPipeID(28, 0), getModelLoc("side", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, 0, 90));
        builder.config(getPipeID(35, 0), getModelLoc("side", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(90, 0, -90));
        builder.config(getPipeID(44, 0), getModelLoc("side", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, 0, -90));
        builder.config(getPipeID(49, 0), getModelLoc("side", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, 90, 180));
        builder.config(getPipeID(50, 0), getModelLoc("side", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, 90, 0));
        builder.config(getPipeID(52, 0), getModelLoc("side", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, 90, -90));
        builder.config(getPipeID(56, 0), getModelLoc("side", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, 90, 90));

        //Corner Shapes (3 Connections)
        builder.config(getPipeID(21, 0), getModelLoc("corner", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, 0, 180));
        builder.config(getPipeID(22, 0), getModelLoc("corner", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, 90, 0));
        builder.config(getPipeID(25, 0), getModelLoc("corner", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, -270, 180));
        builder.config(getPipeID(26, 0), getModelLoc("corner", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, 180, 0));
        builder.config(getPipeID(41, 0), getModelLoc("corner", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, -180, 180));
        builder.config(getPipeID(42, 0), getModelLoc("corner", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, -90, 0));
        builder.config(getPipeID(37, 0), getModelLoc("corner", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, -90, 180));
        builder.config(getPipeID(38, 0), getModelLoc("corner", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))));

        //Arrow Shapes (4 Connections)
        builder.config(getPipeID(23, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, 0, 90));
        builder.config(getPipeID(27, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, -270, 90));
        builder.config(getPipeID(29, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, 90, 180));
        builder.config(getPipeID(30, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, 90, 0));
        builder.config(getPipeID(39, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, -90, 90));
        builder.config(getPipeID(43, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, -180, 90));
        builder.config(getPipeID(45, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, -90, 180));
        builder.config(getPipeID(46, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, -90, 0));
        builder.config(getPipeID(53, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(180, 180, 0));
        builder.config(getPipeID(54, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))));
        builder.config(getPipeID(57, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(180, 0, 0));
        builder.config(getPipeID(58, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, 180, 0));

        //Cross Shapes (4 Connections)
        builder.config(getPipeID(15, 0), getModelLoc("cross", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, 0, 90));
        builder.config(getPipeID(51, 0), getModelLoc("cross", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(90, 0, 0));
        builder.config(getPipeID(60, 0), getModelLoc("cross", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))));

        //Five Shapes (5 Connections)
        builder.config(getPipeID(31, 0), getModelLoc("five", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, 0, 90));
        builder.config(getPipeID(47, 0), getModelLoc("five", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(0, 0, -90));
        builder.config(getPipeID(55, 0), getModelLoc("five", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(-90, 0, 0));
        builder.config(getPipeID(59, 0), getModelLoc("five", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(90, 0, 0));
        builder.config(getPipeID(61, 0), getModelLoc("five", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))).rot(180, 0, 0));
        builder.config(getPipeID(62, 0), getModelLoc("five", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))));

        //All Shapes (6 Connections)
        builder.config(getPipeID(63, 0), getModelLoc("all", 0), c -> c.tex(of("all", getType().getSide(), "overlay", getType().getFace(getSize()))));

        return builder.loader(AntimatterModelManager.LOADER_PIPE);
    }

    @Override
    public List<String> getInfo(List<String> info, World world, BlockState state, BlockPos pos) {
        super.getInfo(info, world, state, pos);
        info.add("Pipe Type: " + getType().getId());
        info.add("Pipe Material: " + getMaterial().getId());
        info.add("Pipe Size: " + getSize().getId());
        return info;
    }

    public abstract static class BlockPipeBuilder {

        protected String domain;
        protected Material material;
        protected PipeSize[] sizes;

        public BlockPipeBuilder(String domain, Material material, PipeSize[] sizes) {
            this.domain = domain;
            this.material = material;
            this.sizes = sizes;
        }

        public abstract void build();
    }
}
