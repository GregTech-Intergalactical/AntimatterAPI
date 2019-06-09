package muramasa.gtu.api.pipe;

import muramasa.gtu.Ref;
import muramasa.gtu.api.blocks.pipe.BlockPipe;
import muramasa.gtu.api.registration.IGregTechObject;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class PipeStack implements IGregTechObject {

    private BlockPipe block;
    private PipeSize size;

    public PipeStack(BlockPipe block, PipeSize size) {
        this.block = block;
        this.size = size;
    }

    public PipeSize getSize() {
        return size;
    }

    @Override
    public String getId() {
        return block.getId() + "_" + size.getName();
    }

    @Override
    public ItemStack asItemStack() {
        ItemStack stack = new ItemStack(block);
        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger(Ref.KEY_PIPE_STACK_SIZE, size.ordinal());
        return stack;
    }
}
