package muramasa.antimatter.capability.energy;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.EnergyHandler;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullSupplier;
import tesseract.api.capability.TesseractGTCapability;
import tesseract.api.gt.IEnergyHandler;

/**
 * ItemEnergyHandler represents the Antimatter Energy capability implementation for items.
 * It wraps an item and provides the ability to charge it & remove it, depending on if the item supports it.
 */
public class ItemEnergyHandler extends EnergyHandler {

    protected boolean discharge = true;

    public ItemEnergyHandler(long capacity, int voltageIn, int voltageOut, int amperageIn, int amperageOut) {
        super(0, capacity, voltageIn, voltageOut, amperageIn, amperageOut);
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

    public void setEnergy(long energy) {
        this.energy = energy;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putLong(Ref.KEY_ITEM_ENERGY, this.energy);
        nbt.putBoolean(Ref.KEY_ITEM_DISCHARGE_MODE, this.discharge);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        this.energy = nbt.getLong(Ref.KEY_ITEM_ENERGY);
        this.discharge = nbt.getBoolean(Ref.KEY_ITEM_DISCHARGE_MODE);
    }


    public static class Provider implements ICapabilityProvider, INBTSerializable<CompoundNBT> {
        private final LazyOptional<IEnergyHandler> energy;

        public Provider(NonNullSupplier<IEnergyHandler> cap) {
            this.energy = LazyOptional.of(cap);
        }

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
            return cap == TesseractGTCapability.ENERGY_HANDLER_CAPABILITY ? energy.cast() : LazyOptional.empty();
        }

        @Override
        public CompoundNBT serializeNBT() {
            return energy.map(INBTSerializable::serializeNBT).orElse(new CompoundNBT());
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) {
            energy.ifPresent(t -> t.deserializeNBT(nbt));
        }

    }
}
