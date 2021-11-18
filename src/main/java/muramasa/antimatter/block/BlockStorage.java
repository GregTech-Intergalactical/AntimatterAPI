package muramasa.antimatter.block;

import muramasa.antimatter.Data;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.registration.IItemBlockProvider;
import muramasa.antimatter.registration.ISharedAntimatterObject;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.PushReaction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

import javax.annotation.Nullable;

public class BlockStorage extends BlockMaterialType implements IItemBlockProvider, ISharedAntimatterObject {

    private static final VoxelShape FRAME_SHAPE = VoxelShapes.box(0.05, 0.0, 0.05, 0.95, 1.0, 0.95);

    public BlockStorage(String domain, Material material, MaterialType<?> type) {
        super(domain, material, type, Block.Properties.of(net.minecraft.block.material.Material.METAL).strength(8.0f).sound(SoundType.METAL));
    }

    @Override
    public AntimatterItemBlock getItemBlock() {

        return new AntimatterItemBlock(this) {

            @Override
            @Nullable
            public BlockItemUseContext updatePlacementContext(BlockItemUseContext ctx) {
                if (ctx.getPlayer().isCrouching()) return ctx;
                BlockPos actualPos = ctx.getClickedPos().relative(ctx.getClickedFace().getOpposite());
                Block currentBlock = ctx.getLevel().getBlockState(actualPos).getBlock();
                // if (!(currentBlock instanceof BlockStorage) || ((BlockStorage) currentBlock).getType() != MaterialType.FRAME) return ctx; // Change to Block#isIn
                if (!currentBlock.is(Data.FRAME.getTag())) return ctx;
                BlockPos.Mutable mutablePos = new BlockPos.Mutable(actualPos.getX(), actualPos.getY(), actualPos.getZ());
                while (ctx.getLevel().getMaxBuildHeight() > mutablePos.getY()) {
                    if (ctx.getLevel().getBlockState(mutablePos.move(Direction.UP)).canBeReplaced(ctx)) {
                        ctx.getPlayer().swing(ctx.getHand());
                        ctx.getLevel().setBlockAndUpdate(mutablePos, ((BlockItem) ctx.getItemInHand().getItem()).getBlock().defaultBlockState());
                        if (!ctx.getPlayer().isCreative()) ctx.getItemInHand().shrink(1);
                        ctx.getLevel().playSound(ctx.getPlayer(), mutablePos, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
                        return null;
                    }
                }
                return ctx;
            }
        };

    }

    @Override
    public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!(entity instanceof LivingEntity)) return;
        if (type == Data.BLOCK) return;
        entity.setDeltaMovement(MathHelper.clamp(entity.getDeltaMovement().x, -0.15, 0.15), entity.getDeltaMovement().y, MathHelper.clamp(entity.getDeltaMovement().z, -0.15, 0.15));
        entity.fallDistance = 0.0F;
        if (entity.isCrouching() && entity instanceof PlayerEntity) {
            if (entity.isInWater())
                entity.setDeltaMovement(entity.getDeltaMovement().x, 0.02D, entity.getDeltaMovement().z);
            else entity.setDeltaMovement(entity.getDeltaMovement().x, 0.08D, entity.getDeltaMovement().z);
        } else if (entity.horizontalCollision)
            entity.setDeltaMovement(entity.getDeltaMovement().x, 0.22D + (material.getToolSpeed() / 75), entity.getDeltaMovement().z);
        else
            entity.setDeltaMovement(entity.getDeltaMovement().x, Math.max(entity.getDeltaMovement().y, -0.2D), entity.getDeltaMovement().z);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        if (context != ISelectionContext.empty()) return super.getShape(state, world, pos, context);
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
    @Override
    public int getHarvestLevel(BlockState state) {
        return material.getMiningLevel();
    }

    @Nullable
    @Override
    public ToolType getHarvestTool(BlockState state) {
        return ToolType.PICKAXE;
    }

    @Override
    public boolean isLadder(BlockState state, IWorldReader world, BlockPos pos, LivingEntity entity) {
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
