package muramasa.antimatter.tool.behaviour;

import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CarvedPumpkinBlock;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BehaviourPumpkinCarving implements IItemUse<IAntimatterTool> {
    public static final BehaviourPumpkinCarving INSTANCE = new BehaviourPumpkinCarving();

    @Override
    public String getId() {
        return "pumpkin_carving";
    }

    @Override
    public ActionResultType onItemUse(IAntimatterTool instance, ItemUseContext c) {
        World worldIn = c.getWorld();
        BlockPos pos = c.getPos();
        BlockState state = worldIn.getBlockState(pos);
        if (c.getPlayer() != null && state.matchesBlock(Blocks.PUMPKIN)) {
            Direction facing = c.getFace().getAxis() == Direction.Axis.Y ? c.getPlayer().getHorizontalFacing().getOpposite() : c.getFace();
            worldIn.playSound(null, pos, SoundEvents.BLOCK_PUMPKIN_CARVE, SoundCategory.BLOCKS, 1.0F, 1.0F);
            worldIn.setBlockState(pos, Blocks.CARVED_PUMPKIN.getDefaultState().with(CarvedPumpkinBlock.FACING, facing), 11);
            ItemEntity itementity = new ItemEntity(worldIn, (double) pos.getX() + 0.5D + (double) facing.getXOffset() * 0.65D, (double) pos.getY() + 0.1D, (double) pos.getZ() + 0.5D + (double) facing.getZOffset() * 0.65D, new ItemStack(Items.PUMPKIN_SEEDS, 4));
            itementity.setMotion(0.05D * (double) facing.getXOffset() + worldIn.rand.nextDouble() * 0.02D, 0.05D, 0.05D * (double) facing.getZOffset() + worldIn.rand.nextDouble() * 0.02D);
            worldIn.addEntity(itementity);
            c.getItem().damageItem(1, c.getPlayer(), (playerIn) -> {
                playerIn.sendBreakAnimation(c.getHand());
            });
            return ActionResultType.SUCCESS;
        }

        return ActionResultType.PASS;
    }
}
