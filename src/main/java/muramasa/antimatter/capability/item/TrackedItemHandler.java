package muramasa.antimatter.capability.item;

import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraftforge.items.ItemStackHandler;

public class TrackedItemHandler<T extends TileEntityMachine> extends ItemStackHandler {

    private final T tile;
    private final ContentEvent contentEvent;

    public TrackedItemHandler(T tile, int size, ContentEvent contentEvent) {
        super(size);
        this.tile = tile;
        this.contentEvent = contentEvent;
    }

    @Override
    protected void onContentsChanged(int slot) {
        tile.markDirty();
        tile.onMachineEvent(contentEvent, slot);
    }

}
