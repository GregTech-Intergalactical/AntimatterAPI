package muramasa.gtu.api.pipe;

import muramasa.gtu.Ref;
import muramasa.gtu.api.blocks.pipe.BlockCable;
import net.minecraft.item.ItemStack;

public class CableStack extends PipeStack {

    private boolean insulated;

    public CableStack(BlockCable block, PipeSize size, boolean insulated) {
        super(block, size);
        this.insulated = insulated;
    }

    @Override
    public ItemStack asItemStack() {
        ItemStack stack = super.asItemStack();
        stack.getTagCompound().setBoolean(Ref.KEY_CABLE_STACK_INSULATED, insulated);
        return stack;
    }
}
