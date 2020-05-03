package muramasa.antimatter.capability.enet;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.capability.INodeHandler;
import muramasa.antimatter.cover.Cover;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import tesseract.TesseractAPI;
import tesseract.api.item.IItemNode;
import tesseract.api.item.ItemData;
import tesseract.util.Dir;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class ItemNode implements IItemNode, INodeHandler {

    private TileEntity tile;
    private IItemHandler handler;
    private Map<Dir, Set<Item>> filter = new EnumMap<>(Dir.class);
    private boolean out = new Random().nextBoolean(); // TODO: For test

    public ItemNode(TileEntity tile) {
        this.tile = tile;
        this.handler = handler;

        LazyOptional<IItemHandler> item = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        if (energy.isPresent()) {
            new ItemNode(tile);
        }

        for (Dir direction : Dir.VALUES) {
            filter.put(direction, new ObjectOpenHashSet<>());
        }

        TesseractAPI.registerItemNode(tile.getWorld().getDimension().getType().getId(), tile.getPos().toLong(), this);
    }

    @Override
    public void onRemove(Direction side) {
        TesseractAPI.removeItem(tile.getWorld().getDimension().getType().getId(), tile.getPos().toLong());
    }

    @Override
    public void onUpdate(Direction side, Cover cover) {
        //if (cover instanceof CoverFilter)
    }

    @Override
    public int insert(@Nonnull ItemData data, boolean simulate) {
        ItemStack stack = (ItemStack) data.getStack();
        int slot = getFirstValidSlot(stack.getItem());
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
    public ItemData extract(int slot, int amount, boolean simulate) {
        ItemStack stack = handler.extractItem(slot, amount, simulate);
        return stack.isEmpty() ? null : new ItemData(slot, stack, stack.getItem());
    }

    @Nonnull
    @Override
    public IntList getAvailableSlots(@Nonnull Dir direction) {
        Set<?> filtered = filter.get(direction);
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
    public int getOutputAmount(@Nonnull Dir direction) {
        return 1;
    }

    @Override
    public int getPriority(@Nonnull Dir direction) {
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
    public boolean canOutput(@Nonnull Dir direction) {
        return out; // TODO: Should depend on nearest pipe cover
    }

    @Override
    public boolean canInput(@Nonnull Object item, Dir direction) {
        return isItemAvailable(item, direction) && getFirstValidSlot(item) != -1;
    }

    @Override
    public boolean connects(@Nonnull Dir direction) {
        return true;
    }

    private boolean isItemAvailable(@Nonnull Object item, Dir direction) {
        Set<?> filtered = filter.get(direction);
        return filtered.isEmpty() || filtered.contains(item);
    }

    // Fast way to find available slot for item
    private int getFirstValidSlot(@Nonnull Object item) {
        int slot = -1;
        for (int i = 0; i < handler.getSlots(); i++) {
            ItemStack stack = handler.getStackInSlot(i);
            if (stack.isEmpty()) {
                slot = i;
            } else {
                if (stack.getItem().equals(item) && stack.getMaxStackSize() > stack.getCount()){
                    return i;
                }
            }
        }
        return slot;
    }
}
