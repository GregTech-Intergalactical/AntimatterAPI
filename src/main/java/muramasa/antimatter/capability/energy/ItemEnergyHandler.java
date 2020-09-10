package muramasa.antimatter.capability.energy;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.AntimatterCaps;
import muramasa.antimatter.capability.EnergyHandler;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * ItemEnergyHandler represents the Antimatter Energy capability implementation for items.
 * It wraps an item and provides the ability to charge it & remove it, depending on if the item supports it.
 */
public class ItemEnergyHandler extends EnergyHandler implements ICapabilityProvider {
    protected ItemStack stack;

    public ItemEnergyHandler(ItemStack stack, long energy, long capacity, int voltage_in, int voltage_out, int amperage_in, int amperage_out) {
        super(energy, capacity, voltage_in, voltage_out, amperage_in, amperage_out);
        this.stack = stack;
    }

    public static CompoundNBT initNBT(CompoundNBT tag) {
        if (tag == null) {
            tag = new CompoundNBT();
        }
        if (!tag.contains(Ref.TAG_ITEM_CHARGE)) {
            tag.putLong(Ref.TAG_ITEM_CHARGE, 0);
        }
        return tag;
    }

    //Override the following methods to use getTagEnergy instead
    @Override
    public boolean canInput() {
        return voltage_in > 0 /*&& getTagEnergy() != capacity*/;
    }

    @Override
    public boolean canOutput() {
        return !canModeBlock() && voltage_out > 0 /*&& getTagEnergy() >= voltage_out*/;
    }

    /**
     * Returns whether the item can block output.
     * @return false as default non-blocking, true on block discharge.
     */
    private boolean canModeBlock() {
        CompoundNBT tag = stack.getTag();
        return tag != null && tag.getBoolean(Ref.TAG_ITEM_MODE);
    }

    @Override
    public long getEnergy() {
        return getTagEnergy();
    }

    private void setTagEnergy(long energy) {
        this.energy = energy;
        setStackEnergy(stack, energy);
    }

    private long getTagEnergy() {
        return getEnergyFromStack(stack);
    }

    /**
     * Sets the energy value of an item.
     * @param stack the stack to set the energy of.
     * @param energy the fix energy value.
     * @return energy parameter
     */
    public static long setStackEnergy(ItemStack stack, long energy) {
        CompoundNBT tag = stack.getOrCreateTag();
        tag.putLong(Ref.TAG_ITEM_CHARGE, energy);
        return energy;
    }

    public static long getEnergyFromStack(ItemStack stack) {
        CompoundNBT tag = stack.getOrCreateTag();
        return tag.getLong(Ref.TAG_ITEM_CHARGE);
    }

    @Override
    public long insert(long maxReceive, boolean simulate) {
        this.energy = getTagEnergy();
        long toInsert = Math.max(Math.min(capacity - energy, maxReceive), 0);
        if (simulate) {
            return toInsert;
        }
        this.energy += toInsert;
        setTagEnergy(this.energy);
        return toInsert;
    }

    @Override
    public long extract(long maxExtract, boolean simulate) {
        this.energy = getTagEnergy();
        long toExtract = Math.max(Math.min(energy, maxExtract), 0);
        if (simulate) {
            return toExtract;
        }
        this.energy -= toExtract;
        setStackEnergy(stack, energy);
        return toExtract;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == AntimatterCaps.ENERGY_HANDLER_CAPABILITY) {
            return LazyOptional.of(() -> this).cast();
        }
        return LazyOptional.empty();
    }
}
