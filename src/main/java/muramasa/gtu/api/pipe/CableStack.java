package muramasa.gtu.api.pipe;

import muramasa.gtu.Ref;
import muramasa.gtu.api.pipe.types.Cable;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

public class CableStack extends PipeStack implements IStringSerializable {

    private boolean insulated;

    public CableStack(Block block, Cable type, PipeSize size, boolean insulated) {
        super(block, type, size);
        this.insulated = insulated;
    }

    public ItemStack asItemStack() {
        ItemStack stack = super.asItemStack();
        stack.getTagCompound().setBoolean(Ref.KEY_CABLE_STACK_INSULATED, insulated);
        return stack;
    }
}
