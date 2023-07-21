package muramasa.antimatter.capability.item;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import tesseract.api.item.ExtendedItemContainer;

public class EmptyContainer implements ExtendedItemContainer {
    public static final EmptyContainer INSTANCE = new EmptyContainer();
    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return stack;
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        return ItemStack.EMPTY;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 0;
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
        return 0;
    }

    @Override
    public ItemStack getItem(int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int index, ItemStack stack) {

    }
}
