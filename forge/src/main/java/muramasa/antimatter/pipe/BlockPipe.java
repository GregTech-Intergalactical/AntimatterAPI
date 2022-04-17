package muramasa.antimatter.pipe;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.AntimatterItemBlock;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.cover.IHaveCover;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.dynamic.BlockDynamic;
import muramasa.antimatter.dynamic.ModelConfig;
import muramasa.antimatter.pipe.types.PipeType;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.registration.IItemBlockProvider;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.tile.TileEntityTickable;
import muramasa.antimatter.tile.pipe.TileEntityPipe;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.client.model.generators.ConfiguredModel;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

import static com.google.common.collect.ImmutableMap.of;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public abstract class BlockPipe<T extends PipeType<T>> extends BlockDynamic implements IItemBlockProvider, EntityBlock, IColorHandler, SimpleWaterloggedBlock, ISharedAntimatterObject {

    protected T type;
    protected PipeSize size;

    protected final int modelId;
    protected Texture side;
    protected Texture[] faces;

    public static final BooleanProperty TICKING = BooleanProperty.create("ticking");

    public BlockPipe(String prefix, T type, PipeSize size, int modelId) {
        this(prefix, type, size, modelId, type.getMaterial() == Data.Wood ? Block.Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.0f, 3.0f).noOcclusion() : Block.Properties.of(Data.WRENCH_MATERIAL).strength(1.0f, 3.0f).noOcclusion().requiresCorrectToolForDrops());
    }

    public BlockPipe(String prefix, T type, PipeSize size, int modelId, BlockBehaviour.Properties properties) {
        super(type.domain, prefix + "_" + size.getId(), properties);
        this.type = type;
        this.size = size;
        side = new Texture(type.getMaterial().getSet().getDomain(), type.getMaterial().getSet().getPath() + "/pipe/pipe_side");
        faces = new Texture[]{new Texture(type.getMaterial().getSet().getDomain(), type.getMaterial().getSet().getPath() + "/pipe/pipe_vtiny"), new Texture(type.getMaterial().getSet().getDomain(), type.getMaterial().getSet().getPath() + "/pipe/pipe_tiny"), new Texture(type.getMaterial().getSet().getDomain(), type.getMaterial().getSet().getPath() + "/pipe/pipe_small"), new Texture(type.getMaterial().getSet().getDomain(), type.getMaterial().getSet().getPath() + "/pipe/pipe_normal"), new Texture(type.getMaterial().getSet().getDomain(), type.getMaterial().getSet().getPath() + "/pipe/pipe_large"), new Texture(type.getMaterial().getSet().getDomain(), type.getMaterial().getSet().getPath() + "/pipe/pipe_huge")};
        AntimatterAPI.register(BlockPipe.class, this);
        registerDefaultState(getStateDefinition().any().setValue(WATERLOGGED, false).setValue(TICKING, false));
        this.modelId = modelId;
        buildShapes();
    }

    private void buildShapes() {
        recursiveShapeBuild(0, (byte) 0);
        shapes.put(getPipeID(0, 0), Shapes.create(size.getAABB()));
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        //If we are replacing with the same block, remove tile since we are replacing with a covered/uncovered tile.
        //Also make sure it is the covered data that actually changes.
        if (state.hasBlockEntity() && state.is(newState.getBlock()) && (state.equals(newState.setValue(TICKING, !newState.getValue(TICKING))))) {
            worldIn.removeBlockEntity(pos);
        } else if (!state.is(newState.getBlock())) {
            BlockEntity tile = worldIn.getBlockEntity(pos);
            if (tile == null) return;
            TileEntityPipe<T> pipe = (TileEntityPipe<T>) tile;
            pipe.coverHandler.ifPresent(t -> t.getDrops().forEach(stack -> Containers.dropItemStack(worldIn, pipe.getBlockPos().getX(), pipe.getBlockPos().getY(), pipe.getBlockPos().getZ(), stack)));
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    private void recursiveShapeBuild(int index, byte acc) {
        if (index > 5) {
            shapes.put(getPipeID(acc, 0), makeShapes(acc));
            return;
        }
        recursiveShapeBuild(index + 1, (byte) (acc | (1 << index)));
        recursiveShapeBuild(index + 1, acc);
    }

    private VoxelShape makeShapes(byte which) {
        float offset = 0.0625f * size.ordinal();
        VoxelShape shape = Shapes.create(size.getAABB());
        if ((which & (1 << 0)) > 0)
            shape = Shapes.or(shape, Shapes.box(0.4375 - offset, 0, 0.4375 - offset,0.5625 + offset, 0.4375 - offset, 0.5625 + offset));
        if ((which & (1 << 1)) > 0)
            shape = Shapes.or(shape, Shapes.box(0.4375 - offset, 0.5625 + offset, 0.4375 - offset, 0.5625 + offset, 1, 0.5625 + offset));
        if ((which & (1 << 2)) > 0)
            shape = Shapes.or(shape, Shapes.box(0.4375 - offset, 0.4375 - offset, 0, 0.5625 + offset, 0.5625 + offset, 0.4375 - offset));
        if ((which & (1 << 3)) > 0)
            shape = Shapes.or(shape, Shapes.box(0.4375 - offset, 0.4375 - offset, 0.5625 + offset, 0.5625 + offset, 0.5625 + offset, 1));
        if ((which & (1 << 4)) > 0)
            shape = Shapes.or(shape, Shapes.box(0, 0.4375 - offset, 0.4375 - offset, 0.4375 - offset, 0.5625 + offset, 0.5625 + offset));
        if ((which & (1 << 5)) > 0)
            shape = Shapes.or(shape, Shapes.box(0.5625 + offset, 0.4375 - offset, 0.4375 - offset, 1, 0.5625 + offset, 0.5625 + offset));
        return shape;
    }

    public T getType() {
        return type;
    }

    public PipeSize getSize() {
        return size;
    }

    public int getRGB() {
        return type.getMaterial().getRGB();
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

    public AntimatterToolType getToolType() {
        if (type.getMaterial() == Data.Wood) return Data.AXE;
        return Data.WRENCH;
    }

    @Override // Used to set connection for sides where neighbor has pre-set connection
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        TileEntityPipe<?> tile = getTilePipe(worldIn, pos);
        if (tile != null) {
            for (Direction side : Ref.DIRS) {
                TileEntityPipe<?> neighbour = tile.getPipe(side);
                if (neighbour != null && neighbour.connects(side.getOpposite())) {
                    tile.setConnection(side);
                }
            }
        }
    }

    // Used to set connection between pipes on which block was placed
    public boolean onBlockPlacedTo(Level world, BlockPos pos, Direction face) {
        TileEntityPipe<?> tile = getTilePipe(world, pos);
        if (tile != null) {
            if (!world.getBlockState(pos.relative(face.getOpposite())).hasBlockEntity()) return false;
            tile.setConnection(face.getOpposite());
            return true;
        }
        return false;
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        if (!worldIn.isClientSide) {
            TileEntityPipe<?> tile = (TileEntityPipe<?>) worldIn.getBlockEntity(pos);
            if (tile != null) {
                tile.onBlockUpdate(fromPos);
            }
        }
    }

    @Override // Used to clear connection for sides where neighbor was removed
    public void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor) {
        if (!world.isClientSide()) {
            TileEntityPipe<?> tile = (TileEntityPipe<?>) world.getBlockEntity(pos);
            if (tile != null) {
                tile.onBlockUpdate(neighbor);
            }
        }    
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        onNeighborChange(stateIn, worldIn, currentPos, facingPos);
        return stateIn;
    }

    @Nonnull
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        TileEntityPipe<T> tile = (TileEntityPipe) world.getBlockEntity(pos);
        if (tile == null) {
            return InteractionResult.PASS;
        }
        if (!world.isClientSide && hand == InteractionHand.MAIN_HAND) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() instanceof IHaveCover) {
                CoverFactory factory = ((IHaveCover) stack.getItem()).getCover();
                Direction dir = Utils.getInteractSide(hit);
                boolean ok = tile.getCapability(AntimatterPlatformUtils.getCoverCap(), Utils.getInteractSide(hit)).map(i -> i.placeCover(player, Utils.getInteractSide(hit), stack, factory.get().get(i, ((IHaveCover) stack.getItem()).getTier(), dir, factory))).orElse(false);
                if (ok) {
                    return InteractionResult.SUCCESS;
                }
            }
            AntimatterToolType type = Utils.getToolType(player);
            if (type == null) {
                return InteractionResult.PASS;
            }
            if (type == Data.CROWBAR) {
                if (!player.isCrouching()) {
                    if (tile.getCapability(AntimatterPlatformUtils.getCoverCap(), hit.getDirection()).map(h -> h.removeCover(player, Utils.getInteractSide(hit), false)).orElse(false)) {
                        Utils.damageStack(stack, hand, player);
                        return InteractionResult.SUCCESS;
                    }
                    return InteractionResult.PASS;
                } else {
                    if (tile.getCapability(AntimatterPlatformUtils.getCoverCap(), hit.getDirection()).map(h -> h.moveCover(player, hit.getDirection(), Utils.getInteractSide(hit))).orElse(false)) {
                        Utils.damageStack(stack, player);
                        return InteractionResult.SUCCESS;
                    }
                    return InteractionResult.PASS;
                }
            } else if (type == Data.SCREWDRIVER || type == Data.ELECTRIC_SCREWDRIVER) {
                if (player.isCrouching()) {
                    AntimatterPlatformUtils.openGui((ServerPlayer) player, tile, extra -> extra.writeBlockPos(pos));
                    Utils.damageStack(stack, hand, player);
                    return InteractionResult.SUCCESS;
                }
                ICover instance = tile.getCapability(AntimatterPlatformUtils.getCoverCap(), hit.getDirection()).map(h -> h.get(Utils.getInteractSide(hit))).orElse(ICover.empty);
                if (!player.isCrouching()) {
                    if (!instance.isEmpty() && instance.openGui(player, Utils.getInteractSide(hit))) {
                        Utils.damageStack(stack, hand, player);
                        return InteractionResult.SUCCESS;
                    }
                    return InteractionResult.PASS;
                }
            }
            if (getToolType().equals(type)) {
                Direction side = Utils.getInteractSide(hit);
                if (tile.blocksSide(side)) return InteractionResult.CONSUME;
                tile.toggleConnection(side);
                Utils.damageStack(stack, hand, player);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext cont) {
            Player player = (Player) cont.getEntity();
            if (player != null && Utils.isPlayerHolding(player, InteractionHand.MAIN_HAND, getToolType(), Data.CROWBAR, Data.SCREWDRIVER)) {
                return Shapes.block();
            }
            if (player != null && !player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem() instanceof IHaveCover) {
                return Shapes.block();
            }
            BlockPipe<?> pipe = null;
            if (player != null && player.getMainHandItem().getItem() instanceof PipeItemBlock) {
                pipe = ((PipeItemBlock) player.getMainHandItem().getItem()).getPipe();
            }
            if (player != null && player.getOffhandItem().getItem() instanceof PipeItemBlock) {
                pipe = ((PipeItemBlock) player.getOffhandItem().getItem()).getPipe();
            }
            if (getClass().isInstance(pipe)) {
                return Shapes.block();
            }
        }
        int config = getConfig(state, world, new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ()), pos).getConfig()[0];
        VoxelShape shape = this.shapes.get(config);
        return shape != null ? shape : Shapes.block();
    }

    public int getPipeID(int config, int cull) {
        return ((size.ordinal() + 1) * 100) + ((getModelId() + 1) * 1000) + (cull == 0 ? 0 : 10000) + config;
    }

    @Nullable
    private static TileEntityPipe<?> getTilePipe(BlockGetter world, BlockPos pos) {
        BlockEntity tile = world.getBlockEntity(pos);
        return tile instanceof TileEntityPipe ? (TileEntityPipe<?>) tile : null;
    }

    @Override
    public ModelConfig getConfig(BlockState state, BlockGetter world, BlockPos.MutableBlockPos mut, BlockPos pos) {
        int ct = 0;
        TileEntityPipe tile = getTilePipe(world, pos);
        if (tile != null) {
            for (int s = 0; s < 6; s++) {
                if (tile.canConnect(s)) {
                    ct += 1 << s;
                }
            }
        }
        return config.set(pos, new int[]{getPipeID(ct, 0)});
    }

    @Override
    public int getBlockColor(BlockState state, @Nullable BlockGetter world, @Nullable BlockPos pos, int i) {
        return getRGB();
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        return getRGB();
    }

    @Override
    public void onItemModelBuild(ItemLike item, AntimatterItemModelProvider prov) {
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
        builder.particle(getFace());

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
    public List<String> getInfo(List<String> info, Level world, BlockState state, BlockPos pos) {
        super.getInfo(info, world, state, pos);
        info.add("Pipe Type: " + getType().getId());
        info.add("Pipe Material: " + getType().getMaterial().getId());
        info.add("Pipe Size: " + getSize().getId());
        return info;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
        builder.add(TICKING);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluidstate = context.getLevel().getFluidState(context.getClickedPos());
        return super.defaultBlockState().setValue(WATERLOGGED, fluidstate.getType() == Fluids.WATER);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return type.getTileType().create(pos, state);
    }

    @Nullable
    @Override
    public <TILE extends BlockEntity> BlockEntityTicker<TILE> getTicker(Level level, BlockState state, BlockEntityType<TILE> type) {
        if (state.getValue(TICKING) && !level.isClientSide()) {
            return TileEntityTickable::commonTick;
        }
        return null;
    }
}
