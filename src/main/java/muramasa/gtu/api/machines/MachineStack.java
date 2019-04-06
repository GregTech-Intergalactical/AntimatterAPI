package muramasa.gtu.api.machines;

import muramasa.gtu.api.machines.types.Machine;
import muramasa.gtu.Ref;
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
        ItemStack stack = new ItemStack(Item.getItemFromBlock(type.getBlock()));
        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setString(Ref.KEY_MACHINE_STACK_TIER, tier.getName());
        return stack;
    }
}
