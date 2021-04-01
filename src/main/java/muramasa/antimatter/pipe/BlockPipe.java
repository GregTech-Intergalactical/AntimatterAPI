package muramasa.antimatter.pipe;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.AntimatterItemBlock;
import muramasa.antimatter.block.IInfoProvider;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.dynamic.BlockDynamic;
import muramasa.antimatter.dynamic.ModelConfig;
import muramasa.antimatter.pipe.types.PipeType;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.registration.IItemBlockProvider;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.pipe.TileEntityCable;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;
import static muramasa.antimatter.Data.WIRE_CUTTER;
import static muramasa.antimatter.Data.WRENCH;
import static net.minecraft.state.properties.BlockStateProperties.WATERLOGGED;

public abstract class BlockPipe<T extends PipeType<?>> extends BlockDynamic implements IItemBlockProvider, IColorHandler, IInfoProvider, IWaterLoggable {

    protected T type;
    protected PipeSize size;

    protected int modelId = 0;
    protected Texture side = new Texture(Ref.ID, "block/pipe/pipe_side");
    protected Texture[] faces = new Texture[]{new Texture(Ref.ID, "block/pipe/pipe_vtiny"), new Texture(Ref.ID, "block/pipe/pipe_tiny"), new Texture(Ref.ID, "block/pipe/pipe_small"), new Texture(Ref.ID, "block/pipe/pipe_normal"), new Texture(Ref.ID, "block/pipe/pipe_large"), new Texture(Ref.ID, "block/pipe/pipe_huge")};
    protected final VoxelShape COLLISION_SHAPE_CENTER;
    protected final VoxelShape COLLISION_SHAPE_TOP;
    protected final VoxelShape COLLISION_SHAPE_BOTTOM;
    protected final VoxelShape COLLISION_SHAPE_NORTH;
    protected final VoxelShape COLLISION_SHAPE_SOUTH;
    protected final VoxelShape COLLISION_SHAPE_WEST;
    protected final VoxelShape COLLISION_SHAPE_EAST;

    public BlockPipe(String prefix, T type, PipeSize size) {
        this(prefix, type, size, Block.Properties.create(net.minecraft.block.material.Material.IRON).hardnessAndResistance(1.0f, 3.0f).notSolid());
    }

    public BlockPipe(String prefix, T type, PipeSize size, AbstractBlock.Properties properties) {
        super(type.getDomain(), prefix + "_" + type.getMaterial().getId() + "_" + size.getId(), properties);
        this.type = type;
        this.size = size;
        AntimatterAPI.register(BlockPipe.class, getId(), this);
        setDefaultState(getStateContainer().getBaseState().with(WATERLOGGED, false));
        this.COLLISION_SHAPE_CENTER = VoxelShapes.create(size.getAABB());
        VoxelShape[] voxels = makeShapes();
        COLLISION_SHAPE_TOP = voxels[0];
        COLLISION_SHAPE_BOTTOM = voxels[1];
        COLLISION_SHAPE_NORTH = voxels[2];
        COLLISION_SHAPE_SOUTH = voxels[3];
        COLLISION_SHAPE_WEST = voxels[4];
        COLLISION_SHAPE_EAST = voxels[5];
    }

    private VoxelShape[] makeShapes(){
        float offset = 0.0625f * size.ordinal();
        AxisAlignedBB top = new AxisAlignedBB(0.4375 - offset, 0.5625 + offset, 0.4375 - offset, 0.5625 + offset, 1, 0.5625 + offset);
        AxisAlignedBB bottom = new AxisAlignedBB(0.4375 - offset, 0.4375 - offset, 0.4375 - offset, 0.5625 + offset, 0, 0.5625 + offset);
        AxisAlignedBB north = new AxisAlignedBB(0.4375 - offset, 0.4375 - offset, 0.4375 - offset, 0.5625 + offset, 0.5625 + offset, 0);
        AxisAlignedBB south = new AxisAlignedBB(0.4375 - offset, 0.4375 - offset, 0.5625 + offset, 0.5625 + offset, 0.5625 + offset, 1);
        AxisAlignedBB west = new AxisAlignedBB(0.4375 - offset, 0.4375 - offset, 0.4375 - offset, 0, 0.5625 + offset, 0.5625 + offset);
        AxisAlignedBB east = new AxisAlignedBB(0.5625 + offset, 0.4375 - offset, 0.4375 - offset, 1, 0.5625 + offset, 0.5625 + offset);
        return new VoxelShape[]{VoxelShapes.create(top), VoxelShapes.create(bottom), VoxelShapes.create(north), VoxelShapes.create(south), VoxelShapes.create(west), VoxelShapes.create(east)};
    }

    public T getType() {
        return (T) type;
    }

    public PipeSize getSize() {
        return size;
    }

