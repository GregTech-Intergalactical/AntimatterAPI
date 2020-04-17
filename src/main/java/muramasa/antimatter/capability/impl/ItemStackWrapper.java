package muramasa.antimatter.capability.impl;

import org.apache.commons.lang3.tuple.Pair;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;

abstract public class ItemStackWrapper extends ItemStackHandler {

    public ItemStackWrapper(int size) {
        super(size);
    }

    public int getFirstEmptySlot() {
        for (int i = 0; i < getSlots(); i++) {
            if (getStackInSlot(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    public int getFirstValidSlot() {
        for (int i = 0; i < getSlots(); i++) {
            if (!getStackInSlot(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    @Nullable
    public Pair<Integer, ItemStack> findItemInSlots(Item item) {
        for (int i = 0; i < getSlots(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem().equals(item) && stack.getMaxStackSize() > stack.getCount()) {
                return Pair.of(i, stack);
            }
        }
        return null;
    }

    public int setFirstEmptyOrValidSlot(ItemStack item) {
        int slot = getFirstEmptySlot();
        if (slot != -1) slot = getFirstValidSlot();
        if (slot != -1) {
            setStackInSlot(slot, item);
            return item.getCount();
        }
        return 0;
    }
}
