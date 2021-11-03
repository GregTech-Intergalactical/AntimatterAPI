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

    private static final VoxelShape FRAME_SHAPE = VoxelShapes.create(0.05, 0.0, 0.05, 0.95, 1.0, 0.95);

    public BlockStorage(String domain, Material material, MaterialType<?> type) {
        super(domain, material, type, Block.Properties.create(net.minecraft.block.material.Material.IRON).hardnessAndResistance(8.0f).sound(SoundType.METAL));
    }

    @Override
    public AntimatterItemBlock getItemBlock() {

        return new AntimatterItemBlock(this) {

            @Override
            @Nullable
            public BlockItemUseContext getBlockItemUseContext(BlockItemUseContext ctx) {
                if (ctx.getPlayer().isCrouching()) return ctx;
                BlockPos actualPos = ctx.getPos().offset(ctx.getFace().getOpposite());
                Block currentBlock = ctx.getWorld().getBlockState(actualPos).getBlock();
                // if (!(currentBlock instanceof BlockStorage) || ((BlockStorage) currentBlock).getType() != MaterialType.FRAME) return ctx; // Change to Block#isIn
                if (!currentBlock.isIn(Data.FRAME.getTag())) return ctx;
                BlockPos.Mutable mutablePos = new BlockPos.Mutable(actualPos.getX(), actualPos.getY(), actualPos.getZ());
                while (ctx.getWorld().getHeight() > mutablePos.getY()) {
                    if (ctx.getWorld().getBlockState(mutablePos.move(Direction.UP)).isReplaceable(ctx)) {
                        ctx.getPlayer().swingArm(ctx.getHand());
                        ctx.getWorld().setBlockState(mutablePos, ((BlockItem) ctx.getItem().getItem()).getBlock().getDefaultState());
                        if (!ctx.getPlayer().isCreative()) ctx.getItem().shrink(1);
                        ctx.getWorld().playSound(ctx.getPlayer(), mutablePos, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 2.0F, soundType.getPitch() * 0.8F);
                        return null;
                    }
                }
                return ctx;
            }
        };

    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (!(entity instanceof LivingEntity)) return;
        if (type == Data.BLOCK) return;
        entity.setMotion(MathHelper.clamp(entity.getMotion().x, -0.15, 0.15), entity.getMotion().y, MathHelper.clamp(entity.getMotion().z, -0.15, 0.15));
        entity.fallDistance = 0.0F;
        if (entity.isCrouching() && entity instanceof PlayerEntity) {
            if (entity.isInWater()) entity.setMotion(entity.getMotion().x, 0.02D, entity.getMotion().z);
            else entity.setMotion(entity.getMotion().x, 0.08D, entity.getMotion().z);
        } else if (entity.collidedHorizontally)
            entity.setMotion(entity.getMotion().x, 0.22D + (material.getToolSpeed() / 75), entity.getMotion().z);
        else entity.setMotion(entity.getMotion().x, Math.max(entity.getMotion().y, -0.2D), entity.getMotion().z);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        if (context != ISelectionContext.dummy()) return super.getShape(state, world, pos, context);
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
    public PushReaction getPushReaction(BlockState state) {
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
