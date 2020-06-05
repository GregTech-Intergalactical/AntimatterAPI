package muramasa.antimatter.capability.impl;

import muramasa.antimatter.capability.AntimatterCaps;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemEnergyHandler extends EnergyHandler implements ICapabilityProvider {
    protected ItemStack stack;

    public static String TAG_CHARGE = "c";

    public ItemEnergyHandler(ItemStack stack, long energy, long capacity, int voltage_in, int voltage_out, int amperage_in, int amperage_out, boolean canInput) {
        super(energy, capacity, voltage_in, voltage_out, amperage_in, amperage_out);
        this.stack = stack;
        if (!canInput) {
            this.voltage_in = 0;
        }
    }

    public static CompoundNBT initNBT(CompoundNBT nbt) {
        if (nbt == null) {
            nbt = new CompoundNBT();
        }
        nbt.putLong(TAG_CHARGE,0);
        return nbt;
    }

    private void setTagEnergy(long energy) {
        this.energy = energy;
        setStackEnergy(stack, energy);
    }

    private long getTagEnergy() {
        return getEnergyFromStack(stack);
    }

    public static long setStackEnergy(ItemStack stack, long energy) {
        CompoundNBT nbt = stack.getOrCreateTag();
        nbt.putLong(TAG_CHARGE, energy);
        return energy;
    }

    public static long getEnergyFromStack(ItemStack stack) {
        CompoundNBT nbt = stack.getOrCreateTag();
        return nbt.getLong(TAG_CHARGE);
    }

    @Override
    public long insert(long maxReceive, boolean simulate) {
        if (energy == 0) {
            energy = getTagEnergy();
        }
        long toInsert =  Math.max(Math.min(capacity-energy, maxReceive),0);
        if (simulate) {
            return toInsert;
        }
        setTagEnergy(toInsert + getTagEnergy());
        return toInsert;
    }

    @Override
    public long extract(long maxExtract, boolean simulate) {
        if (energy == 0) {
            energy = getTagEnergy();
        }
        long toExtract = Math.max(Math.min(energy, maxExtract),0);
        if (simulate) {
            return toExtract;
        }
        setStackEnergy(stack, getTagEnergy()-toExtract);
        return toExtract;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == AntimatterCaps.ENERGY) {
            return LazyOptional.of(() -> this).cast();
        }
        return null;
    }
}
