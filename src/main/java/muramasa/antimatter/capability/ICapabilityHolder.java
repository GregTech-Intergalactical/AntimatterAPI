package muramasa.antimatter.capability;

import net.minecraft.nbt.CompoundNBT;

public interface ICapabilityHolder {

    void update(CompoundNBT tag);

    ICapabilityHandler getCapability(String name);
}
