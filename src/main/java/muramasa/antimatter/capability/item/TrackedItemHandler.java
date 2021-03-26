package muramasa.antimatter.capability.item;

import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class TrackedItemHandler<T extends TileEntityMachine> extends ItemStackHandler {

    private final T tile;
    private final ContentEvent contentEvent;

    public TrackedItemHandler(T tile, int size, ContentEvent contentEvent) {
        super(size);
        this.tile = tile;
        this.contentEvent = contentEvent;
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
}
