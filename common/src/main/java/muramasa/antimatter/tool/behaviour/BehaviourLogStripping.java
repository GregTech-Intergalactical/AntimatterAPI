package muramasa.antimatter.tool.behaviour;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class BehaviourLogStripping implements IItemUse<IAntimatterTool> {

    public static final BehaviourLogStripping INSTANCE = new BehaviourLogStripping();

    private static final Object2ObjectOpenHashMap<Block, Block> STRIPPING_MAP = new Object2ObjectOpenHashMap<>();

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
    public InteractionResult onItemUse(IAntimatterTool instance, UseOnContext c) {
        BlockState state = c.getLevel().getBlockState(c.getClickedPos());
        BlockState stripped = getToolModifiedState(state, c.getLevel(), c.getClickedPos(), c.getPlayer(), c.getItemInHand(), "axe_strip");
        if (stripped != null) {
            if (state.hasProperty(RotatedPillarBlock.AXIS) && stripped.hasProperty(RotatedPillarBlock.AXIS)) {
                stripped = stripped.setValue(RotatedPillarBlock.AXIS, state.getValue(RotatedPillarBlock.AXIS));
            }
            c.getLevel().playSound(c.getPlayer(), c.getClickedPos(), SoundEvents.AXE_STRIP, SoundSource.BLOCKS, 1.0F, 1.0F);
            c.getLevel().setBlockAndUpdate(c.getClickedPos(), stripped);
            Utils.damageStack(c.getItemInHand(), c.getPlayer());
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private BlockState getToolModifiedState(BlockState originalState, Level world, BlockPos pos, Player player, ItemStack stack, String action) {
        BlockState eventState = BehaviourUtil.onToolUse(originalState, world, pos, player, stack, action);
        if (eventState != originalState) return eventState;
        Block stripped = STRIPPING_MAP.get(originalState.getBlock());
        if (stripped == null) return null;
        BlockState state = stripped.defaultBlockState();
        for (Property property : originalState.getProperties()) {
            if (state.hasProperty(property)){
                state = state.setValue(property, originalState.getValue(property));
            }
        }
        return state;
    }

    public static void addStrippedBlock(Block from, Block to) {
        STRIPPING_MAP.put(from, to);
    }
}
