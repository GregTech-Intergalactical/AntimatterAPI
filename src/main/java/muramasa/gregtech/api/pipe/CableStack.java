package muramasa.gregtech.api.pipe;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.pipe.types.Cable;
import muramasa.gregtech.loaders.GregTechRegistry;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IStringSerializable;

public class CableStack implements IStringSerializable {

    private Cable type;
    private PipeSize size;
    private boolean insulated;

    public CableStack(Cable type, PipeSize size, boolean insulated) {
        this.type = type;
        this.size = size;
        this.insulated = insulated;
    }

    public PipeSize getSize() {
        return size;
    }

    @Override
    public String getName() {
        return type.getName() + "_" + size.getName();
    }

    public ItemStack asItemStack() {
        ItemStack stack = new ItemStack(Item.getItemFromBlock(GregTechRegistry.getCable(type)));
        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setInteger(Ref.KEY_PIPE_STACK_SIZE, size.ordinal());
        stack.getTagCompound().setBoolean(Ref.KEY_CABLE_STACK_INSULATED, insulated);
        return stack;
    }
}
