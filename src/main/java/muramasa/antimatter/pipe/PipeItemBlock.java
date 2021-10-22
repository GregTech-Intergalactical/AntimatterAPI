package muramasa.antimatter.pipe;

import muramasa.antimatter.block.AntimatterItemBlock;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.ActionResultType;

import javax.annotation.Nonnull;

public class PipeItemBlock extends AntimatterItemBlock {

    private final BlockPipe<?> pipe;

    public PipeItemBlock(BlockPipe block) {
        super(block);
        this.pipe = block;
    }

    public BlockPipe<?> getPipe() {
        return pipe;
    }

    @Nonnull
    @Override
    public ActionResultType tryPlace(BlockItemUseContext context) {
        ActionResultType action = super.tryPlace(context);
        if (action.isSuccessOrConsume()) {
            if (context.replacingClickedOnBlock()) return action;
            if (pipe.onBlockPlacedTo(context.getWorld(), context.getPos(), context.getFace())) {
                return ActionResultType.SUCCESS;
            }
        }
        return action;
    }
}
