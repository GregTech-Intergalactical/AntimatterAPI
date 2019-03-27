package muramasa.gregtech.api.pipe;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.pipe.types.Pipe;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;

public class ItemPipeStack extends PipeStack {

    private boolean restrictive;

    public ItemPipeStack(Block block, Pipe type, PipeSize size, boolean restrictive) {
        super(block, type, size);
        this.restrictive = restrictive;
    }

    @Override
    public ItemStack asItemStack() {
        ItemStack stack = super.asItemStack();
        stack.getTagCompound().setBoolean(Ref.KEY_ITEM_PIPE_STACK_RESTRICTIVE, restrictive);
        return stack;
    }
}
