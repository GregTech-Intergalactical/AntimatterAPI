package muramasa.gregtech.api.machines;

import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.machines.types.Machine;
import muramasa.gregtech.common.utils.Ref;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class MachineStack {

    private String type, tier;

    public MachineStack(Machine type, Tier tier) {
        this.type = type.getName();
        this.tier = tier.getName();
    }

    public Machine getMachineType() {
        return Machines.get(type);
    }

    public String getType() {
        return type;
    }

    public String getTier() {
        return tier;
    }

    public ItemStack asItemStack() {
        if (type == null || tier == null)  {
            System.out.println(type + " - " + tier);
            return ItemStack.EMPTY;
        }
        ItemStack stack = new ItemStack(Item.getItemFromBlock(getMachineType().getBlock()), 1, 0);
        stack.setTagCompound(new NBTTagCompound());
        NBTTagCompound data = new NBTTagCompound();
        data.setString(Ref.KEY_MACHINE_STACK_TYPE, type);
        data.setString(Ref.KEY_MACHINE_STACK_TIER, tier);
        stack.getTagCompound().setTag(Ref.TAG_MACHINE_STACK_DATA, data);
        return stack;
    }
}
