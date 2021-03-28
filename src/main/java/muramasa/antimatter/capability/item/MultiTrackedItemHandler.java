package muramasa.antimatter.capability.item;

import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;

public class MultiTrackedItemHandler extends CombinedInvWrapper {

    public MultiTrackedItemHandler(IItemHandlerModifiable... itemHandler) {
        super(itemHandler);
    }
    @Nonnull
    public ItemStack insertOutputItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        int index = getIndexForSlot(slot);
        IItemHandlerModifiable handler = getHandlerFromIndex(index);
        slot = getSlotFromIndex(slot, index);
        return MachineItemHandler.insertIntoOutput(handler, slot, stack, simulate);
    }
}
