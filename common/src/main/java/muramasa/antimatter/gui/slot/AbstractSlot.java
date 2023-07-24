package muramasa.antimatter.gui.slot;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.capability.item.TrackedItemHandler;
import muramasa.antimatter.capability.machine.MachineItemHandler;
import muramasa.antimatter.gui.SlotType;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import tesseract.api.item.ExtendedItemContainer;

import javax.annotation.Nonnull;

public class AbstractSlot<T extends Slot> extends Slot {
    protected final int index;
    public final SlotType<T> type;
    protected final IGuiHandler holder;
    private final ExtendedItemContainer container;

    public AbstractSlot(SlotType<T> type, IGuiHandler tile, ExtendedItemContainer stackHandler, int index, int x, int y) {
        super(stackHandler, index, x, y);
        this.container = stackHandler;
        this.index = index;
        this.type = type;
        this.holder = tile;
    }

    @Override
    public void onQuickCraft(@Nonnull ItemStack oldStackIn, @Nonnull ItemStack newStackIn) {
        super.onQuickCraft(oldStackIn, newStackIn);
        if (this.container instanceof TrackedItemHandler<?> trackedItemHandler) {
            trackedItemHandler.onContentsChanged(this.index);
        }
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.container instanceof TrackedItemHandler<?> trackedItemHandler) {
            trackedItemHandler.onContentsChanged(this.index);
        }
    }


    @Override
    @Nonnull
    public ItemStack remove(int amount) {
        return MachineItemHandler.extractFromInput(this.container, index, amount, false);
    }

    @Override
    public boolean mayPickup(Player playerIn) {
        return !MachineItemHandler.extractFromInput(this.container, index, 1, true).isEmpty();
    }

    @Override
    public boolean mayPlace(@Nonnull ItemStack stack) {
        return this.type.tester.test(this.holder, stack);
    }

    @Override
    public int getMaxStackSize(@Nonnull ItemStack stack) {
        ItemStack maxAdd = stack.copy();
        int maxInput = stack.getMaxStackSize();
        maxAdd.setCount(maxInput);
        ItemStack currentStack = container.getItem(index);
        container.setItem(index, ItemStack.EMPTY);
        ItemStack remainder = container.insertItem(index, maxAdd, true);
        container.setItem(index, currentStack);
        return maxInput - remainder.getCount();
    }

    public ExtendedItemContainer getContainer() {
        return container;
    }
}
