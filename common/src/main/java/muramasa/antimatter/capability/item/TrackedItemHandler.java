package muramasa.antimatter.capability.item;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.gui.SlotType;
import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.blockentity.BlockEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiPredicate;

public class TrackedItemHandler<T extends IGuiHandler> extends ItemStackHandler implements ITrackedHandler {

    private final T tile;
    private final ContentEvent contentEvent;
    private final boolean output;
    private final boolean input;
    private final BiPredicate<IGuiHandler, ItemStack> validator;
    private final int limit;
    private final int size;
    private final SlotType<?> type;

    public TrackedItemHandler(T tile, SlotType<?> type, int size, boolean output, boolean input, BiPredicate<IGuiHandler, ItemStack> validator, ContentEvent contentEvent) {
        this(tile, type, size, output, input, validator, contentEvent, 64);
    }

    public TrackedItemHandler(T tile, SlotType<?> type, int size, boolean output, boolean input, BiPredicate<IGuiHandler, ItemStack> validator, ContentEvent contentEvent, int limit) {
        super(size);
        this.tile = tile;
        this.output = output;
        this.input = input;
        this.contentEvent = contentEvent;
        this.validator = validator;
        this.limit = limit;
        this.size = size;
        this.type = type;
    }

    @Override
    public int getSlotLimit(int slot) {
        return limit;
    }

    @Override
    public void onContentsChanged(int slot) {
        if (tile instanceof BlockEntityMachine<?> machine){
            machine.setChanged();
            machine.onMachineEvent(contentEvent, slot);
        } else if (tile instanceof ICover cover){
            cover.source().getTile().setChanged();
        }

    }

    @NotNull
    @Override
    public ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        if (this.type == SlotType.IT_IN){
            for (int i = 0; i < size; i++){
                if (i == slot) continue;
                if (this.getItem(i).isEmpty()) continue;
                if (Utils.equals(this.getItem(i), stack)) return stack;
            }
        }
        if (!input)
            return stack;
        boolean validate = validator.test(tile, stack);
        if (!validate)
            return stack;
        /*if (simulate) {

        }*/
        return super.insertItem(slot, stack, simulate);
    }

    @NotNull
    public ItemStack insertOutputItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return super.insertItem(slot, stack, simulate);
    }

    @NotNull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (!output)
            return ItemStack.EMPTY;
        return super.extractItem(slot, amount, simulate);
    }

    @NotNull
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
    public boolean canPlaceItem(int slot, @NotNull ItemStack stack) {
        return true;//validator.test(tile, stack);
    }
}
