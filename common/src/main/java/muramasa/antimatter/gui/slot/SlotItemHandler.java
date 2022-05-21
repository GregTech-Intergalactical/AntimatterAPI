package muramasa.antimatter.gui.slot;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;

public class SlotItemHandler extends Slot {
    private static Container emptyInventory = new SimpleContainer(0);
    private final IItemHandler itemHandler;
    private final int index;

    public SlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
        super(emptyInventory, index, xPosition, yPosition);
        this.itemHandler = itemHandler;
        this.index = index;
    }

    public boolean mayPlace(@Nonnull ItemStack stack) {
        return stack.isEmpty() ? false : this.itemHandler.isItemValid(this.index, stack);
    }

    @Nonnull
    public ItemStack getItem() {
        return this.getItemHandler().getStackInSlot(this.index);
    }

    public void set(@Nonnull ItemStack stack) {
        ((IItemHandlerModifiable)this.getItemHandler()).setStackInSlot(this.index, stack);
        this.setChanged();
    }

    public void onQuickCraft(@Nonnull ItemStack oldStack, @Nonnull ItemStack newStack) {
    }

    public int getMaxStackSize() {
        return this.itemHandler.getSlotLimit(this.index);
    }

    public int getMaxStackSize(@Nonnull ItemStack stack) {
        ItemStack maxAdd = stack.copy();
        int maxInput = stack.getMaxStackSize();
        maxAdd.setCount(maxInput);
        IItemHandler handler = this.getItemHandler();
        ItemStack currentStack = handler.getStackInSlot(this.index);
        if (handler instanceof IItemHandlerModifiable handlerModifiable) {
            handlerModifiable.setStackInSlot(this.index, ItemStack.EMPTY);
            ItemStack remainder = handlerModifiable.insertItem(this.index, maxAdd, true);
            handlerModifiable.setStackInSlot(this.index, currentStack);
            return maxInput - remainder.getCount();
        } else {
            ItemStack remainder = handler.insertItem(this.index, maxAdd, true);
            int current = currentStack.getCount();
            int added = maxInput - remainder.getCount();
            return current + added;
        }
    }

    public boolean mayPickup(Player player) {
        return !this.getItemHandler().extractItem(this.index, 1, true).isEmpty();
    }

    @Nonnull
    public ItemStack remove(int amount) {
        return this.getItemHandler().extractItem(this.index, amount, false);
    }

    public IItemHandler getItemHandler() {
        return this.itemHandler;
    }
}
