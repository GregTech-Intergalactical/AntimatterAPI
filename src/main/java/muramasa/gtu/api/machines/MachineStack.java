package muramasa.gtu.api.machines;

import muramasa.gtu.api.machines.types.Machine;
import muramasa.gtu.api.registration.IGregTechObject;
import net.minecraft.item.ItemStack;

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
    public String getId() {
        return type.getId() + "_" + tier.getId();
    }

    @Override
    public ItemStack asItemStack() {
        return new ItemStack(type.getBlock(), 1, tier.getInternalId());
    }
}
