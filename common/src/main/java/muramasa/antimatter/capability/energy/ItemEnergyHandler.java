package muramasa.antimatter.capability.energy;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.EnergyHandler;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;
import tesseract.api.context.TesseractItemContext;
import tesseract.api.gt.GTTransaction;
import tesseract.api.gt.IEnergyHandlerItem;

/**
 * ItemEnergyHandler represents the Antimatter Energy capability implementation for items.
 * It wraps an item and provides the ability to charge it & remove it, depending on if the item supports it.
 */
public class ItemEnergyHandler extends EnergyHandler implements IEnergyHandlerItem {

    protected long maxEnergy;

    protected TesseractItemContext context;

    public ItemEnergyHandler(TesseractItemContext context, long capacity, long voltageIn, long voltageOut, int amperageIn, int amperageOut) {
        super(0, capacity, voltageIn, voltageOut, amperageIn, amperageOut);
        this.maxEnergy = capacity;
        this.context = context;
        if (AntimatterPlatformUtils.isFabric()) readFromContextOnFabric();
    }

    private void readFromContextOnFabric(){
        CompoundTag nbt = this.context.getTag();
        if (!nbt.contains(Ref.TAG_ITEM_ENERGY_DATA)) return;
        CompoundTag energyTag = nbt.getCompound(Ref.TAG_ITEM_ENERGY_DATA);
        if (energyTag.contains(Ref.KEY_ITEM_ENERGY)) this.energy = energyTag.getLong(Ref.KEY_ITEM_ENERGY);
        if (energyTag.contains(Ref.KEY_ITEM_MAX_ENERGY)) this.energy = energyTag.getLong(Ref.KEY_ITEM_MAX_ENERGY);
    }

    @Override
    public long getEnergy() {
        CompoundTag nbt = this.context.getTag();
        if (!nbt.contains(Ref.TAG_ITEM_ENERGY_DATA)) return 0;
        CompoundTag energyTag = nbt.getCompound(Ref.TAG_ITEM_ENERGY_DATA);
        return energyTag.getLong(Ref.KEY_ITEM_ENERGY);
    }

    @Override
    public long getCapacity() {
        CompoundTag nbt = this.context.getTag();
        if (!nbt.contains(Ref.TAG_ITEM_ENERGY_DATA)) return capacity;
        CompoundTag energyTag = nbt.getCompound(Ref.TAG_ITEM_ENERGY_DATA);
        if (!energyTag.contains(Ref.KEY_ITEM_MAX_ENERGY)) return capacity;
        return energyTag.getLong(Ref.KEY_ITEM_MAX_ENERGY);
    }

    @Override
    public boolean canInput() {
        return voltageIn > 0 /*&& getTagEnergy() != capacity*/;
    }

    @Override
    public boolean canOutput() {
        return /*canDischarge() &&*/ voltageOut > 0 /*&& getTagEnergy() >= voltageOut*/;
    }

    private boolean canDischarge() {
        CompoundTag nbt = this.context.getTag();
        if (!nbt.contains(Ref.TAG_ITEM_ENERGY_DATA)) return true;
        CompoundTag energyTag = nbt.getCompound(Ref.TAG_ITEM_ENERGY_DATA);
        if (!energyTag.contains(Ref.KEY_ITEM_DISCHARGE_MODE)) return true;
        return energyTag.getBoolean(Ref.KEY_ITEM_DISCHARGE_MODE);
    }

    @Override
    public void setEnergy(long energy) {
        this.energy = energy;
        CompoundTag energyTag = getContainer().getOrCreateTagElement(Ref.TAG_ITEM_ENERGY_DATA);
        energyTag.putLong(Ref.KEY_ITEM_ENERGY, energy);
    }

    @Override
    public void setCapacity(long capacity) {
        this.maxEnergy = capacity;
        CompoundTag energyTag = getContainer().getOrCreateTagElement(Ref.TAG_ITEM_ENERGY_DATA);
        energyTag.putLong(Ref.KEY_ITEM_MAX_ENERGY, capacity);
    }

    @Override
    public long insertEu(long voltage, boolean simulate) {
        long toAdd = Math.min(voltage, this.getCapacity() - this.getEnergy());
        if (!simulate) this.setEnergy(getEnergy() + toAdd);
        return toAdd;
    }

    @Override
    public long extractEu(long voltage, boolean simulate) {
        long toDrain = Math.min(voltage, this.getEnergy());
        if (!simulate) this.setEnergy(this.getEnergy() - toDrain);
        return toDrain;
    }

    @Override
    public @NotNull TesseractItemContext getContainer() {
        return context;
    }

    @Override
    public CompoundTag serialize(CompoundTag nbt) {
        nbt.putLong(Ref.KEY_ITEM_ENERGY, this.energy);
        nbt.putLong(Ref.KEY_ITEM_MAX_ENERGY, this.maxEnergy);
        return nbt;
    }

    @Override
    public void deserialize(CompoundTag nbt) {
        this.energy = nbt.contains(Ref.KEY_ITEM_ENERGY_OLD) ? nbt.getLong(Ref.KEY_ITEM_ENERGY_OLD) : nbt.getLong(Ref.KEY_ITEM_ENERGY);
        this.maxEnergy = nbt.getLong(Ref.KEY_ITEM_MAX_ENERGY);
        //context.getOrCreateTagElement(Ref.TAG_ITEM_ENERGY_DATA).merge(nbt);
    }
}
