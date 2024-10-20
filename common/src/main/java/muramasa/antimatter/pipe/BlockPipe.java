package muramasa.antimatter.pipe;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import it.unimi.dsi.fastutil.ints.Int2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import lombok.Getter;
import muramasa.antimatter.*;
import muramasa.antimatter.block.AntimatterItemBlock;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.blockentity.pipe.BlockEntityPipe;
import muramasa.antimatter.client.AntimatterModelManager;
import muramasa.antimatter.client.glu.Util;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.cover.IHaveCover;
import muramasa.antimatter.data.AntimatterDefaultTools;
import muramasa.antimatter.data.AntimatterMaterials;
import muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder;
import muramasa.antimatter.datagen.builder.VariantBlockStateBuilder;
import muramasa.antimatter.datagen.providers.AntimatterBlockStateProvider;
import muramasa.antimatter.datagen.providers.AntimatterItemModelProvider;
import muramasa.antimatter.dynamic.BlockDynamic;
import muramasa.antimatter.dynamic.ModelConfig;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.material.IMaterialObject;
import muramasa.antimatter.pipe.types.PipeType;
import muramasa.antimatter.registration.IColorHandler;
import muramasa.antimatter.registration.IItemBlockProvider;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.texture.Texture;
import muramasa.antimatter.blockentity.BlockEntityTickable;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static com.google.common.collect.ImmutableMap.of;
import static muramasa.antimatter.datagen.builder.AntimatterBlockModelBuilder.SIMPLE;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public abstract class BlockPipe<T extends PipeType<T>> extends BlockDynamic implements IItemBlockProvider, EntityBlock, IColorHandler, IMaterialObject, SimpleWaterloggedBlock, ISharedAntimatterObject {

    @Getter
    protected T type;
    @Getter
    protected PipeSize size;

    @Getter
    protected final int modelId;
    @Getter
    protected Texture side;
    protected Texture overlay;
    protected Texture[] faces;

    public static long ticksTotal;

    protected static Map<PipeSize, Cache<Integer, VoxelShape>> shapes = new Object2ObjectLinkedOpenHashMap<>();

    public static final BooleanProperty TICKING = BooleanProperty.create("ticking");

    public BlockPipe(String prefix, T type, PipeSize size, int modelId) {
        this(prefix, type, size, modelId, type.getMaterial() == AntimatterMaterials.Wood ? Properties.of(Material.WOOD).sound(SoundType.WOOD).strength(1.0f, 3.0f) : Properties.of(Data.WRENCH_MATERIAL).strength(1.0f, 3.0f).requiresCorrectToolForDrops());
    }

    public BlockPipe(String prefix, T type, PipeSize size, int modelId, Properties properties) {
        super(type.domain, prefix + "_" + size.getId(), size.ordinal() < 6 ? properties.noOcclusion() : properties);
        shapes.computeIfAbsent(size, s -> CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.MINUTES).build());

        this.type = type;
        this.size = size;
        side = new Texture(type.getMaterial().getSet().getDomain(), type.getMaterial().getSet().getPath() + "/pipe/pipe_side");
        overlay = new Texture(Ref.ID, "block/empty");
        faces = new Texture[]{
                new Texture(type.getMaterial().getSet().getDomain(), type.getMaterial().getSet().getPath() + "/pipe/pipe_vtiny"),
                new Texture(type.getMaterial().getSet().getDomain(), type.getMaterial().getSet().getPath() + "/pipe/pipe_tiny"),
                new Texture(type.getMaterial().getSet().getDomain(), type.getMaterial().getSet().getPath() + "/pipe/pipe_small"),
                new Texture(type.getMaterial().getSet().getDomain(), type.getMaterial().getSet().getPath() + "/pipe/pipe_normal"),
                new Texture(type.getMaterial().getSet().getDomain(), type.getMaterial().getSet().getPath() + "/pipe/pipe_large"),
                new Texture(type.getMaterial().getSet().getDomain(), type.getMaterial().getSet().getPath() + "/pipe/pipe_huge"),
                new Texture(type.getMaterial().getSet().getDomain(), type.getMaterial().getSet().getPath() + "/pipe/pipe_quadruple"),
                new Texture(type.getMaterial().getSet().getDomain(), type.getMaterial().getSet().getPath() + "/pipe/pipe_nonuple")};
        AntimatterAPI.register(BlockPipe.class, this);
        registerDefaultState(getStateDefinition().any().setValue(WATERLOGGED, false).setValue(TICKING, false));
        this.modelId = modelId;
        //long time = System.nanoTime();
        //buildShapes();
        //time = System.nanoTime() - time;
        //ticksTotal += time;
    }


    private void buildShapes() {
        if (size.ordinal() > 5) return;
        //recursiveShapeBuild(0, (short) 0);
        /*if (!getShapes().containsKey(0)) {
            getShapes().get(0, Shapes.create(size.getAABB()));
        }*/
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        if (stack.getTag() != null && stack.getTag().contains("covers")){
            CompoundTag covers = stack.getTag().getCompound("covers");
            if (!Screen.hasShiftDown()) {
                tooltip.add(Utils.translatable("antimatter.tooltip.more"));
            } else {
                tooltip.add(Utils.translatable("antimatter.tooltip.cover.covers_on_item"));
                byte sides = covers.getByte(Ref.TAG_MACHINE_COVER_SIDE);
                for (int i = 0; i < Ref.DIRS.length; i++) {
                    if ((sides & (1 << i)) > 0) {
                        Direction dir = Direction.from3DDataValue(i);
                        String domain = covers.getString(dir.get3DDataValue() + "d");
                        String id = covers.getString(dir.get3DDataValue() + "i");
                        ResourceLocation location = new ResourceLocation(domain, id);
                        if (AntimatterRemapping.getCoverRemappingMap().containsKey(location)) location = AntimatterRemapping.getCoverRemappingMap().get(location);
                        CoverFactory factory = AntimatterAPI.get(CoverFactory.class, location);
                        Tier tier = covers.contains(dir.get3DDataValue() + "t")
                                ? AntimatterAPI.get(Tier.class, covers.getString(dir.get3DDataValue() + "t"))
                                : null;
                        if (factory != null) {
                            ItemStack item = factory.getItem(tier);
                            Component itemTip = item.isEmpty() ? Utils.translatable("cover." + domain + "."+ id) : item.getHoverName();
                            tooltip.add(Utils.translatable("antimatter.tooltip.cover.stack", dir.getName(), itemTip));
                        }
                    }
                }
            }
        }
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> list = super.getDrops(state, builder);
        BlockEntity tileentity = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (tileentity instanceof BlockEntityPipe<?> pipe){
            if (!list.isEmpty()) {
                pipe.coverHandler.ifPresent(c -> c.writeToStack(list.get(0)));
            }
            pipe.addInventoryDrops(list);
        }
        return list;
    }

    @Override
    public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        //If we are replacing with the same block, remove tile since we are replacing with a covered/uncovered tile.
        //Also make sure it is the covered data that actually changes.
        if (state.hasBlockEntity() && state.is(newState.getBlock()) && (state.equals(newState.setValue(TICKING, !newState.getValue(TICKING))))) {
            worldIn.removeBlockEntity(pos);
        } else if (!state.is(newState.getBlock())) {
            super.onRemove(state, worldIn, pos, newState, isMoving);
        }
    }

    protected Cache<Integer, VoxelShape> getShapes(){
        return shapes.get(size);
    }

    private void recursiveShapeBuild(int index, short acc) {
        if (index > 11) {
            /*if (!getShapes().(acc)) {
                getShapes().put(acc, makeShapes(acc));
            }*/
            return;
        }
        short which = (short) (acc | (1 << index));
        recursiveShapeBuild(index + 1, which);
        recursiveShapeBuild(index + 1, acc);
    }

    private VoxelShape makeShapes(short which) {
        float offset = 0.0625f * size.ordinal();
        VoxelShape shape = Shapes.create(size.getAABB());
        if ((which & (1)) > 0)
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
        if ((which & (1 << 6)) > 0)
            shape = Shapes.or(shape, Shapes.box(0, 0, 0, 1, 0.0625f, 1));
        if ((which & (1 << 7)) > 0)
            shape = Shapes.or(shape, Shapes.box(0, 0.9375, 0, 1, 1, 1));
        if ((which & (1 << 8)) > 0)
            shape = Shapes.or(shape, Shapes.box(0, 0, 0, 1, 1, 0.0625f));
        if ((which & (1 << 9)) > 0)
            shape = Shapes.or(shape, Shapes.box(0, 0, 0.9375, 1, 1, 1));
        if ((which & (1 << 10)) > 0)
            shape = Shapes.or(shape, Shapes.box(0, 0, 0, 0.0625f, 1, 1));
        if ((which & (1 << 11)) > 0)
            shape = Shapes.or(shape, Shapes.box(0.9375, 0, 0, 1, 1, 1));
        return shape;
    }

    public int getRGB() {
        return type.getMaterial().getRGB();
    }

    public Texture getFace() {
        return faces[size.ordinal()];
    }

    @Override
    public AntimatterItemBlock getItemBlock() {
        return new PipeItemBlock(this);
    }

    public AntimatterToolType getToolType() {
        //if (type.getMaterial() == Data.Wood) return Data.AXE;
        return AntimatterDefaultTools.WRENCH;
    }

    @Override // Used to set connection for sides where neighbor has pre-set connection
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        BlockEntityPipe<?> tile = getTilePipe(worldIn, pos);
        if (tile != null && !worldIn.isClientSide()) {
            tile.coverHandler.ifPresent(c -> c.readFromStack(stack));
            if (stack.getTag() != null && stack.getTag().contains(Ref.KEY_PIPE_TILE_COLOR)){
                tile.setPipeColor(stack.getTag().getInt(Ref.KEY_PIPE_TILE_COLOR));
            }
            for (Direction side : Ref.DIRS) {
                BlockEntityPipe<?> neighbour = tile.getPipe(side);

                if (neighbour != null && neighbour.connects(side.getOpposite())) {
                    /*if (neighbour.blocksSide(side.getOpposite()) || tile.blocksSide(side)){
                        neighbour.clearConnection(side.getOpposite());
                    } else */if (!neighbour.blocksSide(side.getOpposite()) && !tile.blocksSide(side)) {
                        tile.setConnection(side);
                    }
                }
            }
        }
    }

    // Used to set connection between pipes on which block was placed
    public boolean onBlockPlacedTo(Level world, BlockPos pos, Direction face) {
        BlockEntityPipe<?> tile = getTilePipe(world, pos);
        if (tile != null && !world.isClientSide()) {
            if (!world.getBlockState(pos.relative(face.getOpposite())).hasBlockEntity()) return false;
            BlockEntityPipe<?> side = tile.getPipe(face.getOpposite());
            if (side != null && side.blocksSide(face)) return false;
            tile.setConnection(face.getOpposite());
            return true;
        }
        return false;
    }

    @Override
    public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
        if (!worldIn.isClientSide) {
            BlockEntityPipe<?> tile = (BlockEntityPipe<?>) worldIn.getBlockEntity(pos);
            if (tile != null) {
                tile.onBlockUpdate(fromPos);
            }
        }
    }

    // Used to clear connection for sides where neighbor was removed
    public void onNeighborChange(BlockState state, LevelReader world, BlockPos pos, BlockPos neighbor) {
        if (!world.isClientSide()) {
            BlockEntityPipe<?> tile = (BlockEntityPipe<?>) world.getBlockEntity(pos);
            if (tile != null) {
                tile.onBlockUpdate(neighbor);
            }
        }    
    }

    @Override
    public int getSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        BlockEntity entity = blockAccess.getBlockEntity(pos);
        if (entity instanceof BlockEntityPipe<?> pipe) {
            return pipe.getWeakRedstonePower(side == null ? null : side.getOpposite());
        }
        return super.getSignal(blockState, blockAccess, pos, side);
    }

    @Override
    public int getDirectSignal(BlockState blockState, BlockGetter blockAccess, BlockPos pos, Direction side) {
        BlockEntity entity = blockAccess.getBlockEntity(pos);
        if (entity instanceof BlockEntityPipe<?> pipe) {
            return pipe.getStrongRedstonePower(side == null ? null : side.getOpposite());
        }
        return super.getDirectSignal(blockState, blockAccess, pos, side);
    }

    @Override
    public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
        onNeighborChange(stateIn, worldIn, currentPos, facingPos);
        return stateIn;
    }

    @NotNull
    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        BlockEntityPipe<T> tile = (BlockEntityPipe) world.getBlockEntity(pos);
        if (tile == null) {
            return InteractionResult.PASS;
        }
        if (!world.isClientSide && hand == InteractionHand.MAIN_HAND) {
            ItemStack stack = player.getItemInHand(hand);
            if (stack.getItem() instanceof IHaveCover) {
                CoverFactory factory = ((IHaveCover) stack.getItem()).getCover();
                Direction dir = Utils.getInteractSide(hit);
                boolean ok = tile.getCoverHandler().map(i -> i.placeCover(player, Utils.getInteractSide(hit), stack, factory.get().get(i, ((IHaveCover) stack.getItem()).getTier(), dir, factory))).orElse(false);
                if (ok) {
                    return InteractionResult.SUCCESS;
                }
            }
            AntimatterToolType type = Utils.getToolType(player);
            if (type == AntimatterDefaultTools.CROWBAR) {
                if (!player.isCrouching()) {
                    if (tile.getCoverHandler().map(h -> h.removeCover(player, Utils.getInteractSide(hit), false)).orElse(false)) {
                        Utils.damageStack(stack, hand, player);
                        return InteractionResult.SUCCESS;
                    }
                    return InteractionResult.PASS;
                } else {
                    if (tile.getCoverHandler().map(h -> h.moveCover(player, hit.getDirection(), Utils.getInteractSide(hit))).orElse(false)) {
                        Utils.damageStack(stack, player);
                        return InteractionResult.SUCCESS;
                    }
                    return InteractionResult.PASS;
                }
            }
            InteractionResult coverInteract = tile.getCoverHandler().map(h -> h.onInteract(player, hand, Utils.getInteractSide(hit), type)).orElse(InteractionResult.PASS);
            if (coverInteract != InteractionResult.PASS) return coverInteract;
            if (type == null) {
                return InteractionResult.PASS;
            }
            if (type == AntimatterDefaultTools.SCREWDRIVER) {
                /*if (player.isCrouching()) {
                    AntimatterPlatformUtils.INSTANCE.openGui((ServerPlayer) player, tile, extra -> extra.writeBlockPos(pos));
                    Utils.damageStack(stack, hand, player);
                    return InteractionResult.SUCCESS;
                }*/
                ICover instance = tile.getCoverHandler().map(h -> h.get(Utils.getInteractSide(hit))).orElse(ICover.empty);
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
                if (tile.blocksSide(side) || (tile.getPipe(side) != null && tile.getPipe(side).blocksSide(side.getOpposite()))) return InteractionResult.CONSUME;
                tile.toggleConnection(side);
                Utils.damageStack(stack, hand, player);
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (size.ordinal() > 5) return Shapes.block();
        if (context instanceof EntityCollisionContext cont && cont.getEntity() instanceof Player player) {
            if (Utils.isPlayerHolding(player, InteractionHand.MAIN_HAND, getToolType(), AntimatterDefaultTools.CROWBAR, AntimatterDefaultTools.SCREWDRIVER)) {
                return Shapes.block();
            }
            if (!player.getMainHandItem().isEmpty() && player.getMainHandItem().getItem() instanceof IHaveCover) {
                return Shapes.block();
            }
        }
        int config = getShapeConfig(state, world, new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ()), pos);
        VoxelShape shape = null;
        try {
            shape = getShapes().get(config, () -> makeShapes((short) config));
        } catch (ExecutionException e) {
            Antimatter.LOGGER.error(e);
        }
        return shape != null ? shape : Shapes.block();
    }

    public int getPipeID(int config, int cull) {
        return ((size.ordinal() + 1) * 100) + ((getModelId() + 1) * 1000) + (cull == 0 ? 0 : 10000) + config;
    }

    @Nullable
    protected static BlockEntityPipe<?> getTilePipe(BlockGetter world, BlockPos pos) {
        BlockEntity tile = world.getBlockEntity(pos);
        return tile instanceof BlockEntityPipe ? (BlockEntityPipe<?>) tile : null;
    }

    public int getShapeConfig(BlockState state, BlockGetter world, BlockPos.MutableBlockPos mut, BlockPos pos){
        int ct = 0;
        BlockEntityPipe<?> tile = getTilePipe(world, pos);
        if (tile != null) {
            for (int s = 0; s < 6; s++) {
                if (tile.canConnect(s)) {
                    ct += 1 << s;
                }
                if (tile.coverHandler.isPresent()){
                    var coverHandler = tile.coverHandler.get();
                    if (!coverHandler.get(Direction.from3DDataValue(s)).isEmpty()){
                        ct += 1 << (s + 6);
                    }
                }
            }
        }
        return ct;
    }

    @Override
    public ModelConfig getConfig(BlockState state, BlockGetter world, BlockPos.MutableBlockPos mut, BlockPos pos) {
        int ct = 0;
        BlockEntityPipe<?> tile = getTilePipe(world, pos);
        if (tile != null) {
            for (int s = 0; s < 6; s++) {
                if (tile.canConnect(s)) {
                    ct += 1 << s;
                }
                /*if (tile.coverHandler.isPresent()){
                    var coverHandler = tile.coverHandler.get();
                    if (!coverHandler.get(Direction.from3DDataValue(s)).isEmpty()){
                        ct += 1 << (s + 6);
                    }
                }*/
            }
        }
        return config.set(pos, new int[]{getPipeID(ct, 0)});
    }

    @Override
    public int getBlockColor(BlockState state, @Nullable BlockGetter world, @Nullable BlockPos pos, int i) {
        BlockEntityPipe<?> pipe = getTilePipe(world, pos);
        if (pipe != null && pipe.getPipeColor() != -1) return pipe.getPipeColor();
        return getRGB();
    }

    @Override
    public int getItemColor(ItemStack stack, @Nullable Block block, int i) {
        if (stack.getTag() != null && stack.getTag().contains(Ref.KEY_PIPE_TILE_COLOR)){
            return stack.getTag().getInt(Ref.KEY_PIPE_TILE_COLOR);
        }
        return getRGB();
    }

    @Override
    public void onItemModelBuild(ItemLike item, AntimatterItemModelProvider prov) {
        if (size.ordinal() > 5){
            prov.getBuilder(item).parent(new ResourceLocation(SIMPLE)).texture("all", getSide()).texture("north", getFace());
        } else {
            prov.getBuilder(item).parent(prov.existing("antimatter", "block/pipe/" + getSize().getId() + "/line_inv")).texture("all", getSide()).texture("overlay", getFace());
        }
    }

    @Override
    public void onBlockModelBuild(Block block, AntimatterBlockStateProvider prov) {
        prov.getVariantBuilder(block).forAllStates(s -> new VariantBlockStateBuilder.VariantBuilder()
                .modelFile(getPipeConfig(prov.getBuilder(block)))
                .uvLock()
        );
    }

    public String getModelLoc(String shape, int cull) {
        return "antimatter:block/pipe/" + getSize().getId() + "/" + shape + (cull == 0 ? "" : (shape.equals("base") ? "" : "_culled"));
    }

    public AntimatterBlockModelBuilder getPipeConfig(AntimatterBlockModelBuilder builder) {
        if (size.ordinal() > 5) return getPipeConfigForFullBlock(builder);
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

    public AntimatterBlockModelBuilder getPipeConfigForFullBlock(AntimatterBlockModelBuilder builder) {
        builder.model(SIMPLE, of("all", getFace()));
        builder.staticConfigId("pipe");
        builder.particle(getFace());

        //Default Shape (0 Connections)
        builder.config(getPipeID(0, 0), SIMPLE, c -> c.tex(of("all", getFace())));

        //Single Shapes (1 Connections)
        builder.config(getPipeID(1, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace())).rot(-90, 0, 0));
        builder.config(getPipeID(2, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace())).rot(90, 0, 0));
        builder.config(getPipeID(4, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace())));
        builder.config(getPipeID(8, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace())).rot(0, 180, 0));
        builder.config(getPipeID(16, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace())).rot(0, 90, 0));
        builder.config(getPipeID(32, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace())).rot(0, -90, 0));

        //Line Shapes (2 Connections)
        builder.config(getPipeID(3, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "south", getFace())).rot(90, 0, 0));
        builder.config(getPipeID(12, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "south", getFace())));
        builder.config(getPipeID(48, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "south", getFace())).rot(0, 90, 0));

        //Elbow Shapes (2 Connections)
        builder.config(getPipeID(5, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "east", getFace())).rot(0, 0, -90));
        builder.config(getPipeID(6, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "east", getFace())).rot(0, 0, 90));
        builder.config(getPipeID(9, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "east", getFace())).rot(0, 180, -90));
        builder.config(getPipeID(10, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "east", getFace())).rot(0, 180, 90));
        builder.config(getPipeID(17, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "east", getFace())).rot(90, 180, 0));
        builder.config(getPipeID(18, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "east", getFace())).rot(-90, 180, 0));
        builder.config(getPipeID(20, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "east", getFace())).rot(0, 90, 0));
        builder.config(getPipeID(24, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "east", getFace())).rot(0, -180, 0));
        builder.config(getPipeID(33, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "east", getFace())).rot(-90, 0, 0));
        builder.config(getPipeID(34, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "east", getFace())).rot(90, 0, 0));
        builder.config(getPipeID(36, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "east", getFace())));
        builder.config(getPipeID(40, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "east", getFace())).rot(0, -90, 0));

        //Side Shapes (3 Connections)
        builder.config(getPipeID(7, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "south", getFace(), "up", getFace())).rot(-90, 0, 0));
        builder.config(getPipeID(11, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "south", getFace(), "up", getFace())).rot(90, 0, 0));
        builder.config(getPipeID(13, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "south", getFace(), "up", getFace())).rot(0, 0, 180));
        builder.config(getPipeID(14, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "south", getFace(), "up", getFace())));
        builder.config(getPipeID(19, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "south", getFace(), "up", getFace())).rot(90, 0, 90));
        builder.config(getPipeID(28, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "south", getFace(), "up", getFace())).rot(0, 0, 90));
        builder.config(getPipeID(35, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "south", getFace(), "up", getFace())).rot(90, 0, -90));
        builder.config(getPipeID(44, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "south", getFace(), "up", getFace())).rot(0, 0, -90));
        builder.config(getPipeID(49, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "south", getFace(), "up", getFace())).rot(0, 90, 180));
        builder.config(getPipeID(50, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "south", getFace(), "up", getFace())).rot(0, 90, 0));
        builder.config(getPipeID(52, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "south", getFace(), "up", getFace())).rot(0, 90, -90));
        builder.config(getPipeID(56, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "south", getFace(), "up", getFace())).rot(0, 90, 90));

        //Corner Shapes (3 Connections)
        builder.config(getPipeID(21, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "east", getFace(), "up", getFace())).rot(0, 0, 180));
        builder.config(getPipeID(22, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "east", getFace(), "up", getFace())).rot(0, 90, 0));
        builder.config(getPipeID(25, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "east", getFace(), "up", getFace())).rot(0, -270, 180));
        builder.config(getPipeID(26, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "east", getFace(), "up", getFace())).rot(0, 180, 0));
        builder.config(getPipeID(41, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "east", getFace(), "up", getFace())).rot(0, -180, 180));
        builder.config(getPipeID(42, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "east", getFace(), "up", getFace())).rot(0, -90, 0));
        builder.config(getPipeID(37, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "east", getFace(), "up", getFace())).rot(0, -90, 180));
        builder.config(getPipeID(38, 0), SIMPLE, c -> c.tex(of("all", getSide(), "north", getFace(), "east", getFace(), "up", getFace())));

        //Arrow Shapes (4 Connections)
        builder.config(getPipeID(23, 0), SIMPLE, c -> c.tex(of("all", getFace(), "south", getSide(), "down", getSide())).rot(0, 0, 90));
        builder.config(getPipeID(27, 0), SIMPLE, c -> c.tex(of("all", getFace(), "south", getSide(), "down", getSide())).rot(0, -270, 90));
        builder.config(getPipeID(29, 0), SIMPLE, c -> c.tex(of("all", getFace(), "south", getSide(), "down", getSide())).rot(0, 90, 180));
        builder.config(getPipeID(30, 0), SIMPLE, c -> c.tex(of("all", getFace(), "south", getSide(), "down", getSide())).rot(0, 90, 0));
        builder.config(getPipeID(39, 0), SIMPLE, c -> c.tex(of("all", getFace(), "south", getSide(), "down", getSide())).rot(0, -90, 90));
        builder.config(getPipeID(43, 0), SIMPLE, c -> c.tex(of("all", getFace(), "south", getSide(), "down", getSide())).rot(0, -180, 90));
        builder.config(getPipeID(45, 0), SIMPLE, c -> c.tex(of("all", getFace(), "south", getSide(), "down", getSide())).rot(0, -90, 180));
        builder.config(getPipeID(46, 0), SIMPLE, c -> c.tex(of("all", getFace(), "south", getSide(), "down", getSide())).rot(0, -90, 0));
        builder.config(getPipeID(53, 0), SIMPLE, c -> c.tex(of("all", getFace(), "south", getSide(), "down", getSide())).rot(180, 180, 0));
        builder.config(getPipeID(54, 0), SIMPLE, c -> c.tex(of("all", getFace(), "south", getSide(), "down", getSide())));
        builder.config(getPipeID(57, 0), SIMPLE, c -> c.tex(of("all", getFace(), "south", getSide(), "down", getSide())).rot(180, 0, 0));
        builder.config(getPipeID(58, 0), SIMPLE, c -> c.tex(of("all", getFace(), "south", getSide(), "down", getSide())).rot(0, 180, 0));

        //Cross Shapes (4 Connections)
        builder.config(getPipeID(15, 0), SIMPLE, c -> c.tex(of("all", getFace(), "up", getSide(), "down", getSide())).rot(0, 0, 90));
        builder.config(getPipeID(51, 0), SIMPLE, c -> c.tex(of("all", getFace(), "up", getSide(), "down", getSide())).rot(90, 0, 0));
        builder.config(getPipeID(60, 0), SIMPLE, c -> c.tex(of("all", getFace(), "up", getSide(), "down", getSide())));

        //Five Shapes (5 Connections)
        builder.config(getPipeID(31, 0), SIMPLE, c -> c.tex(of("all", getFace(), "down", getSide())).rot(0, 0, 90));
        builder.config(getPipeID(47, 0), SIMPLE, c -> c.tex(of("all", getFace(), "down", getSide())).rot(0, 0, -90));
        builder.config(getPipeID(55, 0), SIMPLE, c -> c.tex(of("all", getFace(), "down", getSide())).rot(-90, 0, 0));
        builder.config(getPipeID(59, 0), SIMPLE, c -> c.tex(of("all", getFace(), "down", getSide())).rot(90, 0, 0));
        builder.config(getPipeID(61, 0), SIMPLE, c -> c.tex(of("all", getFace(), "down", getSide())).rot(180, 0, 0));
        builder.config(getPipeID(62, 0), SIMPLE, c -> c.tex(of("all", getFace(), "down", getSide())));

        //All Shapes (6 Connections)
        builder.config(getPipeID(63, 0), SIMPLE, c -> c.tex(of("all", getFace())));

        return builder.loader(AntimatterModelManager.LOADER_PIPE);
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
            return BlockEntityTickable::commonTick;
        }
        return null;
    }

    @Override
    public muramasa.antimatter.material.Material getMaterial() {
        return type.getMaterial();
    }
}
