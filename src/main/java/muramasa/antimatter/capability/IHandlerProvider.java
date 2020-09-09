package muramasa.antimatter.capability;

import net.minecraft.nbt.CompoundNBT;

public interface IHandlerProvider {

    void update(CompoundNBT tag);

    CompoundNBT getCapabilityTag(String cap);
}
