package muramasa.antimatter.capability.item;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.SlotType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;

public class FakeTrackedItemHandler<T extends IGuiHandler> extends TrackedItemHandler<T> {
    public FakeTrackedItemHandler(T tile, SlotType<?> type, int size, boolean output, boolean input, BiPredicate<IGuiHandler, ItemStack> validator) {
        super(tile, type, size, output, input, validator);
    }

    @NotNull
    @Override
    public ItemStack extractFromInput(int slot, int amount, boolean simulate) {
        super.extractFromInput(slot, amount, simulate);
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        super.setItem(slot, stack.copy());
    }
}
