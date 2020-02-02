package muramasa.antimatter.blocks.pipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.blocks.BlockDynamic;
import muramasa.antimatter.blocks.IInfoProvider;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.materials.Material;
import muramasa.antimatter.pipe.PipeShape;
import muramasa.antimatter.pipe.PipeSize;
import muramasa.antimatter.pipe.PipeType;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.registration.IItemBlockProvider;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;
import java.util.List;

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

    public void register(Class c) {
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

    public static int getPipeID(int config, PipeSize size, PipeType type, int cull) {
        return ((size.ordinal() + 1) * 100) + ((type.getModelId() + 1) * 1000) + (cull == 0 ? 0 : 10000) + config;
    }

    @Override
    public int[] getConfig(BlockState state, IBlockReader world, BlockPos.Mutable mut, BlockPos pos) {
        int ct = 0;
        int cull = 0;
        BlockState adjState;
        for (int s = 0; s < 6; s++) {
            adjState = world.getBlockState(mut.setPos(pos.offset(Ref.DIRECTIONS[s])));
            if (canConnect(world, adjState, mut)) {
                ct += 1 << s;
                if (((BlockPipe) adjState.getBlock()).getSize().ordinal() < getSize().ordinal()) cull += 1;
            }
        }
        return new int[]{getPipeID(ct, getSize(), getType(), cull > 0 ? 0 : 1)};
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
    public void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        JsonArray models = new JsonArray();
        for (PipeShape shape : PipeShape.VALUES) {
            JsonObject o = new JsonObject();
            o.addProperty("parent", Ref.ID + ":block/pipe/" + getSize().getId() + "/" + shape.getId());
            models.add(o);
        }

        prov.simpleBlock(this, prov.getBuilder(this)
            //.loader(AntimatterModelLoader.INSTANCE)
            .property("models", models)
            //.property("models", "parent", Ref.ID + ":block/pipe/" + getSize().getId() + "/line_inv")
            //.texture("0", getType().getSide()).texture("1", getType().getFace(getSize()))
        );
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
