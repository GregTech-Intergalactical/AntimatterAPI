package muramasa.antimatter.tool.behaviour;

import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.tool.IBasicAntimatterTool;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CarvedPumpkinBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BehaviourPumpkinCarving implements IItemUse<IBasicAntimatterTool> {
    public static final BehaviourPumpkinCarving INSTANCE = new BehaviourPumpkinCarving();

    @Override
    public String getId() {
        return "pumpkin_carving";
    }

    @Override
    public InteractionResult onItemUse(IBasicAntimatterTool instance, UseOnContext c) {
        Level worldIn = c.getLevel();
        BlockPos pos = c.getClickedPos();
        BlockState state = worldIn.getBlockState(pos);
        if (c.getPlayer() != null && state.is(Blocks.PUMPKIN)) {
            Direction facing = c.getClickedFace().getAxis() == Direction.Axis.Y ? c.getPlayer().getDirection().getOpposite() : c.getClickedFace();
            worldIn.playSound(null, pos, SoundEvents.PUMPKIN_CARVE, SoundSource.BLOCKS, 1.0F, 1.0F);
            worldIn.setBlock(pos, Blocks.CARVED_PUMPKIN.defaultBlockState().setValue(CarvedPumpkinBlock.FACING, facing), 11);
            ItemEntity itementity = new ItemEntity(worldIn, (double) pos.getX() + 0.5D + (double) facing.getStepX() * 0.65D, (double) pos.getY() + 0.1D, (double) pos.getZ() + 0.5D + (double) facing.getStepZ() * 0.65D, new ItemStack(Items.PUMPKIN_SEEDS, 4));
            itementity.setDeltaMovement(0.05D * (double) facing.getStepX() + worldIn.random.nextDouble() * 0.02D, 0.05D, 0.05D * (double) facing.getStepZ() + worldIn.random.nextDouble() * 0.02D);
            worldIn.addFreshEntity(itementity);
            c.getItemInHand().hurtAndBreak(1, c.getPlayer(), (playerIn) -> {
                playerIn.broadcastBreakEvent(c.getHand());
            });
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}
