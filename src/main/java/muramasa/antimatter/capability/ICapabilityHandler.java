package muramasa.antimatter.capability;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.Capability;

public interface ICapabilityHandler {

    Capability<?> getCapability();

    default CompoundNBT serialize() {
        return new CompoundNBT();
    }

    default void deserialize(CompoundNBT tag) {
        // NOOP
    }
}
