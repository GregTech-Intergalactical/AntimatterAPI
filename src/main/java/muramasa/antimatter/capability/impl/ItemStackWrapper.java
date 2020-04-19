package muramasa.antimatter.capability.impl;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import tesseract.api.item.ItemData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemStackWrapper extends ItemStackHandler {

    public ItemStackWrapper(int size) {
        super(size);
    }

    public int getFirstEmptySlot() {
        for (int i = 0; i < getSlots(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (stack.isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    @Nullable
    public ItemData findItemInSlots(@Nonnull ItemStack resource) {
        Item item = resource.getItem();
        for (int i = 0; i < getSlots(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (!stack.isEmpty() && stack.getItem().equals(item) && stack.getMaxStackSize() > stack.getCount()) {
                return new ItemData(i, stack);
            }
        }
        return null;
    }

    @Nonnull
    public int[] getAvailableSlots() {
        int[] slots = new int[getSlots()]; int count = 0;
        for (int i = 0; i < getSlots(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (!stack.isEmpty()) {
                slots[count++] = i;
            }
        }
        return slots;
    }

    public int setFirstEmptySlot(@Nonnull ItemStack resource) {
        int slot = getFirstEmptySlot();
        if (slot != -1) {
            setStackInSlot(slot, resource);
            return resource.getCount();
        }
        return 0;
    }
}
