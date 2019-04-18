package muramasa.gtu.api.machines;

import muramasa.gtu.api.interfaces.IGregTechObject;
import muramasa.gtu.api.machines.types.Machine;
import muramasa.gtu.Ref;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class MachineStack implements IGregTechObject {

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

    @Override
    public ItemStack asItemStack() {
        ItemStack stack = new ItemStack(Item.getItemFromBlock(type.getBlock()));
        stack.setTagCompound(new NBTTagCompound());
        stack.getTagCompound().setString(Ref.KEY_MACHINE_STACK_TIER, tier.getName());
        return stack;
    }
}
