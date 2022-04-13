package muramasa.antimatter.block;

import muramasa.antimatter.Data;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.registration.IItemBlockProvider;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class BlockStorage extends BlockMaterialType implements IItemBlockProvider, ISharedAntimatterObject {

    private static final VoxelShape FRAME_SHAPE = Shapes.box(0.05, 0.0, 0.05, 0.95, 1.0, 0.95);

    public BlockStorage(String domain,  MaterialType<?> type, Material material) {
        super(domain, material, type, Properties.of(net.minecraft.world.level.material.Material.METAL).strength(8.0f).sound(SoundType.METAL));
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
                // if (!(currentBlock instanceof BlockStorage) || ((BlockStorage) currentBlock).getType() != MaterialType.FRAME) return ctx; // Change to Block#isIn
                if (!state.is(Data.FRAME.getTag())) return ctx;
                BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos(actualPos.getX(), actualPos.getY(), actualPos.getZ());
                while (ctx.getLevel().getMaxBuildHeight() > mutablePos.getY()) {
                    if (ctx.getLevel().getBlockState(mutablePos.move(Direction.UP)).canBeReplaced(ctx)) {
                        ctx.getPlayer().swing(ctx.getHand());
                        ctx.getLevel().setBlockAndUpdate(mutablePos, ((BlockItem) ctx.getItemInHand().getItem()).getBlock().defaultBlockState());
                        if (!ctx.getPlayer().isCreative()) ctx.getItemInHand().shrink(1);
                        ctx.getLevel().playSound(ctx.getPlayer(), mutablePos, soundType.getPlaceSound(), SoundSource.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
                        return null;
                    }
                }
                return ctx;
            }
        };

    }

    @Override
    public void entityInside(BlockState state, Level world, BlockPos pos, Entity entity) {
        if (!(entity instanceof LivingEntity)) return;
        if (type == Data.BLOCK) return;
        entity.setDeltaMovement(Mth.clamp(entity.getDeltaMovement().x, -0.15, 0.15), entity.getDeltaMovement().y, Mth.clamp(entity.getDeltaMovement().z, -0.15, 0.15));
        entity.fallDistance = 0.0F;
        if (entity.isCrouching() && entity instanceof Player) {
            if (entity.isInWater())
                entity.setDeltaMovement(entity.getDeltaMovement().x, 0.02D, entity.getDeltaMovement().z);
            else entity.setDeltaMovement(entity.getDeltaMovement().x, 0.08D, entity.getDeltaMovement().z);
        } else if (entity.horizontalCollision)
            entity.setDeltaMovement(entity.getDeltaMovement().x, 0.22D + (material.getToolSpeed() / 75), entity.getDeltaMovement().z);
        else
            entity.setDeltaMovement(entity.getDeltaMovement().x, Math.max(entity.getDeltaMovement().y, -0.2D), entity.getDeltaMovement().z);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (context != CollisionContext.empty()) return super.getShape(state, world, pos, context);
        return type == Data.FRAME ? FRAME_SHAPE : super.getShape(state, world, pos, context);
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

    @Override
    public boolean isLadder(BlockState state, LevelReader world, BlockPos pos, LivingEntity entity) {
        return type == Data.FRAME;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return type == Data.FRAME ? PushReaction.DESTROY : PushReaction.NORMAL;
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
