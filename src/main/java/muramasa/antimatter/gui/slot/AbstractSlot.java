package muramasa.antimatter.gui.slot;

import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class AbstractSlot<T extends Slot & IAntimatterSlot> extends SlotItemHandler implements IAntimatterSlot {
    protected final int index;
    public final SlotType<T> type;
    protected final TileEntityMachine<?> holder;

    public AbstractSlot(SlotType<T> type, TileEntityMachine<?> tile, IItemHandler stackHandler, int index, int x, int y) {
        super(stackHandler, index, x, y);
        this.index = index;
        this.type = type;
        this.holder = tile;
    }

    @Override
    @Nonnull
    public ItemStack decrStackSize(int amount) {
        return MachineItemHandler.extractFromInput(this.getItemHandler(), index, amount, false);
    }

    @Override
    public boolean canTakeStack(PlayerEntity playerIn)
    {
        return !MachineItemHandler.extractFromInput(this.getItemHandler(), index, 1, true).isEmpty();
    }

    @Override
    public boolean isItemValid(@Nonnull ItemStack stack) {
        return this.type.tester.test(this.holder, stack);
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
