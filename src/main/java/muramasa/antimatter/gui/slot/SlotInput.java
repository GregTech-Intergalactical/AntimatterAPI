package muramasa.antimatter.gui.slot;

import muramasa.antimatter.machine.event.ContentEvent;
import muramasa.antimatter.tile.TileEntityMachine;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.items.IItemHandler;

public class SlotInput extends AbstractSlot {

    protected TileEntityMachine tile;

    public SlotInput(TileEntityMachine tile, IItemHandler stackHandler, int index, int x, int y) {
        super(stackHandler, index, x, y);
        this.tile = tile;
    }

    @Override
    public boolean canTakeStack(PlayerEntity player) {
        return true;
    }

    @Override
    public void onSlotChanged() {
        super.onSlotChanged();
        tile.onMachineEvent(ContentEvent.ITEM_INPUT_CHANGED);
    }
}
