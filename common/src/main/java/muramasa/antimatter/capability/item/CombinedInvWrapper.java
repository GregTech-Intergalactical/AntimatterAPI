package muramasa.antimatter.capability.item;

import muramasa.antimatter.blockentity.multi.BlockEntityHatch;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.util.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import tesseract.api.item.ExtendedItemContainer;

public class CombinedInvWrapper implements ExtendedItemContainer {

    protected final ExtendedItemContainer[] itemHandler; // the handlers
    protected final int[] baseIndex; // index-offsets of the different handlers
    protected final int slotCount; // number of total slots

    public CombinedInvWrapper(ExtendedItemContainer... itemHandler)
    {
        this.itemHandler = itemHandler;
        this.baseIndex = new int[itemHandler.length];
        int index = 0;
        for (int i = 0; i < itemHandler.length; i++)
        {
            index += itemHandler[i].getContainerSize();
            baseIndex[i] = index;
        }
        this.slotCount = index;
    }
    // returns the handler index for the slot
    protected int getIndexForSlot(int slot)
    {
        if (slot < 0)
            return -1;

        for (int i = 0; i < baseIndex.length; i++)
        {
            if (slot - baseIndex[i] < 0)
            {
                return i;
            }
        }
        return -1;
    }

    protected ExtendedItemContainer getHandlerFromIndex(int index)
    {
        if (index < 0 || index >= itemHandler.length)
        {
            return EmptyContainer.INSTANCE;
        }
        return itemHandler[index];
    }

    protected int getSlotFromIndex(int slot, int index)
    {
        if (index <= 0 || index >= baseIndex.length)
        {
            return slot;
        }
        return slot - baseIndex[index - 1];
    }

    @Override
    @NotNull
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        int index = getIndexForSlot(slot);
        ExtendedItemContainer handler = getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        if (handler instanceof TrackedItemHandler<?> trackedItemHandler){
            if (trackedItemHandler.getType() == SlotType.IT_IN && !(trackedItemHandler.getTile() instanceof BlockEntityHatch<?>)){
                for (int i = 0; i < trackedItemHandler.getSize(); i++){
                    if (i == slot) continue;
                    if (trackedItemHandler.getItem(i).isEmpty()) continue;
                    if (Utils.equals(trackedItemHandler.getItem(i), stack)) return stack;
                }
            }
        }
        return handler.insertItem(slot, stack, simulate);
    }

    @Override
    @NotNull
    public ItemStack extractItem(int slot, int amount, boolean simulate)
    {
        int index = getIndexForSlot(slot);
        ExtendedItemContainer handler = getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        return handler.extractItem(slot, amount, simulate);
    }

    @Override
    public int getSlotLimit(int slot)
    {
        int index = getIndexForSlot(slot);
        ExtendedItemContainer handler = getHandlerFromIndex(index);
        int localSlot = getSlotFromIndex(slot, index);
        return handler.getSlotLimit(localSlot);
    }

    @Override
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack)
    {
        int index = getIndexForSlot(slot);
        ExtendedItemContainer handler = getHandlerFromIndex(index);
        int localSlot = getSlotFromIndex(slot, index);
        return handler.canPlaceItem(localSlot, stack);
    }

    @Override
    public void deserialize(CompoundTag nbt) {

    }

    @Override
    public CompoundTag serialize(CompoundTag nbt) {
        return null;
    }

    @Override
    public int getContainerSize() {
        return slotCount;
    }

    @Override
    public ItemStack getItem(int slot) {
        int index = getIndexForSlot(slot);
        ExtendedItemContainer handler = getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        return handler.getItem(slot);
    }

    @Override
    public void setItem(int slot, ItemStack stack) {
        int index = getIndexForSlot(slot);
        ExtendedItemContainer handler = getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        handler.setItem(slot, stack);
    }
}
