package muramasa.antimatter.tool.behaviour;

import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.ForgeEventFactory;

public class BehaviourVanillaShovel implements IItemUse<IAntimatterTool> {

    public static final BehaviourVanillaShovel INSTANCE = new BehaviourVanillaShovel();

    @Override
    public String getId() {
        return "vanilla_shovel";
    }

    @Override
    public ActionResultType onItemUse(IAntimatterTool instance, ItemUseContext c) {
        if (c.getClickedFace() == Direction.DOWN) return ActionResultType.PASS;
        BlockState state = c.getLevel().getBlockState(c.getClickedPos());
        BlockState changedState = null;
        if (state.getBlock() == Blocks.GRASS_BLOCK && c.getLevel().isEmptyBlock(c.getClickedPos().above())) {
            changedState = getToolModifiedState(state, Blocks.GRASS_PATH.defaultBlockState(), c.getLevel(), c.getClickedPos(), c.getPlayer(), c.getItemInHand(), ToolType.SHOVEL);
            if (changedState != null) {
                SoundEvent soundEvent = instance.getAntimatterToolType().getUseSound() == null ? SoundEvents.SHOVEL_FLATTEN : instance.getAntimatterToolType().getUseSound();
                c.getLevel().playSound(c.getPlayer(), c.getClickedPos(), soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
            }
        } else if (state.getBlock() instanceof CampfireBlock && state.getValue(CampfireBlock.LIT)) {
            changedState = getToolModifiedState(state, state.setValue(CampfireBlock.LIT, false), c.getLevel(), c.getClickedPos(), c.getPlayer(), c.getItemInHand(), ToolType.SHOVEL);
            if (changedState != null) {
                c.getLevel().levelEvent(c.getPlayer(), 1009, c.getClickedPos(), 0);
            }
        }
        if (changedState != null) {
            c.getLevel().setBlock(c.getClickedPos(), changedState, 11);
            Utils.damageStack(c.getItemInHand(), c.getPlayer());
            return ActionResultType.SUCCESS;
        } else return ActionResultType.PASS;
    }

    private BlockState getToolModifiedState(BlockState originalState, BlockState changedState, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
        BlockState eventState = ForgeEventFactory.onToolUse(originalState, world, pos, player, stack, toolType);
        return eventState != originalState ? eventState : changedState;
    }
}
