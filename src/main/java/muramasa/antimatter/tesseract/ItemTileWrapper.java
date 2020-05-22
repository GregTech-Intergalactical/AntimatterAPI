package muramasa.antimatter.tesseract;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import it.unimi.dsi.fastutil.objects.ObjectSets;
import muramasa.antimatter.Data;
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
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Set;

@ParametersAreNonnullByDefault
public class ItemTileWrapper implements IItemNode, ITileWrapper {

    private TileEntity tile;
    private boolean removed;
    private IItemHandler handler;

    private CoverInstance[] covers = new CoverInstance[] {
        Data.COVER_EMPTY, Data.COVER_EMPTY, Data.COVER_EMPTY, Data.COVER_EMPTY, Data.COVER_EMPTY, Data.COVER_EMPTY
    };

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
        if (side == null) {
            if (tile.isRemoved()) {
                Tesseract.ITEM.remove(tile.getWorld().getDimension().getType().getId(), tile.getPos().toLong());
                removed = true;
            } else {
                // What if tile is recreate cap ?
            }
        } else {
            covers[side.getIndex()] = Data.COVER_EMPTY;
        }
    }

    @Override
    public void onUpdate(Direction side, Cover cover) {
        covers[side.getIndex()] = new CoverInstance(cover, this.tile);
    }

    @Override
    public boolean isRemoved() {
        return removed;
    }

    @Override
    public int insert(ItemData data, boolean simulate) {
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
    public IntList getAvailableSlots(Dir direction) {
        Set<?> filtered = getFiltered(direction.getIndex());
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
        return covers[direction.getIndex()].backing() instanceof CoverOutput;
    }

    @Override
    public boolean canInput(Object item, Dir direction) {
        return isItemAvailable(item, direction.getIndex()) && getFirstValidSlot(item) != -1;
    }

    @Override
    public boolean connects(Dir direction) {
        return true;
    }

    private boolean isItemAvailable(Object item, int dir) {
        if (covers[dir].backing() instanceof CoverTintable) return false;
        Set<?> filtered = getFiltered(dir);
        return filtered.isEmpty() || filtered.contains(item);
    }

    // Fast way to find available slot for item
    private int getFirstValidSlot(Object item) {
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

    private Set<?> getFiltered(int index) {
        return covers[index].backing() instanceof CoverFilter<?> ? ((CoverFilter<?>) covers[index].backing()).getFilter() : ObjectSets.EMPTY_SET;
    }
}
