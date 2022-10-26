package muramasa.antimatter.capability.energy;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.EnergyHandler;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import tesseract.api.TesseractCaps;
import tesseract.api.gt.IEnergyHandler;
import tesseract.api.gt.IEnergyHandlerItem;

/**
 * ItemEnergyHandler represents the Antimatter Energy capability implementation for items.
 * It wraps an item and provides the ability to charge it & remove it, depending on if the item supports it.
 */
public class ItemEnergyHandler extends EnergyHandler {

    protected boolean discharge = true;
    protected long maxEnergy;

    public ItemEnergyHandler(long capacity, int voltageIn, int voltageOut, int amperageIn, int amperageOut) {
        super(0, capacity, voltageIn, voltageOut, amperageIn, amperageOut);
        this.maxEnergy = capacity;
    }

    @Override
    public boolean canInput() {
        return voltageIn > 0 /*&& getTagEnergy() != capacity*/;
    }

    @Override
    public boolean canOutput() {
        return canDischarge() && voltageOut > 0 /*&& getTagEnergy() >= voltageOut*/;
    }

    private boolean canDischarge() {
        return discharge;
    }

    public boolean chargeModeSwitch() {
        discharge = !discharge;
        return discharge;
    }

    @Override
    public void setCapacity(long capacity) {
        this.maxEnergy = capacity;
    }

    @Override
    public void setEnergy(long energy) {
        this.energy = energy;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        nbt.putLong(Ref.KEY_ITEM_ENERGY, this.energy);
        nbt.putLong(Ref.KEY_ITEM_MAX_ENERGY, this.maxEnergy);
        nbt.putBoolean(Ref.KEY_ITEM_DISCHARGE_MODE, this.discharge);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        this.energy = nbt.contains(Ref.KEY_ITEM_ENERGY_OLD) ? nbt.getLong(Ref.KEY_ITEM_ENERGY_OLD) : nbt.getLong(Ref.KEY_ITEM_ENERGY);
        this.maxEnergy = nbt.getLong(Ref.KEY_ITEM_MAX_ENERGY);
        this.discharge = nbt.getBoolean(Ref.KEY_ITEM_DISCHARGE_MODE);
    }

    @Override
    public long getCapacity() {
        return maxEnergy;
    }


    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
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
}
