package muramasa.gregtech.api.pipe;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.pipe.types.Pipe;
import muramasa.gregtech.loaders.GregTechRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IStringSerializable;

public class PipeStack implements IStringSerializable {

    private Pipe type;
    private PipeSize size;

    public PipeStack(Pipe type, PipeSize size) {
        this.type = type;
        this.size = size;
    }

    public PipeSize getSize() {
        return size;
    }

    @Override
    public String getName() {
        return type.getMaterial().getName() + "_" + size.getName();
    }

    public ItemStack asItemStack() {
        ItemStack stack = new ItemStack(Item.getItemFromBlock(GregTechRegistry.getPipe(type)));
        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger(Ref.KEY_PIPE_STACK_SIZE, size.ordinal());
        return stack;
    }
}
