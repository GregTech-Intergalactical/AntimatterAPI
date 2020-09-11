package muramasa.antimatter.capability;

import net.minecraft.nbt.CompoundNBT;

public interface ICapabilityHandler {

    CapabilityType getCapabilityType();

    default CompoundNBT serialize() {
        return new CompoundNBT();
    }

    default void deserialize(CompoundNBT tag) {
        // NOOP
    }
}
