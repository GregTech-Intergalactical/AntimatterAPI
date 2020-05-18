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

    @Nonnull
    @Override
    public ActionResultType tryPlace(BlockItemUseContext context) {
        ActionResultType action = super.tryPlace(context);
        if (action == ActionResultType.SUCCESS) {
            if (context.replacingClickedOnBlock()) return action;
            pipe.onBlockPlacedTo(context.getWorld(), context.getPos(), context.getFace());
        }
        return action;
    }
}
