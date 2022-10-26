package muramasa.antimatter.tool.behaviour;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

public class BehaviourBlockTilling implements IItemUse<IAntimatterTool> {

    public static final BehaviourBlockTilling INSTANCE = new BehaviourBlockTilling();

    private static final Object2ObjectMap<BlockState, BlockState> TILLING_MAP = new Object2ObjectOpenHashMap<>();

    static {
        ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.FARMLAND, Blocks.DIRT_PATH, Blocks.FARMLAND, Blocks.DIRT, Blocks.FARMLAND, Blocks.COARSE_DIRT, Blocks.DIRT)
                .forEach(BehaviourBlockTilling::addStrippedBlock);
    }

    @Override
    public String getId() {
        return "block_tilling";
    }

    @Override
    public InteractionResult onItemUse(IAntimatterTool instance, UseOnContext c) {
        if (c.getClickedFace() != Direction.DOWN && c.getLevel().isEmptyBlock(c.getClickedPos().above())) {
            BlockState blockstate = getToolModifiedState(c.getLevel().getBlockState(c.getClickedPos()), c.getLevel(), c.getClickedPos(), c.getPlayer(), c.getItemInHand(), BehaviourToolAction.HOE_DIG);
            if (blockstate == null) return InteractionResult.PASS;
            if (BehaviourUtil.onUseHoe(c)) return InteractionResult.PASS;
            Utils.damageStack(c.getItemInHand(), c.getPlayer());
            SoundEvent soundEvent = instance.getAntimatterToolType().getUseSound() == null ? SoundEvents.HOE_TILL : instance.getAntimatterToolType().getUseSound();
            c.getLevel().playSound(c.getPlayer(), c.getClickedPos(), soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
            if (!c.getLevel().isClientSide) c.getLevel().setBlock(c.getClickedPos(), blockstate, 11);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private BlockState getToolModifiedState(BlockState originalState, Level world, BlockPos pos, Player player, ItemStack stack, BehaviourToolAction action) {
        BlockState eventState = BehaviourUtil.onToolUse(originalState, world, pos, player, stack, action);
        return eventState != originalState ? eventState : TILLING_MAP.get(originalState);
    }

    public static void addStrippedBlock(Block from, Block to) {
        addStrippedState(from.defaultBlockState(), to.defaultBlockState());
    }

    public static void addStrippedState(BlockState from, BlockState to) {
        TILLING_MAP.put(from, to);
    }
}
