package muramasa.antimatter.tool.behaviour;

import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.tool.behaviour.BehaviourUtil.BehaviourToolAction;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

public class BehaviourVanillaShovel implements IItemUse<IAntimatterTool> {

    public static final BehaviourVanillaShovel INSTANCE = new BehaviourVanillaShovel();

    @Override
    public String getId() {
        return "vanilla_shovel";
    }

    @Override
    public InteractionResult onItemUse(IAntimatterTool instance, UseOnContext c) {
        if (c.getClickedFace() == Direction.DOWN) return InteractionResult.PASS;
        BlockState state = c.getLevel().getBlockState(c.getClickedPos());
        BlockState changedState = null;
        if (state.getBlock() == Blocks.GRASS_BLOCK && c.getLevel().isEmptyBlock(c.getClickedPos().above())) {
            changedState = getToolModifiedState(state, Blocks.DIRT_PATH.defaultBlockState(), c.getLevel(), c.getClickedPos(), c.getPlayer(), c.getItemInHand(), BehaviourToolAction.SHOVEL_FLATTEN);
            if (changedState != null) {
                SoundEvent soundEvent = instance.getAntimatterToolType().getUseSound() == null ? SoundEvents.SHOVEL_FLATTEN : instance.getAntimatterToolType().getUseSound();
                c.getLevel().playSound(c.getPlayer(), c.getClickedPos(), soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
            }
        } else if (state.getBlock() instanceof CampfireBlock && state.getValue(CampfireBlock.LIT)) {
            changedState = getToolModifiedState(state, state.setValue(CampfireBlock.LIT, false), c.getLevel(), c.getClickedPos(), c.getPlayer(), c.getItemInHand(), BehaviourToolAction.SHOVEL_DIG);
            if (changedState != null) {
                c.getLevel().levelEvent(c.getPlayer(), 1009, c.getClickedPos(), 0);
            }
        }
        if (changedState != null) {
            c.getLevel().setBlock(c.getClickedPos(), changedState, 11);
            Utils.damageStack(c.getItemInHand(), c.getPlayer());
            return InteractionResult.SUCCESS;
        } else return InteractionResult.PASS;
    }

    private BlockState getToolModifiedState(BlockState originalState, BlockState changedState, Level world, BlockPos pos, Player player, ItemStack stack, BehaviourToolAction action) {
        BlockState eventState = BehaviourUtil.onToolUse(originalState, world, pos, player, stack, action);
        return eventState != originalState ? eventState : changedState;
    }
}
