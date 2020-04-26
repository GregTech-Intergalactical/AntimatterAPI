package muramasa.antimatter.capability.impl;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tesseract.api.item.ItemData;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class ItemStackWrapper implements IItemHandler, IItemHandlerModifiable {

    private ItemStackHandler handler;
    private Map<Dir, Set<?>> filter = new EnumMap<>(Dir.class);

    public ItemStackWrapper(TileEntityMachine machine, int size, ContentEvent event) {
        handler = new ItemStackHandler(size) {
            @Override
            protected void onContentsChanged(int slot) {
                machine.onMachineEvent(event, slot);
            }
        };

        for (Dir direction : Dir.VALUES) {
            filter.put(direction, new ObjectOpenHashSet<>(size));
        }
    }

    public int getFirstEmptySlot() {
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty()) {
                return i;
            }
        }
        return -1;
    }

    @Nullable
    public ItemData findItemInSlots(@Nonnull ItemStack stack) {
        Item item = stack.getItem();
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack resource = handler.getStackInSlot(i);
            if (!resource.isEmpty() && resource.getItem().equals(item) && resource.getMaxStackSize() > resource.getCount()) {
                return new ItemData(i, resource, resource.getItem());
            }
        }
        return null;
    }

    @Nullable
    public ItemData findItemInSlots(@Nonnull Object item) {
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack resource = handler.getStackInSlot(i);
            if (!resource.isEmpty() && resource.getItem().equals(item) && resource.getMaxStackSize() > resource.getCount()) {
                return new ItemData(i, resource, resource.getItem());
            }
        }
        return null;
    }

    @Nonnull
    public IntList getAvailableSlots() {
        int size = handler.getSlots();
        IntList slots = new IntArrayList(size);
        for (int i = 0; i < size; i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty()) {
                slots.add(i);
            }
        }
        return slots;
    }

    @Nonnull
    public IntList getAvailableSlots(@Nonnull Dir direction) {
        Set<?> filtered = filter.get(direction);
        if (filtered.isEmpty()) return getAvailableSlots();
        int size = handler.getSlots();
        IntList slots = new IntArrayList(size);
        for (int i = 0; i < size; i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (!stack.isEmpty() && filtered.contains(stack.getItem())) {
                slots.add(i);
            }
        }
        return slots;
    }

    public int setFirstEmptySlot(@Nonnull ItemStack stack) {
        int slot = getFirstEmptySlot();
        if (slot != -1) {
            handler.setStackInSlot(slot, stack);
            return stack.getCount();
        }
        return 0;
    }

    @Override
    public int getSlots() {
        return handler.getSlots();
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        return handler.getStackInSlot(slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return handler.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return handler.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot) {
        return handler.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return handler.isItemValid(slot, stack);
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        handler.setStackInSlot(slot, stack);
    }

    public void setSize(int size) {
        handler.setSize(size);
    }

    public boolean isItemAvailable(@Nonnull Object item, @Nonnull Dir direction) {
        Set<?> filtered = filter.get(direction);
        return filtered.isEmpty() || filtered.contains(item);
    }
}
