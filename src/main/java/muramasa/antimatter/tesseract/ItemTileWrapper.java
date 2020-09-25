package muramasa.antimatter.tesseract;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import muramasa.antimatter.cover.*;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import tesseract.Tesseract;
import tesseract.api.item.IItemNode;
import tesseract.api.item.ItemData;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Set;

public class ItemTileWrapper extends TileWrapper<IItemHandler> implements IItemNode<ItemStack> {

    public ItemTileWrapper(TileEntity tile) {
        super(tile, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
    }

    @Override
    public void onInit() {
        Tesseract.ITEM.registerNode(tile.getWorld().getDimension().getType().getId(), tile.getPos().toLong(), this);
    }

    @Override
    public void onRemove() {
        Tesseract.ITEM.remove(tile.getWorld().getDimension().getType().getId(), tile.getPos().toLong());
    }

    @Override
    public int insert(ItemData<ItemStack> data, boolean simulate) {
        ItemStack stack = data.getStack();
        int slot = getFirstValidSlot(stack);
        if (slot == -1) {
            return 0;
        }

        ItemStack inserted = handler.insertItem(slot, stack, simulate);
        int count = stack.getCount();
        if (!inserted.isEmpty()) {
            count -= inserted.getCount() ;
        }

        return count;
    }

    @Nullable
    @Override
    public ItemData<ItemStack> extract(int slot, int amount, boolean simulate) {
        ItemStack stack = handler.extractItem(slot, amount, simulate);
        return stack.isEmpty() ? null : new ItemData<>(slot, stack);
    }

    @Nonnull
    @Override
    public IntList getAvailableSlots(Dir direction) {
        Set<?> filtered = getFilterAt(direction.getIndex());
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
    public int getOutputAmount(Dir direction) {
        return 1;
    }

    @Override
    public int getPriority(Dir direction) {
        return 0;
    }

    @Override
    public boolean isEmpty(int slot) {
        return handler.getStackInSlot(slot).isEmpty();
    }

    @Override
    public boolean canOutput() {
        return handler != null;
    }

    @Override
    public boolean canInput() {
        return handler != null;
    }

    @Override
    public boolean canOutput(Dir direction) {
        return getCoverAt(direction.getIndex()) instanceof CoverOutput;
    }

    @Override
    public boolean canInput(ItemStack item, Dir direction) {
        return isItemAvailable(item, direction.getIndex()) && getFirstValidSlot(item) != -1;
    }

    @Override
    public boolean connects(Dir direction) {
        return true;
    }

    private boolean isItemAvailable(ItemStack item, int dir) {
        if (getCoverAt(dir) instanceof CoverTintable) return false;
        Set<?> filtered = getFilterAt(dir);
        return filtered.isEmpty() || filtered.contains(item.getItem());
    }

    // Fast way to find available slot for item
    private int getFirstValidSlot(ItemStack item) {
        int slot = -1;
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty()) {
                slot = i;
            } else {
                if (stack.isItemEqual(item) && stack.getMaxStackSize() > stack.getCount()){
                    return i;
                }
            }
        }
        return slot;
    }
}
