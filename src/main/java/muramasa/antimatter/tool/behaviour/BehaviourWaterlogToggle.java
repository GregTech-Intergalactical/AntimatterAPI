package muramasa.antimatter.tool.behaviour;

import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.util.Utils;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;

public class BehaviourWaterlogToggle implements IItemUse<IAntimatterTool> {

    public static final BehaviourWaterlogToggle INSTANCE = new BehaviourWaterlogToggle();

    @Override
    public String getId() {
        return "waterlog_toggle";
    }

    @Override
    public ActionResultType onItemUse(IAntimatterTool instance, ItemUseContext c) {
        BlockState state = c.getLevel().getBlockState(c.getClickedPos());
        if (state.hasProperty(BlockStateProperties.WATERLOGGED)) {
            if (state.getValue(BlockStateProperties.WATERLOGGED)) {
                c.getLevel().setBlock(c.getClickedPos(), state.setValue(BlockStateProperties.WATERLOGGED, false), 11);
                c.getLevel().playSound(c.getPlayer(), c.getClickedPos(), SoundEvents.BUCKET_EMPTY, SoundCategory.BLOCKS, 1.0F, 1.0F);
                Utils.damageStack(c.getItemInHand(), c.getPlayer());
                return ActionResultType.SUCCESS;
            }
        }
        return ActionResultType.PASS;
    }
}
