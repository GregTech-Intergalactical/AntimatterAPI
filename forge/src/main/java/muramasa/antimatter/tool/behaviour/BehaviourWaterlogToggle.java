package muramasa.antimatter.tool.behaviour;

import muramasa.antimatter.behaviour.IItemUse;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class BehaviourWaterlogToggle implements IItemUse<IAntimatterTool> {

    public static final BehaviourWaterlogToggle INSTANCE = new BehaviourWaterlogToggle();

    @Override
    public String getId() {
        return "waterlog_toggle";
    }

    @Override
    public InteractionResult onItemUse(IAntimatterTool instance, UseOnContext c) {
        BlockState state = c.getLevel().getBlockState(c.getClickedPos());
        if (state.hasProperty(BlockStateProperties.WATERLOGGED)) {
            if (state.getValue(BlockStateProperties.WATERLOGGED)) {
                c.getLevel().setBlock(c.getClickedPos(), state.setValue(BlockStateProperties.WATERLOGGED, false), 11);
                c.getLevel().playSound(c.getPlayer(), c.getClickedPos(), SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                Utils.damageStack(c.getItemInHand(), c.getPlayer());
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }
}
