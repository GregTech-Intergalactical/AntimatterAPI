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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import static muramasa.antimatter.material.MaterialTags.*;

public class BlockFrame extends BlockStorage implements IItemBlockProvider {
    private static final VoxelShape FRAME_SHAPE = Shapes.box(0.05, 0.0, 0.05, 0.95, 1.0, 0.95);
    int maxRange;
    private static final Object2ObjectMap<Integer, IntegerProperty> PROPERTIES = new Object2ObjectArrayMap<>();
    public BlockFrame(String domain, MaterialType<?> type, Material material) {
        super(domain, type, material);
        this.maxRange = material.has(WOOD) || material.has(RUBBERTOOLS) ? 9 : material.has(METAL) ? 129 : 33;
        if (!PROPERTIES.containsKey(maxRange)){
            PROPERTIES.put(maxRange, IntegerProperty.create("distance", 0, maxRange));
        }
    }

    @Override
    public AntimatterItemBlock getItemBlock() {

        return new AntimatterItemBlock(this) {

            @Override
            @Nullable
            public BlockPlaceContext updatePlacementContext(BlockPlaceContext ctx) {
                if (ctx.getPlayer().isCrouching()) return ctx;
                BlockPos actualPos = ctx.getClickedPos().relative(ctx.getClickedFace().getOpposite());
                Direction direction = ctx.getClickedFace() == Direction.UP ? ctx.getHorizontalDirection() : Direction.UP;
                int i = 0;
                BlockPos.MutableBlockPos mutableBlockPos = actualPos.mutable().move(direction);
                while (i < maxRange) {
                    if (!ctx.getLevel().isClientSide && !ctx.getLevel().isInWorldBounds(mutableBlockPos)) {
                        Player player = ctx.getPlayer();
                        int j = ctx.getLevel().getMaxBuildHeight();
                        if (player instanceof ServerPlayer serverPlayer && mutableBlockPos.getY() >= j) {
                            serverPlayer.sendMessage((Utils.translatable("build.tooHigh", j - 1)).withStyle(ChatFormatting.RED), ChatType.GAME_INFO, Util.NIL_UUID);
                        }
                        break;
                    }
                    BlockState blockState = ctx.getLevel().getBlockState(mutableBlockPos);
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
                return ctx;
            }
        };

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
