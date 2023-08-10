package muramasa.antimatter.capability.item;

import muramasa.antimatter.capability.CoverHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import tesseract.api.item.ExtendedItemContainer;
import tesseract.api.item.IItemNode;

import javax.annotation.Nonnull;

public class SidedCombinedInvWrapper extends CombinedInvWrapper implements IItemNode {
    Direction side;
    CoverHandler<?> coverHandler;

    public SidedCombinedInvWrapper(Direction side, CoverHandler<?> coverHandler, ExtendedItemContainer... itemHandler) {
        super(itemHandler);
        this.side = side;
        this.coverHandler = coverHandler;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (coverHandler != null) {
            if (coverHandler.get(side).blocksInput(ExtendedItemContainer.class, side)) {
                return stack;
            }
            ItemStack copy = stack.copy();
            coverHandler.onTransfer(stack, side, side, simulate);
            if (copy.isEmpty()) return stack;
        }
        return super.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (coverHandler != null && coverHandler.get(side).blocksOutput(ExtendedItemContainer.class, side))
            return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }

    @Override
    public int getPriority(Direction direction) {
        return coverHandler == null ? 0 : coverHandler.get(direction).getPriority(ExtendedItemContainer.class);
    }

    @Override
    public boolean isEmpty(int slot) {
        return super.getItem(slot).isEmpty();
    }

    @Override
    public boolean canOutput() {
        return coverHandler == null || !coverHandler.get(side).blocksOutput(ExtendedItemContainer.class, side);
    }

    @Override
    public boolean canInput() {
        return coverHandler == null || !coverHandler.get(side).blocksInput(ExtendedItemContainer.class, side);
    }

    @Override
    public boolean canInput(Direction direction) {
        return coverHandler == null || !coverHandler.get(direction).blocksInput(ExtendedItemContainer.class, direction);
    }

    @Override
    public boolean canOutput(Direction direction) {
        return coverHandler == null || !coverHandler.get(direction).blocksOutput(ExtendedItemContainer.class, direction);
    }
}
