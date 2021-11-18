package muramasa.antimatter.tool.behaviour;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.UseHoeEvent;

public class BehaviourBlockTilling implements IItemUse<IAntimatterTool> {

    public static final BehaviourBlockTilling INSTANCE = new BehaviourBlockTilling();

    private static final Object2ObjectMap<BlockState, BlockState> TILLING_MAP = new Object2ObjectOpenHashMap<>();

    static {
        ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.FARMLAND, Blocks.GRASS_PATH, Blocks.FARMLAND, Blocks.DIRT, Blocks.FARMLAND, Blocks.COARSE_DIRT, Blocks.DIRT)
                .forEach(BehaviourBlockTilling::addStrippedBlock);
    }

    @Override
    public String getId() {
        return "block_tilling";
    }

    @Override
    public ActionResultType onItemUse(IAntimatterTool instance, ItemUseContext c) {
        if (c.getClickedFace() != Direction.DOWN && c.getLevel().isEmptyBlock(c.getClickedPos().above())) {
            BlockState blockstate = getToolModifiedState(c.getLevel().getBlockState(c.getClickedPos()), c.getLevel(), c.getClickedPos(), c.getPlayer(), c.getItemInHand(), ToolType.HOE);
            if (blockstate == null) return ActionResultType.PASS;
            UseHoeEvent hoeEvent = new UseHoeEvent(c);
            if (MinecraftForge.EVENT_BUS.post(hoeEvent)) return ActionResultType.PASS;
            Utils.damageStack(c.getItemInHand(), c.getPlayer());
            SoundEvent soundEvent = instance.getAntimatterToolType().getUseSound() == null ? SoundEvents.HOE_TILL : instance.getAntimatterToolType().getUseSound();
            c.getLevel().playSound(c.getPlayer(), c.getClickedPos(), soundEvent, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!c.getLevel().isClientSide) c.getLevel().setBlock(c.getClickedPos(), blockstate, 11);
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    private BlockState getToolModifiedState(BlockState originalState, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
        BlockState eventState = ForgeEventFactory.onToolUse(originalState, world, pos, player, stack, toolType);
        return eventState != originalState ? eventState : TILLING_MAP.get(originalState);
    }

    public static void addStrippedBlock(Block from, Block to) {
        addStrippedState(from.defaultBlockState(), to.defaultBlockState());
    }

    public static void addStrippedState(BlockState from, BlockState to) {
        TILLING_MAP.put(from, to);
    }
}
