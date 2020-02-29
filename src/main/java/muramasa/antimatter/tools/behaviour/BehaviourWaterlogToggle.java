package muramasa.antimatter.tools.behaviour;

import muramasa.antimatter.behaviour.IBehaviour;
import muramasa.antimatter.tools.base.MaterialTool;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

public class BehaviourWaterlogToggle implements IBehaviour<MaterialTool> {

    @Override
    public ActionResultType onItemUse(MaterialTool instance, ItemUseContext c) {
        BlockState state = c.getWorld().getBlockState(c.getPos());
        if (state.has(BlockStateProperties.WATERLOGGED)) {
            if (state.get(BlockStateProperties.WATERLOGGED)) {
                c.getWorld().setBlockState(c.getPos(), state.with(BlockStateProperties.WATERLOGGED, false), 11);
                c.getWorld().playSound(c.getPlayer(), c.getPos(), SoundEvents.ITEM_BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                if (c.getPlayer() != null) MaterialTool.damage(c.getPlayer().getHeldItem(c.getHand()), instance.getType().getUseDurability(), c.getPlayer(), c.getHand());
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }
}
