package muramasa.antimatter.capability.item;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.function.BiPredicate;

public class FakeTrackedItemHandler<T extends TileEntityMachine<T>> extends TrackedItemHandler<T>{
    public FakeTrackedItemHandler(T tile, int size, boolean output, boolean input, BiPredicate<IGuiHandler, ItemStack> validator, ContentEvent contentEvent) {
        super(tile, size, output, input, validator, contentEvent);
    }

    @Nonnull
    @Override
    public ItemStack extractFromInput(int slot, int amount, boolean simulate) {
        super.extractFromInput(slot, amount, simulate);
        return ItemStack.EMPTY;
    }

    @Nonnull
    public ItemStack insertItemFromGui(int slot, @Nonnull ItemStack stack, boolean simulate){
        if (stack.isEmpty())
            return ItemStack.EMPTY;

        if (!isItemValid(slot, stack))
            return stack;

        validateSlotIndex(slot);

        ItemStack copy = stack.copy();

        int limit = getStackLimit(slot, copy);

        boolean reachedLimit = copy.getCount() > limit;

        if (!simulate)
        {
            this.stacks.set(slot, reachedLimit ? ItemHandlerHelper.copyStackWithSize(copy, limit) : copy);
            onContentsChanged(slot);
        }

        return stack;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        super.setStackInSlot(slot, stack.copy());
    }
}
