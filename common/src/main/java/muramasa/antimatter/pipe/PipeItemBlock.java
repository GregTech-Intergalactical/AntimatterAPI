package muramasa.antimatter.pipe;

import muramasa.antimatter.block.AntimatterItemBlock;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.context.BlockPlaceContext;

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
    public InteractionResult place(BlockPlaceContext context) {
        InteractionResult action = super.place(context);
        if (action.consumesAction()) {
            if (context.replacingClickedOnBlock()) return action;
            if (pipe.onBlockPlacedTo(context.getLevel(), context.getClickedPos(), context.getClickedFace())) {
                return InteractionResult.SUCCESS;
            }
        }
        return action;
    }
}
