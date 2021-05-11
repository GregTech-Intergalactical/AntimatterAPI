package muramasa.antimatter.gui.slot;

import muramasa.antimatter.capability.item.TrackedItemHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class AbstractSlot extends SlotItemHandler {
    protected final int index;

    public AbstractSlot(IItemHandler stackHandler, int index, int x, int y) {
        super(stackHandler, index, x, y);
        this.index = index;
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int amount) {
        return MachineItemHandler.extractFromInput(this.getItemHandler(), index, amount, false);
    }

    @Override
    public int getItemStackLimit(@Nonnull ItemStack stack)
    {
       /* IItemHandler handler = this.getItemHandler();
        if (handler instanceof TrackedItemHandler) {
            ItemStack maxAdd = stack.copy();
            int maxInput = stack.getMaxStackSize();
            maxAdd.setCount(maxInput);

            ItemStack currentStack = handler.getStackInSlot(index);
            TrackedItemHandler<?> handlerModifiable = (TrackedItemHandler<?>) handler;

            handlerModifiable.setStackInSlot(index, ItemStack.EMPTY);

            ItemStack remainder = handlerModifiable.insertItem(index, maxAdd, true);

            handlerModifiable.setStackInSlot(index, currentStack);

            return maxInput - remainder.getCount();
        } else {*/
            return super.getItemStackLimit(stack);
      //  }
    }
}
