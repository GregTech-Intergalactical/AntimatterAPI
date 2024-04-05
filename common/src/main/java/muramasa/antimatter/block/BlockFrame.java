package muramasa.antimatter.block;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTags;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.registration.IItemBlockProvider;
import muramasa.antimatter.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.ChatType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.levelgen.RandomSource;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Random;

import static muramasa.antimatter.material.MaterialTags.*;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.BOTTOM;
import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public class BlockFrame extends BlockStorage implements IItemBlockProvider, SimpleWaterloggedBlock {
    private static final VoxelShape FRAME_SHAPE = Shapes.box(0.05, 0.0, 0.05, 0.95, 1.0, 0.95);
    private static final Object2ObjectMap<Integer, IntegerProperty> PROPERTIES = new Object2ObjectArrayMap<>();
    protected final StateDefinition<Block, BlockState> stateContainer;
    private final int maxRange;
    private final boolean initialized;
    public BlockFrame(String domain, MaterialType<?> type, Material material) {
        super(domain, type, material);
        this.maxRange = material.has(WOOD) || material.has(RUBBERTOOLS) ? 9 : material.has(METAL) ? 65 : 33;
        if (!PROPERTIES.containsKey(maxRange)){
            PROPERTIES.put(maxRange, IntegerProperty.create("distance", 0, maxRange));
        }
        initialized = true;
        StateDefinition.Builder<Block, BlockState> builder = new StateDefinition.Builder<>(this);
        this.createBlockStateDefinition(builder);
        this.stateContainer = builder.create(Block::defaultBlockState, BlockState::new);
        this.registerDefaultState(this.stateContainer.any().setValue(PROPERTIES.get(maxRange), maxRange).setValue(WATERLOGGED, false).setValue(BOTTOM, false));
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        if (!initialized) return;
        builder.add(PROPERTIES.get(maxRange), WATERLOGGED, BOTTOM);
    }

    @Override
    public StateDefinition<Block, BlockState> getStateDefinition() {
        return stateContainer;
    }

    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        BlockPos blockPos = blockPlaceContext.getClickedPos();
        Level level = blockPlaceContext.getLevel();
        int i = getDistance(level, blockPos);
        return this.defaultBlockState().setValue(WATERLOGGED, level.getFluidState(blockPos).getType() == Fluids.WATER).setValue(PROPERTIES.get(maxRange), i).setValue(BOTTOM, this.isBottom(level, blockPos, i));
    }

    public void onPlace(BlockState blockState, Level level, BlockPos blockPos, BlockState blockState2, boolean bl) {
        if (!level.isClientSide) {
            level.scheduleTick(blockPos, this, 1);
        }

    }

    public BlockState updateShape(BlockState blockState, Direction direction, BlockState blockState2, LevelAccessor levelAccessor, BlockPos blockPos, BlockPos blockPos2) {
        if (blockState.getValue(WATERLOGGED)) {
            levelAccessor.scheduleTick(blockPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));
        }

        if (!levelAccessor.isClientSide()) {
            levelAccessor.scheduleTick(blockPos, this, 1);
        }

        return blockState;
    }

    public void tick(BlockState blockState, ServerLevel serverLevel, BlockPos blockPos, Random randomSource) {
        int i = getDistance(serverLevel, blockPos);
        BlockState blockState2 = blockState.setValue(PROPERTIES.get(maxRange), i).setValue(BOTTOM, this.isBottom(serverLevel, blockPos, i));
        if (blockState2.getValue(PROPERTIES.get(maxRange)) == maxRange) {
            if (blockState.getValue(PROPERTIES.get(maxRange)) == maxRange) {
                FallingBlockEntity.fall(serverLevel, blockPos, blockState2);
            } else {
                serverLevel.destroyBlock(blockPos, true);
            }
        } else if (blockState != blockState2) {
            serverLevel.setBlock(blockPos, blockState2, 3);
        }

    }

    public boolean canSurvive(BlockState blockState, LevelReader levelReader, BlockPos blockPos) {
        return getDistance(levelReader, blockPos) < maxRange;
    }

    public FluidState getFluidState(BlockState blockState) {
        return blockState.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(blockState);
    }

    private boolean isBottom(BlockGetter blockGetter, BlockPos blockPos, int i) {
        return i > 0 && !blockGetter.getBlockState(blockPos.below()).is(this);
    }

    public int getDistance(BlockGetter blockGetter, BlockPos blockPos) {
        BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable().move(Direction.DOWN);
        BlockState blockState = blockGetter.getBlockState(mutableBlockPos);
        int i = maxRange;
        if (blockState.is(this)) {
            i = blockState.getValue(PROPERTIES.get(maxRange));
        } else if (blockState.isFaceSturdy(blockGetter, mutableBlockPos, Direction.UP)) {
            return 0;
        }

        for (Direction direction : Direction.Plane.HORIZONTAL) {
            BlockState blockState2 = blockGetter.getBlockState(mutableBlockPos.setWithOffset(blockPos, direction));
            if (blockState2.is(this)) {
                i = Math.min(i, blockState2.getValue(PROPERTIES.get(maxRange)) + 1);
                if (i == 1) {
                    break;
                }
            }
        }

        return i;
    }

    @Override
    public AntimatterItemBlock getItemBlock() {

        return new AntimatterItemBlock(this) {

            @Override
            @Nullable
            public BlockPlaceContext updatePlacementContext(BlockPlaceContext ctx) {
                BlockPos blockPos = ctx.getClickedPos();
                BlockState blockState = ctx.getLevel().getBlockState(blockPos);
                Block block = this.getBlock();
                if (!blockState.is(block)) {
                    return getDistance(ctx.getLevel(), blockPos) == maxRange ? null : ctx;
                }
                Direction direction;
                if (ctx.isSecondaryUseActive()) {
                    direction = ctx.getClickedFace();
                } else {
                    direction = ctx.getClickedFace() == Direction.UP ? ctx.getHorizontalDirection() : Direction.UP;
                }
                int i = 0;
                BlockPos.MutableBlockPos mutableBlockPos = blockPos.mutable().move(direction);
                while (i < maxRange) {
                    if (!ctx.getLevel().isClientSide && !ctx.getLevel().isInWorldBounds(mutableBlockPos)) {
                        Player player = ctx.getPlayer();
                        int j = ctx.getLevel().getMaxBuildHeight();
                        if (player instanceof ServerPlayer serverPlayer && mutableBlockPos.getY() >= j) {
                            serverPlayer.sendMessage((Utils.translatable("build.tooHigh", j - 1)).withStyle(ChatFormatting.RED), ChatType.GAME_INFO, Util.NIL_UUID);
                        }
                        break;
                    }
                    blockState = ctx.getLevel().getBlockState(mutableBlockPos);
                    if (!blockState.is(this.getBlock())) {
                        if (blockState.canBeReplaced(ctx)) {
                            return BlockPlaceContext.at(ctx, mutableBlockPos, direction);
                        }
                        break;
                    }
                    mutableBlockPos.move(direction);
                    if (direction.getAxis().isHorizontal()) {
                        ++i;
                    }
                }
                return null;
            }

            @Override
            protected boolean mustSurvive() {
                return false;
            }
        };

    }

    public boolean canBeReplaced(BlockState blockState, BlockPlaceContext blockPlaceContext) {
        return blockPlaceContext.getItemInHand().is(this.asItem());
    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (!(entity instanceof LivingEntity)) return;
        if (type == AntimatterMaterialTypes.BLOCK) return;
        entity.setDeltaMovement(Mth.clamp(entity.getDeltaMovement().x, -0.15, 0.15), entity.getDeltaMovement().y, Mth.clamp(entity.getDeltaMovement().z, -0.15, 0.15));
        entity.fallDistance = 0.0F;
        if (entity.isCrouching() && entity instanceof Player) {
            if (entity.isInWater())
                entity.setDeltaMovement(entity.getDeltaMovement().x, 0.02D, entity.getDeltaMovement().z);
            else entity.setDeltaMovement(entity.getDeltaMovement().x, 0.08D, entity.getDeltaMovement().z);
        } else if (entity.horizontalCollision) {
            float toolSpeed = material.has(MaterialTags.TOOLS) ? MaterialTags.TOOLS.get(material).toolSpeed() : 0;
            entity.setDeltaMovement(entity.getDeltaMovement().x, 0.22D + (toolSpeed / 75), entity.getDeltaMovement().z);
        } else
            entity.setDeltaMovement(entity.getDeltaMovement().x, Math.max(entity.getDeltaMovement().y, -0.2D), entity.getDeltaMovement().z);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (context != CollisionContext.empty()) return super.getShape(state, world, pos, context);
        return FRAME_SHAPE;
    }

    //todo in felt
    public boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
        return true;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }
}
