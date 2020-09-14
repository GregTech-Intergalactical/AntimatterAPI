package muramasa.antimatter.capability;

import net.minecraft.nbt.CompoundNBT;

public interface ICapabilityHost {

    void update(CompoundNBT tag);

    CompoundNBT getCapabilityTag(CapabilityType cap);
}
