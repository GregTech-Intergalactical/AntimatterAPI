package muramasa.antimatter.capability.item;

import muramasa.antimatter.capability.machine.MachineItemHandler;
import net.minecraft.world.item.ItemStack;
import tesseract.api.item.ExtendedItemContainer;

import javax.annotation.Nonnull;

public class MultiTrackedItemHandler extends CombinedInvWrapper implements ITrackedHandler {

    public MultiTrackedItemHandler(ExtendedItemContainer... itemHandler) {
        super(itemHandler);
    }

    @Nonnull
    @Override
    public ItemStack insertOutputItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        int index = getIndexForSlot(slot);
        ExtendedItemContainer handler = getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        return MachineItemHandler.insertIntoOutput(handler, slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractFromInput(int slot, int amount, boolean simulate) {
        int index = getIndexForSlot(slot);
        ExtendedItemContainer handler = getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        return MachineItemHandler.extractFromInput(handler, slot, amount, simulate);
    }
}
