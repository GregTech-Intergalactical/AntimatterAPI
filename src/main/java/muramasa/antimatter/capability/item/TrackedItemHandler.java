package muramasa.antimatter.capability.item;

import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.recipe.map.RecipeMap;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import java.util.function.Predicate;

public class TrackedItemHandler<T extends TileEntityMachine> extends ItemStackHandler implements ITrackedHandler {

    private final T tile;
    private final ContentEvent contentEvent;
    private final boolean output;
    private final Predicate<ItemStack> validator;

    public TrackedItemHandler(T tile, int size, boolean output, Predicate<ItemStack> validator, ContentEvent contentEvent) {
        super(size);
        this.tile = tile;
        this.output = output;
        this.contentEvent = contentEvent;
        this.validator = validator;
    }

    @Override
    public int getStackLimit(int slot, @Nonnull ItemStack stack) {
        return super.getStackLimit(slot, stack);
    }

    @Override
    protected void onContentsChanged(int slot) {
        tile.markDirty();
        tile.onMachineEvent(contentEvent, slot);
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (output)
            return stack;
        if (simulate) {
            RecipeMap<?> map = tile.getMachineType().getRecipeMap();
            if (!tile.recipeHandler.map(t -> t.accepts(stack)).orElse(true)) return stack;
        }
        return super.insertItem(slot, stack, simulate);
    }

    @Nonnull
    public ItemStack insertOutputItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        return super.insertItem(slot, stack, simulate);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!output)
            return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }

    @Nonnull
    public ItemStack extractFromInput(int slot, @Nonnull int amount, boolean simulate) {
        return super.extractItem(slot, amount, simulate);
    }


    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return validator.test(stack);
    }
}
