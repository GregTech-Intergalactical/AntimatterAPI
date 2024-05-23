package muramasa.antimatter.capability.item;

import muramasa.antimatter.capability.CoverHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import tesseract.api.item.ExtendedItemContainer;
import tesseract.api.item.IItemNode;

import java.util.function.Predicate;

public class SidedCombinedInvWrapper extends CombinedInvWrapper implements IItemNode {
    protected Direction side;
    protected CoverHandler<?> coverHandler;
    private final Predicate<Direction> inputFunction;
    private final Predicate<Direction> outputFunction;

    public SidedCombinedInvWrapper(Direction side, CoverHandler<?> coverHandler, Predicate<Direction> inputFunction, Predicate<Direction> outputFunction, ExtendedItemContainer... itemHandler) {
        super(itemHandler);
        this.side = side;
        this.coverHandler = coverHandler;
        this.inputFunction = inputFunction;
        this.outputFunction = outputFunction;
    }

    public SidedCombinedInvWrapper(Direction side, CoverHandler<?> coverHandler, ExtendedItemContainer... itemHandler) {
        this(side, coverHandler, d -> true, d -> true, itemHandler);
    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (!inputFunction.test(side)) return stack;
        if (coverHandler != null) {
            if (coverHandler.get(side).blocksInput(ExtendedItemContainer.class, side)) {
                return stack;
            }
            ItemStack copy = stack.copy();
            if (coverHandler.onTransfer(copy, side, true, simulate)) {
                return copy;
            }
        }
        return super.insertItem(slot, stack, simulate);
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!outputFunction.test(side)) return ItemStack.EMPTY;
        if (coverHandler != null && (coverHandler.get(side).blocksOutput(ExtendedItemContainer.class, side) || coverHandler.onTransfer(getItem(slot), side, false, simulate)))
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
