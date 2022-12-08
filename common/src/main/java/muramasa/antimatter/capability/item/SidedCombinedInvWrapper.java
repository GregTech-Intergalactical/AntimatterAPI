package muramasa.antimatter.capability.item;

import muramasa.antimatter.capability.CoverHandler;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nonnull;

public class SidedCombinedInvWrapper extends CombinedInvWrapper {
    Direction side;
    CoverHandler<?> coverHandler;

    public SidedCombinedInvWrapper(Direction side, CoverHandler<?> coverHandler, IItemHandlerModifiable... itemHandler) {
        super(itemHandler);
        this.side = side;
        this.coverHandler = coverHandler;
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (coverHandler != null && coverHandler.get(side).blocksInput(IItemHandler.class, side))
            return stack;
        return super.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (coverHandler != null && coverHandler.get(side).blocksOutput(IItemHandler.class, side))
            return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }
}
