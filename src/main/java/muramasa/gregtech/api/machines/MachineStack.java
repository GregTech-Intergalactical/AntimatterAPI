package muramasa.gregtech.api.machines;

import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IStringSerializable;

public class MachineStack implements IStringSerializable {

    private Machine type;
    private Tier tier;

    public MachineStack(Machine type, Tier tier) {
        this.type = type;
        this.tier = tier;
    }

    public Machine getType() {
        return type;
    }

    public Tier getTier() {
        return tier;
    }

    @Override
    public String getName() {
        return type.getName() + "_" + tier.getName();
    }

    public ItemStack asItemStack() {
        if (type == null || tier == null)  {
            System.out.println(type + " - " + tier);
            return ItemStack.EMPTY;
        }
        ItemStack stack = new ItemStack(Item.getItemFromBlock(type.getBlock()), 1, 0);
        stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound data = new NBTTagCompound();
        data.setString(Ref.KEY_MACHINE_STACK_TIER, tier.getName());
        stack.getTagCompound().setTag(Ref.TAG_MACHINE_STACK_DATA, data);
        return stack;
    }
}
