package muramasa.gtu.api.pipe;

import muramasa.gtu.Ref;
import muramasa.gtu.api.blocks.pipe.BlockItemPipe;
import net.minecraft.item.ItemStack;

public class ItemPipeStack extends PipeStack {

    private boolean restrictive;

    public ItemPipeStack(BlockItemPipe block, PipeSize size, boolean restrictive) {
        super(block, size);
        this.restrictive = restrictive;
    }

    @Override
    public ItemStack asItemStack() {
        ItemStack stack = super.asItemStack();
        stack.getTagCompound().setBoolean(Ref.KEY_ITEM_PIPE_STACK_RESTRICTIVE, restrictive);
        return stack;
    }
}
