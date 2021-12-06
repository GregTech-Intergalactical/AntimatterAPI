package muramasa.antimatter.capability.item;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.BiPredicate;

public class FakeTrackedItemHandler<T extends TileEntityMachine<T>> extends TrackedItemHandler<T> {
    public FakeTrackedItemHandler(T tile, int size, boolean output, boolean input, BiPredicate<IGuiHandler, ItemStack> validator, ContentEvent contentEvent) {
        super(tile, size, output, input, validator, contentEvent);
    }

    @Nonnull
    @Override
    public ItemStack extractFromInput(int slot, int amount, boolean simulate) {
        super.extractFromInput(slot, amount, simulate);
        return ItemStack.EMPTY;
    }

    @Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        super.setStackInSlot(slot, stack.copy());
    }
}
