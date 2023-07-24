package muramasa.antimatter.capability.item;

import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import tesseract.api.item.ExtendedItemContainer;
import tesseract.api.item.IItemNode;

import javax.annotation.Nonnull;

public class ROCombinedInvWrapper extends CombinedInvWrapper implements IItemNode {
    public ROCombinedInvWrapper(ExtendedItemContainer... itemHandler) {
        super(itemHandler);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        return super.extractItem(slot, amount, true);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return stack;
    }

    @Override
    public void setItem(int slot, @Nonnull ItemStack stack) {
    }

    @Override
    public int getPriority(Direction direction) {
        return 0;
    }

    @Override
    public boolean isEmpty(int slot) {
        return getItem(slot).isEmpty();
    }

    @Override
    public boolean canOutput() {
        return false;
    }

    @Override
    public boolean canInput() {
        return false;
    }

    @Override
    public boolean canInput(Direction direction) {
        return false;
    }

    @Override
    public boolean canOutput(Direction direction) {
        return false;
    }
}
