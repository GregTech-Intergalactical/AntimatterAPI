package muramasa.antimatter.capability.forge.energy;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import tesseract.api.TesseractCaps;
import tesseract.api.gt.IEnergyHandler;

public class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    private final LazyOptional<IEnergyHandler> energy;

    public Provider(NonNullSupplier<IEnergyHandler> cap) {
        this.energy = LazyOptional.of(cap);
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return cap == TesseractCaps.getENERGY_HANDLER_CAPABILITY() ? energy.cast() : LazyOptional.empty();
    }

    @Override
    public CompoundTag serializeNBT() {
        return energy.map(INBTSerializable::serializeNBT).orElse(new CompoundTag());
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        energy.ifPresent(t -> t.deserializeNBT(nbt));
    }

}
