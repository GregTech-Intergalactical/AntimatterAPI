package muramasa.antimatter.capability.impl;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.ItemStackHandler;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

public class ItemStackWrapper implements IItemHandler, IItemHandlerModifiable {

    // TODO: Add black/white lister filter mode
    private ItemStackHandler handler;
    private Set<Item>[] filter = new Set[]{new ObjectOpenHashSet<>(), new ObjectOpenHashSet<>(), new ObjectOpenHashSet<>(), new ObjectOpenHashSet<>(), new ObjectOpenHashSet<>(), new ObjectOpenHashSet<>()};

    public ItemStackWrapper(TileEntityMachine machine, int size, ContentEvent event) {
        handler = new ItemStackHandler(size) {
            @Override
            protected void onContentsChanged(int slot) {
                machine.onMachineEvent(event, slot);
            }
        };
    }

    public ItemStackWrapper(ItemStackHandler handler) {
        this.handler = handler;
    }

    @Nonnull
    public IntList getAvailableSlots(@Nonnull Dir direction) {
        Set<?> filtered = filter[direction.getIndex()];
        int size = handler.getSlots();
        IntList slots = new IntArrayList(size);
        if (filtered.isEmpty()) {
            for (int i = 0; i < size; i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty()) {
                    slots.add(i);
                }
            }
        } else {
            for (int i = 0; i < size; i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty() && filtered.contains(stack.getItem())) {
                    slots.add(i);
                }
            }
        }
        return slots;
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
        Set<?> filtered = filter[direction.getIndex()];
        return filtered.isEmpty() || filtered.contains(item);
    }

    // Fast way to find available slot for item
    public int getFirstValidSlot(@Nonnull Object item) {
        int slot = -1;
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty()) {
                slot = i;
            } else {
                if (stack.getItem().equals(item) && stack.getMaxStackSize() > stack.getCount()) {
                    return i;
                }
            }
        }
        return slot;
    }
}
