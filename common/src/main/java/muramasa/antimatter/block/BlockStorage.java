package muramasa.antimatter.block;

import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.data.AntimatterMaterials;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTags;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.registration.IItemBlockProvider;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import muramasa.antimatter.util.Utils;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ScaffoldingBlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.ScaffoldingBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class BlockStorage extends BlockMaterialType implements IItemBlockProvider, ISharedAntimatterObject {

    private static final VoxelShape FRAME_SHAPE = Shapes.box(0.05, 0.0, 0.05, 0.95, 1.0, 0.95);

    public BlockStorage(String domain,  MaterialType<?> type, Material material) {
        super(domain, material, type, Properties.of(material == AntimatterMaterials.Wood ? net.minecraft.world.level.material.Material.WOOD : net.minecraft.world.level.material.Material.METAL).strength(type == AntimatterMaterialTypes.FRAME ? 2.0f : 8.0f).sound(material == AntimatterMaterials.Wood ? SoundType.WOOD : SoundType.METAL).requiresCorrectToolForDrops().isValidSpawn((blockState, blockGetter, blockPos, object) -> false));
    }

    @Override
    public AntimatterItemBlock getItemBlock() {

        return new AntimatterItemBlock(this) {

            @Override
            @Nullable
            public BlockPlaceContext updatePlacementContext(BlockPlaceContext ctx) {
                if (ctx.getPlayer().isCrouching()) return ctx;
                BlockPos actualPos = ctx.getClickedPos().relative(ctx.getClickedFace().getOpposite());
                BlockState state = ctx.getLevel().getBlockState(actualPos);
                if (!state.is(AntimatterMaterialTypes.FRAME.getTag())) return ctx;
                Direction direction = ctx.getClickedFace() == Direction.UP ? ctx.getHorizontalDirection() : Direction.UP;
                int i = 0;
                BlockPos.MutableBlockPos mutableBlockPos = actualPos.mutable().move(direction);
                while (true) {
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
        return type == AntimatterMaterialTypes.FRAME ? FRAME_SHAPE : super.getShape(state, world, pos, context);
    }

    // @Override
    // public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
    // return super.getDrops(state, builder);
    // }

    /**
     * Ladder Stuffs - End
     **/

//    @Override
//    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, BlockState state, int fortune) {
//        drops.clear();
//        drops.add(new ItemStack(this, 1, state.getValue(STORAGE_MATERIAL)));
//    }
    /*@Override
    public int getHarvestLevel(BlockState state) {
        return material.getMiningLevel();
    }

    @Nullable
    @Override
    public Tag<Block> getHarvestTool(BlockState state) {
        return Tag<Block>.PICKAXE;
    }*/
    //todo in felt
    public boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
        return type == AntimatterMaterialTypes.FRAME;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return type == AntimatterMaterialTypes.FRAME ? PushReaction.DESTROY : PushReaction.NORMAL;
    }

    //    @Override
//    public boolean isOpaqueCube(BlockState state) {
//        return type == MaterialType.BLOCK;
//    }
//
//    @Override
//    public boolean isFullBlock(BlockState state) {
//        return type == MaterialType.BLOCK;
//    }
}
