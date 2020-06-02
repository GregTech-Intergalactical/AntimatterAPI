package muramasa.antimatter.capability.impl;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemEnergyHandler extends EnergyHandler {
    protected ItemStack stack;
    public static final String TAG_CHARGE = "charge";

    public ItemEnergyHandler(ItemStack stack, long energy, long capacity, int voltage_in, int voltage_out, int amperage_in, int amperage_out) {
        super(energy, capacity, voltage_in, voltage_out, amperage_in, amperage_out);
        this.stack = stack;
    }

    private void setTagEnergy(long energy) {
        stack.getTag().putLong(TAG_CHARGE, energy);
    }

    private long getTagEnergy() {
        return stack.getTag().getLong(TAG_CHARGE);
    }

    @Override
    public long insert(long maxReceive, boolean simulate) {
        if (simulate) {
            return maxReceive;
        }
        setTagEnergy(getTagEnergy()+maxReceive);
        return getTagEnergy()+maxReceive;
    }

    @Override
    public long extract(long maxExtract, boolean simulate) {
        if (simulate) {
            return maxExtract;
        }
        stack.getTag().putLong(TAG_CHARGE,getTagEnergy()-maxExtract);
        return maxExtract;
    }
}
