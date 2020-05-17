package muramasa.antimatter.tesseract;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import muramasa.antimatter.cover.Cover;
import muramasa.antimatter.cover.CoverOutput;
import net.minecraft.item.Item;
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

public class ItemTileWrapper implements IItemNode, ITileWrapper {

    // TODO: Add black/white lister filter mode
    private TileEntity tile;
    private IItemHandler handler;
    private Set<Item>[] filter = new Set[]{new ObjectOpenHashSet<>(), new ObjectOpenHashSet<>(), new ObjectOpenHashSet<>(), new ObjectOpenHashSet<>(), new ObjectOpenHashSet<>(), new ObjectOpenHashSet<>()};
    private boolean[] output = new boolean[]{false, false, false, false, false, false};
    private boolean[] input = new boolean[]{false, false, false, false, false, false};
    private int[] priority = new int[]{0, 0, 0, 0, 0, 0};
    private boolean valid = true;

    private ItemTileWrapper(TileEntity tile, IItemHandler handler) {
        this.tile = tile;
        this.handler = handler;
    }

    @Nullable
    public static ItemTileWrapper of(TileEntity tile) {
        LazyOptional<IItemHandler> capability = tile.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        if (capability.isPresent()) {
            ItemTileWrapper node = new ItemTileWrapper(tile, capability.orElse(null));
            capability.addListener(o -> node.onRemove(null));
            Tesseract.ITEM.registerNode(tile.getWorld().getDimension().getType().getId(), tile.getPos().toLong(), node);
            return node;
        }
        return null;
    }

    @Override
    public void onRemove(@Nullable Direction side) {
        if (side != null) {
            output[side.getIndex()] = input[side.getIndex()] = false;
        } else {
            Tesseract.ITEM.remove(tile.getWorld().getDimension().getType().getId(), tile.getPos().toLong());
            valid = false;
        }
    }

    @Override
    public void onUpdate(@Nonnull Direction side, @Nullable Cover cover) {
        /*if (cover instanceof CoverFilter) {
            filter.put(side, ((CoverFilter<Item>)cover).getFilter());
        }*/
        if (cover instanceof CoverOutput) {
            output[side.getIndex()] = true;
        }
        input[side.getIndex()] = true;
    }

    @Override
    public boolean isValid() {
        return valid;
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
    public int getOutputAmount(@Nonnull Dir direction) {
        return 1;
    }

    @Override
    public int getPriority(@Nonnull Dir direction) {
        return priority[direction.getIndex()];
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
        return output[direction.getIndex()];
    }

    @Override
    public boolean canInput(@Nonnull Object item, @Nonnull Dir direction) {
        return isItemAvailable(item, direction.getIndex()) && getFirstValidSlot(item) != -1;
    }

    @Override
    public boolean connects(@Nonnull Dir direction) {
        return true;
    }

    private boolean isItemAvailable(@Nonnull Object item, int dir) {
        Set<?> filtered = filter[dir];
        return input[dir] && (filtered.isEmpty() || filtered.contains(item));
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
