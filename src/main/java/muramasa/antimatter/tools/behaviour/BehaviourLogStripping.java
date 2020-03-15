package muramasa.antimatter.tools.behaviour;

import com.google.common.collect.ImmutableMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import muramasa.antimatter.behaviour.IBehaviour;
import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.tools.base.MaterialTool;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RotatedPillarBlock;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

public class BehaviourLogStripping implements IItemUse<MaterialTool> {

    public static final Object2ObjectOpenHashMap<BlockState, BlockState> STRIPPING_MAP = new Object2ObjectOpenHashMap<>();

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
    public ActionResultType onItemUse(MaterialTool instance, ItemUseContext c) {
        BlockState state = c.getWorld().getBlockState(c.getPos());
        BlockState stripped = STRIPPING_MAP.get(state);
        if (stripped != null) {
            if (state.has(RotatedPillarBlock.AXIS) && stripped.has(RotatedPillarBlock.AXIS)) {
                stripped = stripped.with(RotatedPillarBlock.AXIS, state.get(RotatedPillarBlock.AXIS));
            }
            c.getWorld().playSound(c.getPlayer(), c.getPos(), SoundEvents.ITEM_AXE_STRIP, SoundCategory.BLOCKS, 1.0F, 1.0F);
            c.getWorld().setBlockState(c.getPos(), stripped);
            if (c.getPlayer() != null) c.getPlayer().getHeldItem(c.getHand()).damageItem(instance.getType().getUseDurability(), c.getPlayer(), (onBroken) -> onBroken.sendBreakAnimation(c.getHand()));
            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    public static void addStrippedBlock(Block from, Block to) {
        addStrippedState(from.getDefaultState(), to.getDefaultState());
    }

    public static void addStrippedState(BlockState from, BlockState to) {
        STRIPPING_MAP.put(from, to);
    }
}