    public int getRGB() {
        return getType().getMaterial().getRGB();
    }

    public int getModelId() {
        return modelId;
    }

    public Texture getSide() {
        return side;
    }

    public Texture getFace() {
        return faces[size.ordinal()];
    }

    @Override
    public AntimatterItemBlock getItemBlock() {
        return new PipeItemBlock(this);
    }


//    @Override
//    public BlockState getExtendedState(BlockState state, IBlockReader world, BlockPos pos) {
//        IExtendedBlockState exState = (IExtendedBlockState) state;
//        TileEntityPipe tile = getTilePipe(world, pos);
//        if (tile != null) {
//            exState = exState.withProperty(PIPE_CONNECTIONS, tile.getConnections());
//            exState = exState.withProperty(TEXTURE, getData());
//            if (tile.coverHandler.isPresent()) {
//                exState = exState.withProperty(COVER, tile.coverHandler.get().getAll());
//            }
//        }
//        return exState;
//    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return getType().getTileType().create();
    }

    @Override
    public ToolType getHarvestTool(BlockState state) {
        return Data.WRENCH.getToolType();
    }

    // TODO: Block if covers are exist

    @Override // Used to set connection for sides where neighbor has pre-set connection
    public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        TileEntityPipe tile = getTilePipe(worldIn, pos);
        if (tile != null) {
            for (Direction side : Ref.DIRS) {
                TileEntityPipe neighbor = getTilePipe(worldIn, pos.offset(side));
                if (neighbor != null && tile.validateTile(neighbor, side.getOpposite())) {
                    if (neighbor.canConnect(side.getOpposite().getIndex())) {
                        tile.setConnection(side);
                    }
                }
            }
        }
    }

    // Used to set connection between pipes on which block was placed
    public boolean onBlockPlacedTo(World world, BlockPos pos, Direction face) {
        TileEntityPipe tile = getTilePipe(world, pos);
        if (tile != null) {
            TileEntity neighbor = world.getTileEntity(pos.offset(face.getOpposite()));
            if (neighbor != null) {
                if (tile.validateTile(neighbor, face)) {
                    tile.setConnection(face.getOpposite());
                    if (neighbor instanceof TileEntityPipe) {
                        TileEntityPipe pipe = (TileEntityPipe) neighbor;
                        if (!pipe.canConnect(face.getIndex())) {
                            pipe.setConnection(face);
                        }
                    } else {
                        tile.setInteract(face.getOpposite());
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override // Used to clear connection for sides where neighbor was removed
    public void onNeighborChange(BlockState state, IWorldReader world, BlockPos pos, BlockPos neighbor) {
        TileEntityPipe tile = getTilePipe(world, pos);
        if (tile != null) {
            for (Direction side : Ref.DIRS) {
                // Looking for the side where is a neighbor was
                // Check if the block is actually air or there was another reason for change.
                if (pos.offset(side).equals(neighbor)) {
                    if (isAir(world.getBlockState(neighbor), world, pos)) {
                        tile.clearInteract(side);
                        return;
                    } else {
                        tile.refreshSide(side);
                    }
                }
            }
        }
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        onNeighborChange(stateIn, worldIn, currentPos, facingPos);
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    /*@Override // Used to catch new placed neighbors near pipe which enable connection
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos from, boolean isMoving) {
        TileEntityPipe tile = getTilePipe(world, pos);
        if (tile != null) {
            for (Direction side : Ref.DIRS) {
                // Looking for the side where is a neighbor changed
                if (pos.offset(side).equals(from)) {
                    if (tile.canConnect(side.getIndex())) {
                        tile.changeConnection(side);
                    }
                    return;
                }
            }
        }
    }*/

    private AntimatterToolType getTool(TileEntity tile) {
        return tile instanceof TileEntityCable ? WIRE_CUTTER : WRENCH;
    }

    @Nonnull
    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        TileEntityPipe tile = (TileEntityPipe) world.getTileEntity(pos);
        AntimatterToolType type = Utils.getToolType(player);
        Direction side = Utils.getInteractSide(hit);

        if (!world.isRemote && type == getTool(tile) && hand == Hand.MAIN_HAND) {
            TileEntity target = tile.getWorld().getTileEntity(tile.getPos().offset(side));
            if (target != null) {
                if (tile.validateTile(target, side.getOpposite())) {
                    if (target instanceof TileEntityPipe) {
                        ((TileEntityPipe) target).toggleConnection(side.getOpposite());
                    } else {
                        tile.toggleInteract(side);
                    }
                    tile.toggleConnection(side);
                    return ActionResultType.SUCCESS;
                }
            } else if (world.isAirBlock(pos.offset(side))) {
                tile.toggleConnection(side);
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        TileEntityPipe tile = (TileEntityPipe) world.getTileEntity(pos);
        if (tile == null){
            return super.getShape(state, world, pos, context);
        }
        if (context.getEntity() instanceof PlayerEntity) {
            PlayerEntity entity = (PlayerEntity) context.getEntity();
            if (getTool(tile) == Utils.getToolType(entity)) {
                return super.getShape(state, world, pos, context);
            }
        }
        List<VoxelShape> shapes = new ArrayList<>();
        if (tile.hasConnection(Direction.UP.getIndex())){
            shapes.add(COLLISION_SHAPE_TOP);
        }
        if (tile.hasConnection(Direction.DOWN.getIndex())){
            shapes.add(COLLISION_SHAPE_BOTTOM);
        }
        if (tile.hasConnection(Direction.NORTH.getIndex())){
            shapes.add(COLLISION_SHAPE_NORTH);
        }
        if (tile.hasConnection(Direction.SOUTH.getIndex())){
            shapes.add(COLLISION_SHAPE_SOUTH);
        }
        if (tile.hasConnection(Direction.WEST.getIndex())){
            shapes.add(COLLISION_SHAPE_WEST);
        }
        if (tile.hasConnection(Direction.EAST.getIndex())){
            shapes.add(COLLISION_SHAPE_EAST);
        }
        VoxelShape[] voxelShapes = shapes.toArray(new VoxelShape[0]);
        if (voxelShapes.length > 0){
            return VoxelShapes.or(COLLISION_SHAPE_CENTER, voxelShapes);
        }
        return this.COLLISION_SHAPE_CENTER;
    }

    @Override
    public VoxelShape getRayTraceShape(BlockState state, IBlockReader reader, BlockPos pos, ISelectionContext context) {
        return super.getRayTraceShape(state, reader, pos, context);
    }

    public int getPipeID(int config, int cull) {
        return ((size.ordinal() + 1) * 100) + ((getModelId() + 1) * 1000) + (cull == 0 ? 0 : 10000) + config;
    }

    @Nullable
    private static TileEntityPipe getTilePipe(IBlockReader world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        return tile instanceof TileEntityPipe ? (TileEntityPipe) tile : null;
    }

    @Override
    public ModelConfig getConfig(BlockState state, IBlockReader world, BlockPos.Mutable mut, BlockPos pos) {
        int ct = 0;
        TileEntityPipe tile = getTilePipe(world, pos);
        if (tile != null) {
            for (int s = 0; s < 6; s++) {
                if (tile.canConnect(s)) {
                    ct += 1 << s;
                }
            }
        }
        return config.set(new int[]{getPipeID(ct, 0)});
    }

    @Override
    public int getBlockColor(BlockState state, @Nullable IBlockReader world, @Nullable BlockPos pos, int i) {
        return state.getBlock() instanceof BlockPipe ? getRGB() : -1;
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return stack.getItem() instanceof PipeItemBlock ? getRGB() : -1;
    }

    @Override
    public void onItemModelBuild(IItemProvider item, AntimatterItemModelProvider prov) {
        prov.getBuilder(item).parent(prov.existing("antimatter", "block/pipe/" + getSize().getId() + "/line_inv")).texture("all", getSide()).texture("overlay", getFace());
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
        builder.model(getModelLoc("base", 0), of("all", getSide(), "overlay", getFace()));
        builder.staticConfigId("pipe");

        //Default Shape (0 Connections)
        builder.config(getPipeID(0, 0), getModelLoc("base", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())));

        //Single Shapes (1 Connections)
        builder.config(getPipeID(1, 0), getModelLoc("single", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(-90, 0, 0));
        builder.config(getPipeID(2, 0), getModelLoc("single", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(90, 0, 0));
        builder.config(getPipeID(4, 0), getModelLoc("single", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())));
        builder.config(getPipeID(8, 0), getModelLoc("single", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 180, 0));
        builder.config(getPipeID(16, 0), getModelLoc("single", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 90, 0));
        builder.config(getPipeID(32, 0), getModelLoc("single", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, -90, 0));

        //Line Shapes (2 Connections)
        builder.config(getPipeID(3, 0), getModelLoc("line", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(90, 0, 0));
        builder.config(getPipeID(12, 0), getModelLoc("line", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())));
        builder.config(getPipeID(48, 0), getModelLoc("line", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 90, 0));

        //Elbow Shapes (2 Connections)
        builder.config(getPipeID(5, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 0, -90));
        builder.config(getPipeID(6, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 0, 90));
        builder.config(getPipeID(9, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 180, -90));
        builder.config(getPipeID(10, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 180, 90));
        builder.config(getPipeID(17, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(90, 180, 0));
        builder.config(getPipeID(18, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(-90, 180, 0));
        builder.config(getPipeID(20, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 90, 0));
        builder.config(getPipeID(24, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, -180, 0));
        builder.config(getPipeID(33, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(-90, 0, 0));
        builder.config(getPipeID(34, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(90, 0, 0));
        builder.config(getPipeID(36, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())));
        builder.config(getPipeID(40, 0), getModelLoc("elbow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, -90, 0));

        //Side Shapes (3 Connections)
        builder.config(getPipeID(7, 0), getModelLoc("side", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(-90, 0, 0));
        builder.config(getPipeID(11, 0), getModelLoc("side", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(90, 0, 0));
        builder.config(getPipeID(13, 0), getModelLoc("side", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 0, 180));
        builder.config(getPipeID(14, 0), getModelLoc("side", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())));
        builder.config(getPipeID(19, 0), getModelLoc("side", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(90, 0, 90));
        builder.config(getPipeID(28, 0), getModelLoc("side", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 0, 90));
        builder.config(getPipeID(35, 0), getModelLoc("side", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(90, 0, -90));
        builder.config(getPipeID(44, 0), getModelLoc("side", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 0, -90));
        builder.config(getPipeID(49, 0), getModelLoc("side", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 90, 180));
        builder.config(getPipeID(50, 0), getModelLoc("side", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 90, 0));
        builder.config(getPipeID(52, 0), getModelLoc("side", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 90, -90));
        builder.config(getPipeID(56, 0), getModelLoc("side", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 90, 90));

        //Corner Shapes (3 Connections)
        builder.config(getPipeID(21, 0), getModelLoc("corner", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 0, 180));
        builder.config(getPipeID(22, 0), getModelLoc("corner", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 90, 0));
        builder.config(getPipeID(25, 0), getModelLoc("corner", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, -270, 180));
        builder.config(getPipeID(26, 0), getModelLoc("corner", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 180, 0));
        builder.config(getPipeID(41, 0), getModelLoc("corner", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, -180, 180));
        builder.config(getPipeID(42, 0), getModelLoc("corner", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, -90, 0));
        builder.config(getPipeID(37, 0), getModelLoc("corner", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, -90, 180));
        builder.config(getPipeID(38, 0), getModelLoc("corner", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())));

        //Arrow Shapes (4 Connections)
        builder.config(getPipeID(23, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 0, 90));
        builder.config(getPipeID(27, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, -270, 90));
        builder.config(getPipeID(29, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 90, 180));
        builder.config(getPipeID(30, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 90, 0));
        builder.config(getPipeID(39, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, -90, 90));
        builder.config(getPipeID(43, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, -180, 90));
        builder.config(getPipeID(45, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, -90, 180));
        builder.config(getPipeID(46, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, -90, 0));
        builder.config(getPipeID(53, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(180, 180, 0));
        builder.config(getPipeID(54, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())));
        builder.config(getPipeID(57, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(180, 0, 0));
        builder.config(getPipeID(58, 0), getModelLoc("arrow", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 180, 0));

        //Cross Shapes (4 Connections)
        builder.config(getPipeID(15, 0), getModelLoc("cross", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 0, 90));
        builder.config(getPipeID(51, 0), getModelLoc("cross", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(90, 0, 0));
        builder.config(getPipeID(60, 0), getModelLoc("cross", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())));

        //Five Shapes (5 Connections)
        builder.config(getPipeID(31, 0), getModelLoc("five", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 0, 90));
        builder.config(getPipeID(47, 0), getModelLoc("five", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(0, 0, -90));
        builder.config(getPipeID(55, 0), getModelLoc("five", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(-90, 0, 0));
        builder.config(getPipeID(59, 0), getModelLoc("five", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(90, 0, 0));
        builder.config(getPipeID(61, 0), getModelLoc("five", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())).rot(180, 0, 0));
        builder.config(getPipeID(62, 0), getModelLoc("five", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())));

        //All Shapes (6 Connections)
        builder.config(getPipeID(63, 0), getModelLoc("all", 0), c -> c.tex(of("all", getSide(), "overlay", getFace())));

        return builder.loader(AntimatterModelManager.LOADER_PIPE);
    }

    @Override
    public List<String> getInfo(List<String> info, World world, BlockState state, BlockPos pos) {
        super.getInfo(info, world, state, pos);
        info.add("Pipe Type: " + getType().getId());
        info.add("Pipe Material: " + getType().getMaterial().getId());
        info.add("Pipe Size: " + getSize().getId());
        return info;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    public BlockState getStateForPlacement(BlockItemUseContext context) {
        FluidState fluidstate = context.getWorld().getFluidState(context.getPos());
        BlockState state = super.getStateForPlacement(context);
        return fluidstate.isEmpty() ? state : state.with(WATERLOGGED, true);
    }
}
