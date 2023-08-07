package muramasa.antimatter.capability.item;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.capability.IMachineHandler;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.function.BiPredicate;

public class TrackedItemHandler<T extends IGuiHandler> extends ItemStackHandler implements ITrackedHandler {

    private final T tile;
    private final ContentEvent contentEvent;
    private final boolean output;
    private final boolean input;
    private final BiPredicate<IGuiHandler, ItemStack> validator;
    private final int limit;
    private final int size;

    public TrackedItemHandler(T tile, int size, boolean output, boolean input, BiPredicate<IGuiHandler, ItemStack> validator, ContentEvent contentEvent) {
        this(tile, size, output, input, validator, contentEvent, 64);
    }

    public TrackedItemHandler(T tile, int size, boolean output, boolean input, BiPredicate<IGuiHandler, ItemStack> validator, ContentEvent contentEvent, int limit) {
        super(size);
        this.tile = tile;
        this.output = output;
        this.input = input;
        this.contentEvent = contentEvent;
        this.validator = validator;
        this.limit = limit;
        this.size = size;
    }

    @Override
    public int getSlotLimit(int slot) {
        return limit;
    }

    @Override
    public void onContentsChanged(int slot) {
        if (tile instanceof TileEntityMachine<?> machine){
            machine.setChanged();
            machine.onMachineEvent(contentEvent, slot);
        } else if (tile instanceof ICover cover){
            cover.source().getTile().setChanged();
        }

    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (!input)
            return stack;
        boolean validate = validator.test(tile, stack);
        if (!validate)
            return stack;
        /*if (simulate) {

        }*/
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
    public ItemStack extractFromInput(int slot, int amount, boolean simulate) {
        return super.extractItem(slot, amount, simulate);
    }
    //Size is defined by GUI and not the NBT data.
    @Override
    public CompoundTag serialize(CompoundTag nbt) {
        super.serialize(nbt);
        nbt.remove("Size");
        return nbt;
    }

    @Override
    public boolean canPlaceItem(int slot, @Nonnull ItemStack stack) {
        return true;//validator.test(tile, stack);
    }
}
