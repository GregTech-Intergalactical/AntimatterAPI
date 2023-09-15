package muramasa.antimatter.capability.item;

import muramasa.antimatter.capability.machine.MachineItemHandler;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import tesseract.api.item.ExtendedItemContainer;

public class MultiTrackedItemHandler extends CombinedInvWrapper implements ITrackedHandler {

    public MultiTrackedItemHandler(ExtendedItemContainer... itemHandler) {
        super(itemHandler);
    }

    @NotNull
    @Override
    public ItemStack insertOutputItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        int index = getIndexForSlot(slot);
        ExtendedItemContainer handler = getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        return MachineItemHandler.insertIntoOutput(handler, slot, stack, simulate);
    }

    @NotNull
    @Override
    public ItemStack extractFromInput(int slot, int amount, boolean simulate) {
        int index = getIndexForSlot(slot);
        ExtendedItemContainer handler = getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        return MachineItemHandler.extractFromInput(handler, slot, amount, simulate);
    }
}
