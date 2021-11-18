package muramasa.antimatter.tool.behaviour;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.event.ForgeEventFactory;

public class BehaviourLogStripping implements IItemUse<IAntimatterTool> {

    public static final BehaviourLogStripping INSTANCE = new BehaviourLogStripping();

    private static final Object2ObjectOpenHashMap<BlockState, BlockState> STRIPPING_MAP = new Object2ObjectOpenHashMap<>();

    static {
        new ImmutableMap.Builder<Block, Block>().put(Blocks.OAK_WOOD, Blocks.STRIPPED_OAK_WOOD).put(Blocks.OAK_LOG, Blocks.STRIPPED_OAK_LOG).put(Blocks.DARK_OAK_WOOD, Blocks.STRIPPED_DARK_OAK_WOOD)
                .put(Blocks.DARK_OAK_LOG, Blocks.STRIPPED_DARK_OAK_LOG).put(Blocks.ACACIA_WOOD, Blocks.STRIPPED_ACACIA_WOOD).put(Blocks.ACACIA_LOG, Blocks.STRIPPED_ACACIA_LOG)
                .put(Blocks.BIRCH_WOOD, Blocks.STRIPPED_BIRCH_WOOD).put(Blocks.BIRCH_LOG, Blocks.STRIPPED_BIRCH_LOG).put(Blocks.JUNGLE_WOOD, Blocks.STRIPPED_JUNGLE_WOOD)
                .put(Blocks.JUNGLE_LOG, Blocks.STRIPPED_JUNGLE_LOG).put(Blocks.SPRUCE_WOOD, Blocks.STRIPPED_SPRUCE_WOOD).put(Blocks.SPRUCE_LOG, Blocks.STRIPPED_SPRUCE_LOG)
                .build().forEach(BehaviourLogStripping::addStrippedBlock);
    }

    @Override
    public String getId() {
        return "log_stripping";
    }

    @Override
    public ActionResultType onItemUse(IAntimatterTool instance, ItemUseContext c) {
        BlockState state = c.getLevel().getBlockState(c.getClickedPos());
        BlockState stripped = getToolModifiedState(state, c.getLevel(), c.getClickedPos(), c.getPlayer(), c.getItemInHand(), ToolType.AXE);
        if (stripped != null) {
            if (state.hasProperty(RotatedPillarBlock.AXIS) && stripped.hasProperty(RotatedPillarBlock.AXIS)) {
                stripped = stripped.setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
            }
            c.getLevel().playSound(c.getPlayer(), c.getClickedPos(), SoundEvents.AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            c.getLevel().setBlockAndUpdate(c.getClickedPos(), stripped);
            Utils.damageStack(c.getItemInHand(), c.getPlayer());
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    private BlockState getToolModifiedState(BlockState originalState, World world, BlockPos pos, PlayerEntity player, ItemStack stack, ToolType toolType) {
        BlockState eventState = ForgeEventFactory.onToolUse(originalState, world, pos, player, stack, toolType);
        return eventState != originalState ? eventState : STRIPPING_MAP.get(originalState);
    }

    public static void addStrippedBlock(Block from, Block to) {
        addStrippedState(from.defaultBlockState(), to.defaultBlockState());
    }

    public static void addStrippedState(BlockState from, BlockState to) {
        STRIPPING_MAP.put(from, to);
    }
}
